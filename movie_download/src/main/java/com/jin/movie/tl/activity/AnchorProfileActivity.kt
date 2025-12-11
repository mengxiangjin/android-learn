package com.jin.movie.tl.activity



import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.jin.movie.R
import com.jin.movie.tl.bean.AnchorBean
import com.jin.movie.tl.bean.UserInfoData
import com.jin.movie.tl.bean.UserInfoResponse
import com.jin.movie.tl.fragment.ReplayVideoListFragment
import com.jin.movie.tl.fragment.SimpleVideoListFragment
import com.jin.movie.tl.net.RetrofitClient
import com.jin.movie.tl.utils.SignUtils
import com.jin.movie.utils.UIUtils
import jp.wasabeef.glide.transformations.BlurTransformation
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AnchorProfileActivity : AppCompatActivity() {

    // 视图控件
    private lateinit var ivBg: ImageView
    private lateinit var ivAvatar: ImageView
    private lateinit var tvNickname: TextView
    private lateinit var tvIdLoc: TextView
    private lateinit var tvSlogan: TextView
    private lateinit var tvFansCount: TextView
    private lateinit var tvFollowCount: TextView
    private lateinit var ivBack: ImageView

    // 底部栏
    private lateinit var btnFollow: LinearLayout
    private lateinit var ivBottomFollow: ImageView
    private lateinit var tvBottomFollow: TextView

    // 标签页
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    // 数据
    private var anchorId: Int = 0 // 只存个 ID 用来请求
    private var isFollowed = false

    companion object {
        private const val KEY_ANCHOR_DATA = "key_anchor_data"

        fun start(context: Context, anchor: AnchorBean) {
            val intent = Intent(context, AnchorProfileActivity::class.java)
            intent.putExtra(KEY_ANCHOR_DATA, anchor)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anchor_profile)
        UIUtils.setActivityBarStyle(this)

        initViews()
        setupViewPager()

        // 从 Intent 获取数据 (只拿 ID 去请求，不再做预显示)
        val anchorData = intent.getSerializableExtra(KEY_ANCHOR_DATA) as? AnchorBean
        // ---------------------------------------------------------
        // 【关键修复】先把传过来的数据图片显示出来，防止网络请求慢导致白屏
        // ---------------------------------------------------------
        anchorData?.let { anchor ->
            anchorId = anchor.id

            // 1. 先显示 Intent 里带过来的头像
            Glide.with(this)
                .load(anchor.userLogo)
                .placeholder(R.drawable.ic_default_avatar)
                .into(ivAvatar)

            // 2. 先显示 Intent 里带过来的背景 (高斯模糊)
            // 既然刚才这里能显示，说明 anchor.userLogo 是好的
            Glide.with(this)
                .load(anchor.userLogo)
                .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                .into(ivBg)

            // 3. 先显示基本文字
            tvNickname.text = anchor.nickName
            tvIdLoc.text = "ID:${anchor.id}"

            // 4. 发起网络请求 (请求成功后会再次刷新这些 UI)
            fetchUserInfo(anchor.id.toString())
        }
    }

    private fun initViews() {
        ivBg = findViewById(R.id.iv_bg)
        ivAvatar = findViewById(R.id.iv_avatar)
        tvNickname = findViewById(R.id.tv_nickname)
        tvIdLoc = findViewById(R.id.tv_id_loc)
        tvSlogan = findViewById(R.id.tv_slogan)

        tvFansCount = findViewById(R.id.tv_fans_count)
        tvFollowCount = findViewById(R.id.tv_follow_count) // 确保XML里加了ID

        ivBack = findViewById(R.id.iv_back)
        btnFollow = findViewById(R.id.btn_bottom_follow)
        ivBottomFollow = findViewById(R.id.iv_bottom_follow)
        tvBottomFollow = findViewById(R.id.tv_bottom_follow)

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)

        ivBack.setOnClickListener { finish() }

        // 关注按钮点击
        btnFollow.setOnClickListener {
            toggleFollow()
        }
    }

    private fun fetchUserInfo(userId: String) {
        // 1. 准备路径
        val path = "/user/userInfo/getUserInfo"

        // 2. 准备参数 (公共参数 + 签名 + 业务ID)
        val queryParams = SignUtils.getSignedParams(path).toMutableMap()
        queryParams["id"] = userId

        // 3. 发起 GET 请求
        RetrofitClient.apiService.getUserInfo(queryParams).enqueue(object : Callback<UserInfoResponse> {
            override fun onResponse(call: Call<UserInfoResponse>, response: Response<UserInfoResponse>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.success && body.data != null) {
                        // 请求成功，一次性更新所有 UI
                        updateUI(body.data)
                    } else {
                        Log.e("API", "业务报错: ${body?.message}")
                    }
                } else {
                    Log.e("API", "HTTP 失败: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserInfoResponse>, t: Throwable) {
                Log.e("API", "网络异常: ${t.message}")
                Toast.makeText(this@AnchorProfileActivity, "加载失败，请检查网络", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(data: UserInfoData) {
        // 1. 文本信息
        tvNickname.text = data.nickName ?: "未知用户"
        tvSlogan.text = if (data.userSlogan.isNullOrEmpty()) "这家伙很懒..." else data.userSlogan

        // ID 和 城市
        val cityInfo = if (!data.city.isNullOrEmpty()) " | ${data.city}" else ""
        tvIdLoc.text = "ID:${data.userId}$cityInfo"

        // 2. 数据统计
        tvFansCount.text = data.userFans.toString()
        tvFollowCount.text = data.userFollows.toString()

        // 3. 头像
        Glide.with(this)
            .load(data.userLogo)
            .placeholder(R.drawable.ic_default_avatar)
            .into(ivAvatar)

//        // 4. 背景图 (优先用 floatingBackgroundImage，没有则用 userLogo)
//        val bgUrl = if (!data.floatingBackgroundImage.isNullOrEmpty()) {
//            data.floatingBackgroundImage
//        } else {
//            data.userLogo
//        }
//
//        // 加载背景 (带高斯模糊)
//        Glide.with(this)
//            .load(bgUrl)
//            .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
//            .placeholder(R.color.black)
//                .into(ivBg)

        // 5. 关注状态
        isFollowed = (data.followStatus == 1)
        updateFollowUi()

        // 6. 如果你要更新贡献榜头像，可以在这里继续写...
        // if (!data.contributionUserList.isNullOrEmpty()) { ... }
    }

    private fun updateFollowUi() {
        if (isFollowed) {
            ivBottomFollow.setImageResource(R.drawable.ic_heart_filled)
            ivBottomFollow.setColorFilter(android.graphics.Color.parseColor("#FF6699"))
            tvBottomFollow.text = "已关注"
            tvBottomFollow.setTextColor(android.graphics.Color.parseColor("#FF6699"))
        } else {
            ivBottomFollow.setImageResource(R.drawable.ic_heart_outline)
            ivBottomFollow.setColorFilter(android.graphics.Color.parseColor("#CCCCCC"))
            tvBottomFollow.text = "关注"
            tvBottomFollow.setTextColor(android.graphics.Color.parseColor("#CCCCCC"))
        }
    }

    private fun toggleFollow() {
        // TODO: 这里后续接入"关注/取消关注"接口
        isFollowed = !isFollowed
        updateFollowUi()
        val msg = if (isFollowed) "关注成功" else "已取消关注"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun setupViewPager() {
        val tabs = listOf("短视频", "动态", "巡游", "短剧", "回放", "推广")
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = tabs.size
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> SimpleVideoListFragment.newInstance(position, anchorId) // 短视频
                    4 -> ReplayVideoListFragment.newInstance(anchorId) // 【新增】回放
                    else -> SimpleVideoListFragment.newInstance(position, anchorId) // 其他暂时用旧的占位
                }
            }
        }
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabs[position]
        }.attach()
    }
}