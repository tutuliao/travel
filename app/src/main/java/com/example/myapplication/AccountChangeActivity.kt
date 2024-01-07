package com.example.myapplication
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityAccountChangeBinding


class AccountChangeActivity: BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityAccountChangeBinding = DataBindingUtil.setContentView(this,R.layout.activity_account_change)
        // 启用ActionBar显示返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //修改公共基类的title
        setTitleText("修改账号")
    }
}