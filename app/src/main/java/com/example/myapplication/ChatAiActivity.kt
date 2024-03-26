package com.example.myapplication

import android.os.Bundle
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityChatAiBinding
import http.RetrofitManager
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import model.ChatAiMessageResponse
import okhttp3.ResponseBody
import retrofit2.Response
import service.Toast

class ChatAiActivity : BaseActivity() {

    private lateinit var text: String
    private lateinit var editText: EditText
    private lateinit var binding: ActivityChatAiBinding
    private val apiService = RetrofitManager.getInstance().provideApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_ai)
        editText = findViewById(R.id.messageEditText)

        binding.sendButton.setOnClickListener {
            text = editText.text.toString()
            chat(text)
        }
    }

    private fun chat(text: String) {
        apiService.chatAi(text)
            .subscribeOn(Schedulers.io()) // IO 线程执行网络请求
            .observeOn(AndroidSchedulers.mainThread()) // 主线程更新数据
            .subscribe(object : Observer<Response<ResponseBody>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: Response<ResponseBody>) {
                    // 登录成功后的操作
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string()
                        val messageResponse =
                            GsonSingleton.gson.fromJson(responseBody, ChatAiMessageResponse::class.java)
                    } else {
                        Toast.showToast(this@ChatAiActivity, "发送失败")

                    }
                }

                override fun onError(e: Throwable) {
                    Toast.showToast(this@ChatAiActivity, "网络请求失败: ${e.message}")
                }

                override fun onComplete() {

                }
            })
    }
}