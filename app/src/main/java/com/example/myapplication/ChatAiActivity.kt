package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityChatAiBinding
import http.RetrofitManager
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import model.ChatAiMessageResponse
import model.Message
import okhttp3.ResponseBody
import retrofit2.Response
import service.Toast

class ChatAiActivity : BaseActivity() {

    private lateinit var messageResponse : ChatAiMessageResponse
    private var messageList : MutableList<Message> = mutableListOf()
    private lateinit var text: String
    private lateinit var editText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: ChatAdapter
    private lateinit var binding: ActivityChatAiBinding
    private val apiService = RetrofitManager.getInstance().provideApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_ai)
        messageList.clear()
        editText = findViewById(R.id.messageEditText)

        recyclerView = binding.chatRecyclerView
        val manager = LinearLayoutManager(this@ChatAiActivity)
        manager.orientation = LinearLayoutManager.VERTICAL;
        recyclerView.layoutManager = manager
        recyclerViewAdapter = ChatAdapter(messageList)
        recyclerView.adapter = recyclerViewAdapter

        binding.sendButton.setOnClickListener {
            if(editText.text.isNotEmpty()){
                text = editText.text.toString()
                chat(text)
                binding.messageEditText.text.clear()
                // 隐藏键盘
                val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)
            }
            else{
                Toast.showToast(this@ChatAiActivity,"输入不能为空")
            }
        }
    }

    private fun chat(text: String) {
        messageList.add(Message(text, true))
        recyclerViewAdapter.notifyItemInserted(messageList.size - 1)
        apiService.chatAi(text)
            .subscribeOn(Schedulers.io()) // IO 线程执行网络请求
            .observeOn(AndroidSchedulers.mainThread()) // 主线程更新数据
            .subscribe(object : Observer<Response<ResponseBody>> {
                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(response: Response<ResponseBody>) {
                    // 登录成功后的操作
                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string()
                         messageResponse =
                            GsonSingleton.gson.fromJson(responseBody, ChatAiMessageResponse::class.java)
                        messageList.add(Message(messageResponse.data.msg, false))
                        runOnUiThread {
                            recyclerViewAdapter.notifyItemInserted(messageList.size - 1)
                        }
                    } else {
                        Toast.showToast(this@ChatAiActivity, "发送失败")
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.showToast(this@ChatAiActivity, "网络请求失败: ${e.message}")
                }

                override fun onComplete() {

                }
            })
    }
}
class ChatAdapter(private val messageList: MutableList<Message>) : RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.activity_chat_ai_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        if (messageList[position].isUser) {
            // 显示用户消息，并隐藏AI消息
            holder.userMessage.text = messageList[position].msg
            holder.aiMessage.visibility = View.GONE
            holder.userMessage.visibility = View.VISIBLE
        } else {
            // 显示AI消息，并隐藏用户消息
            holder.aiMessage.text = messageList[position].msg
            holder.aiMessage.visibility = View.VISIBLE
            holder.userMessage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = messageList.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var aiMessage: TextView = itemView.findViewById(R.id.ai_message)
         var userMessage: TextView = itemView.findViewById(R.id.user_message)
    }
}
