package com.jin.movie.tl.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.jin.movie.R
import com.jin.movie.activity.PlayerActivityNew
import com.jin.movie.tl.adapter.VideoAdapter
import com.jin.movie.tl.bean.RankApiResponse
import com.jin.movie.tl.net.RetrofitClient
import com.jin.movie.tl.utils.ApiDecryptor
import com.jin.movie.tl.utils.SignUtils
import com.jin.movie.utils.UIUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RankActivity : AppCompatActivity() {

    // 控件引用
    private lateinit var tabMainCategory: TabLayout
    private lateinit var tabSubCategory: TabLayout
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VideoAdapter

    // 状态记录
    // 大分类: 1=回放榜, 2=短剧榜
    private var currentMainCategory = 1
    // 小分类: 1=日榜, 2=周榜, 3=月榜
    private var currentSubCategory = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rank)
        UIUtils.setActivityBarStyle(this)

        initViews()
        setupMainTabs()
        setupSubTabs()
        refreshLayout.autoRefresh()
    }

    private fun initViews() {
        // 返回按钮
        findViewById<View>(R.id.iv_back).setOnClickListener {
            finish()
        }

        tabMainCategory = findViewById(R.id.tab_main_category)
        tabSubCategory = findViewById(R.id.tab_sub_category)
        refreshLayout = findViewById(R.id.refreshLayout)
        recyclerView = findViewById(R.id.rv_rank_list)

        // 初始化适配器
        adapter = VideoAdapter()
        val layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        // 点击跳转播放器
        adapter.setOnItemClickListener { item ->
            // 优先使用 S3 线路
            val finalVideoUrl = if (!item.s3VideoUrl.isNullOrEmpty()) {
                item.s3VideoUrl
            } else {
                item.videoUrl
            }

            val title = item.videoTitle ?: item.descs ?: "未知视频"
            val cover = item.coverImage ?: ""

            if (!finalVideoUrl.isNullOrEmpty()) {
                val decr_url = "${finalVideoUrl}?sign=${SignUtils.calculateSignature(finalVideoUrl)}"
                PlayerActivityNew.start(this, decr_url, title, cover)
            } else {
                Toast.makeText(this, "视频链接无效", Toast.LENGTH_SHORT).show()
            }
        }

        // 只保留下拉刷新，不加载更多
        refreshLayout.setOnRefreshListener { loadData(layout = it) }
        refreshLayout.setEnableLoadMore(false)
    }

    private fun setupMainTabs() {
        val mainTabs = listOf(
            "回放榜" to 1,
            "短剧榜" to 2
        )

        tabMainCategory.removeAllTabs()
        mainTabs.forEach { (name, type) ->
            val tab = tabMainCategory.newTab().setText(name)
            tab.tag = type
            tabMainCategory.addTab(tab)
        }

        tabMainCategory.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentMainCategory = tab?.tag as? Int ?: 1
                adapter.setNewData(emptyList())
                refreshLayout.autoRefresh()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupSubTabs() {
        val subTabs = listOf(
            "日榜" to 1,
            "周榜" to 2,
            "月榜" to 3
        )

        tabSubCategory.removeAllTabs()
        subTabs.forEach { (name, type) ->
            val tab = tabSubCategory.newTab().setText(name)
            tab.tag = type
            tabSubCategory.addTab(tab)
        }

        tabSubCategory.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentSubCategory = tab?.tag as? Int ?: 1
                adapter.setNewData(emptyList())
                refreshLayout.autoRefresh()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadData(layout: RefreshLayout) {
        // 大分类 + 小分类组合成 rankType
        val apiPath = "/live/video/rank/topics/${currentMainCategory}"
        val queryParams = SignUtils.getSignedParams(apiPath).toMutableMap()

        val call = RetrofitClient.apiService.getRankVideos(
            currentMainCategory,
            queryParams
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    val encryptedBase64 = response.body()!!.string()
                    val result = ApiDecryptor.decryptAndParse<RankApiResponse>(encryptedBase64) ?: return

                    if (result.success) {
                        val records = result.data
                        runOnUiThread {
                            val datas = when(currentSubCategory) {
                                1 -> records.todayRank
                                2 -> records.thisWeekRank
                                3 -> records.thisMonthRank
                                else -> records.todayRank
                            }
                            adapter.setNewData(datas)
                            layout.finishRefresh(true)
                        }
                    } else {
                        fail(layout, result.message)
                    }
                } else {
                    fail(layout, "Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                fail(layout, "网络错误: ${t.message}")
            }
        })
    }

    private fun fail(layout: RefreshLayout, msg: String?) {
        runOnUiThread {
            Toast.makeText(this, msg ?: "Error", Toast.LENGTH_SHORT).show()
            layout.finishRefresh(false)
        }
    }
}
