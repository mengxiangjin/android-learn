package com.jin.movie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jin.movie.fragment.ActorFragment
import com.jin.movie.fragment.DownloadFragment
import com.jin.movie.fragment.HomeFragment
import com.jin.movie.fragment.MineFragment
import com.jin.movie.fragment.RankFragment
import com.jin.movie.utils.UIUtils
import com.shuyu.gsyvideoplayer.cache.CacheFactory
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager


import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import android.util.Log
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    // 预先创建 Fragment 实例
    private val homeFragment = HomeFragment()
    // 暂时用 HomeFragment 占位，你可以新建 ProfileFragment, FavoritesFragment
    private val rankFragment = RankFragment()
    private val actorFragment = ActorFragment()
    private val downloadFragment = DownloadFragment()
    private val mineFragment = MineFragment()

    // 记录当前显示的 Fragment
    private var activeFragment: Fragment = homeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 全局配置 (只需一次)
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)

        // 3. 【核心解决卡顿】开启硬解码！
        // ExoPlayer 默认软解，看高清 m3u8 会卡。开启这个利用 GPU 解码，非常流畅。
        com.shuyu.gsyvideoplayer.utils.GSYVideoType.enableMediaCodec()
        com.shuyu.gsyvideoplayer.utils.GSYVideoType.enableMediaCodecTexture()

        setContentView(R.layout.activity_main)
        UIUtils.setActivityBarStyle(this)

        bottomNav = findViewById(R.id.bottom_navigation)

        // 1. 初始化 Fragment
        // 将所有 Fragment 添加到 FragmentManager，但只显示第一个
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, homeFragment, "HOME").commit()
        // 这里为了懒加载，其他 Fragment 可以等点击时再 add，或者现在都 add 但 hide
        // 简单起见，我们先只 add 首页

        // 2. 设置点击监听
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> switchFragment(homeFragment)
                R.id.nav_favorites -> switchFragment(rankFragment)
                R.id.nav_profile -> switchFragment(actorFragment)
                R.id.nav_download -> switchFragment(downloadFragment)
                R.id.nav_mine -> {
                    switchFragment(mineFragment)
//                    testRawRequest()
                }
            }
            true
        }
    }

    fun testRawRequest() {
        // 1. 强制 HTTP/1.1 (保持这个配置，非常重要)
        val client = OkHttpClient.Builder()
            .protocols(listOf(okhttp3.Protocol.HTTP_1_1))
            .build()

        // ==========================================
        // 核心修改点：签名用的 Path 和 发送用的 Path 分离
        // ==========================================

        // A. 签名计算：必须全小写 (Python 脚本证明了这一点)
        val pathForSign = "/user/follow/followlist/1/20"
        val sign = generateSign(pathForSign)

        // B. 实际请求：必须保留大写 (CamelCase)
        // 如果发出去变成小写，服务器可能找不到路由，或者校验逻辑对不上
        val urlPathSegments = "user/follow/followList/1/20"

        val httpUrl = okhttp3.HttpUrl.Builder()
            .scheme("https")
            .host("pro.api.taolu6.cc")
            .addPathSegments(urlPathSegments) // 这里用带大写的 Path
            .addQueryParameter("uid", "218904")
            .addQueryParameter("systemModel", "Pixel 2 XL")
            .addQueryParameter("appType", "1")
            .addQueryParameter("appVer", "3.9.5.9")
            .addQueryParameter("phoneBrand", "google")
            .addQueryParameter("sign", sign) // 填入基于小写算出的 sign
            .addQueryParameter("version", "3.9.5.9")
            .addQueryParameter("deviceId", "63bd2e866c6ef324")
            .addQueryParameter("systemVersion", "11")
            .addQueryParameter("versionCode", "20260203")
            .build()

        Log.d("DEBUG_TEST", "最终发送 URL: $httpUrl")
        Log.d("DEBUG_TEST", "签名使用的 Path: $pathForSign")

        // Header 保持不变
        val appVersionHeaderValue = "{\"uid\":\"218904\",\"systemModel\":\"Pixel 2 XL\",\"appType\":\"1\",\"appVer\":\"3.9.5.9\",\"phoneBrand\":\"google\",\"version\":\"3.9.5.9\",\"deviceId\":\"63bd2e866c6ef324\",\"systemVersion\":\"11\",\"versionCode\":\"20260203\"}"

        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .addHeader("Connection", "keep-alive")
            .addHeader("Accept", "*/*")
            .addHeader("User-Agent", "Dalvik/2.1.0 (Linux; U; Android 11; Pixel 2 XL Build/RP1A.201005.004)")
            .addHeader("token", "aiya_41e9d628-aa7a-4eb9-b449-a941e71d26c5ov")
            .addHeader("appversion", appVersionHeaderValue)
            .addHeader("versionname", "3.9.5.9")
            .addHeader("versioncode", "20260203")
            .addHeader("clienttype", "Android")
            .addHeader("referer", "https://pro.api.taolu6.cc")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("DEBUG_TEST", "失败: ${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                Log.d("DEBUG_TEST", "Code: ${response.code}, Body: $body")
            }
        })
    }

    // 签名方法：保持你现在的代码 (Path 不要在方法里再次 .lowercase()，传入什么用什么)
    fun generateSign(path: String): String {
        val cleanPath = path.trim() // 不要在这里 .lowercase()，在外面控制
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val nonce = "43600c095fa84f059f2c02725f4c0b0d"
        val rawData = "$cleanPath-$timestamp-$nonce-0-$AUTH_KEY"
        println("DEBUG_SIGN: 计算串 = [$rawData]")
        val md5Hash = md5(rawData)
        return "$timestamp-$nonce-0-$md5Hash"
    }

    // 你的 generateSign 保持不变，注意 Path 大小写要和 rawPath 变量一致
    val AUTH_KEY = "3vtWp15zCm" // 你的密钥

    /**
     * 计算最终的 sign 参数值
     * 对应 Python: f'{ts}-{nonce}-0-{result}'
     */

// 1. 修正 MD5，强制 UTF-8
    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }


    // 切换 Fragment 的核心方法
    private fun switchFragment(targetFragment: Fragment) {
        if (activeFragment == targetFragment) return

        val transaction = supportFragmentManager.beginTransaction()

        // 如果目标 Fragment 还没添加过，就添加
        if (!targetFragment.isAdded) {
            transaction.add(R.id.fragment_container, targetFragment)
        }

        // 隐藏当前，显示目标
        transaction.hide(activeFragment).show(targetFragment)
        transaction.commit()

        activeFragment = targetFragment
    }
}