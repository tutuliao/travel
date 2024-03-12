package fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.CollectionPageBinding
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import http.RetrofitManager
import model.ActivityCollected
import model.ItemCollectedListManager
import model.ItemCollectedResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import service.Toast

class CollectFragment : Fragment(R.layout.collection_page){

    private lateinit var smartRefreshLayout : SmartRefreshLayout

    private var list: MutableList<ActivityCollected> = mutableListOf()
    private var apiService = RetrofitManager.getInstance().provideApiService()

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: CollectRecyclerViewAdapter
    private var index = 0

    private lateinit var binding: CollectionPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        list.clear()
        loadData(index)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.collection_page, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerView
        recyclerAdapter = CollectRecyclerViewAdapter(requireContext(),list)
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL;
        recyclerView.layoutManager = manager;
        recyclerView.adapter = recyclerAdapter;
        binding.smartRefresh.setRefreshHeader(ClassicsHeader(context))
        binding.smartRefresh.setRefreshFooter(ClassicsFooter(context))
        binding.smartRefresh.setOnRefreshListener{

        }
        binding.smartRefresh.setOnLoadMoreListener {

        }

    }

    override fun onStart() {
        super.onStart()
    }

    private fun loadData(index: Int) {
        apiService.getCollectList(index).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    val itemCollectedResponse = GsonSingleton.gson.fromJson(responseBody, ItemCollectedResponse::class.java)
                    ItemCollectedListManager.getInstance().setItemCollectedResponse(itemCollectedResponse)

                    if(itemCollectedResponse.data==null){
                        Toast.showToast(requireContext(),"数据加载完了")
                        smartRefreshLayout.finishLoadMoreWithNoMoreData() // 通知没有更多数据可加载
                    }else{
                        itemCollectedResponse.data.forEach { activityCollected ->
                            list.add(activityCollected)
                        }
                        binding.collectNumber.text = list.size.toString()
                        recyclerAdapter.notifyItemRangeChanged(0, list.size)
                    }
                } else {
                    Toast.showToast(requireContext(),"加载失败")
                    smartRefreshLayout.finishLoadMoreWithNoMoreData()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 处理网络请求失败的情况
                Toast.showToast(requireContext(),"网络请求失败")
            }
        })
    }
}

class CollectRecyclerViewAdapter(private val context: Context,
                                 private val list: MutableList<ActivityCollected>
) : RecyclerView.Adapter<CollectRecyclerViewAdapter.MyViewHolder>(){

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView : TextView = itemView.findViewById(R.id.title_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.collection_item,parent,false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.titleView.text = list[position].title
    }
}