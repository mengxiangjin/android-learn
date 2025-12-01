package com.jin.movie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jin.movie.fragment.ActorFragment
import com.jin.movie.fragment.HomeFragment
import com.jin.movie.fragment.RankFragment
import com.jin.movie.utils.UIUtils
import com.shuyu.gsyvideoplayer.cache.CacheFactory
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    // 预先创建 Fragment 实例
    private val homeFragment = HomeFragment()
    // 暂时用 HomeFragment 占位，你可以新建 ProfileFragment, FavoritesFragment
    private val rankFragment = RankFragment()
    private val actorFragment = ActorFragment()

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
            }
            true
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