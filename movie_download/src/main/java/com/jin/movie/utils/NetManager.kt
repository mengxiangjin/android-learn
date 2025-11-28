package com.jin.movie.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * 网络请求工具类 (单例模式)
 */
object NetManager {

    private const val TAG = "NetManager"


    // 1. 配置 OkHttpClient
    private val client: OkHttpClient by lazy {
        // 日志拦截器：打印请求头、参数、响应体
        val logging = HttpLoggingInterceptor { message ->
            Log.d(TAG, "OkHttp: $message")
        }
        logging.level = HttpLoggingInterceptor.Level.BODY

        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS) // 连接超时
            .readTimeout(20, TimeUnit.SECONDS)    // 读取超时
            .writeTimeout(20, TimeUnit.SECONDS)   // 写入超时
            .addInterceptor(logging)              // 添加日志
            .retryOnConnectionFailure(true)       // 失败重连
            .build()
    }

    // 主线程 Handler，用于把结果抛回 UI 线程
    private val mainHandler = Handler(Looper.getMainLooper())

    // 定义一个通用的回调接口
    interface Callback {
        fun onSuccess(response: String)
        fun onError(msg: String)
    }

    /**
     * 发送 GET 请求
     * @param url 请求地址
     * @param callback 回调接口
     */
    fun get(url: String, callback: Callback) {
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Android) VideoApp/1.0") // 伪装成浏览器或App
            .get()
            .build()

        // 异步执行
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                postError(callback, "网络请求失败: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, callback)
            }
        })
    }

    /**
     * 发送 POST 请求 (提交 JSON 数据)
     * @param url 请求地址
     * @param json JSON 字符串 (例如: "{\"key\":\"value\"}")
     * @param callback 回调接口
     */
    fun postJson(url: String, json: String, callback: Callback) {
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                postError(callback, "网络请求失败: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, callback)
            }
        })
    }

    /**
     * 发送 POST 请求 (提交表单 Form)
     * @param url 请求地址
     * @param params Map参数集合
     */
    fun postForm(url: String, params: Map<String, String>, callback: Callback) {
        val builder = FormBody.Builder()
        for ((key, value) in params) {
            builder.add(key, value)
        }
        val request = Request.Builder()
            .url(url)
            .post(builder.build())
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                postError(callback, e.message ?: "未知错误")
            }

            override fun onResponse(call: Call, response: Response) {
                handleResponse(response, callback)
            }
        })
    }

    // --- 内部辅助方法 ---

    // 统一处理响应
    private fun handleResponse(response: Response, callback: Callback) {
        response.use { // 自动关闭流
            if (!response.isSuccessful) {
                postError(callback, "服务器错误: ${response.code}")
                return
            }

            val bodyString = response.body?.string()
            if (bodyString == null) {
                postError(callback, "返回数据为空")
                return
            }

            // 切换回主线程返回成功结果
            mainHandler.post {
                try {
                    callback.onSuccess(bodyString)
                } catch (e: Exception) {
                    callback.onError("解析错误: ${e.message}")
                }
            }
        }
    }

    // 切换回主线程返回错误
    private fun postError(callback: Callback, msg: String) {
        mainHandler.post {
            callback.onError(msg)
        }
    }
}