package com.jin.movie.tl.activity

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.R
import com.jin.movie.tl.adapter.AnchorAdapter
import com.jin.movie.tl.adapter.VideoAdapter
import com.jin.movie.tl.bean.AnchorResponse
import com.jin.movie.tl.bean.ApiResponse
import com.jin.movie.tl.net.RetrofitClient
import com.jin.movie.tl.utils.SignUtils
import com.jin.movie.utils.UIUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    // Views
    private lateinit var etSearch: EditText
    private lateinit var tvSearchBtn: TextView
    private lateinit var tvSearchType: TextView
    private lateinit var ivBack: ImageView
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView

    // Adapters
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var anchorAdapter: AnchorAdapter

    // State
    private var isSearchVideo = true // true: 搜视频, false: 搜主播
    private var currentPage = 1
    private val pageSize = 20
    private var currentKeyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tl_activity_search) // 确保这里是你修改后的布局文件名
        UIUtils.setActivityBarStyle(this)

        initViews()
        initListener()

        // 默认初始化为视频模式
        switchSearchMode(true)
    }

    private fun initViews() {
        etSearch = findViewById(R.id.et_search)
        tvSearchBtn = findViewById(R.id.tv_search_btn)
        tvSearchType = findViewById(R.id.tv_search_type)
        ivBack = findViewById(R.id.iv_back)
        refreshLayout = findViewById(R.id.refresh_layout)
        recyclerView = findViewById(R.id.rv_search_result)
        tvEmpty = findViewById(R.id.tv_empty)

        // 初始化两个 Adapter
        videoAdapter = VideoAdapter()
        anchorAdapter = AnchorAdapter()

        // 视频点击事件
        videoAdapter.setOnItemClickListener { item ->
            val finalVideoUrl = if (!item.s3VideoUrl.isNullOrEmpty()) item.s3VideoUrl else item.videoUrl
            val title = item.videoTitle ?: item.descs ?: "未知视频"
            val cover = item.coverImage ?: ""
            if (!finalVideoUrl.isNullOrEmpty()) {
                com.jin.movie.activity.PlayerActivity.start(this, finalVideoUrl, title, cover)
            }
        }
    }

    private fun initListener() {
        ivBack.setOnClickListener { finish() }

        // 1. 类型选择点击事件 (弹出简单的 PopupMenu)
        tvSearchType.setOnClickListener { view ->
            showTypeSelector(view)
        }

        tvSearchBtn.setOnClickListener { performSearch() }

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@setOnEditorActionListener true
            }
            false
        }

        refreshLayout.setOnRefreshListener { doSearchRequest(true, it) }
        refreshLayout.setOnLoadMoreListener { doSearchRequest(false, it) }
    }

    /**
     * 显示选择类型的弹窗
     */
    private fun showTypeSelector(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menu.add(0, 0, 0, "搜视频")
        popup.menu.add(0, 1, 1, "搜主播")

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                0 -> switchSearchMode(true)
                1 -> switchSearchMode(false)
            }
            true
        }
        popup.show()
    }

    /**
     * 切换搜索模式（视频 <-> 主播）
     */
    private fun switchSearchMode(isVideo: Boolean) {
        isSearchVideo = isVideo
        tvSearchType.text = if (isVideo) "视频 ▼" else "主播 ▼"

        // 切换 LayoutManager 和 Adapter
        if (isVideo) {
            recyclerView.layoutManager = GridLayoutManager(this, 2)
            recyclerView.adapter = videoAdapter
            etSearch.hint = "搜索视频内容..."
            // 恢复视频数据或者清空
            if (videoAdapter.itemCount == 0 && currentKeyword.isNotEmpty()) {
                refreshLayout.autoRefresh() // 如果没数据且有词，自动刷
            }
        } else {
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = anchorAdapter
            etSearch.hint = "搜索主播昵称..."
            // 恢复主播数据或者清空
            if (anchorAdapter.itemCount == 0 && currentKeyword.isNotEmpty()) {
                refreshLayout.autoRefresh()
            }
        }
    }

    private fun performSearch() {
        val keyword = etSearch.text.toString().trim()
        if (TextUtils.isEmpty(keyword)) {
            Toast.makeText(this, "请输入搜索内容", Toast.LENGTH_SHORT).show()
            return
        }
        currentKeyword = keyword
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(etSearch.windowToken, 0)
        refreshLayout.autoRefresh()
    }

    private fun doSearchRequest(isRefresh: Boolean, layout: RefreshLayout) {
        if (isRefresh) {
            currentPage = 1
            layout.setNoMoreData(false)
        }

        if (isSearchVideo) {
            searchVideos(isRefresh, layout)
        } else {
            searchAnchors(isRefresh, layout)
        }
    }

    // ================== 视频搜索逻辑 (原有逻辑) ==================
    private fun searchVideos(isRefresh: Boolean, layout: RefreshLayout) {
        val apiPath = "/live/live/video/list"
        val queryParams = SignUtils.getSignedParams(apiPath, currentPage, pageSize).toMutableMap()
        val bodyMap = mapOf("videoTitle" to currentKeyword)

        RetrofitClient.apiService.searchVideos(currentPage, pageSize, queryParams, bodyMap)
            .enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    val result = response.body()
                    if (response.isSuccessful && result != null && result.success) {
                        val records = result.data.records
                        val hasData = records.isNotEmpty()

                        if (isRefresh) {
                            videoAdapter.setNewData(records)
                            layout.finishRefresh(true)
                            updateEmptyView(hasData)
                        } else {
                            if (hasData) {
                                videoAdapter.addData(records)
                                layout.finishLoadMore(true)
                            } else {
                                layout.finishLoadMoreWithNoMoreData()
                            }
                        }
                        if (hasData) currentPage++
                    } else {
                        fail(isRefresh, layout, result?.message ?: "请求失败")
                    }
                }
                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    fail(isRefresh, layout, "网络错误: ${t.message}")
                }
            })
    }

    // ================== 【新增】主播搜索逻辑 ==================
    private fun searchAnchors(isRefresh: Boolean, layout: RefreshLayout) {
        // 主播接口似乎没有分页参数，但为了统一逻辑，我们只在刷新时请求
        if (!isRefresh) {
            layout.finishLoadMoreWithNoMoreData()
            return
        }

        val apiPath = "/user/user/list"
        // 假设 sign 逻辑通用，如果不通用去掉 queryParams
        val queryParams = SignUtils.getSignedParams(apiPath, -1).toMutableMap()

        // 关键：构建 Body 参数
        val bodyMap = mapOf("nickName" to currentKeyword)

        // 注意：你需要去 ApiService 加一个 searchAnchors 方法
        RetrofitClient.apiService.searchAnchors(queryParams, bodyMap)
            .enqueue(object : Callback<AnchorResponse> {
                override fun onResponse(call: Call<AnchorResponse>, response: Response<AnchorResponse>) {
                    val result = response.body()
                    if (response.isSuccessful && result != null && result.success) {
                        val list = result.data ?: emptyList()

                        anchorAdapter.setNewData(list)
                        layout.finishRefresh(true)
                        layout.finishLoadMoreWithNoMoreData() // 主播接口一次性返回，没有更多了

                        updateEmptyView(list.isNotEmpty())
                    } else {
                        fail(isRefresh, layout, result?.message ?: "请求失败")
                    }
                }

                override fun onFailure(call: Call<AnchorResponse>, t: Throwable) {
                    fail(isRefresh, layout, "网络错误: ${t.message}")
                }
            })
    }

    private fun updateEmptyView(hasData: Boolean) {
        tvEmpty.visibility = if (hasData) View.GONE else View.VISIBLE
        recyclerView.visibility = if (hasData) View.VISIBLE else View.GONE
    }

    private fun fail(isRefresh: Boolean, layout: RefreshLayout, msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        if (isRefresh) layout.finishRefresh(false) else layout.finishLoadMore(false)
    }
}