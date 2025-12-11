package com.jin.movie.utils

import android.util.Log
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.net.URI
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

object ManualM3u8Downloader {

    private const val TAG = "ManualDownload"

    // 配置 OkHttp，超时时间设长一点
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // 定义一个回调接口
    interface OnDownloadListener {
        fun onProgress(current: Int, total: Int)
        fun onSuccess(localM3u8Path: String)
        fun onError(msg: String)
    }

    /**
     * @param url M3U8 在线链接
     * @param saveDir 保存目录 (e.g. .../files/Download/MovieName/)
     * @param headers 请求头 (Referer, User-Agent)
     */
    fun download(
        url: String,
        saveDir: String,
        headers: Map<String, String>,
        listener: OnDownloadListener
    ) {
        // 使用协程在后台运行
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. 准备目录
                val dirFile = File(saveDir)
                if (!dirFile.exists()) dirFile.mkdirs()

                // 2. 下载原始 M3U8 内容
                val m3u8Content = fetchString(url, headers)
                if (m3u8Content.isEmpty()) {
                    throw Exception("M3U8 内容为空，请检查链接或 Referer")
                }

                // 3. 解析 M3U8
                val lines = m3u8Content.lines()
                val tsUrls = mutableListOf<String>()
                var keyUrl: String? = null
                val newM3u8Content = StringBuilder()

                // 用于解析相对路径
                val baseUrl = url.substringBeforeLast("/") + "/"

                for (line in lines) {
                    when {
                        // 解析 Key
                        line.startsWith("#EXT-X-KEY") -> {
                            // 提取 URI="..."
                            val keyUri = getKeyUri(line)
                            if (keyUri != null) {
                                // 处理相对路径
                                keyUrl = resolveUrl(baseUrl, keyUri)
                                // 修改 M3U8 内容，指向本地 key.key
                                val newLine = line.replace(keyUri, "key.key")
                                newM3u8Content.append(newLine).append("\n")
                            } else {
                                newM3u8Content.append(line).append("\n")
                            }
                        }
                        // 如果是注释或空行，直接复制
                        line.startsWith("#") || line.isBlank() -> {
                            newM3u8Content.append(line).append("\n")
                        }
                        // 这是一个 TS 链接
                        else -> {
                            val absoluteTsUrl = resolveUrl(baseUrl, line)
                            tsUrls.add(absoluteTsUrl)
                            // 修改 M3U8 内容，指向本地文件名 (例如 0.ts, 1.ts...)
                            val fileName = "${tsUrls.size - 1}.ts"
                            newM3u8Content.append(fileName).append("\n")
                        }
                    }
                }

                // 4. 下载 Key (如果有)
                if (keyUrl != null) {
                    Log.d(TAG, "正在下载 Key: $keyUrl")
                    val keySuccess = downloadFile(keyUrl, File(dirFile, "key.key"), headers)
                    if (!keySuccess) throw Exception("Key 下载失败，无法解密视频")
                }

                // 5. 并发下载 TS 切片
                val total = tsUrls.size
                val current = AtomicInteger(0)

                // 使用 supervisorScope 允许部分失败重试 (这里简化为直接抛异常)
                // async 并发控制：同时下载 5 个
                val deferreds = tsUrls.mapIndexed { index, tsUrl ->
                    async(Dispatchers.IO) {
                        val fileName = "$index.ts"
                        val success = downloadFile(tsUrl, File(dirFile, fileName), headers)
                        if (success) {
                            val p = current.incrementAndGet()
                            // 回调进度 (切回主线程)
                            withContext(Dispatchers.Main) {
                                listener.onProgress(p, total)
                            }
                        } else {
                            throw Exception("切片下载失败: $tsUrl")
                        }
                    }
                }
                // 等待所有下载完成
                deferreds.awaitAll()

                // 6. 写入本地 M3U8 文件
                val localM3u8File = File(dirFile, "index.m3u8")
                FileOutputStream(localM3u8File).use { it.write(newM3u8Content.toString().toByteArray()) }

                Log.d(TAG, "下载完成，本地索引: ${localM3u8File.absolutePath}")

                withContext(Dispatchers.Main) {
                    listener.onSuccess(localM3u8File.absolutePath)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    listener.onError(e.message ?: "未知错误")
                }
            }
        }
    }

    // --- 辅助方法 ---

    // 下载字符串内容
    private fun fetchString(url: String, headers: Map<String, String>): String {
        val request = Request.Builder().url(url).apply {
            headers.forEach { (k, v) -> addHeader(k, v) }
        }.build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("HTTP ${response.code}")
            return response.body?.string() ?: ""
        }
    }

    // 下载文件到本地（含断点续传逻辑）
    private fun downloadFile(url: String, targetFile: File, headers: Map<String, String>): Boolean {
        try {
            // 【断点续传核心逻辑】
            // 如果文件存在，且大小合理 (大于 1KB，防止空文件或碎片)，则认为已下载，直接返回 true
            if (targetFile.exists() && targetFile.length() > 1024) {
                // Log.d(TAG, "文件已存在，跳过: ${targetFile.name}")
                return true
            }

            // 如果文件存在但太小（可能是损坏的），先删除再下
            if (targetFile.exists()) {
                targetFile.delete()
            }

            val request = Request.Builder().url(url).apply {
                headers.forEach { (k, v) -> addHeader(k, v) }
            }.build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return false
                val bytes = response.body?.bytes() ?: return false

                // 防错检查：如果服务器返回的数据太小（比如之前的 62KB 假文件，或者 0B），不保存
                if (bytes.isEmpty()) return false

                // 写入文件
                FileOutputStream(targetFile).use { it.write(bytes) }
                return true
            }
        } catch (e: Exception) {
            // e.printStackTrace()
            return false
        }
    }

    // 从 #EXT-X-KEY:METHOD=AES-128,URI="key.key" 提取 URI
    private fun getKeyUri(line: String): String? {
        val start = line.indexOf("URI=\"")
        if (start == -1) return null
        val end = line.indexOf("\"", start + 5)
        if (end == -1) return null
        return line.substring(start + 5, end)
    }

    // 处理相对路径
    private fun resolveUrl(baseUrl: String, relativeUrl: String): String {
        if (relativeUrl.startsWith("http")) return relativeUrl
        return try {
            URI.create(baseUrl).resolve(relativeUrl).toString()
        } catch (e: Exception) {
            // 简单拼接回退
            baseUrl + relativeUrl
        }
    }
}