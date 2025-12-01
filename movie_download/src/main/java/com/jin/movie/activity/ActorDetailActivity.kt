package com.jin.movie.activity


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.R
import com.jin.movie.adapter.VideoAdapter
import com.jin.movie.utils.HtmlParseHelper
import com.jin.movie.utils.NetManager
import com.jin.movie.utils.UIUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class ActorDetailActivity : AppCompatActivity() {

    companion object {
        fun start(context: Context, name: String, url: String) {
            val intent = Intent(context, ActorDetailActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var rvWorks: RecyclerView
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var tvPageIndicator: TextView // 新增

    private var baseUrl: String = ""
    private var currentPage = 1
    private var totalPage = 1 // 新增：记录总页数

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actor_detail)
        UIUtils.setActivityBarStyle(this)

        val name = intent.getStringExtra("name") ?: "演员详情"
        val rawUrl = intent.getStringExtra("url") ?: ""
        baseUrl = if (rawUrl.startsWith("http")) rawUrl else "https://www.zimuquan23.uk$rawUrl"

        findViewById<TextView>(R.id.tv_title).text = "$name 的作品"

        initViews()
        refreshLayout.autoRefresh()
    }

    private fun initViews() {
        refreshLayout = findViewById(R.id.refreshLayout)
        rvWorks = findViewById(R.id.rv_works)
        tvPageIndicator = findViewById(R.id.tv_page_indicator) // 绑定控件

        // 1. 设置点击事件 (跳转)
        tvPageIndicator.setOnClickListener {
            showJumpDialog()
        }

        rvWorks.layoutManager = GridLayoutManager(this, 2)
        videoAdapter = VideoAdapter(mutableListOf()) { video ->
            PlayerActivity.start(this, video.movieUrl, video.title, video.coverUrl)
        }
        rvWorks.adapter = videoAdapter

        // 2. 监听滑动，实时更新页码显示
        rvWorks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val firstPosition = layoutManager.findFirstVisibleItemPosition()
                if (firstPosition != RecyclerView.NO_POSITION) {
                    val video = videoAdapter.getItem(firstPosition)
                    // 只要 Video 对象里 page 字段被正确赋值了，这里就能更新
                    if (video != null && video.page > 0) {
                        tvPageIndicator.text = "${video.page} / $totalPage"
                    }
                }
            }
        })

        refreshLayout.setOnRefreshListener {
            // 下拉刷新重置为第1页
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

    // 给一个默认值防止除以0，但主要依靠动态获取
    private var pageSize = 20

    private fun fetchData(isRefresh: Boolean) {
        val url = getUrlForPage(currentPage)

        NetManager.get(url, object : NetManager.Callback {
            override fun onSuccess(response: String) {
                val html = HtmlParseHelper.decodeHtml(response)

                // 1. 解析当前页的视频列表
                val videos = HtmlParseHelper.parseVideoList(html, HtmlParseHelper.ParseType.ACTOR)

                // 2. 解析总视频数 (例如: 67)
                val totalVideoCount = HtmlParseHelper.parseActorVideoCount(html)

                // 3. 【核心逻辑】动态推算 PageSize (不写死！)
                // 条件：当前是第1页 且 列表不为空 且 总数大于当前页数量
                // 解释：如果总数67个，第1页回来了24个，说明这一页满了，那容量肯定就是24
                if (currentPage == 1 && videos.isNotEmpty()) {
                    if (totalVideoCount > videos.size) {
                        pageSize = videos.size
                    }
                }

                // 标记页码
                videos.forEach { it.page = currentPage }

                runOnUiThread {
                    // 4. 计算总页数
                    if (totalVideoCount > 0) {
                        // 公式：(总数 + 容量 - 1) / 容量
                        totalPage = (totalVideoCount + pageSize - 1) / pageSize
                    } else {
                        // 兜底：如果没抓到总数，至少当前页是存在的
                        if (videos.isNotEmpty() && currentPage > totalPage) {
                            totalPage = currentPage
                        }
                    }

                    // --- 【核心修改点 START】 ---

                    if (isRefresh) {
                        // 场景：下拉刷新 或 跳转页码
                        // 这种情况下，列表会被清空重建，屏幕上显示的就是新页码的数据
                        // 所以这里必须强制更新 UI
                        tvPageIndicator.text = "$currentPage / $totalPage"

                        videoAdapter.setNewData(videos)
                        refreshLayout.finishRefresh()
                        rvWorks.scrollToPosition(0)

                        if (videos.isEmpty()) Toast.makeText(this@ActorDetailActivity, "暂无视频", Toast.LENGTH_SHORT).show()
                    } else {
                        // 场景：上拉加载更多
                        // 这种情况下，新数据加在底部，用户当前看到的还是旧页码的数据
                        // 【不要】在这里设置 tvPageIndicator.text
                        // 等用户手指往下滑，滑到新数据时，onScrolled 会自动更新页码

                        if (videos.isEmpty()) {
                            refreshLayout.finishLoadMoreWithNoMoreData()
                        } else {
                            videoAdapter.addData(videos)
                            refreshLayout.finishLoadMore()
                        }
                    }
                    // --- 【核心修改点 END】 ---
                }
            }

            override fun onError(msg: String) {
                runOnUiThread {
                    Toast.makeText(this@ActorDetailActivity, "Err: $msg", Toast.LENGTH_SHORT).show()
                    if (isRefresh) refreshLayout.finishRefresh(false)
                    else refreshLayout.finishLoadMore(false)
                }
            }
        })
    }

    private fun getUrlForPage(page: Int): String {
        if (page == 1) return baseUrl
        return if (baseUrl.endsWith(".html")) {
            val prefix = baseUrl.substringBeforeLast(".html")
            "$prefix/page/$page.html"
        } else {
            "$baseUrl/page/$page.html"
        }
    }

    // 弹窗跳转逻辑
    private fun showJumpDialog() {
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.hint = "1 - $totalPage"
        input.gravity = Gravity.CENTER

        AlertDialog.Builder(this)
            .setTitle("跳转到页码")
            .setView(input)
            .setPositiveButton("前往") { _, _ ->
                val pageStr = input.text.toString()
                if (pageStr.isNotEmpty()) {
                    val page = pageStr.toInt()
                    if (page in 1..totalPage) {
                        // 执行跳转：清空列表 -> 重设页码 -> 刷新
                        currentPage = page
                        // 设为 true 视为重新刷新，会清空旧数据
                        fetchData(isRefresh = true)
                        refreshLayout.resetNoMoreData() // 重置“没有更多数据”的状态
                    } else {
                        Toast.makeText(this, "页码超出范围", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
}