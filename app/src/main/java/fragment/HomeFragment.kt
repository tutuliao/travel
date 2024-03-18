package fragment
import GsonSingleton
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
import com.bumptech.glide.Glide
import com.example.myapplication.ItemDetailActivity
import com.example.myapplication.LoginActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.HomePageBinding
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import http.RetrofitManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import model.Activity
import model.ItemListManager
import model.ItemResponse
import retrofit2.HttpException
import service.SharedPreferencesManager
import service.Toast
import java.util.Timer
import java.util.TimerTask


class HomeFragment : Fragment(R.layout.home_page){
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var recyclerView: RecyclerView // 声明 RecyclerView
    private lateinit var recyclerAdapter: RecyclerViewAdapter // 声明适配器
    private lateinit var smartRefreshLayout : SmartRefreshLayout
    private var list: MutableList<Activity> = mutableListOf()//List 是不可变的 需要一个可变的列表（MutableList），您可以将列表声明为 MutableList 类型而不是 List 类型
    private val apiService = RetrofitManager.getInstance().provideApiService()
    private var index = 0
    private var currentPage = 0
    private var timer: Timer? = null
    private val DELAY_MS: Long = 500 // 延迟时间，在开始轮播前等待的时间
    private val PERIOD_MS: Long = 2000 // 周期时间，每隔多久轮播一次
    private val images = listOf(R.drawable.hotel1, R.drawable.hotel2, R.drawable.hotel3)
    // 用于管理RxJava订阅的CompositeDisposable
    private val disposables = CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        list.clear()
        super.onCreate(savedInstanceState)
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
            refreshData()
            smartRefreshLayout.finishRefresh(1000)
        }

        smartRefreshLayout.setOnLoadMoreListener{
            index+1
            loadData(index)
            smartRefreshLayout.finishRefresh(1000)
        }

        initBannerAutoScroll()
        loadData(index)
    }

    override fun onStart() {
        super.onStart()
        smartRefreshLayout.finishRefresh(1000)
    }

    override fun onDestroy() {
        timer?.cancel()
        disposables.clear() // 取消所有订阅
        super.onDestroy()
    }


    private fun loadData(index: Int) {
        val disposable = apiService.getList(index)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    val itemResponse = GsonSingleton.gson.fromJson(responseBody, ItemResponse::class.java)
                    ItemListManager.getInstance().setItemResponse(itemResponse)
                    itemResponse.data?.forEach { activity ->
                        list.add(activity)
                    }
                    recyclerAdapter.notifyDataSetChanged()
                } else {
                    val sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
                    sharedPreferencesManager.resetUserToken(null)
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    Toast.showToast(requireContext(), "token过期")
                }
            }, { error ->
                if ((error is HttpException) && (error.code() >= 500)) {
                    // 处理HTTP错误，例如跳转到登录页面
                    val sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
                    sharedPreferencesManager.resetUserToken(null)
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                } else {
                    // 处理其他类型的错误
                    val sharedPreferencesManager = SharedPreferencesManager.getInstance(requireContext())
                    sharedPreferencesManager.resetUserToken(null)
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    Toast.showToast(requireContext(), "网络请求失败")
                }
            })

        // 添加到CompositeDisposable中
        disposables.add(disposable)
    }

    private fun refreshData() {
        // 这里假设 disposables 是一个 CompositeDisposable 实例，用于管理RxJava订阅
        val disposable = apiService.getList(0)
            .subscribeOn(Schedulers.io()) // 在 IO 线程执行网络请求
            .observeOn(AndroidSchedulers.mainThread()) // 在主线程处理请求结果
            .subscribe({ response ->
                // onResponse
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    val itemResponse = GsonSingleton.gson.fromJson(responseBody, ItemResponse::class.java)
                    ItemListManager.getInstance().setItemResponse(itemResponse)
                    itemResponse.data?.forEach { activity ->
                        list.add(activity)
                    }
                    recyclerAdapter.notifyDataSetChanged() // 通知数据改变
                } else {
                    // 处理错误响应
                    Toast.showToast(requireContext(), "加载失败")
                }
            }, { error ->
                // onFailure
                Toast.showToast(requireContext(), "网络请求失败: ${error.message}")
            })

        // 将订阅添加到 CompositeDisposable 中
        disposables.add(disposable)
    }


    private fun initBannerAutoScroll() {
        val handler = Handler(Looper.getMainLooper())
        val update = Runnable {
            if (currentPage == images.size) {
                currentPage = 0
            }
            binding.viewPager.setCurrentItem(currentPage++, true)
        }

        timer = Timer() // This will create a new Thread
        timer?.schedule(object : TimerTask() {
            // task to be scheduled
            override fun run() {
                handler.post(update)
            }
        }, DELAY_MS, PERIOD_MS)
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
open class RecyclerViewAdapter(private val context: Context, private val list: MutableList<Activity>) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
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
        // 使用 Glide 加载网络图片
        Glide.with(context)
            .load(list[position].image) // 假设是 activityItem 对象的 imageUrl 属性
            .into(holder.pictureView) // 将图片加载到 ViewHolder 的 pictureView 中
        holder.itemView.setOnClickListener{

            val intent = Intent(context, ItemDetailActivity::class.java).apply {
                // 传递数据，例如活动的 ID 或标题
                putExtra("ITEM_ID", list[position].id)
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
        var pictureView : ImageView = itemView.findViewById(R.id.home_item_picture)
    }

}

