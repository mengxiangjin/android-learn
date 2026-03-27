package com.jin.movie.dog.fragment


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.R
import com.jin.movie.activity.PlayerActivity
import com.jin.movie.adapter.BigCategoryAdapter
import com.jin.movie.adapter.VideoAdapter
import com.jin.movie.bean.BigCategory
import com.jin.movie.dog.DogMainActivity
import com.jin.movie.dog.activity.DogSearchActivity
import com.jin.movie.utils.HtmlParseHelper
import com.jin.movie.utils.NetManager
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/**
 * 仿照 HomeFragment 风格重写的页面
 * 结构：顶部动态大Tab -> 中间固定3Tab -> 底部视频列表
 */
class DogLocalFragment : Fragment() {

    // --- UI 控件 ---
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var rvVideoList: RecyclerView
    private lateinit var rvDynamicTabs: RecyclerView // 顶部大分类
    private lateinit var tvPageIndicator: TextView
    private lateinit var ivSearch: View

    // 中间固定的三个 Tab
    private lateinit var tvTabNew: TextView
    private lateinit var tvTabViews: TextView
    private lateinit var tvTabCollect: TextView

    // --- 适配器 ---
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var bigCategoryAdapter: BigCategoryAdapter // 复用你现有的

    // --- 数据状态 ---
    private var allCategories: List<BigCategory> = emptyList()
    private var currentPage = 1
    private var totalPage = 1

    // 默认选中的大分类 ID (需要初始化，或者第一次请求后获取)
    private var currentBigCategoryId = "0"

    // 当前排序方式：new=最新(默认), hot=最多观看, favorites=最多收藏
    private var currentSortType = "new"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dog_local, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initAdapters()

        fetchBigCategory()
    }

    private fun initViews(view: View) {
        refreshLayout = view.findViewById(R.id.refreshLayout)
        rvVideoList = view.findViewById(R.id.rv_video_list)
        rvDynamicTabs = view.findViewById(R.id.rv_dynamic_tabs)
        tvPageIndicator = view.findViewById(R.id.tv_page_indicator)
        ivSearch = view.findViewById(R.id.iv_search)

        tvTabNew = view.findViewById(R.id.tv_tab_new)
        tvTabViews = view.findViewById(R.id.tv_tab_views)
        tvTabCollect = view.findViewById(R.id.tv_tab_collect)

        // 搜索点击
        ivSearch.setOnClickListener {
            startActivity(Intent(requireContext(), DogSearchActivity::class.java))
        }

        // --- 中间固定 Tab 点击事件 ---
        tvTabNew.setOnClickListener { switchFixedTab("new") }
        tvTabViews.setOnClickListener { switchFixedTab("hot") }
        tvTabCollect.setOnClickListener { switchFixedTab("favorites") } // 假设收藏是 score 或 up

        tvPageIndicator.setOnClickListener {
            showJumpDialog()
        }

        // --- 刷新/加载 ---
        refreshLayout.setOnRefreshListener {
            if (currentPage > 1) currentPage = 1
            // 注意：刷新时通常不重置分类，只重置页码
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

    private fun initAdapters() {
        // 1. 视频列表适配器
        videoAdapter = VideoAdapter(mutableListOf()) { video ->
            // 1. 先弹出一个加载框，告诉用户“我在干活，别急”
            val loadingDialog = android.app.ProgressDialog(requireContext()).apply {
                setMessage("正在解析视频地址...")
                setCancelable(false) // 解析期间禁止点击其他地方
                show()
            }

            // 2. 启动协程在后台解析地址
            // 注意：这里使用的是 lifecycleScope，需要导入 androidx.lifecycle:lifecycle-runtime-ktx
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                var finalUrl = video.movieUrl

                try {
                    // 定义防盗链 Header（和你播放页里的一样）
                    val headers = mapOf(
                        "User-Agent" to "Mozilla/5.0 (Android) VideoApp/1.0",
//                        "Referer" to "http://MAfAIOo0E8EMOWPA.black"
                    )

                    // 执行网络请求获取重定向地址
                    finalUrl = getRedirectLocation(video.movieUrl, headers)

                } catch (e: Exception) {
                    e.printStackTrace()
                    // 如果解析报错，还是用原地址去碰碰运气，或者在这里提示错误
                    // finalUrl = video.movieUrl
                }

                // 3. 回到主线程跳转
                withContext(Dispatchers.Main) {
                    loadingDialog.dismiss() // 关掉弹窗

                    // 跳转播放页，传入解析好的 finalUrl
                    PlayerActivity.start(requireContext(), finalUrl, video.title, video.coverUrl)
                }
            }

        }
        rvVideoList.layoutManager = GridLayoutManager(requireContext(), 2)
        rvVideoList.adapter = videoAdapter

        // 2. 顶部大分类适配器
        bigCategoryAdapter = BigCategoryAdapter(emptyList()) { category ->
            // 如果点击的是当前选中的，不处理
            if (currentBigCategoryId == category.id) return@BigCategoryAdapter

            // 切换大分类逻辑
            currentBigCategoryId = category.id
            Toast.makeText(requireContext(), "切换: ${category.name}", Toast.LENGTH_SHORT).show()

            // 切换大分类后，通常重置回“最新”，页码归1
            switchFixedTab("new", needRefresh = false) // UI变一下，下面手动刷新
            currentPage = 1
            refreshLayout.autoRefresh()

            // 滚动居中
            smoothScrollToCenter(rvDynamicTabs, allCategories.indexOf(category))
        }
        rvDynamicTabs.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvDynamicTabs.adapter = bigCategoryAdapter

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

    /**
     * 切换中间的固定 Tab (最新/观看/收藏)
     */
    private fun switchFixedTab(sortType: String, needRefresh: Boolean = true) {
        if (currentSortType == sortType && needRefresh) return

        currentSortType = sortType

        // 更新 UI 颜色状态
        updateFixedTabUI()

        if (needRefresh) {
            currentPage = 1
            refreshLayout.autoRefresh()
        }
    }

    private fun updateFixedTabUI() {
        val selectedColor = Color.parseColor("#FF6699")
        val unSelectedColor = Color.parseColor("#999999")

        tvTabNew.setTextColor(if (currentSortType == "new") selectedColor else unSelectedColor)
        tvTabNew.paint.isFakeBoldText = (currentSortType == "new")

        tvTabViews.setTextColor(if (currentSortType == "hot") selectedColor else unSelectedColor)
        tvTabViews.paint.isFakeBoldText = (currentSortType == "hot")

        tvTabCollect.setTextColor(if (currentSortType == "favorites") selectedColor else unSelectedColor)
        tvTabCollect.paint.isFakeBoldText = (currentSortType == "favorites")
    }


    private fun fetchBigCategory() {
        NetManager.get(DogMainActivity.URL_LOCAL, object : NetManager.Callback {
            override fun onSuccess(response: String) {
                // 异步解析
                Thread {
                    // 解析顶部大分类 (仅刷新时解析，避免加载更多时覆盖)
                    val categories = HtmlParseHelper.parseDogCategoryList(response)

                    // UI 更新
                    activity?.runOnUiThread {
                        if (!isAdded) return@runOnUiThread
                        // 更新顶部大分类
                        if (categories.isNotEmpty()) {
                            allCategories = categories
                            bigCategoryAdapter.updateData(categories)
                            // 如果 currentBigCategoryId 为空，默认选中第一个
                            if (currentBigCategoryId == "0" || currentBigCategoryId.isEmpty()) {
                                categories.firstOrNull()?.let {
                                    currentBigCategoryId = it.id
                                    // 选中第一个后，可能需要把UI设为选中状态
                                }
                            }
                        }
                        refreshLayout.autoRefresh()
//                        refreshLayout.finishRefresh(true)

                    }
                }.start()
            }

            override fun onError(msg: String) {
                activity?.runOnUiThread {
                    if (!isAdded) return@runOnUiThread
                    Toast.makeText(context, "Err: $msg", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    /**
     * 核心数据请求
     */
    private fun fetchData(isRefresh: Boolean) {

        var currentSelectedCategory = allCategories.find {
            it.id == currentBigCategoryId
        }?:return

        // 注意：如果大分类ID是0或者空，可能代表首页，URL结构可能不同，需根据实际情况调整
        // 这里假设必定有分类ID
        val url = if (isRefresh) {
            "${currentSelectedCategory.url}?sort=${currentSortType}"
        } else {
            "${currentSelectedCategory.url}?sort=${currentSortType}&page=${currentPage}"
        }

        NetManager.get(url, object : NetManager.Callback {
            override fun onSuccess(response: String) {
                val videos = HtmlParseHelper.parseDogVideoList(response)
                val pageCount = HtmlParseHelper.parseDogTotalPage(response)

                videos.forEach { it.page = currentPage }

                // Fragment 中切换主线程：activity?.runOnUiThread
                activity?.runOnUiThread {
                    if (pageCount > 0) totalPage = pageCount
                    if (isRefresh) {
                        tvPageIndicator.text = "$currentPage / $totalPage"
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
                    if (!isAdded) return@runOnUiThread
                    Toast.makeText(context, "Err: $msg", Toast.LENGTH_SHORT).show()
                    if (isRefresh) refreshLayout.finishRefresh(false)
                    else refreshLayout.finishLoadMore(false)
                }
            }
        })
    }

    // 辅助方法：RecyclerView 滚动居中
    private fun smoothScrollToCenter(recyclerView: RecyclerView, position: Int) {
        if (position == -1) return
        val context = recyclerView.context
        val smoothScroller = object : androidx.recyclerview.widget.LinearSmoothScroller(context) {
            override fun calculateDtToFit(viewStart: Int, viewEnd: Int, boxStart: Int, boxEnd: Int, snapPreference: Int): Int {
                return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
            }
        }
        smoothScroller.targetPosition = position
        recyclerView.layoutManager?.startSmoothScroll(smoothScroller)
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


    /**
     * 获取重定向后的真实地址
     * 必须在子线程 (Dispatchers.IO) 中调用
     */
    private fun getRedirectLocation(originalUrl: String, headers: Map<String, String>): String {
        var realUrl = originalUrl
        var conn: HttpURLConnection? = null
        try {
            val urlObj = URL(originalUrl)
            conn = urlObj.openConnection() as HttpURLConnection

            // 关键设置：不自动跟随重定向，我们自己拿 Location 头
            // 如果设置为 true，HttpURLConnection 会自动跳，但有时候拿不到最终 URL
            conn.instanceFollowRedirects = false
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000 // 5秒超时
            conn.readTimeout = 5000

            // 设置 Header
            for ((key, value) in headers) {
                conn.setRequestProperty(key, value)
            }

            conn.connect()

            val code = conn.responseCode
            // 301, 302, 303, 307 都是重定向
            if (code in 300..399) {
                val location = conn.getHeaderField("Location")
                if (!location.isNullOrEmpty()) {
                    realUrl = location
                }
            }
        } catch (e: Exception) {
            throw e // 抛出异常给外面处理
        } finally {
            conn?.disconnect()
        }
        return realUrl
    }
}