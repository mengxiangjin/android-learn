package com.jin.movie.tl.utils


import java.net.URL
import java.security.MessageDigest

object SignUtils {

    private const val BRAND_ID = "43600c095fa84f059f2c02725f4c0b0d"
    private const val SIGN_KEY = "3vtWp15zCm"

    /**
     * 获取带有签名的完整参数 Map
     * @param path 接口路径 (例如 "/live/live/video/anchor")，必须以 / 开头
     * @param page 当前页码  -1,代表无需page和pageSize
     * @param pageSize 每页数量 (默认为 10，必须与接口请求的 pageSize 一致)
     */
    fun getSignedParams(path: String, page: Int = -1, pageSize: Int = 10): Map<String, String> {
        val params = HashMap<String, String>()

        // 1. 公共参数
        params["uid"] = "559207"
        params["systemModel"] = "Pixel 2 XL"
        params["appType"] = "1"
        params["appVer"] = "3.9.6.6"
        params["phoneBrand"] = "google"
        params["version"] = "3.9.6.6"
        params["deviceId"] = "2079ef7da722b063"
        params["systemVersion"] = "11"
        params["versionCode"] = "20260228"

        // 2. 根据路径和页码生成动态 Sign
        val sign = generateSign(path, page, pageSize)
        params["sign"] = sign

        return params
    }

    fun generateSign(basePath: String, page: Int, pageSize: Int): String {
        val nowTime = System.currentTimeMillis() / 1000

        // 模拟 Java 中的 getAiYaRand()。
        // 如果原版是用 UUID 或者是固定的设备指纹，这里需要对应。
        // 这里假设它是一个随机生成的 32位 hex 字符串 (类似 UUID 去掉横线)
//        val randomStr = UUID.randomUUID().toString().replace("-", "")
        // 或者如果之前的日志里那个长字符串是固定的，你就定义成常量：
//         val randomStr = "19cffb31b67647299318a680bbec3b67"

        // 1. 拼接完整路径
        var fullPath = if (basePath.startsWith("/")) basePath else "/$basePath"
        if (page != -1) {
            fullPath = "$fullPath/$page/$pageSize"
        }
        // 2. 【关键修正】强制转小写！
        val lowerCasePath = fullPath.lowercase()

        // 3. 拼接加密字符串
        // 格式：path小写-时间戳-随机串-0-key
        val encryptData = "$lowerCasePath-$nowTime-$BRAND_ID-0-$SIGN_KEY"

        // 4. 计算 MD5
        val encryptRes = md5(encryptData)

        // 5. 返回最终 Sign
        // 格式：时间戳-随机串-0-MD5
        return "$nowTime-$BRAND_ID-0-$encryptRes"
    }

    private fun md5(input: String): String {
        try {
            val md = MessageDigest.getInstance("MD5")
            val messageDigest = md.digest(input.toByteArray())
            val hexString = StringBuilder()
            for (b in messageDigest) {
                val hex = Integer.toHexString(0xFF and b.toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }
            return hexString.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    fun calculateSignature(fullUrl: String):String {
        return this.calculateSignature(fullUrl,"218904")
    }

    /*
    * 生成sign值（最新版）
    * */
    /**
     * 模拟 Android Native sub_6CB98 的签名生成逻辑
     * @param fullUrl 完整的请求 URL
     * @param uid 用户 ID
     * @param signKey 逆向提取的 Key
     */
    fun calculateSignature(fullUrl: String, uid: String = "218904", signKey: String ="4t3z434vVedm6IYz5gXri"): String {
        // 1. 提取 Path 部分
        val path = try {
            URL(fullUrl).path
        } catch (e: Exception) {
            fullUrl // 如果解析失败，回退到原字符串
        }

        // 2. 准备基础参数
        val timestamp = System.currentTimeMillis() / 1000
        val nonce = generateNonce(32)

        // ---------------------------------------------------------
        // 步骤 1 & 2: 构造待哈希字符串并计算 MD5
        // 格式: Path-Timestamp-Nonce-UID-SignKey
        // ---------------------------------------------------------
        val textToHash = "$path-$timestamp-$nonce-$uid-$signKey"
        val bytes = MessageDigest.getInstance("MD5").digest(textToHash.toByteArray())
        val hashResult =  bytes.joinToString("") { "%02x".format(it) }

        // ---------------------------------------------------------
        // 步骤 3: 构造最终 sign 参数
        // 格式: Timestamp-Nonce-UID-Hash
        // ---------------------------------------------------------
        return "$timestamp-$nonce-$uid-$hashResult"
    }


    /**
     * 生成指定长度的随机字符串 (Nonce)
     */
    private fun generateNonce(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}