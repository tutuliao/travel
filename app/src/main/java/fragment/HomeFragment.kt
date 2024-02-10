package fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.PagerAdapter
import com.example.myapplication.R
import com.example.myapplication.databinding.HomePageBinding


class HomeFragment : Fragment(R.layout.home_page){
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
        val adapter = BannerAdapter(requireContext(), images)
        binding.viewPager.adapter = adapter

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
