package com.example.myapplication
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityItemDetailBinding
import http.RetrofitManager
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import model.ItemDetailManager
import model.ItemDetailResponse
import okhttp3.ResponseBody
import retrofit2.Response
import service.Toast

class ItemDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityItemDetailBinding = DataBindingUtil.setContentView(this,R.layout.activity_item_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 获取从 Intent 传递过来的数据
        val itemId = intent.getIntExtra("ITEM_ID", 0) // 默认值为 0 或其他适当的值
        val apiService = RetrofitManager.getInstance().provideApiService()

        apiService.getItemDetail(itemId)
            .subscribeOn(Schedulers.io()) // IO 线程执行网络请求
            .observeOn(AndroidSchedulers.mainThread()) // 主线程更新数据
            .subscribe(object : Observer<Response<ResponseBody>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string()
                        val itemDetailResponse = GsonSingleton.gson.fromJson(responseBody, ItemDetailResponse::class.java)
                        ItemDetailManager.getInstance().setItemDetailResponse(itemDetailResponse)
                        binding.itemText.text = ItemDetailManager.getInstance().getItemDetailResponse().data.content
                        setTitleText(ItemDetailManager.getInstance().getItemDetailResponse().data.title)
                    } else {
                        Toast.showToast(this@ItemDetailActivity, "加载失败")
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.showToast(this@ItemDetailActivity, "网络请求失败: ${e.message}")
                }

                override fun onComplete() {

                }

            })
    }

}