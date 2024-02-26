package http
import io.reactivex.Observable
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.IOException
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


interface ApiService {
    @FormUrlEncoded
    @POST("user/login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Observable<Response<ResponseBody>>

    @FormUrlEncoded
    @POST("user/register")
    fun register(
        @Field("role") role: Int,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<ResponseBody>


}
class HttpsInterceptor : Interceptor {
    //拦截器
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        try {
            // 获取原始请求
            val originalRequest = chain.request()
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
                println("请求响应body内容: $bodyString")

                // 重新构造响应体，因为 Response 的 body 只能读取一次
                return response.newBuilder()
                    .body(bodyString.toResponseBody(responseBody.contentType()))
                    .build()
            }

            return response
        } catch (e: IOException) {
            // 处理请求失败的情况
            println("请求失败: ${e.message}")
            throw IOException("请求网络请求失败", e)
        }
    }
}
class RetrofitManager private constructor() {

    private  val baseUrl = "http://42.193.127.135:8080/" //baseurl要以/结尾

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
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            //添加对RxJava的支持
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    //外部调用的retrofit实例
    companion object {
        private val instance = RetrofitManager()
        fun getInstance(): RetrofitManager {
            return instance
        }
    }

    // 提供ApiService实例的方法
    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    fun provideApiService(): ApiService {
        return apiService
    }


}
