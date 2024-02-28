package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityPasswordChangeBinding
import http.RetrofitManager
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import model.UserManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import service.Toast

class PasswordChangeActivity: BaseActivity()  {

    private lateinit var passwordText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityPasswordChangeBinding = DataBindingUtil.setContentView(this,R.layout.activity_password_change)
        // 启用ActionBar显示返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //修改公共基类的title
        setTitleText("修改密码")
        val apiService = RetrofitManager.getInstance().provideApiService()

        passwordText = findViewById(R.id.password_input)

        //修改按钮
        binding.bottom.setOnClickListener{
            val password = passwordText.text.toString()
            val username = UserManager.getInstance().getLoginResponse()?.data?.username ?:""

            val resetPasswordCall = apiService.resetPassword(username,password)
            resetPasswordCall.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.showToast(this@PasswordChangeActivity,"修改成功")
                    } else {
                        Toast.showToast(this@PasswordChangeActivity,"修改失败")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // 处理失败情况，如网络错误
                    //println("网络请求失败: ${t.message}")
                    Toast.showToast(this@PasswordChangeActivity,"网络请求失败")
                }
            })
        }
    }
}