package com.jin.movie.tl.net

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.jin.movie.tl.bean.EncryptedImage
import com.jin.movie.tl.utils.EncryptedImageLoader
import java.io.InputStream

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // 注册：当 Glide 遇到 EncryptedImage 类型时，使用我们自定义的 Loader
        registry.prepend(
            EncryptedImage::class.java,
            InputStream::class.java,
            EncryptedImageLoader.Factory()
        )
    }
}