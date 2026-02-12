package com.jin.movie.dog.activity

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.util.Util
import com.jin.movie.R
import com.jin.movie.activity.PlayerActivity
import com.jin.movie.adapter.VideoAdapter
import com.jin.movie.utils.HtmlParseHelper
import com.jin.movie.utils.NetManager
import com.jin.movie.utils.UIUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import jp.wasabeef.glide.transformations.internal.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class DogSearchActivity : AppCompatActivity() {

    // --- UI 组件 ---
    private lateinit var etSearch: EditText
    private lateinit var tvSearchBtn: TextView
    private lateinit var ivBack: android.view.View
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var rvSearchList: RecyclerView
    private lateinit var tvPageIndicator: TextView

    // 排序 Tabs
    private lateinit var tvTabNew: TextView
    private lateinit var tvTabViews: TextView
    private lateinit var tvTabCollect: TextView

    // --- 数据变量 ---
    private lateinit var videoAdapter: VideoAdapter
    private val BASE_URL = "https://taolu.dog" // 你的域名

    // 状态
    private var currentKeyword = ""
    private var currentPage = 1
    private var totalPage = 1

    // 排序参数：这里假设搜索接口也支持排序
    // 通常 CMS 搜索排序参数可能是: time(时间), hits(人气), score(评分/收藏)
    private var currentSortType = "new"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIUtils.setActivityBarStyle(this)
        setContentView(R.layout.dog_activity_search) // 确保这里是你刚才发的 xml 文件名

        initViews()
        initAdapter()
    }

    private fun initViews() {
        // 绑定控件
        etSearch = findViewById(R.id.et_search)
        tvSearchBtn = findViewById(R.id.tv_search_btn)
        ivBack = findViewById(R.id.iv_back)
        refreshLayout = findViewById(R.id.refreshLayout)
        rvSearchList = findViewById(R.id.rv_search_list)
        tvPageIndicator = findViewById(R.id.tv_page_indicator)

        tvTabNew = findViewById(R.id.tv_tab_new)
        tvTabViews = findViewById(R.id.tv_tab_views)
        tvTabCollect = findViewById(R.id.tv_tab_collect)

        // 1. 返回事件
        ivBack.setOnClickListener { finish() }

        // 2. 搜索点击事件
        tvSearchBtn.setOnClickListener {
            performSearch()
        }

        // 3. 软键盘搜索键监听
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@setOnEditorActionListener true
            }
            false
        }

        // 4. 排序切换事件
        tvTabNew.setOnClickListener { switchSort("new") }
        tvTabViews.setOnClickListener { switchSort("hot") }
        tvTabCollect.setOnClickListener { switchSort("favorites") }

        // 5. 刷新加载监听
        refreshLayout.setOnRefreshListener {
            currentPage = 1
            fetchSearchData(isRefresh = true)
        }
        refreshLayout.setOnLoadMoreListener {
            if (currentPage < totalPage) {
                currentPage++
                fetchSearchData(isRefresh = false)
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData()
            }
        }

        // 6. 页码点击跳转
        tvPageIndicator.setOnClickListener { showJumpDialog() }
    }

    private fun initAdapter() {
        videoAdapter = VideoAdapter(mutableListOf()) { video ->
            // --- 点击视频逻辑 (复用 HomeFragment 的防盗链解析) ---
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
                    PlayerActivity.start(this@DogSearchActivity, finalUrl, video.title, video.coverUrl)
                }
            }
        }

        rvSearchList.layoutManager = GridLayoutManager(this, 2)
        rvSearchList.adapter = videoAdapter

        // 滚动监听更新页码显示
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
    }

    /**
     * 执行搜索前的准备
     */
    private fun performSearch() {
        val keyword = etSearch.text.toString().trim()
        if (TextUtils.isEmpty(keyword)) {
            Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show()
            return
        }

        currentKeyword = keyword
        currentPage = 1

        // 收起键盘
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)

        // 显示页码指示器
        tvPageIndicator.visibility = android.view.View.VISIBLE

        // 触发自动刷新（会调用 OnRefreshListener）
        refreshLayout.autoRefresh()
    }

    /**
     * 切换排序
     */
    private fun switchSort(sortType: String) {
        if (currentSortType == sortType) return
        if (TextUtils.isEmpty(currentKeyword)) {
            Toast.makeText(this, "请先搜索内容", Toast.LENGTH_SHORT).show()
            return
        }

        currentSortType = sortType
        updateSortUI()

        // 重置页码并刷新
        currentPage = 1
        refreshLayout.autoRefresh()
    }

    private fun updateSortUI() {
        val selectedColor = Color.parseColor("#FF6699")
        val unSelectedColor = Color.parseColor("#999999")

        tvTabNew.setTextColor(if (currentSortType == "new") selectedColor else unSelectedColor)
        tvTabNew.paint.isFakeBoldText = (currentSortType == "new")

        tvTabViews.setTextColor(if (currentSortType == "hot") selectedColor else unSelectedColor)
        tvTabViews.paint.isFakeBoldText = (currentSortType == "hot")

        tvTabCollect.setTextColor(if (currentSortType == "favorites") selectedColor else unSelectedColor)
        tvTabCollect.paint.isFakeBoldText = (currentSortType == "favorites")
    }

    /**
     * 核心：网络请求
     */
    private fun fetchSearchData(isRefresh: Boolean) {
        if (TextUtils.isEmpty(currentKeyword)) {
            refreshLayout.finishRefresh()
            return
        }

        // 关键词 URL 编码
        val encodedKeyword = try {
            URLEncoder.encode(currentKeyword, "UTF-8")
        } catch (e: Exception) {
            currentKeyword
        }

        // ⚠️ 注意：这里需要根据你实际抓包的搜索接口修改 URL 结构
        // 假设格式为： /search?wd=关键词&order=排序&page=页码
        // 有些网站搜索可能是： /vodsearch/关键词----------页码---.html
        // 下面是通用参数拼法，如果不通，请检查抓包结果
        val url = "$BASE_URL/search/$encodedKeyword?sort=$currentSortType&page=$currentPage"

        // 或者如果是伪静态，可能是这样，你需要根据实际情况二选一：
        // val url = "$BASE_URL/vodsearch/$encodedKeyword----------$currentPage---.html"

        NetManager.get(url, object : NetManager.Callback {
            override fun onSuccess(response: String) {
                // 复用解析类
                val videos = HtmlParseHelper.parseDogVideoList(response)
                val pageCount = HtmlParseHelper.parseDogTotalPage(response)

                videos.forEach { it.page = currentPage }

                runOnUiThread {
                    if (pageCount > 0) totalPage = pageCount

                    if (isRefresh) {
                        tvPageIndicator.text = "$currentPage / $totalPage"
                        if (videos.isEmpty()) {
                            Toast.makeText(this@DogSearchActivity, "未找到相关影片", Toast.LENGTH_SHORT).show()
                        }
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
                    Toast.makeText(this@DogSearchActivity, "搜索失败: $msg", Toast.LENGTH_SHORT).show()
                    if (isRefresh) refreshLayout.finishRefresh(false)
                    else refreshLayout.finishLoadMore(false)
                }
            }
        })
    }

    // --- 辅助方法 ---

    private fun showJumpDialog() {
        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        input.hint = "1 - $totalPage"
        input.gravity = Gravity.CENTER

        AlertDialog.Builder(this)
            .setTitle("跳转到页码")
            .setView(input)
            .setPositiveButton("Go") { _, _ ->
                val pageStr = input.text.toString()
                if (pageStr.isNotEmpty()) {
                    val page = pageStr.toInt()
                    if (page in 1..totalPage) {
                        currentPage = page
                        rvSearchList.scrollToPosition(0)
                        refreshLayout.resetNoMoreData()
                        // 隐藏键盘
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(input.windowToken, 0)

                        refreshLayout.autoRefresh() // 触发刷新加载新页
                    } else {
                        Toast.makeText(this, "页码超出范围", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    /**
     * 获取重定向地址 (子线程调用)
     */
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
            throw e
        } finally {
            conn?.disconnect()
        }
        return realUrl
    }
}