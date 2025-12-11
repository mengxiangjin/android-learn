package com.jin.movie.tl.net


import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // 基础 URL
    private const val BASE_URL = "https://proapi.taolu.cloud/"

    // 配置 OkHttpClient
    private val okHttpClient: OkHttpClient by lazy {
        // 1. 日志拦截器
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 2. 固定请求头拦截器 (这里就是你要求的"写死"部分)
        val headerInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()

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
                .header("token", "aiya_d4ed9fff-9bb0-41ac-a654-e6945559a3cbxn")

                // 复杂的 JSON 字符串，使用 Kotlin 的原始字符串(三引号)或者转义
                .header("appversion", "{\"uid\":\"218904\",\"systemModel\":\"Pixel 2 XL\",\"appType\":\"1\",\"appVer\":\"3.9.3\",\"phoneBrand\":\"google\",\"version\":\"3.9.3\",\"deviceId\":\"63bd2e866c6ef324\",\"systemVersion\":\"11\",\"versionCode\":\"20250729\"}")

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