package com.example.myapplication
import android.os.Bundle
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityAccountChangeBinding
import http.RetrofitManager
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import service.SharedPreferencesManager
import service.Toast


class AccountChangeActivity: BaseActivity() {

    private lateinit var usernameText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityAccountChangeBinding = DataBindingUtil.setContentView(this,R.layout.activity_account_change)
        // 启用ActionBar显示返回按钮
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //修改公共基类的title
        setTitleText("修改账号")

        usernameText = findViewById(R.id.username_input)
        val apiService = RetrofitManager.getInstance().provideApiService()
        val sharedPreferencesManager = SharedPreferencesManager.getInstance(applicationContext)

        //修改按钮
        binding.bottom.setOnClickListener{
            val username = usernameText.text.toString()
            val resetPasswordCall = apiService.resetUsername(username)
            resetPasswordCall.enqueue(object : retrofit2.Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.showToast(this@AccountChangeActivity,"修改成功")
                        sharedPreferencesManager.resetUserName(username)
                    } else {
                        Toast.showToast(this@AccountChangeActivity,"修改失败")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.showToast(this@AccountChangeActivity,"网络请求失败")
                }
            })
        }
    }
}
