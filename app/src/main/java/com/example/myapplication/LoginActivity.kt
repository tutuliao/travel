package com.example.myapplication
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityLoginBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import service.API
import service.Toast


class LoginActivity: AppCompatActivity() {

    private lateinit var accountText: EditText
    private lateinit var passwordText: EditText
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

         //Logic
         val api = API.getInstance()

        //UI
        super.onCreate(savedInstanceState)
        val binding : ActivityLoginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        accountText = findViewById(R.id.login_account)                   
        passwordText = findViewById(R.id.login_password)  // 读取输入框内的内容 先绑定 R.layout后才能找到输入框
        supportActionBar?.hide() //隐藏actionbar

        binding.bottom.setOnClickListener{
            val username =  accountText.text.toString()
            val password =  passwordText.text.toString()

            //使用kotlin协程异步执行登录操作
            GlobalScope.launch(Dispatchers.Main) {
                when (API.getInstance().login(username, password)) {
                    200 -> {
                        Toast.showToast(this@LoginActivity, "登录成功!")
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else -> {
                        Toast.showToast(this@LoginActivity, "登录失败!")
                    }
                }
            }
        }

    }
}