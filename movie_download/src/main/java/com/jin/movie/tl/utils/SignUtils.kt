package com.jin.movie.tl.utils


import java.security.MessageDigest

object SignUtils {

    private const val BRAND_ID = "ab0b98137b57439fbdeee594e386079c"
    private const val SIGN_KEY = "jGZH2Yf77YHpcyeZ"

    /**
     * 获取带有签名的完整参数 Map
     * @param path 接口路径 (例如 "/live/live/video/anchor")，必须以 / 开头
     * @param page 当前页码  -1,代表无需page和pageSize
     * @param pageSize 每页数量 (默认为 10，必须与接口请求的 pageSize 一致)
     */
    fun getSignedParams(path: String, page: Int = -1, pageSize: Int = 10): Map<String, String> {
        val params = HashMap<String, String>()

        // 1. 公共参数
        params["uid"] = "218904"
        params["systemModel"] = "Pixel 2 XL"
        params["appType"] = "1"
        params["appVer"] = "3.9.4.9"
        params["phoneBrand"] = "google"
        params["version"] = "3.9.4.9"
        params["deviceId"] = "63bd2e866c6ef324"
        params["systemVersion"] = "11"
        params["versionCode"] = "20251204"

        // 2. 根据路径和页码生成动态 Sign
        val sign = generateSign(path, page, pageSize)
        params["sign"] = sign

        return params
    }

    private fun generateSign(basePath: String, page: Int, pageSize: Int): String {
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
}