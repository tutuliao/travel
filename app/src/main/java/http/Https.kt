package http

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url
import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


interface ApiService {
    @GET
    suspend fun getRequest(@Url url: String,jsonData: String): String

    @POST
    suspend fun postRequest(@Url url: String,jsonData: String): String

}
class HttpsInterceptor : Interceptor {
    //拦截器
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            // 获取原始请求
            val originalRequest = chain.request()
            // 记录请求时间
            // 使用 DateTimeFormatter 格式化时间
            // 获取当前时间
            val currentTime = LocalTime.now()
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            val formattedTime = currentTime.format(formatter)
            // 打印请求信息
            println("-------------------------打印请求信息-------------------------")
            println("请求链接: ${originalRequest.url}")
            println("请求方法: ${originalRequest.method}")
            println("请求时间: $formattedTime")
            //打印请求头
            val headers = originalRequest.headers
            for (i in 0 until headers.size) {
                println("Header ${headers.name(i)}: ${headers.value(i)}")
            }
            // 打印请求体
            val requestBody = originalRequest.body
            if (requestBody != null) {
                val buffer = Buffer()
                requestBody.writeTo(buffer)
                println("请求body内容: ${buffer.readUtf8()}")
            }

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
class RetrofitManager private constructor() {

    //Retrofit 可以使用 OkHttp 的拦截器来进行拦截和处理网络请求和响应。
    // 由于 Retrofit 底层使用 OkHttp 来执行网络请求，
    // 因此你可以通过在 OkHttp 客户端中添加拦截器来为 Retrofit 添加拦截器。
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(5, TimeUnit.SECONDS)  //连接超时时间
            .readTimeout(5, TimeUnit.SECONDS)  //读取超时时间
            .addInterceptor(HttpsInterceptor())  // 添加拦截器
            .retryOnConnectionFailure(true)  //尝试重新连接
            .build()
    }
    //内部调用的retrofit单例
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //外部调用的retrofit实例
    companion object {
        private const val BASE_URL = "http://42.193.127.135:8080"
        private val instance = RetrofitManager()

        fun getInstance(): RetrofitManager {
            return instance
        }
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    suspend fun makeGetRequest(url: String,jsonData: String): String {
        return apiService.getRequest(url, jsonData)
    }

    suspend fun makePostRequest(url: String,jsonData: String): String {
        return apiService.postRequest(url,jsonData)
    }

}
