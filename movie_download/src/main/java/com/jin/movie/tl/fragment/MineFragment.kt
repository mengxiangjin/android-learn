package com.jin.movie.tl.fragment

import android.os.Bundle
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
    }

    private fun initListeners() {
        // 1. 点击关注
        llFollowing.setOnClickListener {
            // TODO: 跳转到“我的关注”列表 Activity
            Toast.makeText(context, "点击了关注列表", Toast.LENGTH_SHORT).show()
            FollowListActivity.start(requireContext(),TYPE_FOLLOW)
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