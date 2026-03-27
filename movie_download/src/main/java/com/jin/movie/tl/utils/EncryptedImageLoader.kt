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
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.ByteArrayInputStream
import java.io.IOException
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

    class EncryptedImageFetcher(
        private val client: OkHttpClient,
        private val model: EncryptedImage
    ) : DataFetcher<InputStream> {

        private var inputStream: InputStream? = null
        // 增加一个 Call 变量用于取消请求
        @Volatile
        private var call: Call? = null

        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
            try {
                val request = Request.Builder()
                    .url(model.url)
                    .addHeader("Referer", ConstPools.referer)
                    // 注意：这里 Token 是写死的。如果逆向的 Token 会过期，请改成动态获取
                    .addHeader("token", ConstPools.token)
                    .build()

                call = client.newCall(request)

                // 【关键修复 1】：使用 Kotlin 的 .use {}，不管成功失败，自动帮你关闭 response，防止连接池泄漏！
                call?.execute()?.use { response: Response ->
                    val bodyBytes = response.body?.bytes()

                    if (response.isSuccessful && bodyBytes != null) {
                        // 【关键修复 2】：只解密一次，千万别打印整个解密后的数组！
                        val decryptedBytes = ApiDecryptor.decryptData(bodyBytes)

                        if (decryptedBytes.isNotEmpty()) {
                            // ⬇️ 加入这行测试代码 ⬇️
                            val testBitmap = android.graphics.BitmapFactory.decodeByteArray(decryptedBytes, 0, decryptedBytes.size)
                            if (testBitmap == null) {
                                // 如果这里打印了，说明解密彻底失败了！出来的根本不是图片！
                                // 你需要去检查你的 ApiDecryptor 逻辑、Key、IV 或者是不是带了 Magic Header 没有被剔除！
                                Log.e("AvatarError", "解密出的数据不是有效图片！URL: ${model.url}")
                                callback.onLoadFailed(Exception("解码为 Bitmap 失败，解密数据损坏或密钥错误"))
                                return
                            }

                            val bais = ByteArrayInputStream(decryptedBytes)
                            this.inputStream = bais
                            callback.onDataReady(bais)
                            Log.d("model_load", "loadData: success" + model.url)
                        } else {
                            Log.d("model_load", "loadData: 解密后数据为空" + model.url)
                            callback.onLoadFailed(Exception("解密后数据为空 URL: ${model.url}"))
                        }
                    } else {
                        Log.d("model_load", "loadData: HTTP 请求失败" + model.url)
                        callback.onLoadFailed(Exception("HTTP 请求失败: ${response.code}"))
                    }
                }
            } catch (e: Exception) {
                // 如果是因为快速滑动被取消了，OkHttp 会抛出 IOException("Canceled")
                // 我们照样回调 failed 即可，Glide 内部会处理取消状态
                callback.onLoadFailed(e)
            }
        }

        override fun cleanup() {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                // 忽略关闭流时的异常
            }
        }

        override fun cancel() {
            // 【关键修复 3】：在 RecyclerView 滑走时，立即终止网络请求，释放带宽！
            call?.cancel()
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