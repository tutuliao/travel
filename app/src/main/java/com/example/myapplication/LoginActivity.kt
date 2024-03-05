package com.example.myapplication
import GsonSingleton
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.myapplication.databinding.ActivityLoginBinding
import http.RetrofitManager
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import model.UserManager
import model.UserResponse
import okhttp3.ResponseBody
import retrofit2.Response
import service.SharedPreferencesManager
import service.Toast


class LoginActivity: AppCompatActivity() {

    private lateinit var accountText: EditText
    private lateinit var passwordText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val binding : ActivityLoginBinding = DataBindingUtil.setContentView(this,R.layout.activity_login)
        accountText = findViewById(R.id.login_account)                   
        passwordText = findViewById(R.id.login_password)  // 读取输入框内的内容 先绑定 R.layout后才能找到输入框
        supportActionBar?.hide() //隐藏actionbar

        val sharedPreferencesManager = SharedPreferencesManager.getInstance(applicationContext)
        val apiService = RetrofitManager.getInstance().provideApiService()
        val token = sharedPreferencesManager.userToken
        if (token.isNotEmpty()) {
            // Token 为空，跳转到登录页面或其他页面
            startActivity(Intent(this, MainActivity::class.java))
            finish() // 结束当前的 LoginActivity，防止用户回退到此页面
            return  // 退出 onCreate，避免继续执行后面的初始化代码
        }

        binding.loginBottom.setOnClickListener{
            val username =  accountText.text.toString()
            val password =  passwordText.text.toString()
            apiService.login(username,password)
                .subscribeOn(Schedulers.io()) // IO 线程执行网络请求
                .observeOn(AndroidSchedulers.mainThread()) // 主线程更新数据
                .subscribe(object : Observer<Response<ResponseBody>> {
                    override fun onSubscribe(d: Disposable) {
                        // 可以在这里做一些初始化操作
                    }

                    override fun onNext(response: Response<ResponseBody>) {
                        // 登录成功后的操作
                        if (response.isSuccessful) {
                            Toast.showToast(this@LoginActivity,"登录成功")
                            val responseBody = response.body()?.string()
                            val loginResponse = GsonSingleton.gson.fromJson(responseBody,UserResponse::class.java)
                            UserManager.getInstance().setLoginResponse(loginResponse)

                            sharedPreferencesManager.resetUserName(UserManager.getInstance().getLoginResponse()?.data?.username)
                            sharedPreferencesManager.resetUserToken(UserManager.getInstance().getLoginResponse()?.data?.token)
                            UserManager.getInstance().getLoginResponse()?.data?.id?.let { it1 ->
                                sharedPreferencesManager.resetUserId(
                                    it1
                                )
                            }

                            //println("这就是token${sharedPreferencesManager.userToken}")
                            //println("这就是${UserManager.getInstance().getLoginResponse()?.data?.username}")

                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)

                        } else {
                            Toast.showToast(this@LoginActivity,"登录失败")
                        }
                    }

                    override fun onError(e: Throwable) {
                        // 登录失败后的操作
                        // 网络请求错误（如无网络连接）
                        Toast.showToast(this@LoginActivity, "网络请求失败: ${e.message}")
                    }

                    override fun onComplete() {
                        // 完成时的操作
                    }
                })
            }

         binding.registerBottom.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
         }

       }
    }
