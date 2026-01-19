package com.jin.movie

import android.app.Application
import android.content.Context
import com.arialyy.aria.core.Aria

class MyApp : Application() {

    // 伴生对象，相当于 Java 的 static 区域
    companion object {
        // 使用 lateinit 延迟初始化，避免处理可空类型 (?)
        lateinit var instance: MyApp
            private set // 私有化 set，防止外部被修改

        // 如果你需要直接获取 Context，也可以加一个快捷方法
        fun context(): Context {
            return instance.applicationContext
        }
    }


    override fun onCreate() {
        super.onCreate()
        // 在这里赋值，this 就是当前 Application 实例
        instance = this
        // 初始化 Aria
        Aria.init(this)
    }
}