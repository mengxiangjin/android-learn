package com.jin.movie.dog.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.jin.movie.utils.HtmlParseHelper
import com.jin.movie.utils.ResolvedDns
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit

object WebManager {

    // 1. 配置 OkHttpClient
    private val client: OkHttpClient by lazy {

        OkHttpClient.Builder()
            .dns(ResolvedDns())
            .connectTimeout(15, TimeUnit.SECONDS) // 连接超时
            .readTimeout(20, TimeUnit.SECONDS)    // 读取超时
            .writeTimeout(20, TimeUnit.SECONDS)   // 写入超时
            .retryOnConnectionFailure(true)       // 失败重连
            .build()
    }

    const val GUIDE_URL = "https://itaolu.com"


    fun getUrlList(index: Int = 0,callBack: (String)-> Unit) {

        CoroutineScope(Dispatchers.Main).launch {
            val request = Request.Builder()
                .url(GUIDE_URL)
                // 务必加上下面这三个 Header
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("Connection", "keep-alive") // 保持连接
                .get()
                .build()
            // 1. 获取 HTML 结果 (先切到 IO 线程执行同步请求)
            val result = withContext(Dispatchers.IO) {
                try {
                    client.newCall(request).execute().body?.string()
                } catch (e: Exception) {
                    // 把真实错误打印在控制台 (Logcat)
                    Log.e("HttpError", "请求彻底失败: ${e.message}", e)
                    null
                }
            }
            if (result == null) {
                callBack("请求失败了~")
                return@launch
            }

            // 2. 解析列表
            val parseDogUrlList = HtmlParseHelper.parseDogUrlList(result,index)
            if (parseDogUrlList.isEmpty()) {
                callBack("未获取到有效网站列表")
                return@launch
            }

            // 3. 循环遍历检测
            var firstValidUrl: String? = null
            withContext(Dispatchers.IO) {
                for (url in parseDogUrlList) {
                    if (isUrlReachable(url)) {
                        firstValidUrl = url
                        break // 找到第一个，立即跳出循环
                    }
                }
            }

            // 4. 返回结果
            if (firstValidUrl != null) {
                callBack(firstValidUrl!!)
            } else {
                callBack("没有找到可用的网站")
            }
        }

    }



    // 使用挂起函数检测 URL 是否可用 (200 OK)
    suspend fun isUrlReachable(url: String): Boolean = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .head() // 仅请求头部，节省流量和时间
            .build()
        try {
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (e: Exception) {
            false
        }
    }
}

data class UrlResult(val url1: String, val url2: String, val url3: String)
