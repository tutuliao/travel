package service

import android.app.Application
import android.content.Context

class MyApp : Application() {

    companion object {
        lateinit var instance: MyApp
            private set

        fun getContext(): Context = instance.applicationContext
    }

    override fun onCreate() {
        instance = this
        getContext()
        super.onCreate()
    }
}
