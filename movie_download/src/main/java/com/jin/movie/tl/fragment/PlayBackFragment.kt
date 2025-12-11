package com.jin.movie.tl.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.jin.movie.R
import com.jin.movie.tl.activity.SearchActivity
import com.jin.movie.tl.adapter.VideoAdapter
import com.jin.movie.tl.bean.ApiResponse
import com.jin.movie.tl.net.RetrofitClient
import com.jin.movie.tl.utils.SignUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlayBackFragment : Fragment() {

    // 控件引用
    private lateinit var mainTabLayout: TabLayout
    private lateinit var subTabLayout: TabLayout
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoAdapter

    // 状态记录
    // 0 = 回放 (Live Playback), 1 = 短剧 (Plot/Skits)
    private var currentMainCategoryIndex = 0
    private var currentSubCategoryKey = "recommend" // 默认排序key

    private var currentPage = 1
    private val pageSize = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_play_back, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        // 初始化数据：选中“回放”，并加载数据
        initMainTabs()
    }

    private fun initViews(view: View) {
        mainTabLayout = view.findViewById(R.id.tab_main_category)
        subTabLayout = view.findViewById(R.id.tab_sub_category)
        refreshLayout = view.findViewById(R.id.refresh_layout)
        recyclerView = view.findViewById(R.id.rv_video_list)

        // 搜索入口
        view.findViewById<View>(R.id.fl_search_entry).setOnClickListener {
            startActivity(Intent(requireContext(),SearchActivity::class.java))
//            Toast.makeText(context, "跳转搜索", Toast.LENGTH_SHORT).show()
        }

        // 筛选入口
        view.findViewById<View>(R.id.iv_filter).setOnClickListener {
            Toast.makeText(context, "点击了筛选", Toast.LENGTH_SHORT).show()
        }

        // 1. RecyclerView 初始化
        val layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.layoutManager = layoutManager
        adapter = VideoAdapter()

        // 点击跳转播放器
        adapter.setOnItemClickListener { item ->
            // 优先使用 S3 线路
            val finalVideoUrl = if (!item.s3VideoUrl.isNullOrEmpty()) {
                item.s3VideoUrl // 这里你原来写的是 item.videoUrl，逻辑上可能有误，已修正为优先取s3
            } else {
                item.videoUrl
            }

            val title = item.videoTitle ?: item.descs ?: "未知视频"
            val cover = item.coverImage ?: ""

            if (!finalVideoUrl.isNullOrEmpty()) {
                com.jin.movie.activity.PlayerActivity.start(requireContext(), finalVideoUrl, title, cover)
            } else {
                Toast.makeText(context, "视频链接无效", Toast.LENGTH_SHORT).show()
            }
        }

        recyclerView.adapter = adapter

        // 2. 刷新加载监听
        refreshLayout.setOnRefreshListener { loadData(isRefresh = true, layout = it) }
        refreshLayout.setOnLoadMoreListener { loadData(isRefresh = false, layout = it) }

        // 3. 一级 Tab 监听 (回放 vs 短剧)
        mainTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentMainCategoryIndex = tab?.position ?: 0
                // 切换大分类时，更新二级 Tab，并自动重置 SubKey
                updateSubTabs(currentMainCategoryIndex)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 4. 二级 Tab 监听
        subTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentSubCategoryKey = tab?.tag as? String ?: "recommend"
                refreshLayout.autoRefresh()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun initMainTabs() {
        mainTabLayout.removeAllTabs()
        mainTabLayout.addTab(mainTabLayout.newTab().setText("回放"))
        mainTabLayout.addTab(mainTabLayout.newTab().setText("短剧"))
        // 默认选中第一个会触发 onTabSelected
    }

    private fun updateSubTabs(mainIndex: Int) {
        subTabLayout.removeAllTabs()

        // 根据大分类定义 Tab 名称和 Key
        val tabs = if (mainIndex == 0) {
            // --- 回放 (Live Playback) ---
            listOf(
                "推荐" to "recommend",
                "最新" to "newest",
                "关注" to "follow",
                "最热" to "hottest",
                "收藏" to "collect",
                "推广" to "promotion"
            )
        } else {
            // --- 短剧 (Plot) ---
            // 注意：这里的 Key 要配合 loadData 里的 when 分支
            listOf(
                "推荐" to "recommend",
                "最新" to "new",       // 对应 API: isRecommend=2
                "关注" to "follow",    // 对应 API: isRecommend=3
                "收藏" to "collect",   // 对应 API: sort=collect
                "推广" to "promotion"  // 对应 API: sort=recommend
            )
        }

        for ((name, key) in tabs) {
            val tab = subTabLayout.newTab()
            tab.text = name
            tab.tag = key
            subTabLayout.addTab(tab)
        }

        // 默认选中第一个，重置 key
        currentSubCategoryKey = tabs.firstOrNull()?.second ?: "recommend"
        refreshLayout.autoRefresh()
    }

    /**
     * 核心网络请求逻辑
     */
    private fun loadData(isRefresh: Boolean, layout: RefreshLayout) {
        if (isRefresh) {
            currentPage = 1
            layout.setNoMoreData(false)
        }

        // === 1. 判断是 [回放] 还是 [短剧] ===
        val isPlayback = (currentMainCategoryIndex == 0)

        // === 2. 确定 API 路径 ===
        val apiPath = if (isPlayback) {
            "/live/live/video/v2/globalRecommend"
        } else {
            "/user/plot/video/list"
        }

        // === 3. 生成签名参数 (通常签名依赖 path) ===
        val queryParams = SignUtils.getSignedParams(apiPath, currentPage, pageSize).toMutableMap()

        // === 4. 构造 Body 参数 (两者完全不同) ===
        val bodyMap: Map<String, Any> = if (isPlayback) {
            // >>> 回放逻辑 (保持原样) <<<
            when (currentSubCategoryKey) {
                "recommend" -> mapOf("recommend" to 1)
                "newest" -> mapOf("recommend" to 2)
                "follow" -> mapOf("recommend" to 3)
                "hottest" -> mapOf("isAsc" to false, "recommend" to 4, "sort" to "heat")
                "collect" -> mapOf("isAsc" to false, "recommend" to 5, "sort" to "collect")
                "promotion" -> mapOf("isAsc" to false, "recommend" to 6, "sort" to "recommend")
                else -> mapOf("recommend" to 0)
            }
        } else {
            // >>> 短剧逻辑 (根据你的新需求) <<<
            when (currentSubCategoryKey) {
                // 推荐: {"isRecommend": "1"}
                "recommend" -> mapOf("isRecommend" to "1")

                // 最新: {"isRecommend": "2"}
                "new" -> mapOf("isRecommend" to "2")

                // 关注: {"isRecommend": "3"}
                "follow" -> mapOf("isRecommend" to "3")

                // 收藏: {"isAsc": false, "sort": "collect"}
                "collect" -> mapOf("isAsc" to false, "sort" to "collect")

                // 推广: {"isAsc": false, "sort": "recommend"}
                "promotion" -> mapOf("isAsc" to false, "sort" to "recommend")

                else -> mapOf()
            }
        }

        // === 5. 发起请求 ===
        // 注意：由于 path 不同，通常 Retrofit 接口也不同。
        // 如果你的 apiService 有两个不同的方法，请在这里进行判断调用。

        val call: Call<ApiResponse> = if (isPlayback) {
            // 回放接口
            RetrofitClient.apiService.getRecommendVideos(currentPage, pageSize, queryParams, bodyMap)
        } else {
            // 短剧接口
            // 【重要】请在你的 ApiService 中添加这个方法，或者确认原方法支持动态 path
            // 假设你的接口定义为: @POST("/user/plot/video/list/") fun getPlotVideos(...)
            RetrofitClient.apiService.getPlotVideos(currentPage, pageSize, queryParams, bodyMap)
        }

        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!
                    if (result.success) {
                        val records = result.data.records
                        val hasData = records.isNotEmpty()

                        if (isRefresh) {
                            adapter.setNewData(records)
                            layout.finishRefresh(true)
                        } else {
                            if (hasData) {
                                adapter.addData(records)
                                layout.finishLoadMore(true)
                            } else {
                                layout.finishLoadMoreWithNoMoreData()
                            }
                        }

                        if (hasData) {
                            currentPage++
                            if (records.size < pageSize) layout.finishLoadMoreWithNoMoreData()
                        }
                    } else {
                        fail(isRefresh, layout, result.message)
                    }
                } else {
                    fail(isRefresh, layout, "Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                fail(isRefresh, layout, "网络错误: ${t.message}")
            }
        })
    }

    private fun fail(isRefresh: Boolean, layout: RefreshLayout, msg: String?) {
        Toast.makeText(context, msg ?: "Error", Toast.LENGTH_SHORT).show()
        if (isRefresh) layout.finishRefresh(false) else layout.finishLoadMore(false)
    }
}