package com.jin.movie.fragment



import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.jin.movie.R
import com.jin.movie.activity.PlayerActivity
import com.jin.movie.adapter.VideoAdapter
import com.jin.movie.utils.HtmlParseHelper
import com.jin.movie.utils.NetManager
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class RankFragment : Fragment() {

    private lateinit var tabLayout: TabLayout
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var rvList: RecyclerView
    private lateinit var videoAdapter: VideoAdapter

    private var currentPage = 1
    // 默认选中 "日榜" -> hits_day
    private var currentSortType = "hits_day"

    private val BASE_URL = "https://www.zimuquan23.uk"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 布局文件沿用 fragment_rank.xml
        return inflater.inflate(R.layout.fragment_rank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupTabs()
        // 首次自动刷新
        refreshLayout.autoRefresh()
    }

    private fun initViews(view: View) {
        tabLayout = view.findViewById(R.id.tab_layout)
        refreshLayout = view.findViewById(R.id.refreshLayout)
        rvList = view.findViewById(R.id.rv_rank_list)

        // 初始化适配器
        videoAdapter = VideoAdapter(mutableListOf()) { video ->
            PlayerActivity.start(requireContext(), video.movieUrl, video.title, video.coverUrl)
        }

        // 排行榜通常用 GridLayoutManager (2列)
        rvList.layoutManager = GridLayoutManager(requireContext(), 2)
        rvList.adapter = videoAdapter

        refreshLayout.setOnRefreshListener {
            currentPage = 1
            fetchData(isRefresh = true)
        }

        refreshLayout.setOnLoadMoreListener {
            currentPage++
            fetchData(isRefresh = false)
        }
    }

    private fun setupTabs() {
        // 根据你提供的 HTML 配置 Tab
        val tabs = listOf(
            "日榜" to "hits_day",
            "周榜" to "hits_week",
            "月榜" to "hits_month",
            "总榜" to "hits"
        )

        // 清空旧 Tab (防止重建 Fragment 时重复添加)
        tabLayout.removeAllTabs()

        tabs.forEach { (name, type) ->
            val tab = tabLayout.newTab().setText(name)
            tab.tag = type // 把 hits_day 等参数存在 tag 里
            tabLayout.addTab(tab)
        }

        // 监听点击
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val sortType = tab?.tag as? String ?: "hits_day"
                if (currentSortType != sortType) {
                    currentSortType = sortType
                    currentPage = 1

                    // 切换 Tab 时，先清空列表，给用户一种正在切换的感觉
                    videoAdapter.setNewData(emptyList())
                    refreshLayout.autoRefresh()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun fetchData(isRefresh: Boolean) {
        // 【核心修正】URL 拼接规则
        // 根据 HTML: /index.php/label/rank/by/hits_day.html
        // 推测分页: /index.php/label/rank/by/hits_day/page/2.html
        val url = "$BASE_URL/index.php/label/rank/by/$currentSortType/page/$currentPage.html"

        Log.d("RankFragment", "Requesting: $url")

        NetManager.get(url, object : NetManager.Callback {
            override fun onSuccess(response: String) {
                // 1. 解密
                val html = HtmlParseHelper.decodeHtml(response)

                // 2. 解析 (假设排行榜页面的视频列表结构和首页一样)
                // 如果排行榜页面没有显示图片，或者结构不同，这里可能会解析为空
                val videos = HtmlParseHelper.parseVideoList(html,HtmlParseHelper.ParseType.RANK)

                // 记录页码
                videos.forEach { it.page = currentPage }

                activity?.runOnUiThread {
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
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "获取失败: $msg", Toast.LENGTH_SHORT).show()
                    if (isRefresh) refreshLayout.finishRefresh(false)
                    else refreshLayout.finishLoadMore(false)
                }
            }
        })
    }
}