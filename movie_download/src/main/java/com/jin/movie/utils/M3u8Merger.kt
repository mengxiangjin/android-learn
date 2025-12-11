package com.jin.movie.utils

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object M3u8Merger {

    // 合并并解密
    // indexFile: 本地下载好的 index.m3u8 文件
    // outputMp4: 最终生成的 mp4 路径
    fun merge(indexFile: File, outputMp4: File): Boolean {
        if (!indexFile.exists()) return false

        try {
            // 1. 解析 M3U8，找到 Key 和所有 TS 文件
            val lines = indexFile.readLines()
            val tsFiles = mutableListOf<File>()
            var keyFile: File? = null
            var keyMethod = "NONE"
            var currentSequence = 0 // 默认从 0 开始

            // 先找 Sequence 起始位置 (如果有 #EXT-X-MEDIA-SEQUENCE)
            lines.find { it.startsWith("#EXT-X-MEDIA-SEQUENCE:") }?.let {
                currentSequence = it.substringAfter(":").trim().toIntOrNull() ?: 0
            }

            for (line in lines) {
                when {
                    line.startsWith("#EXT-X-KEY") -> {
                        // 解析加密方式
                        if (line.contains("METHOD=AES-128")) {
                            keyMethod = "AES-128"
                            // 我们之前的下载器把 Key 存为了 key.key，在同目录下
                            keyFile = File(indexFile.parentFile, "key.key")
                        }
                    }
                    !line.startsWith("#") && line.isNotEmpty() -> {
                        // 这是一个 TS 文件路径 (下载器保存的是 0.ts, 1.ts 这种相对路径)
                        val file = File(indexFile.parentFile, line)
                        if (file.exists()) {
                            tsFiles.add(file)
                        } else {
                            Log.e("Merger", "缺失 TS 文件: ${file.absolutePath}")
                            return false // 文件缺失，合并失败
                        }
                    }
                }
            }

            // 2. 准备输出流
            val outputStream = FileOutputStream(outputMp4)

            // 3. 准备解密器
            var cipher: Cipher? = null
            if (keyMethod == "AES-128" && keyFile != null && keyFile.exists()) {
                val keyBytes = keyFile.readBytes()
                val secretKey = SecretKeySpec(keyBytes, "AES")
                cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")

                // 注意：这里简化处理，如果 M3U8 没显式写 IV，则根据 HLS 规范，
                // 应该对每个 TS 用它的 Sequence Number 做 IV。
                // 但为了简化，我们先用基础 IV，如果解密失败（播放花屏），需要对每个 TS 重新 init Cipher
            }

            val buffer = ByteArray(8192)

            // 4. 开始循环处理每个 TS
            for ((index, tsFile) in tsFiles.withIndex()) {
                val rawBytes = tsFile.readBytes()

                var dataToWrite = rawBytes

                // 如果需要解密
                if (cipher != null) {
                    // 计算 IV (HLS 规范：IV 默认为 Sequence Number)
                    // Sequence Number = 起始序号 + 当前索引
                    val seqNum = currentSequence + index
                    val iv = generateIv(seqNum)
                    val ivSpec = IvParameterSpec(iv)

                    // 每次解密都需要重新初始化，因为 CBC 模式依赖 IV
                    val keyBytes = keyFile!!.readBytes() // 重新读取或复用
                    val secretKey = SecretKeySpec(keyBytes, "AES")

                    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec)
                    dataToWrite = cipher.doFinal(rawBytes)
                }

                outputStream.write(dataToWrite)
            }

            outputStream.flush()
            outputStream.close()

            // 5. 合并成功后，可选：删除原始的 m3u8 和 ts 文件夹，只留 mp4
            // indexFile.parentFile.deleteRecursively()

            Log.d("Merger", "合并成功: ${outputMp4.absolutePath}, 大小: ${outputMp4.length()}")
            return true

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Merger", "合并异常: ${e.message}")
            // 如果生成了垃圾文件，删掉
            if (outputMp4.exists()) outputMp4.delete()
            return false
        }
    }

    // 根据序号生成 16字节 IV (大端序)
    private fun generateIv(sequence: Int): ByteArray {
        val iv = ByteArray(16)
        // 将 int 转为 byte 填充到最后 4 位 (HLS 规范是填充到整个128位，但通常只有低位有值)
        iv[12] = (sequence shr 24).toByte()
        iv[13] = (sequence shr 16).toByte()
        iv[14] = (sequence shr 8).toByte()
        iv[15] = sequence.toByte()
        return iv
    }
}