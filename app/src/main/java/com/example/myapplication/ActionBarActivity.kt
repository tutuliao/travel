package com.example.myapplication

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 设置默认 ActionBar 行为
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 清除标题
        supportActionBar?.title = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // 处理返回按钮点击事件
                onBackPressedDispatcher.onBackPressed()
                return true
            }
            // 处理其他菜单项（如果有）
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // 提供给外界设置标题的方法
    fun setTitleText(title: CharSequence?) {
        supportActionBar?.title = title
    }
}
