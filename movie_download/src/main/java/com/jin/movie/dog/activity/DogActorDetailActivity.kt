package com.jin.movie.dog.activity


import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.R
import com.jin.movie.activity.PlayerActivity
import com.jin.movie.adapter.VideoAdapter
import com.jin.movie.utils.HtmlParseHelper
import com.jin.movie.utils.NetManager
import com.jin.movie.utils.UIUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class DogActorDetailActivity : AppCompatActivity() {

    // 静态方法，方便跳转
    companion object {
        fun start(context: Context, url: String, title: String) {
            val intent = Intent(context, DogActorDetailActivity::class.java)
            intent.putExtra("url", url)
            intent.putExtra("title", title)
            context.startActivity(intent)
        }
    }

    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var rvVideoList: RecyclerView
    private lateinit var tvTitle: TextView
    private lateinit var tvPageIndicator: TextView

    private lateinit var videoAdapter: VideoAdapter

    // 基础数据
    private var baseUrl: String = "" // 演员的第一页地址
    private var pageTitle: String = ""
    private var currentPage = 1
    private var totalPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIUtils.setActivityBarStyle(this)
        setContentView(R.layout.activity_dog_actor_detail)

        // 获取传参
        // 注意：传入的 url 可能是相对路径 "/actresses/xxx"，也可能是绝对路径
        val rawUrl = intent.getStringExtra("url") ?: ""
        pageTitle = intent.getStringExtra("title") ?: "演员详情"

        // 处理 URL 补全域名
        baseUrl = if (rawUrl.startsWith("http")) {
            rawUrl
        } else {
            "https://taolu.dog$rawUrl" // 补全域名
        }

        initViews()
        initAdapter()

        // 首次加载
        refreshLayout.autoRefresh()
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.tv_title)
        tvTitle.text = pageTitle

        findViewById<android.view.View>(R.id.iv_back).setOnClickListener {
            finish()
        }

        refreshLayout = findViewById(R.id.refreshLayout)
        rvVideoList = findViewById(R.id.rv_video_list)
        tvPageIndicator = findViewById(R.id.tv_page_indicator)

        refreshLayout.setOnRefreshListener {
            currentPage = 1
            fetchData(isRefresh = true)
        }

        refreshLayout.setOnLoadMoreListener {
            if (currentPage < totalPage) {
                currentPage++
                fetchData(isRefresh = false)
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData()
            }
        }
    }

    private fun initAdapter() {
        // 复用 VideoAdapter
        videoAdapter = VideoAdapter(mutableListOf()) { video ->
            // --- 点击播放逻辑 (与首页一致) ---
            val loadingDialog = ProgressDialog(this).apply {
                setMessage("正在解析视频地址...")
                setCancelable(false)
                show()
            }

            lifecycleScope.launch(Dispatchers.IO) {
                var finalUrl = video.movieUrl
                try {
                    val headers = mapOf("User-Agent" to "Mozilla/5.0 (Android) VideoApp/1.0")
                    finalUrl = getRedirectLocation(video.movieUrl, headers)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                withContext(Dispatchers.Main) {
                    loadingDialog.dismiss()
                    PlayerActivity.start(this@DogActorDetailActivity, finalUrl, video.title, video.coverUrl)
                }
            }
        }

        rvVideoList.layoutManager = GridLayoutManager(this, 2)
        rvVideoList.adapter = videoAdapter

        // 滚动监听更新页码
        rvVideoList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val firstPosition = layoutManager.findFirstVisibleItemPosition()
                if (firstPosition != RecyclerView.NO_POSITION) {
                    val video = videoAdapter.getItem(firstPosition)
                    if (video != null && video.page > 0) {
                        tvPageIndicator.text = "${video.page} / $totalPage"
                    }
                }
            }
        })
    }

    private fun fetchData(isRefresh: Boolean) {

        val url = "$baseUrl?page=${currentPage}"

        NetManager.get(url, object : NetManager.Callback {
            override fun onSuccess(response: String) {
                // 复用首页的视频解析逻辑（因为演员详情页结构和首页列表通常是一样的）
                val videos = HtmlParseHelper.parseDogVideoList(response) // 默认按时间
                val pageCount = HtmlParseHelper.parseDogTotalPage(response)

                videos.forEach { it.page = currentPage }

                runOnUiThread {
                    if (pageCount > 0) totalPage = pageCount
                    tvPageIndicator.visibility = android.view.View.VISIBLE
                    tvPageIndicator.text = "$currentPage / $totalPage"

                    if (isRefresh) {
                        videoAdapter.setNewData(videos)
                        refreshLayout.finishRefresh()
                    } else {
                        if (videos.isEmpty()) {
                            refreshLayout.finishLoadMoreWithNoMoreData()
                        } else {
                            videoAdapter.addData(videos)
                            refreshLayout.finishLoadMore()
                        }
                    }
                }
            }

            override fun onError(msg: String) {
                runOnUiThread {
                    Toast.makeText(this@DogActorDetailActivity, "加载失败: $msg", Toast.LENGTH_SHORT).show()
                    if (isRefresh) refreshLayout.finishRefresh(false)
                    else refreshLayout.finishLoadMore(false)
                }
            }
        })
    }

    // --- 工具：解析重定向 ---
    private fun getRedirectLocation(originalUrl: String, headers: Map<String, String>): String {
        var realUrl = originalUrl
        var conn: HttpURLConnection? = null
        try {
            val urlObj = URL(originalUrl)
            conn = urlObj.openConnection() as HttpURLConnection
            conn.instanceFollowRedirects = false
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            for ((key, value) in headers) {
                conn.setRequestProperty(key, value)
            }
            conn.connect()
            val code = conn.responseCode
            if (code in 300..399) {
                val location = conn.getHeaderField("Location")
                if (!location.isNullOrEmpty()) {
                    realUrl = location
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn?.disconnect()
        }
        return realUrl
    }
}