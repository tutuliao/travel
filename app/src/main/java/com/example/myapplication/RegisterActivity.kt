package com.example.myapplication

import android.os.Bundle
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityRegisterBinding
import http.RetrofitManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import service.Toast

class RegisterActivity:BaseActivity() {

    private lateinit var accountText: EditText
    private lateinit var passwordText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding : ActivityRegisterBinding = DataBindingUtil.setContentView(this,R.layout.activity_register)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitleText("注册")

        accountText = findViewById(R.id.register_account)
        passwordText = findViewById(R.id.register_password)
        val apiService = RetrofitManager.getInstance().provideApiService()

        //添加登录按钮点击事件
        binding.registerBottom.setOnClickListener{
            val username =  accountText.text.toString()     // 读取输入框内的内容
            val password =  passwordText.text.toString()
            val registerCall = apiService.register(1,username,password)
            registerCall.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.showToast(this@RegisterActivity,"注册成功")
                    } else {
                        Toast.showToast(this@RegisterActivity,"注册失败")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    // 处理失败情况，如网络错误
                    //println("网络请求失败: ${t.message}")
                    Toast.showToast(this@RegisterActivity,"网络请求失败")
                }
            })
        }


    }
}