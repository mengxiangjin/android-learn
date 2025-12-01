package com.jin.movie.activity


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.R
import com.jin.movie.adapter.SortAdapter
import com.jin.movie.adapter.VideoAdapter
import com.jin.movie.utils.HtmlParseHelper
import com.jin.movie.utils.NetManager
import com.jin.movie.utils.UIUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import java.net.URLEncoder


class SearchActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var tvSearchBtn: TextView
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var rvSearchList: RecyclerView
    private lateinit var tvPageIndicator: TextView
    private lateinit var ivBack: ImageView
    private lateinit var rvSortTags: RecyclerView // 新增

    private lateinit var videoAdapter: VideoAdapter
    private lateinit var sortAdapter: SortAdapter // 新增

    // --- 数据状态 ---
    private var currentKeyword = ""
    private var currentPage = 1
    private var totalPage = 1
    // 默认排序 (time=最新, hits=热门, score/up=推荐)
    private var currentSortType = "time"

    private val BASE_URL = "https://www.zimuquan23.uk"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        UIUtils.setActivityBarStyle(this)

        initViews()
        initAdapter()
    }

    private fun initViews() {
        etSearch = findViewById(R.id.et_search)
        tvSearchBtn = findViewById(R.id.tv_search_btn)
        refreshLayout = findViewById(R.id.refreshLayout)
        rvSearchList = findViewById(R.id.rv_search_list)
        tvPageIndicator = findViewById(R.id.tv_page_indicator)
        ivBack = findViewById(R.id.iv_back)
        rvSortTags = findViewById(R.id.rv_sort_tags) // 绑定控件

        ivBack.setOnClickListener { finish() }

        tvSearchBtn.setOnClickListener { performSearch() }

        // 页码点击
        tvPageIndicator.setOnClickListener {
            if (totalPage > 1) showJumpDialog()
        }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        refreshLayout.setOnRefreshListener {
            if (currentPage > 1) {
                currentPage--
            } else {
                currentPage = 1
            }
            refreshLayout.resetNoMoreData()
            doSearchRequest(isRefresh = true)
        }

        refreshLayout.setOnLoadMoreListener {
            if (currentPage < totalPage) {
                currentPage++
                doSearchRequest(isRefresh = false)
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData()
            }
        }
    }

    private fun initAdapter() {
        // 1. 视频列表适配器
        videoAdapter = VideoAdapter(mutableListOf()) { video ->
            val playUrl = video.movieUrl
            PlayerActivity.start(
                context = this,
                url = playUrl,
                title = video.title,
                coverUrl = video.coverUrl
            )
        }
        rvSearchList.layoutManager = GridLayoutManager(this, 2)
        rvSearchList.adapter = videoAdapter

        // 滚动监听
        rvSearchList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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

        // 2. 【新增】排序适配器
        sortAdapter = SortAdapter(emptyList()) { fixCat ->
            // 点击排序的回调

            val clickSortType = if (fixCat.url.contains("time")) {
                "time"
            } else if (fixCat.url.contains("hits_week")) {
                "hits_week"
            } else {
                "up"
            }

            if (currentSortType != clickSortType) {
                currentSortType = clickSortType
                currentPage = 1
                refreshLayout.autoRefresh() // 触发刷新
            }
        }
        rvSortTags.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvSortTags.adapter = sortAdapter
    }

    private fun performSearch() {
        val keyword = etSearch.text.toString().trim()
        if (keyword.isEmpty()) {
            Toast.makeText(this, "请输入关键词", Toast.LENGTH_SHORT).show()
            return
        }

        // 收键盘
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
        etSearch.clearFocus()

        currentKeyword = keyword
        currentPage = 1
        currentSortType = "time" // 新搜索重置为最新
        sortAdapter.resetSelection() // UI重置

        refreshLayout.autoRefresh()
    }

    // 跳转弹窗 (保留你之前的修改)
    private fun showJumpDialog() {
        etSearch.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)

        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        input.hint = "1 - $totalPage"
        input.gravity = android.view.Gravity.CENTER

        AlertDialog.Builder(this)
            .setTitle("跳转到页码")
            .setView(input)
            .setPositiveButton("Go") { _, _ ->
                val pageStr = input.text.toString()
                imm.hideSoftInputFromWindow(input.windowToken, 0) // 关弹窗键盘
                if (pageStr.isNotEmpty()) {
                    val targetPage = pageStr.toInt()
                    if (targetPage in 1..totalPage) {
                        currentPage = targetPage
                        rvSearchList.scrollToPosition(0)
                        refreshLayout.resetNoMoreData()
                        refreshLayout.autoRefreshAnimationOnly()
                        doSearchRequest(isRefresh = true)
                    } else {
                        Toast.makeText(this, "页码超出范围", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("取消") { _, _ ->
                imm.hideSoftInputFromWindow(input.windowToken, 0)
            }
            .show()
    }

    private fun doSearchRequest(isRefresh: Boolean) {
        // 1. URL 编码
        val encodedKeyword = try {
            URLEncoder.encode(currentKeyword, "UTF-8")
        } catch (e: Exception) {
            currentKeyword
        }

        // 2. 拼接 URL (支持排序)
        // 格式通常为：/index.php/vod/search/by/{sort}/page/{page}/wd/{keyword}.html
        // 注意：如果 currentSortType 是 "time"(默认)，有些网站不带 /by/time 也能跑，但带上通常也没错
        // 为了稳妥，我们可以模仿首页的逻辑：
        val sortPart = if (currentSortType == "time") "" else "/by/$currentSortType"

        // 这里的拼接顺序很关键，通常伪静态是按顺序解析的
        val url = "$BASE_URL/index.php/vod/search${sortPart}/page/$currentPage/wd/$encodedKeyword.html"

        Log.d("SearchActivity", "Search URL: $url")

        NetManager.get(url, object : NetManager.Callback {
            override fun onSuccess(response: String) {
                val html = HtmlParseHelper.decodeHtml(response)

                val videos = HtmlParseHelper.parseVideoList(html,HtmlParseHelper.ParseType.SEARCH)
                val pageCount = HtmlParseHelper.parseTotalPage(html)

                // 【关键】尝试解析排序标签
                // 因为搜索页结构和分类页类似，我们尝试用 parseCategoryList 解析
                // 结果通常会返回一个包含了 fixCategories 的 BigCategory 对象 (虽然它没有 SubCategories)
                // 【修改点】使用新方法独立解析排序标签
                val sortTags = if (isRefresh) HtmlParseHelper.parseSortTags(html) else emptyList()

                videos.forEach { it.page = currentPage }

                runOnUiThread {
                    if (pageCount > 0) totalPage = pageCount
//                    tvPageIndicator.text = "$currentPage / $totalPage"
                    tvPageIndicator.isVisible = true

                    if (isRefresh) {
                        tvPageIndicator.text = "$currentPage / $totalPage"
                        videoAdapter.setNewData(videos)
                        if (sortTags.isNotEmpty()) {
                            sortAdapter.updateData(sortTags)
                            rvSortTags.isVisible = true
                        } else {
                            if (sortAdapter.itemCount == 0) {
                                rvSortTags.isVisible = false
                            }
                        }

                        refreshLayout.finishRefresh()

                        if (videos.isEmpty()) {
                            Toast.makeText(this@SearchActivity, "未找到相关影片", Toast.LENGTH_SHORT).show()
                        }
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
                    Toast.makeText(this@SearchActivity, "网络错误: $msg", Toast.LENGTH_SHORT).show()
                    if (isRefresh) refreshLayout.finishRefresh(false)
                    else refreshLayout.finishLoadMore(false)
                }
            }
        })
    }
}