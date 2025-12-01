package com.jin.movie.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.R
import com.jin.movie.activity.ActorDetailActivity
import com.jin.movie.adapter.ActorAdapter
import com.jin.movie.adapter.SidebarAdapter
import com.jin.movie.utils.HtmlParseHelper
import com.jin.movie.utils.NetManager
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class ActorFragment : Fragment() {

    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var rvList: RecyclerView
    private lateinit var rvSidebar: RecyclerView

    private lateinit var actorAdapter: ActorAdapter
    private lateinit var sidebarAdapter: SidebarAdapter

    private val BASE_URL = "https://www.zimuquan23.uk"

    // 当前选中的字母类型 ("HOT" 代表热门/默认, "A"..."Z")
    private var currentLetter = "HOT"
    private var currentPage = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 使用上面的新布局 fragment_actor.xml
        return inflater.inflate(R.layout.fragment_actor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initSidebar()
        refreshLayout.autoRefresh() // 首次加载
    }

    private fun initViews(view: View) {
        refreshLayout = view.findViewById(R.id.refreshLayout)
        rvList = view.findViewById(R.id.rv_actor_list)
        rvSidebar = view.findViewById(R.id.rv_sidebar)

        // 右侧网格列表
        actorAdapter = ActorAdapter(mutableListOf()) { actor ->
            ActorDetailActivity.start(requireContext(), actor.name, actor.detailUrl)
        }
        rvList.layoutManager = GridLayoutManager(requireContext(), 3) // 3列
        rvList.adapter = actorAdapter

        refreshLayout.setOnRefreshListener {
            currentPage = 1
            fetchData(true)
        }
        refreshLayout.setOnLoadMoreListener {
            currentPage++
            fetchData(false)
        }
    }

    private fun initSidebar() {
        // 构造数据： "热", "A", "B" ... "Z"
        val letters = mutableListOf("热")
        for (c in 'A'..'Z') {
            letters.add(c.toString())
        }

        sidebarAdapter = SidebarAdapter(letters) { letter, position ->
            // 点击侧边栏回调
            sidebarAdapter.setSelected(position)

            // 转换 logic: "热" -> "HOT"
            val newType = if (letter == "热") "HOT" else letter

            if (currentLetter != newType) {
                currentLetter = newType
                currentPage = 1
                actorAdapter.setNewData(emptyList()) // 清空列表，给用户切换感
                refreshLayout.autoRefresh()
            }
        }
        rvSidebar.layoutManager = LinearLayoutManager(requireContext())
        rvSidebar.adapter = sidebarAdapter
    }

    private fun fetchData(isRefresh: Boolean) {
        // 拼接 URL
        val url = if (currentLetter == "HOT") {
            // 热门/默认链接 (你提供的HTML看起来是默认页)
            "$BASE_URL/index.php/actor/index.html"
        } else {
            // 字母筛选链接
            // HTML里是 /index.php/actor/show/letter/A.html
            // 猜测分页是 /index.php/actor/show/letter/A/page/2.html
            "$BASE_URL/index.php/actor/show/letter/$currentLetter/page/$currentPage.html"
        }

        NetManager.get(url, object : NetManager.Callback {
            override fun onSuccess(response: String) {
                val html = HtmlParseHelper.decodeHtml(response)
                val list = HtmlParseHelper.parseActorList(html)

                activity?.runOnUiThread {
                    if (isRefresh) {
                        actorAdapter.setNewData(list)
                        refreshLayout.finishRefresh()
                    } else {
                        if (list.isEmpty()) {
                            refreshLayout.finishLoadMoreWithNoMoreData()
                        } else {
                            actorAdapter.addData(list)
                            refreshLayout.finishLoadMore()
                        }
                    }
                }
            }

            override fun onError(msg: String) {
                activity?.runOnUiThread {
                    if (isRefresh) refreshLayout.finishRefresh(false)
                    else refreshLayout.finishLoadMore(false)
                }
            }
        })
    }
}