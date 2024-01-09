package com.example.myapplication
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityLoginBinding
import service.API


class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        //以下为逻辑层初始化
                    val api = API.getInstance()


        //以下为UI层初始化

        super.onCreate(savedInstanceState)
        val binding : ActivityLoginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        supportActionBar?.hide() //隐藏actionbar

        binding.bottom.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}