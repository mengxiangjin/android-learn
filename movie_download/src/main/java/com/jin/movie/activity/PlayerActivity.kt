package com.jin.movie.activity


import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.jin.movie.R
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

class PlayerActivity : AppCompatActivity() {

    private lateinit var videoPlayer: StandardGSYVideoPlayer
    private lateinit var orientationUtils: OrientationUtils

    private var isPlay = false
    private var isPause = false

    // 伴生对象，方便外部跳转
    companion object {
        fun start(context: Context, url: String, title: String, coverUrl: String) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra("url", url)
            intent.putExtra("title", title)
            intent.putExtra("cover", coverUrl)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // 沉浸式黑色状态栏
        window.statusBarColor = android.graphics.Color.BLACK
        supportActionBar?.hide()

        initView()
    }

    private fun initView() {
        videoPlayer = findViewById(R.id.video_player)

        // 1. 获取传递过来的数据
        val url = intent.getStringExtra("url") ?: ""
        val title = intent.getStringExtra("title") ?: "未知视频"
        val coverUrl = intent.getStringExtra("cover") ?: ""

        // 2. 增加封面图
        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(this).load(coverUrl).into(imageView)

        // 3. 旋转工具类 (处理全屏旋转)
        orientationUtils = OrientationUtils(this, videoPlayer)

        // 初始化不自动旋转
        orientationUtils.isEnable = false

        // 4. 配置播放器
        val gsyVideoOption = GSYVideoOptionBuilder()
        gsyVideoOption
            .setThumbImageView(imageView) // 设置封面
            .setIsTouchWiget(true)        // 是否支持手势
            .setRotateViewAuto(false)     // 是否开启自动旋转
            .setLockLand(false)           // 一开始不锁屏
            .setAutoFullWithSize(true)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setUrl(url)                  // 播放地址
            .setCacheWithPlay(true)       // 边播边缓存
            .setVideoTitle(title)         // 标题
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String?, vararg objects: Any?) {
                    super.onPrepared(url, *objects)
                    // 开始播放了，才能旋转
                    orientationUtils.isEnable = true
                    isPlay = true
                }

                override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                    super.onQuitFullscreen(url, *objects)
                    // 退出全屏时，强制切回竖屏
                    if (orientationUtils != null) {
                        orientationUtils.backToProtVideo()
                    }
                }
            })
            .setLockClickListener { view, lock ->
                // 锁屏点击逻辑
                if (orientationUtils != null) {
                    orientationUtils.isEnable = !lock
                }
            }
            .build(videoPlayer)

        // 设置全屏按键功能
        videoPlayer.fullscreenButton.setOnClickListener {
            // 直接横屏
            orientationUtils.resolveByClick()
            // 第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            videoPlayer.startWindowFullscreen(this, true, true)
        }

        // 设置返回按键功能
        videoPlayer.backButton.setOnClickListener {
            onBackPressed()
        }

        // 立即开始播放
        videoPlayer.startPlayLogic()
    }

    // --- 生命周期管理 (非常重要) ---

    override fun onPause() {
        videoPlayer.currentPlayer.onVideoPause()
        super.onPause()
        isPause = true
    }

    override fun onResume() {
        videoPlayer.currentPlayer.onVideoResume(false)
        super.onResume()
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            videoPlayer.currentPlayer.release()
        }
        if (orientationUtils != null) {
            orientationUtils.releaseListener()
        }
    }

    // --- 处理屏幕旋转 ---

    // 不要在 Manifest 里重启 Activity，而是自己处理配置变化
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (isPlay && !isPause) {
            videoPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true)
        }
    }

    // --- 处理返回键 ---

    override fun onBackPressed() {
        // 如果是横屏/全屏，先退出全屏
        if (orientationUtils != null) {
            orientationUtils.backToProtVideo()
        }
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
        super.onBackPressed()
    }
}