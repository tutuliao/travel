
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object GsonSingleton {
    val gson: Gson by lazy {
        GsonBuilder()
            // 可以在这里添加 Gson 的配置
            .create()
    }
}
