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
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.CollectionPageBinding
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import model.Activity
import model.ActivityCollected

class CollectFragment : Fragment(R.layout.collection_page){

    private lateinit var smartRefreshLayout : SmartRefreshLayout

    private var list: MutableList<ActivityCollected> = mutableListOf()
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private var index = 0

    private lateinit var binding: CollectionPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
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

        binding.smartRefresh.setOnRefreshListener{

        }

        binding.smartRefresh.setOnLoadMoreListener {

        }

    }

}

class CollectRecyclerViewAdapter(context: Context,
                                 list: MutableList<ActivityCollected>
) : RecyclerView.Adapter<CollectRecyclerViewAdapter.MyViewHolder>(){


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}