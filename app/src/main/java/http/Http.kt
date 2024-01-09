package http

import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import service.API
import java.io.IOException
import java.util.concurrent.TimeUnit

class Interceptor : Interceptor {
    //拦截器
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            // 获取原始请求
            val originalRequest = chain.request()

            // 打印请求信息
            println("-------------------------打印请求信息-------------------------")
            println("请求链接: ${originalRequest.url}")
            println("请求方法: ${originalRequest.method}")

            // 执行请求
            val response = chain.proceed(originalRequest)

            // 打印响应信息
            println("-------------------------打印响应信息-------------------------")
            println("请求状态码: ${response.code}")
            println("请求状态码响应消息: ${response.message}")

            // 打印响应体（如果存在）
            val responseBody = response.body
            if (responseBody != null) {
                val bodyString = responseBody.string()
                println("响应body内容: $bodyString")

                // 重新构造响应体，因为 Response 的 body 只能读取一次
                return response.newBuilder()
                    .body(bodyString.toResponseBody(responseBody.contentType()))
                    .build()
            }

            return response
        } catch (e: IOException) {
            // 处理请求失败的情况
            println("请求失败: ${e.message}")
            throw IOException("网络请求失败", e)
        }
    }
}
class Http private constructor(){

    // 内部使用client的单例模式
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)  //连接超时时间
            .readTimeout(15, TimeUnit.SECONDS)  //读取超时时间
            .addInterceptor(Interceptor())  // 添加拦截器
            .retryOnConnectionFailure(true)  //尝试重新连接
            .build()
    }

    // 外部访问 Http 的单例模式
    companion object {
        private val instance = Http()

        // 提供一个全局可访问的方法获取单例
        fun getInstance(): Http {
            return instance
        }
    }
    //Get请求方法
    fun makeGetRequest(url: String, formData: Map<String, String>, callback: Callback) {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        // 使用 OkHttpClient 创建 Call 对象
        val call: Call = client.newCall(request)

        // 异步执行请求
        call.enqueue(callback)
    }

    // POST 请求方法
    fun makePostRequest(url: String, formData: Map<String, String>, callback: Callback) {
        // 构建请求体
        val formBuilder = FormBody.Builder()
        for ((key, value) in formData) {
            formBuilder.add(key, value)
        }
        val requestBody: RequestBody = formBuilder.build()

        // 构建 POST 请求
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        // 使用 OkHttpClient 创建 Call 对象
        val call = client.newCall(request)

        // 异步执行请求
        call.enqueue(callback)
    }
}