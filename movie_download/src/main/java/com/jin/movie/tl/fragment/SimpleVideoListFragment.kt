package com.jin.movie.tl.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jin.movie.R
import com.jin.movie.tl.adapter.VideoStaggeredAdapter
import com.jin.movie.tl.bean.VideoBean
import com.jin.movie.tl.bean.VideoListResponse
import com.jin.movie.tl.net.RetrofitClient
import com.jin.movie.tl.utils.SignUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SimpleVideoListFragment : Fragment() {

    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private val videoList = mutableListOf<VideoBean>()
    private lateinit var adapter: VideoStaggeredAdapter

    private var tabPosition: Int = 0
    private var anchorId: Int = 0

    // 分页相关
    private var currentPage = 1
    private val pageSize = 10

    companion object {
        private const val ARG_POSITION = "position"
        private const val ARG_ANCHOR_ID = "anchor_id"

        fun newInstance(position: Int, userId: Int): SimpleVideoListFragment {
            val fragment = SimpleVideoListFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            args.putInt(ARG_ANCHOR_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_simple_video_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabPosition = arguments?.getInt(ARG_POSITION) ?: 0
        anchorId = arguments?.getInt(ARG_ANCHOR_ID) ?: 0

        initViews(view)

        // 只有短视频 Tab 且 ID 有效才加载
        if (anchorId != 0 && tabPosition == 0) {
            // 触发自动刷新（会显示下拉动画并调用 onRefresh）
//            refreshLayout.autoRefresh()
             loadData(isRefresh = true)
        } else {
            // 其他 Tab 暂时禁用刷新加载，避免误操作
            refreshLayout.setEnableRefresh(false)
            refreshLayout.setEnableLoadMore(false)
        }
    }

    private fun initViews(view: View) {
        refreshLayout = view.findViewById(R.id.refresh_layout)
        recyclerView = view.findViewById(R.id.recycler_view)

        // 1. 初始化 RecyclerView
        val layoutManager = GridLayoutManager(requireContext(),2)
        recyclerView.layoutManager = layoutManager

        adapter = VideoStaggeredAdapter(videoList)
        recyclerView.adapter = adapter

        // 2. 设置刷新逻辑
        refreshLayout.setEnableRefresh(true) // 启用下拉
        refreshLayout.setEnableLoadMore(true) // 启用上拉

        // 下拉刷新监听
        refreshLayout.setOnRefreshListener {
            loadData(isRefresh = true)
        }

        // 上拉加载监听
        refreshLayout.setOnLoadMoreListener {
            loadData(isRefresh = false)
        }
    }

    /**
     * 加载数据核心逻辑
     * @param isRefresh true=下拉刷新(重置为第一页); false=上拉加载(页码+1)
     */
    private fun loadData(isRefresh: Boolean) {
        val requestPage = if (isRefresh) 1 else currentPage + 1

        val path = "/user/video/list"
        val queryParams = SignUtils.getSignedParams(path,requestPage,pageSize)
        val bodyParams = mapOf("userId" to anchorId.toString())

        RetrofitClient.apiService.getUserVideoList(requestPage, pageSize, queryParams, bodyParams)
            .enqueue(object : Callback<VideoListResponse> {
                override fun onResponse(call: Call<VideoListResponse>, response: Response<VideoListResponse>) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        val list = body?.data?.records ?: emptyList()

                        if (isRefresh) {
                            // 下拉刷新：覆盖数据，允许再次上拉加载
                            adapter.setList(list)
                            refreshLayout.finishRefresh(true) // 刷新成功
                            refreshLayout.resetNoMoreData()   // 重置"没有更多数据"状态
                            currentPage = 1
                        } else {
                            // 上拉加载：追加数据
                            if (list.isNotEmpty()) {
                                adapter.addData(list)
                                refreshLayout.finishLoadMore(true) // 加载成功
                                currentPage++
                            } else {
                                // 如果返回空列表，说明没有更多数据了
                                refreshLayout.finishLoadMoreWithNoMoreData()
                            }
                        }

                        // 额外判断：如果当前返回的数据不足 pageSize，说明已经到底了，直接显示"没有更多"
                        if (list.size < pageSize) {
                            refreshLayout.finishLoadMoreWithNoMoreData()
                        }

                    } else {
                        // 业务失败
                        closeLoadingState(isRefresh, false)
                        Log.e("API", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<VideoListResponse>, t: Throwable) {
                    // 网络失败
                    closeLoadingState(isRefresh, false)
                    Log.e("API", "Failure: ${t.message}")
                }
            })
    }

    // 统一处理结束动画
    private fun closeLoadingState(isRefresh: Boolean, isSuccess: Boolean) {
        if (isRefresh) {
            refreshLayout.finishRefresh(isSuccess)
        } else {
            refreshLayout.finishLoadMore(isSuccess)
        }
    }
}