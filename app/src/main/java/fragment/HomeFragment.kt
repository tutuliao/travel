package fragment
import android.content.Context
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
import com.example.myapplication.R
import com.example.myapplication.databinding.HomePageBinding


class HomeFragment : Fragment(R.layout.home_page){
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var recyclerView: RecyclerView // 声明 RecyclerView
    private lateinit var recyclerAdapter: RecyclerViewAdapter // 声明适配器
    private var list: MutableList<String> = mutableListOf()//List 是不可变的 需要一个可变的列表（MutableList），您可以将列表声明为 MutableList 类型而不是 List 类型
    override fun onCreate(savedInstanceState: Bundle?) {
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
        val images = listOf(R.drawable.hotel1, R.drawable.hotel2, R.drawable.hotel3)
        bannerAdapter = BannerAdapter(requireContext(), images)
        binding.viewPager.adapter = bannerAdapter
        println("hhhhh")

        for(i in 0 until 10){
            list.add("这是第${i}个测试")
        }
        recyclerView = binding.recyclerView
        recyclerAdapter = RecyclerViewAdapter(requireContext(),list)
        //创建线性布局
        val manager = LinearLayoutManager(context)
        //垂直方向
        manager.orientation = LinearLayoutManager.VERTICAL;
        //给RecyclerView设置布局管理器
        recyclerView.layoutManager = manager;
        //创建适配器，并且设置
        recyclerView.adapter = recyclerAdapter;
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
class RecyclerViewAdapter(private val context: Context, private val list: List<String>) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    private var inflater: View? = null

    // onCreateViewHolder()方法用于创建ViewHolder实例
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        inflater = LayoutInflater.from(context).inflate(R.layout.home_page_item, parent, false)
        return MyViewHolder(inflater!!)
    }

    // onBindViewHolder()方法用于对RecyclerView子项数据进行赋值
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = list[position]
    }

    // getItemCount()方法告诉RecyclerView一共有多少个子项
    override fun getItemCount(): Int {
        return list.size
    }

    // 内部类，绑定控件
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView = itemView.findViewById(R.id.text_view)
    }
}
