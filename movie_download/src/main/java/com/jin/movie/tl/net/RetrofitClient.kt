package com.jin.movie.tl.net


import android.content.Context
import com.jin.movie.MyApp
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // 基础 URL
    private const val BASE_URL = "https://proapi.taolu.cloud/"


    private const val SP_NAME = "my_app_sp" // 你的 SP 文件名
    private const val SP_KEY_TOKEN = "token"    // 你存 Token 的 Key


    // 配置 OkHttpClient
    private val okHttpClient: OkHttpClient by lazy {
        // 1. 日志拦截器
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 2. 固定请求头拦截器 (这里就是你要求的"写死"部分)
        val headerInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()

            // 【直接使用全局 instance 获取 SP】
            // 注意：一定要确保 manifest 里注册了 MyApplication，否则这里会崩
            val sp = MyApp.instance.getSharedPreferences("my_app_sp", Context.MODE_PRIVATE)

            val token = sp.getString("token", "") ?: ""

            val newRequest = originalRequest.newBuilder()
                // --- 开始注入请求头 ---
                .header("User-Agent", "TaoLuApp (Android 11)")
                .header("Connection", "keep-alive")
                .header("Accept", "*/*")
                // 注意：OkHttp 默认会自动处理 gzip，手动添加 Accept-Encoding 可能会导致无法自动解压。
                // 如果发现返回乱码，请注释掉下面这一行 Accept-Encoding
//                .header("Accept-Encoding", "gzip")

                // 优先使用带编码的 Content-Type
                .header("Content-Type", "application/json; charset=UTF-8")

                // 核心 Token
//                .header("token", "aiya_276ce8e8-536c-4865-bd4c-3712d4c63cd1h8")
                .header("token", "aiya_$token")

                // 复杂的 JSON 字符串，使用 Kotlin 的原始字符串(三引号)或者转义
                .header("appversion", "{\"uid\":\"218904\",\"systemModel\":\"Pixel 2 XL\",\"appType\":\"1\",\"appVer\":\"3.9.4.9\",\"phoneBrand\":\"google\",\"version\":\"3.9.4.9\",\"deviceId\":\"63bd2e866c6ef324\",\"systemVersion\":\"11\",\"versionCode\":\"20251204\"}")

                .header("versionname", "3.9.3")
                .header("versioncode", "20250729")
                .header("clienttype", "Android")
                .header("referer", "https://proapi.taolu.cloud")
                // --- 结束注入请求头 ---
                .build()

            chain.proceed(newRequest)
        }

        OkHttpClient.Builder()
            // 务必把 headerInterceptor 加进去
            .addInterceptor(headerInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // 配置 Retrofit 实例
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 暴露 API
    val apiService: VideoApi by lazy {
        retrofit.create(VideoApi::class.java)
    }
}