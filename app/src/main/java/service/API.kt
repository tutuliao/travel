package service

import com.google.gson.Gson
import http.Http
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class MyCallback : Callback {

    private var code = 0;
    override fun onResponse(call: Call, response: Response){
        // 处理成功的响应
        println("--------------------请求成功: 下面是请求体--------------------")
        println("请求体是: ${response.body?.string()}")
        code = response.code
    }

    fun getStatusCode() :Int {
        return code
    }

    override fun onFailure(call: Call, e: IOException) {
        // 处理请求失败的情况
        println("请求失败: ${e.message}")
        code = 0;
    }
}
class API private constructor() {

    //基础地址
    private val baseUrl = "http://42.193.127.135:8080"
    private val registerText = "/user/register"
    private val loginText = "/user/login"




    //调用封装的http单例
    private val http = Http.getInstance()
    // 使用自定义的 Callback 对象
    private val callback = MyCallback()
    //单例的gson对象
    private val gson = Gson()

    // 在伴生对象中定义 API 的单例
    companion object {
        private val instance = API()

        // 提供一个全局可访问的方法获取单例
        fun getInstance(): API {
            return instance
        }
    }

    //注册
    fun register(username: String, password: String) : Int{
        val formData = mapOf("username" to username, "password" to password)
        val jsonData = gson.toJson(formData)
        val url = baseUrl + registerText
        http.makePostRequest(url, jsonData, callback)
        return callback.getStatusCode()
    }

    //登陆
    fun login(username: String, password: String) : Int{
        val formData = mapOf("username" to username, "password" to password)
        val jsonData = gson.toJson(formData)
        val url = baseUrl + loginText
        http.makePostRequest(url,jsonData,callback)
        return callback.getStatusCode()
    }
}