package fragment
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import androidx.databinding.DataBindingUtil
import com.example.myapplication.AccountChangeActivity
import com.example.myapplication.PasswordChangeActivity
import com.example.myapplication.databinding.MePageBinding

class MeFragment : Fragment(R.layout.me_page){
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }
    private lateinit var binding: MePageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using DataBindingUtil
          binding = DataBindingUtil.inflate(
            inflater, R.layout.me_page, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.meAccountChange.setOnClickListener{
            val intent = Intent(requireActivity(), AccountChangeActivity::class.java)
            startActivity(intent)
        }

        binding.mePasswordChange.setOnClickListener{
            val intent = Intent(requireActivity(), PasswordChangeActivity::class.java)
            startActivity(intent)
        }

    }
}
