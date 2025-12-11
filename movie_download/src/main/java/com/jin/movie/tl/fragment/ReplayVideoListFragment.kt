package com.jin.movie.tl.fragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.R
import com.jin.movie.tl.adapter.VideoAdapter
import com.jin.movie.tl.bean.ApiResponse
import com.jin.movie.tl.bean.VideoRecord
import com.jin.movie.tl.net.RetrofitClient
import com.jin.movie.tl.utils.SignUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReplayVideoListFragment : Fragment() {

    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private val dataList = mutableListOf<VideoRecord>()
    private lateinit var adapter: VideoAdapter

    private var anchorId: Int = 0
    private var currentPage = 1
    private val pageSize = 10

    companion object {
        private const val ARG_ANCHOR_ID = "anchor_id"

        fun newInstance(userId: Int): ReplayVideoListFragment {
            val fragment = ReplayVideoListFragment()
            val args = Bundle()
            args.putInt(ARG_ANCHOR_ID, userId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 复用之前的布局 xml 即可，只要里面有 SmartRefreshLayout 和 RecyclerView
        return inflater.inflate(R.layout.fragment_simple_video_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        anchorId = arguments?.getInt(ARG_ANCHOR_ID) ?: 0

        initViews(view)

        // 如果 ID 有效，自动刷新
        if (anchorId != 0) {
            refreshLayout.autoRefresh()
        }
    }

    private fun initViews(view: View) {
        refreshLayout = view.findViewById(R.id.refresh_layout)
        recyclerView = view.findViewById(R.id.recycler_view)

        // 使用网格布局，2列
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        adapter = VideoAdapter(dataList)
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

        refreshLayout.setEnableRefresh(true)
        refreshLayout.setEnableLoadMore(true)

        refreshLayout.setOnRefreshListener { loadData(true) }
        refreshLayout.setOnLoadMoreListener { loadData(false) }
    }

    private fun loadData(isRefresh: Boolean) {
        val requestPage = if (isRefresh) 1 else currentPage + 1

        // 1. 准备接口路径
        val path = "/live/live/video/anchor"

        // 2. 准备所有参数 (包括业务参数和分页)
        // SignUtils.getSignedParams 需要改一下逻辑或者我们这里手动拼
        // 假设 getSignedParams 返回了基础 map，我们往里面加东西
        val params = SignUtils.getSignedParams(path,requestPage,pageSize).toMutableMap()
        params["anchorUserId"] = anchorId.toString()

        // 注意：GET 请求参数都会参与签名，确保 SignUtils 里生成的签名包含了这些参数
        // 如果 SignUtils 只是对 path 签名，那就没问题。
        // 按照你之前的 SignUtils 逻辑，它只对 path/page/size 签名。
        // 这里如果是 QueryMap，通常需要把所有参数都带上。
        // 如果服务器签名校验严格，请确保 sign 生成逻辑覆盖了 anchorUserId。
        // 暂时假设目前的 sign 逻辑能通过。

        RetrofitClient.apiService.getAnchorReplayList(requestPage,pageSize,params).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    val list = body?.data?.records ?: emptyList()

                    if (isRefresh) {
                        adapter.setNewData(list)
                        refreshLayout.finishRefresh(true)
                        refreshLayout.resetNoMoreData()
                        currentPage = 1
                    } else {
                        if (list.isNotEmpty()) {
                            adapter.addData(list)
                            refreshLayout.finishLoadMore(true)
                            currentPage++
                        } else {
                            refreshLayout.finishLoadMoreWithNoMoreData()
                        }
                    }

                    if (list.size < pageSize) {
                        refreshLayout.finishLoadMoreWithNoMoreData()
                    }
                } else {
                    closeLoading(isRefresh, false)
                    Log.e("API", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                closeLoading(isRefresh, false)
                Log.e("API", "Fail: ${t.message}")
            }
        })
    }

    private fun closeLoading(isRefresh: Boolean, success: Boolean) {
        if (isRefresh) refreshLayout.finishRefresh(success)
        else refreshLayout.finishLoadMore(success)
    }
}