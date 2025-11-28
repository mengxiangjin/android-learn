package com.jin.movie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.activity.PlayerActivity
import com.jin.movie.activity.SearchActivity
import com.jin.movie.adapter.BigCategoryAdapter
import com.jin.movie.adapter.SortAdapter
import com.jin.movie.adapter.SubCategoryAdapter
import com.jin.movie.adapter.VideoAdapter
import com.jin.movie.bean.BigCategory
import com.jin.movie.utils.HtmlParseHelper
import com.jin.movie.utils.NetManager
import com.jin.movie.utils.UIUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.shuyu.gsyvideoplayer.cache.CacheFactory
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.exo2.ExoPlayerCacheManager

class MainActivity : AppCompatActivity() {

    // --- UI 控件 ---
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var rvVideoList: RecyclerView
    private lateinit var rvBigCat: RecyclerView
    private lateinit var tvPageIndicator: TextView
    private lateinit var rvSubCat: RecyclerView
    private lateinit var ivSearch: View
    private lateinit var rvSortTags: RecyclerView // 新增控件

    // 这个变量用来暂存所有的大分类数据，方便切换时查找
    private var allBigCategories: List<BigCategory> = emptyList()

    // --- 适配器 ---
    private lateinit var bigCategoryAdapter: BigCategoryAdapter
    private lateinit var subCategoryAdapter: SubCategoryAdapter
    private lateinit var sortAdapter: SortAdapter

    private lateinit var videoAdapter: VideoAdapter

    // --- 数据状态 ---
    private var currentPage = 1
    private var totalPage = 1
    // 默认首页 URL (后续根据分类点击变化)
    // 假设 ID=1 是默认分类
    private var currentCategoryId = "1"
    private val BASE_URL = "https://www.zimuquan23.uk"
    // 【新增】当前排序参数 (空字符串代表默认/最新，或者 "time", "hits", "up")
    private var currentSortType = "time"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)
        // 2. 【新增】切换缓存模式为 ExoPlayer 专用模式
        // 这个模式对 m3u8 的分片缓存支持更好，拖动过的位置再次拖回去不会卡
        CacheFactory.setCacheManager(ExoPlayerCacheManager::class.java)

        setContentView(R.layout.activity_main)
        UIUtils.setActivityBarStyle(this)

        initViews()
        initAdapters()
        // 首次进入自动刷新，请求数据
        refreshLayout.autoRefresh()
    }

    private fun initViews() {
        refreshLayout = findViewById(R.id.refreshLayout)
        rvVideoList = findViewById(R.id.rv_video_list)
        rvBigCat = findViewById(R.id.rv_big_categories)
        tvPageIndicator = findViewById(R.id.tv_page_indicator)
        rvSubCat = findViewById(R.id.rv_sub_categories)
        ivSearch = findViewById(R.id.iv_search)
        rvSortTags = findViewById(R.id.rv_sort_tags)


        ivSearch.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        // 1. 设置下拉刷新 & 上拉加载
        refreshLayout.setOnRefreshListener {
            if (currentPage > 1) {
                // 如果当前不是第一页，页码减 1 (获取上一页)
                currentPage--
            } else {
                // 如果已经是第一页，保持为 1 (刷新首页)
                currentPage = 1
            }

            // 发起请求
            // isRefresh = true 表示会调用 setNewData() 覆盖旧数据
            // 这样页面内容就会变成上一页的数据

            // 【重要】既然可能是往回翻，说明后面肯定还有数据
            // 所以我们要重置“没有更多数据”的状态，否则上拉加载可能会失效
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

        // 2. 页码点击跳转
        tvPageIndicator.setOnClickListener {
            showJumpDialog()
        }
    }

    private fun initAdapters() {
        // --- 视频列表 (网格布局，一行2个) ---
        videoAdapter = VideoAdapter(mutableListOf()) { video ->
            // 点击视频回调
            // 点击跳转播放
            val playUrl = video.movieUrl // 自动推算的 .m3u8 链接

            PlayerActivity.start(
                context = this,
                url = playUrl,
                title = video.title,
                coverUrl = video.coverUrl
            )
        }
        rvVideoList.layoutManager = GridLayoutManager(this, 2)
        rvVideoList.adapter = videoAdapter

        // --- 大分类列表 ---
        bigCategoryAdapter = BigCategoryAdapter(emptyList()) { category ->
            Log.d("TAG", "initAdapters: " + category.toString())
            // 1. 如果点击的是当前已经选中的，不做重复请求，但可以做滚动
            if (currentCategoryId == category.id) {
                // 即使是已选中，也可以点一下让它滚回到中间（可选）
                val index = allBigCategories.indexOf(category)
                if (index != -1) smoothScrollToCenter(rvBigCat, index)
                return@BigCategoryAdapter
            }

            // 2. 【新增】找到当前点击的位置，并滚动居中
            val index = allBigCategories.indexOf(category)
            if (index != -1) {
                smoothScrollToCenter(rvBigCat, index)
            }


            // 点击分类：更新ID -> 重置页码 -> 触发刷新
            if (currentCategoryId != category.id) {
                Toast.makeText(this, "切换分类: ${category.name}", Toast.LENGTH_SHORT).show()
                handleBigCategoryClick(category)
            }
        }
        rvBigCat.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvBigCat.adapter = bigCategoryAdapter

        // 初始化适配器
        subCategoryAdapter = SubCategoryAdapter(emptyList()) { smallCat ->
            // --- 点击二级分类的回调 ---
            Toast.makeText(this, "点击了子分类: ${smallCat.name}", Toast.LENGTH_SHORT).show()
            Log.d("TAG", "initAdapters: " + smallCat.toString())

            // 点击分类：更新ID -> 重置页码 -> 触发刷新
            if (currentCategoryId != smallCat.id) {
                currentCategoryId = smallCat.id
                // 如果需要从 category.url 里提取纯净的 ID，可以在这里做
                Toast.makeText(this, "切换小分类: ${smallCat.name}", Toast.LENGTH_SHORT).show()
                currentPage = 1
                refreshLayout.autoRefresh() // 自动触发上面的 setOnRefreshListener
            }
        }

        rvSubCat.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvSubCat.adapter = subCategoryAdapter

        // 【新增】监听滚动，实时更新页码显示
        rvVideoList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 1. 获取布局管理器
                val layoutManager = recyclerView.layoutManager as GridLayoutManager

                // 2. 找到屏幕上第一个完全可见的 Item 的位置
                // (也可以用 findFirstVisibleItemPosition，看你喜欢哪种灵敏度)
                val firstPosition = layoutManager.findFirstVisibleItemPosition()

                if (firstPosition != RecyclerView.NO_POSITION) {
                    // 3. 从 Adapter 拿到这个位置的 Video 数据
                    val video = videoAdapter.getItem(firstPosition)

                    // 4. 如果拿到了数据，且它的页码 > 0，就更新右下角的文字
                    if (video != null && video.page > 0) {
                        // 注意：这里只更新文字，不要去修改全局的 currentPage 变量
                        // 因为全局 currentPage 是用来控制"下一次加载第几页"的
                        tvPageIndicator.text = "${video.page} / $totalPage"
                    }
                }
            }
        })

        // --- 【新增】三级分类 (排序) ---
        sortAdapter = SortAdapter(emptyList()) { fixCat ->
            // 点击排序标签的回调
            Toast.makeText(this, "排序: ${fixCat.name}", Toast.LENGTH_SHORT).show()

            // 1. 提取排序参数
            // 假设 fixCat.url 是 "/index.php/vod/show/by/hits/id/1.html"
            // 我们需要提取 "hits"
            val clickSortType = if (fixCat.url.contains("time")) {
                "time"
            } else if (fixCat.url.contains("hits")) {
                "hits"
            } else {
                "up"
            }

            if (currentSortType == clickSortType) {
                //点击的相同的,不进行任何操作
                return@SortAdapter
            }
            currentSortType = clickSortType

            // 2. 重置页码并刷新
            currentPage = 1
            refreshLayout.autoRefresh()
        }
        rvSortTags.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvSortTags.adapter = sortAdapter
    }


    private fun handleBigCategoryClick(category: BigCategory) {
        // 1. 更新二级分类 UI
        subCategoryAdapter.updateData(emptyList())
        sortAdapter.updateData(emptyList())

        // 3. 重置排序状态
        currentSortType = "time"
        sortAdapter.resetSelection() // 让 UI 回到第一个

        // 4. 决定用哪个 ID 去请求数据
        // 优先用第一个子分类的ID，如果没有子分类，用大分类自己的ID
        currentCategoryId = category.id

        // 5. 触发刷新
        currentPage = 1
        refreshLayout.autoRefresh()
    }

    /**
     * 核心网络请求
     */
    private fun fetchData(isRefresh: Boolean) {
        // 拼接 URL
        // 格式参考：https://www.zimuquan23.uk/index.php/vod/show/id/1/page/1.html
        val url = if (currentSortType == "time") {
            "$BASE_URL/index.php/vod/show/id/$currentCategoryId/page/$currentPage.html"
        } else {
            "$BASE_URL/index.php/vod/show/by/${currentSortType}/id/$currentCategoryId/page/$currentPage.html"
        }

        Log.d("MainActivity", "Request URL: $url")

        NetManager.get(url, object : NetManager.Callback {
            override fun onSuccess(response: String) {
                // 1. 解密 HTML
                val html = HtmlParseHelper.decodeHtml(response)

                // 2. 解析不同部分的数据
                // 视频列表
                val videos = HtmlParseHelper.parseVideoList(html)
                // 总页数
                val pageCount = HtmlParseHelper.parseTotalPage(html)

                // 【新增】核心逻辑：给这批新数据的每一个视频，都标记上当前的页码
                // 注意：这里使用的是当前请求的页码 (currentPage)
                videos.forEach { it.page = currentPage }

                // 分类列表 (通常只需要在第一页刷新时更新，或者每次刷新都更新)
                val categories = if (isRefresh) {
                    HtmlParseHelper.parseCategoryList(html)
                } else {
                    emptyList()
                }

                // 3. 回到主线程更新 UI
                runOnUiThread {
                    // 更新页码
                    if (pageCount > 0) totalPage = pageCount
//                    tvPageIndicator.text = "$currentPage / $totalPage"

                    if (isRefresh) {
                        tvPageIndicator.text = "$currentPage / $totalPage"

                        // 如果是刷新，更新分类栏 + 覆盖视频列表
                        if (categories.isNotEmpty()) {
                            allBigCategories = categories // 存下来
                            // 注意：这里可能需要逻辑判断，保留当前选中的分类高亮
                            // 暂时直接更新数据
                            bigCategoryAdapter.updateData(categories)
                            // 2. 【关键】找到当前选中的大分类，把它的子分类给 SubAdapter
                            // 逻辑：找到 ID 匹配的，如果没匹配到就用第一个
                            // B. 找到当前选中的那个大分类
                            // 逻辑：网页返回的 HTML 中，通常 active 的那个 tab 就是当前分类
                            // 你的 HtmlParseHelper 里应该有 isSelected 的判断逻辑
                            val currentActiveCategory = categories.find { it.isSelected }
                                ?: categories.find { it.id == currentCategoryId }

                            // C. 将它的子分类喂给二级适配器
                            if (currentActiveCategory != null) {
                                subCategoryAdapter.updateData(currentActiveCategory.subCategories)
                                sortAdapter.updateData(currentActiveCategory.fixCategories)
                            }
                        }

                        videoAdapter.setNewData(videos)
                        refreshLayout.finishRefresh()
                    } else {
                        // 加载更多，只追加视频
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
                    Toast.makeText(this@MainActivity, "Err: $msg", Toast.LENGTH_SHORT).show()
                    if (isRefresh) refreshLayout.finishRefresh(false)
                    else refreshLayout.finishLoadMore(false)
                }
            }
        })
    }

    /**
     * 将 RecyclerView 的指定 position 滚动到屏幕中间
     */
    private fun smoothScrollToCenter(recyclerView: RecyclerView, position: Int) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val context = recyclerView.context

        // 创建一个自定义的平滑滚动器
        val smoothScroller = object : androidx.recyclerview.widget.LinearSmoothScroller(context) {
            // 核心算法：计算滚动距离，使 Item 居中
            override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
                return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
            }

            // 可选：如果你觉得滚动太慢或太快，可以调整这里 (数值越大越慢)
            // override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            //     return 100f / displayMetrics.densityDpi
            // }
        }

        smoothScroller.targetPosition = position
        layoutManager.startSmoothScroll(smoothScroller)
    }

    // --- 辅助方法：页面跳转弹窗 ---
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

                        rvVideoList.scrollToPosition(0) // 滚回顶部

                        // 如果之前显示了"没有更多数据"，需要重置状态
                        refreshLayout.resetNoMoreData()

                        // 为了有加载动画，我们可以手动把刷新头拉下来（只展示动画，不触发监听）
                        // 或者简单点，直接请求，用户体验差别不大
                        fetchData(isRefresh = true)
                    } else {
                        Toast.makeText(this, "页码超出范围", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
}




