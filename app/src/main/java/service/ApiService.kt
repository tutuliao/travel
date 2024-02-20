package service
import GsonSingleton
import http.RetrofitManager
import kotlinx.coroutines.DelicateCoroutinesApi

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response

class ApiService private constructor() {

    //基础地址
    private val baseUrl = "http://42.193.127.135:8080"
    private val registerText = "/user/register"
    private val loginText = "/user/login"
    private val loginOutText = "/user/logout"
    private val resetPassword = "/user/resetPassword"

    private val https = RetrofitManager.getInstance()

    private val gson = GsonSingleton.gson

    // 提供一个全局可访问的方法获取单例
    companion object {
        private val instance = ApiService()
        fun getInstance(): ApiService {
            return instance
        }
    }
    // 阻塞等待异步操作完成，并返回结果
    //注册
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun register(username: String, password: String): String{
        return GlobalScope.async(Dispatchers.IO) {
            val formData = mapOf("username" to username, "password" to password)
            val jsonData = gson.toJson(formData)
            RetrofitManager.getInstance().makePostRequest(registerText, jsonData)
        }.await()
    }

    //登录
    @OptIn(DelicateCoroutinesApi::class)
    suspend fun login(username: String, password: String): String {
        return GlobalScope.async(Dispatchers.IO) {
            val formData = mapOf("username" to username, "password" to password)
            val jsonData = gson.toJson(formData)
            RetrofitManager.getInstance().makePostRequest(loginText, jsonData)
        }.await()
    }


}
