package fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.myapplication.ItemDetailActivity
import com.example.myapplication.LoginActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.SearchPageBinding
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import http.RetrofitManager
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import model.Activity
import model.ItemListManager
import model.ItemResponse
import okhttp3.ResponseBody
import retrofit2.Response
import service.SharedPreferencesManager
import service.Toast

class SearchFragment : Fragment(R.layout.search_page) {

    private lateinit var binding: SearchPageBinding
    private lateinit var smartRefreshLayout: SmartRefreshLayout
    private lateinit var recyclerView : RecyclerView
    private lateinit var recyclerViewAdapter: SearchRecyclerViewAdapter
    private var apiService = RetrofitManager.getInstance().provideApiService()
    private var list: MutableList<Activity> = mutableListOf()
    private var index = 0
    private var inputText = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.search_page, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        smartRefreshLayout = binding.smartRefresh
        recyclerViewAdapter = SearchRecyclerViewAdapter(requireContext(),list)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = recyclerViewAdapter

        binding.searchButton.setOnClickListener{
            list.clear()
            inputText = binding.searchInput.text.toString()
            apiService.getSearchList(inputText,0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<ResponseBody>> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()?.string()
                            val itemResponse = GsonSingleton.gson.fromJson(responseBody, ItemResponse::class.java)
                            ItemListManager.getInstance().setItemResponse(itemResponse)
                            itemResponse.data?.forEach { activity ->
                                list.add(activity)
                            }
                        } else {
                            val sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
                            sharedPreferencesManager.resetUserToken(null)
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                            Toast.showToast(requireContext(), "token过期")
                        }
                    }

                    override fun onError(e: Throwable) {
                        Toast.showToast(requireContext(), "网络请求失败: ${e.message}")
                    }

                    override fun onComplete() {
                        // 隐藏键盘
                        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                    }
                })
        }
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}

class SearchRecyclerViewAdapter(private val context: Context, private val list: MutableList<Activity>) : RecyclerView.Adapter<SearchRecyclerViewAdapter.MyViewHolder>() {
    private var inflater: View? = null

    // onCreateViewHolder()方法用于创建ViewHolder实例
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        inflater = LayoutInflater.from(context).inflate(R.layout.search_item, parent, false)
        return MyViewHolder(inflater!!)
    }

    // onBindViewHolder()方法用于对RecyclerView子项数据进行赋值
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.titleView.text = list[position].title
        Glide.with(context)
            .load(list[position].image)
            .into(holder.pictureView)
        holder.itemView.setOnClickListener{
            val intent = Intent(context, ItemDetailActivity::class.java).apply {
                putExtra("ITEM_ID", list[position].id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleView : TextView = itemView.findViewById(R.id.search_title)
        var pictureView : ImageView = itemView.findViewById(R.id.search_image)
    }

}
