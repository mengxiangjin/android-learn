package com.jin.movie.fragment


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.R
import com.jin.movie.activity.PlayerActivity
import com.jin.movie.activity.SearchActivity
import com.jin.movie.adapter.BigCategoryAdapter
import com.jin.movie.adapter.SortAdapter
import com.jin.movie.adapter.SubCategoryAdapter
import com.jin.movie.adapter.VideoAdapter
import com.jin.movie.bean.BigCategory
import com.jin.movie.utils.HtmlParseHelper
import com.jin.movie.utils.NetManager
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class HomeFragment : Fragment() {

    // --- UI 控件 ---
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var rvVideoList: RecyclerView
    private lateinit var rvBigCat: RecyclerView
    private lateinit var tvPageIndicator: TextView
    private lateinit var rvSubCat: RecyclerView
    private lateinit var ivSearch: View
    private lateinit var rvSortTags: RecyclerView

    // --- 数据 ---
    private var allBigCategories: List<BigCategory> = emptyList()
    private lateinit var bigCategoryAdapter: BigCategoryAdapter
    private lateinit var subCategoryAdapter: SubCategoryAdapter
    private lateinit var sortAdapter: SortAdapter
    private lateinit var videoAdapter: VideoAdapter

    private var currentPage = 1
    private var totalPage = 1
    private var currentCategoryId = "1"
    private val BASE_URL = "https://www.zimuquan23.uk"
    private var currentSortType = "time"

    // 1. 加载布局
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 注意：这里要确保你有 fragment_home.xml，内容复制你原来的 activity_main.xml 即可
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    // 2. 初始化逻辑
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initAdapters()
        refreshLayout.autoRefresh()
    }

    private fun initViews(view: View) {
        // Fragment 中 findViewById 需要用 view.
        refreshLayout = view.findViewById(R.id.refreshLayout)
        rvVideoList = view.findViewById(R.id.rv_video_list)
        rvBigCat = view.findViewById(R.id.rv_big_categories)
        tvPageIndicator = view.findViewById(R.id.tv_page_indicator)
        rvSubCat = view.findViewById(R.id.rv_sub_categories)
        ivSearch = view.findViewById(R.id.iv_search)
        rvSortTags = view.findViewById(R.id.rv_sort_tags)

        ivSearch.setOnClickListener {
            // Fragment 跳转 Activity，context 使用 requireContext()
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

        refreshLayout.setOnRefreshListener {
            if (currentPage > 1) currentPage-- else currentPage = 1
            refreshLayout.resetNoMoreData()
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

        tvPageIndicator.setOnClickListener {
            showJumpDialog()
        }
    }

    private fun initAdapters() {
        // Context 替换为 requireContext()
        videoAdapter = VideoAdapter(mutableListOf()) { video ->
            val playUrl = video.movieUrl
            PlayerActivity.start(requireContext(), playUrl, video.title, video.coverUrl)
        }
        rvVideoList.layoutManager = GridLayoutManager(requireContext(), 2)
        rvVideoList.adapter = videoAdapter

        bigCategoryAdapter = BigCategoryAdapter(emptyList()) { category ->
            if (currentCategoryId == category.id) {
                val index = allBigCategories.indexOf(category)
                if (index != -1) smoothScrollToCenter(rvBigCat, index)
                return@BigCategoryAdapter
            }
            val index = allBigCategories.indexOf(category)
            if (index != -1) smoothScrollToCenter(rvBigCat, index)

            if (currentCategoryId != category.id) {
                Toast.makeText(requireContext(), "切换分类: ${category.name}", Toast.LENGTH_SHORT).show()
                handleBigCategoryClick(category)
            }
        }
        rvBigCat.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvBigCat.adapter = bigCategoryAdapter

        subCategoryAdapter = SubCategoryAdapter(emptyList()) { smallCat ->
            Toast.makeText(requireContext(), "点击了子分类: ${smallCat.name}", Toast.LENGTH_SHORT).show()
            if (currentCategoryId != smallCat.id) {
                currentCategoryId = smallCat.id
                Toast.makeText(requireContext(), "切换小分类: ${smallCat.name}", Toast.LENGTH_SHORT).show()
                currentPage = 1
                refreshLayout.autoRefresh()
            }
        }
        rvSubCat.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvSubCat.adapter = subCategoryAdapter

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

        sortAdapter = SortAdapter(emptyList()) { fixCat ->
            Toast.makeText(requireContext(), "排序: ${fixCat.name}", Toast.LENGTH_SHORT).show()
            val clickSortType = when {
                fixCat.url.contains("time") -> "time"
                fixCat.url.contains("hits") -> "hits"
                else -> "up"
            }
            if (currentSortType != clickSortType) {
                currentSortType = clickSortType
                currentPage = 1
                refreshLayout.autoRefresh()
            }
        }
        rvSortTags.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvSortTags.adapter = sortAdapter
    }

    private fun handleBigCategoryClick(category: BigCategory) {
        subCategoryAdapter.updateData(emptyList())
        sortAdapter.updateData(emptyList())
        currentSortType = "time"
        sortAdapter.resetSelection()
        currentCategoryId = category.id
        currentPage = 1
        refreshLayout.autoRefresh()
    }

    private fun fetchData(isRefresh: Boolean) {
        val url = if (currentSortType == "time") {
            "$BASE_URL/index.php/vod/show/id/$currentCategoryId/page/$currentPage.html"
        } else {
            "$BASE_URL/index.php/vod/show/by/${currentSortType}/id/$currentCategoryId/page/$currentPage.html"
        }

        NetManager.get(url, object : NetManager.Callback {
            override fun onSuccess(response: String) {
                val html = HtmlParseHelper.decodeHtml(response)
                val videos = HtmlParseHelper.parseVideoList(html)
                val pageCount = HtmlParseHelper.parseTotalPage(html)
                videos.forEach { it.page = currentPage }
                val categories = if (isRefresh) HtmlParseHelper.parseCategoryList(html) else emptyList()

                // Fragment 中切换主线程：activity?.runOnUiThread
                activity?.runOnUiThread {
                    if (pageCount > 0) totalPage = pageCount
                    if (isRefresh) {
                        tvPageIndicator.text = "$currentPage / $totalPage"
                        if (categories.isNotEmpty()) {
                            allBigCategories = categories
                            bigCategoryAdapter.updateData(categories)
                            val currentActiveCategory = categories.find { it.isSelected }
                                ?: categories.find { it.id == currentCategoryId }

                            if (currentActiveCategory != null) {
                                subCategoryAdapter.updateData(currentActiveCategory.subCategories)
                                sortAdapter.updateData(currentActiveCategory.fixCategories)
                            }
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
                activity?.runOnUiThread {
                    Toast.makeText(requireContext(), "Err: $msg", Toast.LENGTH_SHORT).show()
                    if (isRefresh) refreshLayout.finishRefresh(false)
                    else refreshLayout.finishLoadMore(false)
                }
            }
        })
    }

    private fun smoothScrollToCenter(recyclerView: RecyclerView, position: Int) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val context = recyclerView.context // Fragment 中可以直接拿 recyclerView 的 context

        val smoothScroller = object : androidx.recyclerview.widget.LinearSmoothScroller(context) {
            override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
                return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
            }
        }
        smoothScroller.targetPosition = position
        layoutManager.startSmoothScroll(smoothScroller)
    }

    private fun showJumpDialog() {
        val input = EditText(requireContext())
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        input.hint = "1 - $totalPage"
        input.gravity = Gravity.CENTER

        AlertDialog.Builder(requireContext()) // Use requireContext()
            .setTitle("跳转到页码")
            .setView(input)
            .setPositiveButton("Go") { _, _ ->
                val pageStr = input.text.toString()
                if (pageStr.isNotEmpty()) {
                    val page = pageStr.toInt()
                    if (page in 1..totalPage) {
                        currentPage = page
                        rvVideoList.scrollToPosition(0)
                        refreshLayout.resetNoMoreData()
                        fetchData(isRefresh = true)
                    } else {
                        Toast.makeText(requireContext(), "页码超出范围", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
}