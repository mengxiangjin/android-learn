package com.jin.movie.utils

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileInputStream

object VideoStorageUtils {

    /**
     * 将内部的 MP4 文件保存到公共 Download 目录
     * @param context 上下文
     * @param sourceFile 已经合并好的内部 MP4 文件
     * @param fileName 想要显示的文件名 (e.g., "复仇者联盟.mp4")
     * @return 保存后的最终路径 (String)
     */
    fun copyToPublicDownload(context: Context, sourceFile: File, fileName: String): String? {
        if (!sourceFile.exists()) return null

        try {
            // Android 10 (Q) 及以上：使用 MediaStore API
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/JinMovie") // 可以在 Download 下建个子文件夹
                    put(MediaStore.Video.Media.IS_PENDING, 1) // 标记为正在写入
                }

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        FileInputStream(sourceFile).use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    // 写入完成，取消 Pending 状态
                    contentValues.clear()
                    contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)

                    // 返回 Uri 的字符串形式，或者转换成绝对路径（Android 10+ 推荐直接存 Uri）
                    // 为了兼容你的 VideoTask.localPath 存的是 String，这里返回 Uri String
                    return uri.toString()
                }
            } else {
                // Android 9 及以下：直接使用 File API 写入 /storage/emulated/0/Download/
                val publicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val targetDir = File(publicDir, "JinMovie")
                if (!targetDir.exists()) targetDir.mkdirs()

                val targetFile = File(targetDir, fileName)

                // 复制文件
                FileInputStream(sourceFile).use { input ->
                    targetFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                return targetFile.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("StorageUtils", "保存到公共目录失败: ${e.message}")
        }
        return null
    }
}