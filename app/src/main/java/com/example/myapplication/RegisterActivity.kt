package com.example.myapplication

import android.os.Bundle
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityRegisterBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import service.ApiService
import service.Toast

class RegisterActivity:BaseActivity() {

    private lateinit var accountText: EditText
    private lateinit var passwordText: EditText
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityRegisterBinding = DataBindingUtil.setContentView(this,R.layout.activity_register)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitleText("注册")

        val ApiService = ApiService.getInstance()
        accountText = findViewById(R.id.register_account)
        passwordText = findViewById(R.id.register_password)

        //添加登录按钮点击事件
        binding.registerBottom.setOnClickListener{
            val username =  accountText.text.toString()     // 读取输入框内的内容
            val password =  passwordText.text.toString()
            println("hhhhh")
            // 调用登录接口
            GlobalScope.launch(Dispatchers.Main) {
                try {
                    val response : Response<String> = ApiService.register(username,password) // 调用登录接口
                    // 根据登录接口的响应结果进行处理
                    if (response.body()!=null&&response.code()==200) {
                        // 登录成功，跳转到下一个页面或执行其他操作
                        // 示例：Toast 提示登录成功
                        Toast.showToast(this@RegisterActivity, "注册成功")
                    } else if (response.body()!=null&&response.code()!=200){
                        // 登录失败，根据服务器返回的错误信息进行相应处理
                        // 示例：Toast 提示登录失败
                        Toast.showToast(this@RegisterActivity, "账号有误")
                    }else{
                        Toast.showToast(this@RegisterActivity, "注册失败")
                    }
                } catch (e: Exception) {
                    // 发生异常，提示网络请求失败或其他错误
                    // 示例：Toast 提示网络请求失败
                    Toast.showToast(this@RegisterActivity, "网络请求失败，请稍后重试")
                }
            }
        }

    }
}