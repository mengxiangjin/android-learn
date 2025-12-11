package com.jin.movie

import android.app.Application
import com.arialyy.aria.core.Aria

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // 初始化 Aria
        Aria.init(this)
    }
}