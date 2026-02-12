package com.jin.movie.tl.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jin.movie.R
import com.jin.movie.tl.activity.FollowListActivity
import com.jin.movie.tl.activity.FollowListActivity.Companion.TYPE_FOLLOW
import com.jin.movie.tl.utils.LoginHelper
import com.jin.movie.tl.utils.SignUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Exception

class MineFragment : Fragment() {

    // UI 控件
    private lateinit var tvNickname: TextView
    private lateinit var tvUserId: TextView
    private lateinit var ivAvatar: ImageView
    private lateinit var tvFollowingCount: TextView
    private lateinit var tvFansCount: TextView

    // 按钮区域
    private lateinit var llFollowing: LinearLayout
    private lateinit var llFans: LinearLayout
    private lateinit var cardBanner1: View
    private lateinit var cardBanner2: View
    private lateinit var ivSettings: ImageView
    private lateinit var generateTokenView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tl_mine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initListeners()
    }

    private fun initViews(view: View) {
        tvNickname = view.findViewById(R.id.tv_nickname)
        tvUserId = view.findViewById(R.id.tv_user_id)
        ivAvatar = view.findViewById(R.id.iv_avatar)

        tvFollowingCount = view.findViewById(R.id.tv_following_count)
        tvFansCount = view.findViewById(R.id.tv_fans_count)

        llFollowing = view.findViewById(R.id.ll_following)
        llFans = view.findViewById(R.id.ll_fans)

        cardBanner1 = view.findViewById(R.id.card_banner_1)
        cardBanner2 = view.findViewById(R.id.card_banner_2)
        ivSettings = view.findViewById(R.id.iv_settings)

        generateTokenView = view.findViewById(R.id.btn_generate_token)
    }


    fun testFansListRequest() {
        val client = OkHttpClient()



        // 1. 构建 URL 参数 (注意：要完全匹配你抓包看到的顺序和编码)
        // 这里使用 HttpUrl.Builder 确保路径和参数正确
        val urlBuilder = "https://pro.api.taolu6.cc/user/follow/followList/1/20".toHttpUrlOrNull()!!.newBuilder()
            .addQueryParameter("uid", "218904")
            .addEncodedQueryParameter("systemModel", "Pixel%202%20XL") // 使用 Encoded 保持百分号编码
            .addQueryParameter("appType", "1")
            .addQueryParameter("appVer", "3.9.5.9")
            .addQueryParameter("phoneBrand", "google")
            .addQueryParameter("sign", SignUtils.generateSign("/user/follow/followList",1,20))
            .addQueryParameter("version", "3.9.5.9")
            .addQueryParameter("deviceId", "63bd2e866c6ef324")
            .addQueryParameter("systemVersion", "11")
            .addQueryParameter("versionCode", "20260203")

        // 2. 准备请求头中的 JSON 字符串 (appversion)
        // 注意：这里的 systemModel 是带空格的，不是 %20
        val appVersionJson = """{"uid":"218904","systemModel":"Pixel 2 XL","appType":"1","appVer":"3.9.5.9","phoneBrand":"google","version":"3.9.5.9","deviceId":"63bd2e866c6ef324","systemVersion":"11","versionCode":"20260203"}"""

        // 3. 构建 Request
        val request = Request.Builder()
            .url(urlBuilder.build())
            .addHeader("Connection", "keep-alive")
            .addHeader("Accept", "*/*")
            .addHeader("token", "aiya_41e9d628-aa7a-4eb9-b449-a941e71d26c5ov")
            .addHeader("appversion", appVersionJson)
            .addHeader("versionname", "3.9.5.9")
            .addHeader("versioncode", "20260203")
            .addHeader("clienttype", "Android")
            .addHeader("referer", "https://pro.api.taolu6.cc")
            .addHeader("Accept-Encoding","gzip, deflate, br")
            .addHeader("Accept","*/*")
            .addHeader("User-Agent","")
            .get()
            .build()

        // 4. 发起异步请求
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OkHttp_Test", "请求失败: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val resBody = response.body?.string()
                if (response.isSuccessful) {
                    Log.d("OkHttp_Test", "成功获取数据: $resBody")
                } else {
                    Log.e("OkHttp_Test", "错误代码: ${response.code}, 消息: $resBody")
                }
            }
        })
    }


    private fun initListeners() {
        // 1. 点击关注
        llFollowing.setOnClickListener {
            // TODO: 跳转到“我的关注”列表 Activity
            Toast.makeText(context, "点击了关注列表", Toast.LENGTH_SHORT).show()
            FollowListActivity.start(requireContext(),TYPE_FOLLOW)
//            testFansListRequest()
        }

        // 2. 点击粉丝
        llFans.setOnClickListener {
            // TODO: 跳转到“我的粉丝”列表 Activity
            Toast.makeText(context, "点击了粉丝列表", Toast.LENGTH_SHORT).show()
        }

        // 3. 点击大图 Banner 1
        cardBanner1.setOnClickListener {
            Toast.makeText(context, "点击了推广中心", Toast.LENGTH_SHORT).show()
        }

        // 4. 点击大图 Banner 2
        cardBanner2.setOnClickListener {
            Toast.makeText(context, "点击了会员服务", Toast.LENGTH_SHORT).show()
        }

        // 5. 点击设置
        ivSettings.setOnClickListener {
            Toast.makeText(context, "点击了设置", Toast.LENGTH_SHORT).show()
        }

        // 6. 点击头像/个人信息
        view?.findViewById<View>(R.id.layout_user_info)?.setOnClickListener {
            Toast.makeText(context, "点击编辑资料", Toast.LENGTH_SHORT).show()
        }

        generateTokenView.setOnClickListener {
            LoginHelper.login("15655549539","zj1435145737") {
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * 请求用户信息
     */
//    private fun loadUserInfo() {
//        // 1. 构造参数
//        // 假设 getUserInfo 只需要签名，不需要额外 body (根据你的接口定义)
//        // path 必须和你 Api 中 @GET 的路径一致，注意开头斜杠问题
//        val path = "user/userInfo/getUserInfo"
//        val params = SignUtils.getSignedParams(path, -1, 10).toMutableMap()
//
//        // 2. 发起请求
//        RetrofitClient.apiService.getUserInfo(params).enqueue(object : Callback<UserInfoResponse> {
//            override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
//                if (response.isSuccessful && response.body() != null) {
//                    val result = response.body()!!
//                    if (result.success && result.data != null) {
//                        updateUI(result.data)
//                    } else {
//                        // 登录失效或失败
//                        tvNickname.text = "点击登录"
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
//                Toast.makeText(context, "网络错误", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun updateUI(user: UserInfoResponse.UserData) { // 假设你的 Bean 里有 UserData
//        // 设置文本
//        tvNickname.text = user.nickname ?: "未知用户"
//        tvUserId.text = "ID: ${user.userId ?: "--"}"
//
//        // 设置数字
//        tvFollowingCount.text = (user.followCount ?: 0).toString()
//        tvFansCount.text = (user.fansCount ?: 0).toString()
//
//        // 加载头像 (建议使用 Glide)
//        if (!user.avatar.isNullOrEmpty()) {
//            Glide.with(this)
//                .load(user.avatar)
//                .placeholder(R.drawable.ic_launcher_background) // 占位图
//                .circleCrop()
//                .into(ivAvatar)
//        }
//    }
}