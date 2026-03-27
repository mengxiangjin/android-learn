package com.jin.movie.dog

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jin.movie.R
import com.jin.movie.dog.fragment.DogActorFragment
import com.jin.movie.dog.fragment.DogHomeFragment
import com.jin.movie.dog.fragment.DogLocalFragment
import com.jin.movie.dog.utils.WebManager
import com.jin.movie.utils.UIUtils
import com.shuyu.gsyvideoplayer.cache.CacheFactory
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager

class DogMainActivity : AppCompatActivity() {

    companion object {
        var BASE_URL = ""
        var URL_LOCAL = ""
    }

    private lateinit var bottomNav: BottomNavigationView

    // 预先创建 Fragment 实例
    private val dogHomeFragment = DogHomeFragment()
    // 暂时用 HomeFragment 占位，你可以新建 ProfileFragment, FavoritesFragment
    private val dogActorFragment = DogActorFragment()
    private val dogLocalFragment = DogLocalFragment()

    // 记录当前显示的 Fragment
    private var activeFragment: Fragment = dogHomeFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 全局配置 (只需一次)
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)

        // 3. 【核心解决卡顿】开启硬解码！
        // ExoPlayer 默认软解，看高清 m3u8 会卡。开启这个利用 GPU 解码，非常流畅。
        com.shuyu.gsyvideoplayer.utils.GSYVideoType.enableMediaCodec()
        com.shuyu.gsyvideoplayer.utils.GSYVideoType.enableMediaCodecTexture()

        setContentView(R.layout.dog_activity_main)
        UIUtils.setActivityBarStyle(this)

        bottomNav = findViewById(R.id.bottom_navigation)

        WebManager.getUrlList {
            BASE_URL = it
            runOnUiThread {
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, dogHomeFragment, "HOME").commit()
                // 2. 设置点击监听
                bottomNav.setOnItemSelectedListener { item ->
                    when (item.itemId) {
                        R.id.nav_home -> switchFragment(dogHomeFragment)
                        R.id.nav_profile -> switchFragment(dogActorFragment)
                        R.id.nav_local -> {
                            if (URL_LOCAL.isEmpty()) {
                                getLocalUrl()
                            }else {
                                switchFragment(dogLocalFragment)
                            }
                        }

                    }
                    true
                }
            }
        }
    }

    private fun getLocalUrl() {
        WebManager.getUrlList(2) {
            Log.d("TAG", "getLocalUrl: " + it)
            URL_LOCAL = it
            switchFragment(dogLocalFragment)
        }
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