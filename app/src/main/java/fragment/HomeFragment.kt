package fragment
import GsonSingleton
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.example.myapplication.ItemDetailActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.HomePageBinding
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import http.RetrofitManager
import model.Activity
import model.ItemListManager
import model.ItemResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import service.Toast


class HomeFragment : Fragment(R.layout.home_page){
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var recyclerView: RecyclerView // 声明 RecyclerView
    private lateinit var recyclerAdapter: RecyclerViewAdapter // 声明适配器
    private lateinit var smartRefreshLayout : SmartRefreshLayout
    private var list: MutableList<Activity> = mutableListOf()//List 是不可变的 需要一个可变的列表（MutableList），您可以将列表声明为 MutableList 类型而不是 List 类型
    private val apiService = RetrofitManager.getInstance().provideApiService()
    private var index = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        list.clear()
        super.onCreate(savedInstanceState)
        loadData(index)
    }

    private lateinit var binding: HomePageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.home_page, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        super.onViewCreated(view, savedInstanceState)
        //banner
        val images = listOf(R.drawable.hotel1, R.drawable.hotel2, R.drawable.hotel3)
        bannerAdapter = BannerAdapter(requireContext(), images)
        binding.viewPager.adapter = bannerAdapter

        //创建线性布局 垂直方向 给RecyclerView设置布局管理器
        recyclerView = binding.recyclerView
        recyclerAdapter = RecyclerViewAdapter(requireContext(),list)
        val manager = LinearLayoutManager(context)
        manager.orientation = LinearLayoutManager.VERTICAL;
        recyclerView.layoutManager = manager;
        recyclerView.adapter = recyclerAdapter;

        //设置刷新样式
        smartRefreshLayout = binding.smartRefresh
        smartRefreshLayout.setRefreshHeader(ClassicsHeader(context))
        smartRefreshLayout.setRefreshFooter(ClassicsFooter(context))

        smartRefreshLayout.setOnRefreshListener{
            list.clear()
            recyclerAdapter.notifyItemRangeChanged(0, list.size)
            smartRefreshLayout.finishRefresh(1000)
        }

        smartRefreshLayout.setOnLoadMoreListener{
            index += 1
            loadData(index)
            smartRefreshLayout.finishRefresh(1000)
        }

    }

    private fun loadData(index: Int) {
        apiService.getList(index).enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    val itemResponse = GsonSingleton.gson.fromJson(responseBody,ItemResponse::class.java)
                    ItemListManager.getInstance().setItemResponse(itemResponse)

                    if(itemResponse.data==null){
                        Toast.showToast(requireContext(),"数据加载完了")
                        smartRefreshLayout.finishLoadMoreWithNoMoreData() // 通知没有更多数据可加载
                    }else{
                        itemResponse.data.forEach { activity ->
                            list.add(activity)
                        }
                    }
                    recyclerAdapter.notifyItemRangeChanged(0, list.size)
                } else {
                    Toast.showToast(requireContext(),"加载失败")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 处理网络请求失败的情况
                Toast.showToast(requireContext(),"网络请求失败")
            }
        })
    }
}


//banner图适配器
class BannerAdapter(private val context: Context, private val images: List<Int>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val imageView = ImageView(context)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setImageResource(images[position])
        container.addView(imageView)
        return imageView
    }

    override fun getCount(): Int = images.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view === `object`

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}

//RecyclerView 的适配器
class RecyclerViewAdapter(private val context: Context, private val list: MutableList<Activity>) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    private var inflater: View? = null

    // onCreateViewHolder()方法用于创建ViewHolder实例
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        inflater = LayoutInflater.from(context).inflate(R.layout.home_page_item, parent, false)
        return MyViewHolder(inflater!!)
    }

    // onBindViewHolder()方法用于对RecyclerView子项数据进行赋值
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.titleView.text = list[position].title
        holder.textView.text = list[position].content
        holder.itemView.setOnClickListener{

            val intent = Intent(context, ItemDetailActivity::class.java).apply {
                // 传递数据，例如活动的 ID 或标题
                putExtra("ITEM_ID", list[position].id)
                println("666")
            }
            // 从 Context 启动 DetailActivity
            context.startActivity(intent)
        }
    }

    // getItemCount()方法告诉RecyclerView一共有多少个子项
    override fun getItemCount(): Int {
        return list.size
    }

    // 内部类，绑定控件
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var titleView: TextView = itemView.findViewById(R.id.title_view)
        var textView : TextView = itemView.findViewById(R.id.text_view)
    }
}

