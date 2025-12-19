package com.jin.movie.tl.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.R
import com.jin.movie.tl.adapter.AnchorAdapter
import com.jin.movie.tl.bean.AnchorPageResponse
import com.jin.movie.tl.net.RetrofitClient
import com.jin.movie.tl.utils.SignUtils
import com.jin.movie.utils.UIUtils
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowListActivity : AppCompatActivity() {

    companion object {
        const val TYPE_FOLLOW = 0
        const val TYPE_FANS = 1

        fun start(context: Context, type: Int) {
            val intent = Intent(context, FollowListActivity::class.java)
            intent.putExtra("type", type)
            context.startActivity(intent)
        }
    }

    // Views
    private lateinit var tvTitle: TextView
    private lateinit var ivBack: ImageView
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvEmpty: TextView

    // Adapter
    private lateinit var adapter: AnchorAdapter

    // Data
    private var currentType = TYPE_FOLLOW
    private var currentPage = 1
    private val pageSize = 20 // 保持跟你搜索页一样的分页大小

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIUtils.setActivityBarStyle(this)
        setContentView(R.layout.activity_follow_list)

        currentType = intent.getIntExtra("type", TYPE_FOLLOW)

        initViews()

        // 自动刷新加载数据
        refreshLayout.autoRefresh()
    }

    private fun initViews() {
        tvTitle = findViewById(R.id.tv_title)
        ivBack = findViewById(R.id.iv_back)
        refreshLayout = findViewById(R.id.refresh_layout)
        recyclerView = findViewById(R.id.recycler_view)
        tvEmpty = findViewById(R.id.tv_empty)

        // 设置标题
        tvTitle.text = if (currentType == TYPE_FOLLOW) "我的关注" else "我的粉丝"

        ivBack.setOnClickListener { finish() }

        // 初始化 RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = AnchorAdapter(isFromMine = true)
        recyclerView.adapter = adapter

        // 刷新监听
        refreshLayout.setOnRefreshListener { doRequest(true, it) }
        refreshLayout.setOnLoadMoreListener { doRequest(false, it) }
    }

    // 分发请求：根据类型决定调哪个接口
    private fun doRequest(isRefresh: Boolean, layout: RefreshLayout) {
        if (isRefresh) {
            currentPage = 1
            layout.setNoMoreData(false)
        }

        if (currentType == TYPE_FOLLOW) {
            getFollowList(isRefresh, layout)
        } else {
            getFansList(isRefresh, layout)
        }
    }

    // 1. 获取关注列表 (仿照 searchVideos 写法)
    private fun getFollowList(isRefresh: Boolean, layout: RefreshLayout) {
        val apiPath = "/user/follow/followList"
        val queryParams = SignUtils.getSignedParams(apiPath, currentPage, pageSize).toMutableMap()

        RetrofitClient.apiService.getFollowList(currentPage, pageSize, queryParams)
            .enqueue(object : Callback<AnchorPageResponse> {
                override fun onResponse(call: Call<AnchorPageResponse>, response: Response<AnchorPageResponse>) {
                    handleResponse(response, isRefresh, layout)
                }

                override fun onFailure(call: Call<AnchorPageResponse>, t: Throwable) {
                    fail(isRefresh, layout, "网络错误: ${t.message}")
                }
            })
    }

    // 2. 获取粉丝列表 (仿照 searchVideos 写法)
    private fun getFansList(isRefresh: Boolean, layout: RefreshLayout) {
        // TODO: 请确认粉丝列表的 API 路径是否正确
        val apiPath = "/user/follow/fansList"
        val queryParams = SignUtils.getSignedParams(apiPath, currentPage, pageSize).toMutableMap()

        RetrofitClient.apiService.getFansList(currentPage, pageSize, queryParams)
            .enqueue(object : Callback<AnchorPageResponse> {
                override fun onResponse(call: Call<AnchorPageResponse>, response: Response<AnchorPageResponse>) {
                    handleResponse(response, isRefresh, layout)
                }

                override fun onFailure(call: Call<AnchorPageResponse>, t: Throwable) {
                    fail(isRefresh, layout, "网络错误: ${t.message}")
                }
            })
    }

    /**
     * 统一处理返回结果 (避免代码重复)
     */
    private fun handleResponse(response: Response<AnchorPageResponse>, isRefresh: Boolean, layout: RefreshLayout) {
        val result = response.body()
        if (response.isSuccessful && result != null && result.success) {
            // 注意：这里假设 ApiResponse.data.records 也是 AnchorBean 列表
            val records = result.data?.records
            val hasData = !records.isNullOrEmpty()

            if (isRefresh) {
                // 如果是下拉刷新，重置数据
                adapter.setNewData(records ?: emptyList())
                layout.finishRefresh(true)

                // 控制空布局显示
                if (hasData) {
                    tvEmpty.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                } else {
                    tvEmpty.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }
            } else {
                // 如果是加载更多
                if (hasData) {
                    adapter.addData(records!!)
                    layout.finishLoadMore(true)
                } else {
                    layout.finishLoadMoreWithNoMoreData()
                }
            }

            // 如果本次有数据，页码+1
            if (hasData) {
                currentPage++
            }
        } else {
            fail(isRefresh, layout, result?.message ?: "请求失败")
        }
    }

    private fun fail(isRefresh: Boolean, layout: RefreshLayout, msg: String) {
        Log.d("TAG", "fail: " + msg)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        if (isRefresh) layout.finishRefresh(false) else layout.finishLoadMore(false)
    }
}