package com.example.myapplication
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityLoginBinding
import http.RetrofitManager
import kotlinx.coroutines.DelicateCoroutinesApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import service.API
import service.Toast


class LoginActivity: AppCompatActivity() {

    private lateinit var accountText: EditText
    private lateinit var passwordText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {


        //UI
        super.onCreate(savedInstanceState)
        val binding : ActivityLoginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        accountText = findViewById(R.id.login_account)                   
        passwordText = findViewById(R.id.login_password)  // 读取输入框内的内容 先绑定 R.layout后才能找到输入框
        supportActionBar?.hide() //隐藏actionbar

        val apiService = RetrofitManager.getInstance().provideApiService()
        binding.loginBottom.setOnClickListener{
            val username =  accountText.text.toString()
            val password =  passwordText.text.toString()

            val registerCall = apiService.login(username,password)
            registerCall.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.showToast(this@LoginActivity,"登录成功")
                    } else {
                        Toast.showToast(this@LoginActivity,"登录失败")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.showToast(this@LoginActivity,"网络请求失败")
                }
            })
        }

        binding.registerBottom.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}