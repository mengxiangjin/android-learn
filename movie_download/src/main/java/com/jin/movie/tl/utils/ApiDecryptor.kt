package com.jin.movie.tl.utils

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object ApiDecryptor {


    val gson = Gson()
    const val TAG = "ApiDecryptor"

    /**
     * 对应 Python 的 decrypt_final_data 逻辑
     */
    fun decryptHttp(base64Ciphertext: String): String {
        return try {
            // 1. 准备 Key
            val rawKey = "3vtWp15zCm"
            val keyBytes = rawKey.toByteArray(Charsets.UTF_8)
            
            // 补齐 16 字节 (对应 Python 的 ljust(16, b'\0'))
            val paddedKey = ByteArray(16)
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.size, 16))
            // 剩余部分自动就是 0 (\0)，Java ByteArray 默认初始化为 0

            // 2. 解码 Base64
            val ciphertextBytes = Base64.decode(base64Ciphertext, Base64.DEFAULT)

            // 3. 创建 AES-128-ECB 解密器
            // 注意：AES/ECB/PKCS5Padding 是最通用的，
            // 如果 Python 那边 unpad 报错，可能需要改为 "AES/ECB/NoPadding"
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            val keySpec = SecretKeySpec(paddedKey, "AES")
            
            cipher.init(Cipher.DECRYPT_MODE, keySpec)

            // 4. 执行解密
            val decryptedBytes = cipher.doFinal(ciphertextBytes)

            // 返回字符串
            String(decryptedBytes, Charsets.UTF_8)

        } catch (e: Exception) {
            e.printStackTrace()
            "[ERROR] 解密失败: ${e.message}"
        }
    }


    /**
     * 对应 Python 的 decrypt_final_data 逻辑
     */
    fun decryptData(ciphertextBytes: ByteArray): ByteArray {
        return try {
            // 1. 准备 Key
            val rawKey = "jGZH2Yf77YHpcyeZ"
            val keyBytes = rawKey.toByteArray(Charsets.UTF_8)

            // 补齐 16 字节 (对应 Python 的 ljust(16, b'\0'))
            val paddedKey = ByteArray(16)
            System.arraycopy(keyBytes, 0, paddedKey, 0, Math.min(keyBytes.size, 16))
            // 剩余部分自动就是 0 (\0)，Java ByteArray 默认初始化为 0

            // 3. 创建 AES-128-ECB 解密器
            // 注意：AES/ECB/PKCS5Padding 是最通用的，
            // 如果 Python 那边 unpad 报错，可能需要改为 "AES/ECB/NoPadding"
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            val keySpec = SecretKeySpec(paddedKey, "AES")

            cipher.init(Cipher.DECRYPT_MODE, keySpec)

            // 4. 执行解密
            val decryptedBytes = cipher.doFinal(ciphertextBytes)
            decryptedBytes

        } catch (e: Exception) {
            e.printStackTrace()
            byteArrayOf()
        }
    }

    /**
     * 封装方法：解密并直接转换为目标对象
     * 使用 inline + reified 关键字，调用时无需传 Class
     */
    inline fun <reified T> decryptAndParse(base64Ciphertext: String): T? {
        val jsonString = decryptHttp(base64Ciphertext) ?: return null
        return try {
            // 使用 GSON 将解密后的字符串转为泛型对象 T
            gson.fromJson(jsonString, T::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "GSON解析失败: ${e.message}")
            null
        }
    }
}