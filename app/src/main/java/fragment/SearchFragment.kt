package fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.SearchPageBinding
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import http.RetrofitManager
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import model.ItemCollectedListManager
import model.ItemCollectedResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import service.Toast

class SearchFragment : Fragment(R.layout.search_page) {

    private lateinit var binding: SearchPageBinding
    private lateinit var smartRefreshLayout: SmartRefreshLayout
    private var apiService = RetrofitManager.getInstance().provideApiService()
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
        binding.searchButton.setOnClickListener{
            inputText = binding.searchInput.text.toString()
            apiService.getSearchList(inputText,0)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Response<ResponseBody>> {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onNext(response: Response<ResponseBody>) {
                        if (response.isSuccessful) {

                        } else {

                        }
                    }

                    override fun onError(e: Throwable) {
                        Toast.showToast(requireContext(), "网络请求失败: ${e.message}")
                    }

                    override fun onComplete() {}
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