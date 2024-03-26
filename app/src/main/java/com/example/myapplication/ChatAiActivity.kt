package com.example.myapplication

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityChatAiBinding

class ChatAiActivity: BaseActivity() {

     private lateinit var binding: ActivityChatAiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_ai)
    }
}