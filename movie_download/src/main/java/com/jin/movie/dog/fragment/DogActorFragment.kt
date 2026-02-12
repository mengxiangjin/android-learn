package com.jin.movie.dog.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.R
import com.jin.movie.adapter.ActorAdapter
import com.jin.movie.dog.activity.DogActorDetailActivity
import com.jin.movie.utils.HtmlParseHelper
import com.jin.movie.utils.NetManager
import com.scwang.smart.refresh.layout.SmartRefreshLayout

class DogActorFragment : Fragment() {

    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var rvActorList: RecyclerView
    private lateinit var tvPageIndicator: TextView

    private lateinit var adapter: ActorAdapter

    // 数据状态
    private var currentPage = 1
    private var totalPage = 1

    // ⚠️ 请修改为实际的演员列表基础 URL
    private val BASE_ACTOR_URL = "https://taolu.dog/mistress"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dog_actor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)

        // 首次自动刷新
        refreshLayout.autoRefresh()
    }

    private fun initViews(view: View) {
        refreshLayout = view.findViewById(R.id.refreshLayout)
        rvActorList = view.findViewById(R.id.rv_actor_list)
        tvPageIndicator = view.findViewById(R.id.tv_page_indicator)

        // 初始化 Adapter，设置为 3 列网格
        adapter = ActorAdapter(mutableListOf()) { actor ->
            // 点击事件：跳转到演员详情页（通常是一个只显示该演员视频的搜索页或专题页）
            // 你可以复用 SearchActivity 并传入特定参数，或者新建一个 ActorDetailActivity
            Toast.makeText(context, "点击了: ${actor.name}", Toast.LENGTH_SHORT).show()
            DogActorDetailActivity.start(requireContext(), actor.detailUrl, actor.name)

            // 示例：跳转到搜索页搜索该演员
            // val intent = Intent(context, DogSearchActivity::class.java)
            // intent.putExtra("KEYWORD", actor.name)
            // startActivity(intent)
        }

        rvActorList.layoutManager = GridLayoutManager(requireContext(), 3) // 3列
        rvActorList.adapter = adapter

        // 刷新/加载更多
        refreshLayout.setOnRefreshListener {
            currentPage = 1
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

        // 页码点击
        tvPageIndicator.setOnClickListener { showJumpDialog() }
    }

    private fun fetchData(isRefresh: Boolean) {
        val url = "$BASE_ACTOR_URL?page=$currentPage"

        NetManager.get(url, object : NetManager.Callback {
            override fun onSuccess(response: String) {
                // 异步解析数据
                Thread {
                    val actors = HtmlParseHelper.parseDogActorList(response)
                    val pageCount = HtmlParseHelper.parseActorTotalPage(response)

                    activity?.runOnUiThread {
                        if (!isAdded) return@runOnUiThread

                        if (pageCount > 0) totalPage = pageCount
                        tvPageIndicator.visibility = View.VISIBLE
                        tvPageIndicator.text = "$currentPage / $totalPage"

                        if (isRefresh) {
                            adapter.setNewData(actors)
                            refreshLayout.finishRefresh()
                        } else {
                            if (actors.isEmpty()) {
                                refreshLayout.finishLoadMoreWithNoMoreData()
                            } else {
                                adapter.addData(actors)
                                refreshLayout.finishLoadMore()
                            }
                        }
                    }
                }.start()
            }

            override fun onError(msg: String) {
                activity?.runOnUiThread {
                    if (!isAdded) return@runOnUiThread
                    Toast.makeText(context, "加载失败: $msg", Toast.LENGTH_SHORT).show()
                    if (isRefresh) refreshLayout.finishRefresh(false)
                    else refreshLayout.finishLoadMore(false)
                }
            }
        })
    }

    // 页码跳转弹窗
    private fun showJumpDialog() {
        val input = EditText(requireContext())
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        input.hint = "1 - $totalPage"
        input.gravity = android.view.Gravity.CENTER

        AlertDialog.Builder(requireContext())
            .setTitle("跳转到页码")
            .setView(input)
            .setPositiveButton("Go") { _, _ ->
                val pageStr = input.text.toString()
                if (pageStr.isNotEmpty()) {
                    val page = pageStr.toInt()
                    if (page in 1..totalPage) {
                        currentPage = page
                        rvActorList.scrollToPosition(0)
                        refreshLayout.resetNoMoreData()
                        refreshLayout.autoRefresh()
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
}