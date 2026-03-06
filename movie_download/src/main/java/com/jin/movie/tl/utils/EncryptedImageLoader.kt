package com.jin.movie.tl.utils

import android.util.Log
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.signature.ObjectKey
import com.jin.movie.tl.bean.EncryptedImage
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayInputStream
import java.io.InputStream

class EncryptedImageLoader(private val client: OkHttpClient) : ModelLoader<EncryptedImage, InputStream> {

    override fun handles(model: EncryptedImage): Boolean = true

    override fun buildLoadData(
        model: EncryptedImage,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream> {
        // 使用 URL 作为缓存 Key
        return ModelLoader.LoadData(ObjectKey(model.url), EncryptedImageFetcher(client, model))
    }

    // --- 内部类 Fetcher：真正干活的地方 ---
    class EncryptedImageFetcher(
        private val client: OkHttpClient,
        private val model: EncryptedImage
    ) : DataFetcher<InputStream> {

        private var inputStream: InputStream? = null

        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
            try {
                // 1. 模拟原项目的 Request 构造
                val request = Request.Builder()
                    .url(model.url)
                    // 必须添加原项目要求的 Header，否则服务器不给数据
                    .addHeader("Referer", "https://video.taolu.app")
                    .addHeader("token", "aiya_41e9d628-aa7a-4eb9-b449-a941e71d26c5ov")
                    .build()

                // 2. 执行网络请求
                val response = client.newCall(request).execute()
                val bodyBytes = response.body?.bytes()

                if (response.isSuccessful && bodyBytes != null) {
                    // 3. 调用解密逻辑 (你需要把原项目的 SecurityHelper 搬过来)
                    // 传入 Context（如果需要）和 加密字节数组
                    Log.d("TAG", "loadData: " + ApiDecryptor.decryptData(bodyBytes))
                    println()
                    val decryptedBytes = ApiDecryptor.decryptData(bodyBytes)
                    val bais = ByteArrayInputStream(decryptedBytes)
                    this.inputStream = bais
                    callback.onDataReady(bais)
                } else {
                    callback.onLoadFailed(Exception("请求失败或数据为空: ${response.code}"))
                }
            } catch (e: Exception) {
                callback.onLoadFailed(e)
            }
        }

        override fun cleanup() {
            inputStream?.close()
        }

        override fun cancel() {
            // OkHttp 的取消逻辑可选实现
        }

        override fun getDataClass(): Class<InputStream> = InputStream::class.java

        override fun getDataSource(): DataSource = DataSource.REMOTE
    }

    // --- 工厂类 ---
    class Factory(private val client: OkHttpClient = OkHttpClient()) : ModelLoaderFactory<EncryptedImage, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<EncryptedImage, InputStream> {
            return EncryptedImageLoader(client)
        }
        override fun teardown() {}
    }
}