package com.jin.movie.activity

import MyDownloadManager
import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.arialyy.aria.core.Aria
import com.bumptech.glide.Glide
import com.jin.movie.R
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import kotlin.math.abs


class PlayerActivity : AppCompatActivity() {


    private val PERMISSION_REQUEST_CODE = 1001


    // --- 新增：长按倍速/快退相关变量 ---
    private lateinit var tvSpeedHint: TextView // 提示文字 View
    // 新增变量
    private lateinit var touchLayer: View // 触摸层
    private lateinit var gestureDetector: GestureDetector
    private var isLongPressing = false

    // 快退专用 Handler
    private val rewindHandler = Handler(Looper.getMainLooper())
    private val rewindRunnable = object : Runnable {
        override fun run() {
            if (!isLongPressing) return
            try {
                // 获取当前位置
                val currentPosition = videoPlayer.currentPositionWhenPlaying
                // 每次回退 2000毫秒 (2秒)，间隔 200毫秒执行一次，产生“快速倒带”的视觉效果
                val targetPosition = currentPosition - 2000

                if (targetPosition > 0) {
                    videoPlayer.seekTo(targetPosition.toLong())
                    // 继续下一次回退
                    rewindHandler.postDelayed(this, 200)
                } else {
                    // 到头了，停止
                    videoPlayer.seekTo(0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }



    private lateinit var videoPlayer: StandardGSYVideoPlayer
    private lateinit var orientationUtils: OrientationUtils

    // --- 下载按钮相关变量 ---
    private lateinit var downloadBtn: ImageView
    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { minimizeDownloadButton() }

    // 状态标记
    private var isDownloadBtnExpanded = true
    private var isDragging = false
    private val HIDE_DELAY = 3000L

    // 拖动相关变量
    private var lastX = 0f
    private var lastY = 0f
    private var screenWidth = 0
    private var screenHeight = 0

    private var isPlay = false
    private var isPause = false

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
        window.statusBarColor = android.graphics.Color.BLACK
        supportActionBar?.hide()

        // 获取 Aria 的下载配置
        val config = Aria.get(this).downloadConfig

        // 1. 【核心】开启网速转换（默认可能是关闭的，导致一直是 0kb/s）
        config.isConvertSpeed = true

        // 2. 设置进度刷新间隔（建议 1000毫秒，即 1秒刷新一次）
        // 如果太快（比如100ms），网速计算会很不准，容易跳 0
        config.updateInterval = 1000



        // 获取屏幕宽高，用于边界计算
        val displayMetrics = resources.displayMetrics
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        initView()
        initDownloadButton()
        initGesture() // <--- 记得调用这个
    }

    private fun initView() {
        videoPlayer = findViewById(R.id.video_player)
        tvSpeedHint = findViewById(R.id.tv_speed_hint) // <--- 初始化提示View
        touchLayer = findViewById(R.id.v_touch_layer) // 【获取触摸层】


        val url = intent.getStringExtra("url") ?: ""
        val title = intent.getStringExtra("title") ?: "未知视频"
        val coverUrl = intent.getStringExtra("cover") ?: ""

        val imageView = ImageView(this)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(this).load(coverUrl).into(imageView)



        orientationUtils = OrientationUtils(this, videoPlayer)
        orientationUtils.isEnable = false

        // 1. 定义请求头 Map
        val headerMap = HashMap<String, String>()
        headerMap["User-Agent"] = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36"
        headerMap["Referer"] = "http://MAfAIOo0E8EMOWPA.black"


        val gsyVideoOption = GSYVideoOptionBuilder()
        gsyVideoOption
            .setThumbImageView(imageView)
            .setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setAutoFullWithSize(true)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setUrl(url)
            .setMapHeadData(headerMap)
            .setCacheWithPlay(true)
            .setVideoTitle(title)
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String?, vararg objects: Any?) {
                    super.onPrepared(url, *objects)
                    orientationUtils.isEnable = true
                    isPlay = true
                    resetHideTimer()
                }
                override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                    super.onQuitFullscreen(url, *objects)
                    if (orientationUtils != null) orientationUtils.backToProtVideo()
                }
                override fun onClickStartIcon(url: String?, vararg objects: Any?) {
                    super.onClickStartIcon(url, *objects)
                    resetHideTimer()
                }
            })
            .setLockClickListener { view, lock ->
                if (orientationUtils != null) orientationUtils.isEnable = !lock
            }
            .build(videoPlayer)

        videoPlayer.fullscreenButton.setOnClickListener {
            orientationUtils.resolveByClick()
            videoPlayer.startWindowFullscreen(this, true, true)
        }
        videoPlayer.backButton.setOnClickListener { onBackPressed() }
        videoPlayer.startPlayLogic()
    }

    // ==========================================
    //      核心修改：拖动 + 吸边 + 隐藏逻辑
    // ==========================================

    private fun initDownloadButton() {
        downloadBtn = findViewById(R.id.iv_download_side)

        // 初始开始计时
        resetHideTimer()

        // 使用 OnTouchListener 处理拖动和点击
        downloadBtn.setOnTouchListener(object : View.OnTouchListener {
            private var startX = 0f
            private var startY = 0f
            private var startTime = 0L

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 记录按下时的状态
                        lastX = event.rawX
                        lastY = event.rawY
                        startX = event.rawX
                        startY = event.rawY
                        startTime = System.currentTimeMillis()

                        // 按下时，移除自动隐藏，并把按钮变得不透明
                        hideHandler.removeCallbacks(hideRunnable)
                        v.alpha = 1.0f
                        // 如果之前是缩进状态，按下时先别急着完全恢复，等用户决定是拖动还是点击
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // 计算偏移量
                        val dx = event.rawX - lastX
                        val dy = event.rawY - lastY

                        // 移动按钮
                        v.x += dx
                        v.y += dy

                        lastX = event.rawX
                        lastY = event.rawY

                        // 标记是否发生了明显的拖动 (超过10像素算拖动)
                        if (abs(event.rawX - startX) > 10 || abs(event.rawY - startY) > 10) {
                            isDragging = true
                            // 拖动时确保按钮完全展开
                            if (!isDownloadBtnExpanded) {
                                // 简单的恢复大小逻辑（具体吸附在松手时处理）
                            }
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val endTime = System.currentTimeMillis()
                        // 判断是点击还是拖动
                        // 如果移动距离很小 且 时间很短，视为点击
                        if (!isDragging && (endTime - startTime) < 200 && abs(event.rawX - startX) < 20) {
                            handleButtonClick()
                        } else {
                            // 拖动结束，执行吸边动画
                            snapToEdge()
                        }

                        // 重置状态并重新开始计时
                        isDragging = false
                        resetHideTimer()
                        return true
                    }
                }
                return false
            }
        })
    }

    /**
     * 处理点击逻辑
     */
    private fun handleButtonClick() {
        // 如果当前是收缩状态，点击则是展开
        if (!isDownloadBtnExpanded) {
            expandDownloadButton()
        } else {
            // 如果已经是展开状态，再次点击则是下载
            checkPermissionAndDownload()
        }
    }


    private fun initGesture() {
        // 1. 配置手势识别器
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                if (!isPlay) return // 未播放时不处理

                isLongPressing = true

                // 根据按压位置判断逻辑
                if (e.rawX < screenWidth / 2) {
                    // --- 左侧：快退 ---
                    showSpeedHint("<< 正在快退")
                    rewindHandler.post(rewindRunnable)
                } else {
                    // --- 右侧：2.0 倍速 ---
                    showSpeedHint(">> 2.0x 倍速播放中")
                    videoPlayer.setSpeedPlaying(2.0f, true)
                }
            }
            // 我们不需要在这里处理 onSingleTapConfirmed，交给 GSY 自己处理
        })

        // 2. 将触摸事件绑定到【透明触摸层】上
        touchLayer.setOnTouchListener { _, event ->

            // A. 先让手势识别器检测（检测长按）
            gestureDetector.onTouchEvent(event)

            // B. 处理手势抬起 (ACTION_UP)
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                if (isLongPressing) {
                    // 如果刚才是在长按，现在松手了 -> 恢复原状
                    stopLongPressLogic()

                    // 【关键点】：既然是长按结束，我们消耗掉这个 UP 事件，
                    // 不要传给 videoPlayer，否则 videoPlayer 会以为是点击，导致视频暂停。
                    return@setOnTouchListener true
                }
            }

            // C. 事件穿透逻辑
            // 如果当前正在长按，就不要把事件给 videoPlayer 了（防止干扰）
            // 如果没有长按，就把事件手动分发给 videoPlayer，让它处理它的点击暂停、滑动调节音量/亮度
            if (!isLongPressing) {
                videoPlayer.dispatchTouchEvent(event)
            }

            // 返回 true，确保 touchLayer 能持续接收到后续的 MOVE/UP 事件
            true
        }
    }

    private fun stopLongPressLogic() {
        isLongPressing = false
        tvSpeedHint.visibility = View.GONE

        // 停止快退
        rewindHandler.removeCallbacks(rewindRunnable)

        // 恢复正常速度
        videoPlayer.setSpeedPlaying(1.0f, true)
    }

    private fun showSpeedHint(text: String) {
        tvSpeedHint.text = text
        tvSpeedHint.visibility = View.VISIBLE
    }

    private fun checkPermissionAndDownload() {
        // 检查是否有写入权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // 如果没有权限，向用户申请
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // 如果已有权限，直接下载
            performDownload()
        }
    }

    /**
     * 拖动结束后，吸附到最近的屏幕边缘（左或右）
     */
    private fun snapToEdge() {
        val centerX = downloadBtn.x + downloadBtn.width / 2
        val parentWidth = (downloadBtn.parent as View).width

        // 判断离左边近还是右边近
        val targetX = if (centerX < parentWidth / 2) {
            10f // 吸附左边（留一点margin）
        } else {
            (parentWidth - downloadBtn.width - 10).toFloat() // 吸附右边
        }

        // 动画移动到边缘
        downloadBtn.animate()
            .x(targetX)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    isDownloadBtnExpanded = true // 吸附后视为展开状态
                }
            })
            .start()
    }

    private fun performDownload() {
        val url = intent.getStringExtra("url") ?: return
        val title = intent.getStringExtra("title") ?: "Unknow"
        val cover = intent.getStringExtra("cover") ?: ""

        // 一行代码搞定
        MyDownloadManager.startDownload(this, url, title, cover)

        Toast.makeText(this, "已加入下载队列", Toast.LENGTH_SHORT).show()
        minimizeDownloadButton()
    }
    /**
     * 3秒无操作后，执行"强力隐藏"
     */
    private fun minimizeDownloadButton() {
        if (!isDownloadBtnExpanded) return

        val parentWidth = (downloadBtn.parent as View).width
        val centerX = downloadBtn.x + downloadBtn.width / 2

        // 判断当前是在左边还是右边
        val isLeft = centerX < parentWidth / 2

        // 计算目标位置：
        // 如果在左边，往左缩进宽度的 70% (targetX 为负数)
        // 如果在右边，往右缩进宽度的 70% (targetX 为 parentWidth - 30%宽度)
        val offset = downloadBtn.width * 0.7f

        val targetX = if (isLeft) {
            downloadBtn.x - offset // 往左藏
        } else {
            downloadBtn.x + offset // 往右藏
        }

        downloadBtn.animate()
            .x(targetX)
            .alpha(0.3f) // 变得非常淡，几乎透明
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    isDownloadBtnExpanded = false
                }
            })
            .start()
    }

    /**
     * 展开按钮（从隐藏状态弹出来）
     */
    private fun expandDownloadButton() {
        val parentWidth = (downloadBtn.parent as View).width
        val centerX = downloadBtn.x + downloadBtn.width / 2
        val isLeft = centerX < parentWidth / 2

        // 恢复到正常的吸边位置
        val targetX = if (isLeft) 10f else (parentWidth - downloadBtn.width - 10).toFloat()

        downloadBtn.animate()
            .x(targetX)
            .alpha(1.0f) // 恢复完全不透明
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    isDownloadBtnExpanded = true
                }
            })
            .start()
    }

    private fun resetHideTimer() {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, HIDE_DELAY)
    }

    // --- 生命周期 ---

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // 屏幕旋转后重新获取屏幕尺寸
        val displayMetrics = resources.displayMetrics
        screenWidth = displayMetrics.widthPixels
        screenHeight = displayMetrics.heightPixels

        if (isPlay && !isPause) {
            videoPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true)
        }

        // 旋转后，强制把按钮吸附到边缘，防止位置错乱
        downloadBtn.post { snapToEdge() }
        resetHideTimer()
    }

    override fun onPause() {
        videoPlayer.currentPlayer.onVideoPause()
        super.onPause()
        isPause = true
        hideHandler.removeCallbacks(hideRunnable)
    }

    override fun onResume() {
        videoPlayer.currentPlayer.onVideoResume(false)
        super.onResume()
        isPause = false
        resetHideTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        rewindHandler.removeCallbacksAndMessages(null)
        if (isPlay) videoPlayer.currentPlayer.release()
        if (orientationUtils != null) orientationUtils.releaseListener()
        hideHandler.removeCallbacksAndMessages(null)
    }

    override fun onBackPressed() {
        if (orientationUtils != null) orientationUtils.backToProtVideo()
        if (GSYVideoManager.backFromWindowFull(this)) return
        super.onBackPressed()
    }


    // 处理用户点击“允许”或“拒绝”后的回调
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 用户点击了“允许”，开始下载
                performDownload()
            } else {
                // 用户点击了“拒绝”
                Toast.makeText(this, "需要存储权限才能下载视频", Toast.LENGTH_SHORT).show()
            }
        }
    }
}