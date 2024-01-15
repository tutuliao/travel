package com.example.myapplication

import android.os.Bundle
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityRegisterBinding
import service.API
import service.Toast

class RegisterActivity:BaseActivity() {

    private lateinit var accountText: EditText
    private lateinit var passwordText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityRegisterBinding = DataBindingUtil.setContentView(this,R.layout.activity_register)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitleText("注册")

        val api = API.getInstance()
        accountText = findViewById(R.id.register_account)
        passwordText = findViewById(R.id.register_password)

        //添加登录按钮点击事件
        binding.bottom.setOnClickListener{
            val username =  accountText.text.toString()     // 读取输入框内的内容
            val password =  passwordText.text.toString()
            if(api.register(username,password) == 200){
                Toast.showToast(this,"注册成功!")
            }else{
                Toast.showToast(this,"注册失败!")
            }
        }
    }
}