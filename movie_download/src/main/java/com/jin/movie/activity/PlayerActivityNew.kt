package com.jin.movie.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.jin.movie.R
import com.jin.movie.tl.utils.VideoPlayerHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PlayerActivityNew : AppCompatActivity() {
    private var playerView: PlayerView? = null
    private var exoPlayer: ExoPlayer? = null
    private var executorService: ExecutorService? = null
    private var mainHandler: Handler? = null


    companion object {
        fun start(context: Context, url: String, title: String, coverUrl: String) {
            val intent = Intent(context, PlayerActivityNew::class.java)
            intent.putExtra("url", url)
            intent.putExtra("title", title)
            intent.putExtra("cover", coverUrl)
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player_new)

        window.statusBarColor = android.graphics.Color.BLACK
        supportActionBar?.hide()

        val videoUrl = intent.getStringExtra("url") ?: ""
        val title = intent.getStringExtra("title") ?: "未知视频"




        playerView = findViewById<PlayerView>(R.id.player_view)
        executorService = Executors.newSingleThreadExecutor()
        mainHandler = Handler(Looper.getMainLooper())

        // 1. 初始化 ExoPlayer
        initPlayer()
        // 2. 传入你的视频链接进行播放
        startPlay(videoUrl)
    }

    private fun initPlayer() {
        // 创建 ExoPlayer 实例
        exoPlayer = ExoPlayer.Builder(this).build()
        // 绑定到 UI
        playerView!!.player = exoPlayer
    }

    /**
     * 仿照你逆向的 startPlayInner 逻辑
     */
    private fun startPlay(str: String) {
        playVideoOnMainThread(str, true)
//        // 必须在子线程进行 M3U8 探测，因为里面有 OkHttp 的同步网络请求
//        executorService!!.execute {
//            // 1. 探测是否为 M3U8 格式 (对应逆向代码中的 boolean z)
////            val isM3u8: Boolean = M3u8Detector.isM3u8ByMagicNumber(str)
//
//            // 2. ExoPlayer 的操作必须在主线程进行
//            mainHandler!!.post {
//                playVideoOnMainThread(str, true)
//            }
//        }
    }

    /**
     * 在主线程组装 MediaSource 并播放 (对应你逆向里的 lambda$startPlayInner$4$BasePlayerActivity)
     */
    @OptIn(UnstableApi::class)
    private fun playVideoOnMainThread(url: String, isM3u8: Boolean) {
        if (exoPlayer == null) return

        // 1. 创建 MediaItem
        val mediaItem = MediaItem.fromUri(url)

        // 2. 创建 DataSource.Factory (这里会触发你自定义的 SignedHttpDataSourceFactory)
        val dataSourceFactory = VideoPlayerHelper.createDataSourceFactory(this)

        // 3. 根据是否为 M3U8 创建对应的 MediaSource
        val mediaSource = VideoPlayerHelper.createMediaSource(mediaItem, dataSourceFactory, isM3u8)

        // 4. 设置数据源，准备并播放
        exoPlayer!!.setMediaSource(mediaSource)
        exoPlayer!!.prepare()
        exoPlayer!!.play() // 自动开始播放
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放资源，防止内存泄漏
        if (exoPlayer != null) {
            exoPlayer!!.release()
            exoPlayer = null
        }
        if (executorService != null) {
            executorService!!.shutdown()
        }
    }
}