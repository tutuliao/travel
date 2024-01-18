package service

import com.google.gson.Gson
import http.Http


class API private constructor() {

    //基础地址
    private val baseUrl = "http://42.193.127.135:8080"
    private val registerText = "/user/register"
    private val loginText = "/user/login"
    private val loginOutText = "/user/logout"
    private val resetPassword = "/user/resetPassword"


    //调用封装的http单例
    private val http = Http.getInstance()
    //单例的gson对象
    private val gson = GsonSingleton.instance

    // 在伴生对象中定义 API 的单例
    companion object {
        private val instance = API()

        // 提供一个全局可访问的方法获取单例
        fun getInstance(): API {
            return instance
        }
    }

    //注册
    fun register(username: String, password: String): Int {
        val formData = mapOf("username" to username, "password" to password)
        val jsonData = gson.toJson(formData)
        val url = baseUrl + registerText
        return http.makePostRequest(url, jsonData)
    }

    //登陆
    fun login(username: String, password: String): Int {
        val formData = mapOf("username" to username, "password" to password)
        val jsonData = gson.toJson(formData)
        val url = baseUrl + loginText
        return http.makePostRequest(url, jsonData)
    }
}

object GsonSingleton {
    val instance: Gson by lazy { Gson() }
}