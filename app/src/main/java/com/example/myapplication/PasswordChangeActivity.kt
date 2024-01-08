package com.example.myapplication

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityPasswordChangeBinding

class PasswordChangeActivity: BaseActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityPasswordChangeBinding = DataBindingUtil.setContentView(this,R.layout.activity_password_change)
        // 启用ActionBar显示返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //修改公共基类的title
        setTitleText("修改密码")
        //修改按钮
        binding.bottom.setOnClickListener{

        }
    }
}