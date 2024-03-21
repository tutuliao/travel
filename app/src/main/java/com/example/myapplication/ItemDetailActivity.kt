package com.example.myapplication

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
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

    private var isCollected: Boolean = false
    private val apiService = RetrofitManager.getInstance().provideApiService()
    private lateinit var binding: ActivityItemDetailBinding
    private var itemId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_item_detail)
        itemId = intent.getIntExtra("ITEM_ID", 0) //获取从 Intent 传递过来的数据 默认值为 0
        apiService.getItemDetail(itemId)
            .subscribeOn(Schedulers.io()) // IO 线程执行网络请求
            .observeOn(AndroidSchedulers.mainThread()) // 主线程更新数据
            .subscribe(object : Observer<Response<ResponseBody>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string()
                        val itemDetailResponse = GsonSingleton.gson.fromJson(
                            responseBody,
                            ItemDetailResponse::class.java
                        )
                        ItemDetailManager.getInstance().setItemDetailResponse(itemDetailResponse)
                        binding.itemText.text =
                            ItemDetailManager.getInstance().getItemDetailResponse().data.content
                        Glide.with(this@ItemDetailActivity).load(
                            ItemDetailManager.getInstance().getItemDetailResponse().data.image1
                        ).into(binding.itemDetailPicture)
                        setTitleText(
                            ItemDetailManager.getInstance().getItemDetailResponse().data.title
                        )
                        isCollected =
                            ItemDetailManager.getInstance().getItemDetailResponse().data.isCollected
                        updateCollectButtonState()
                    } else {
                        Toast.showToast(this@ItemDetailActivity, "请重新尝试")
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.showToast(this@ItemDetailActivity, "网络请求失败: ${e.message}")
                }

                override fun onComplete() {

                }
            })
    }


    // 假设isCollected变量已经根据网络请求正确设置
    private fun updateCollectButtonState() {
        if (isCollected) {
            binding.collectButton.setImageResource(R.drawable.loved)
            binding.collectButton.setOnClickListener {
                unCollectItem()
            }
        } else {
            binding.collectButton.setImageResource(R.drawable.unloved)
            binding.collectButton.setOnClickListener {
                collectItem()
            }
        }
    }

    private fun collectItem() {
        // 收藏操作
        apiService.collectActivity(itemId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<ResponseBody>> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        isCollected = true
                        updateCollectButtonState() // 更新按钮状态
                        Toast.showToast(this@ItemDetailActivity, "收藏成功")
                    } else {
                        Toast.showToast(this@ItemDetailActivity, "收藏失败")
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.showToast(this@ItemDetailActivity, "网络请求失败: ${e.message}")
                }

                override fun onComplete() {}
            })
    }

    private fun unCollectItem() {
        // 取消收藏操作
        apiService.unCollectActivity(itemId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Response<ResponseBody>> {
                override fun onSubscribe(d: Disposable) {}

                override fun onNext(response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        isCollected = false
                        updateCollectButtonState() // 更新按钮状态
                        Toast.showToast(this@ItemDetailActivity, "取消收藏成功")
                    } else {
                        Toast.showToast(this@ItemDetailActivity, "取消收藏失败")
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.showToast(this@ItemDetailActivity, "网络请求失败: ${e.message}")
                }

                override fun onComplete() {}
            })
    }

}



