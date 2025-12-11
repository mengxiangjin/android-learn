package com.jin.movie.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jin.movie.R
import com.jin.movie.tl.TaoLuMainActivity

class MineFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 加载上面定义的 fragment_mine.xml
        return inflater.inflate(R.layout.fragment_mine, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. 初始化用户信息区域点击事件
        val userInfoLayout = view.findViewById<View>(R.id.cl_user_info)
        userInfoLayout.setOnClickListener {
            Toast.makeText(context, "点击了个人信息", Toast.LENGTH_SHORT).show()
            // 在这里处理登录逻辑
        }

        // 2. 初始化菜单项
        // 注意：这里需要通过 include 的 id 找到对应的 View，然后再找内部的 TextView 修改文字
        setupMenuItem(view.findViewById(R.id.item_history), "观看历史", android.R.drawable.ic_menu_recent_history)
        setupMenuItem(view.findViewById(R.id.item_favorite), "我的收藏", android.R.drawable.star_off)
        setupMenuItem(view.findViewById(R.id.item_settings), "系统设置", android.R.drawable.ic_menu_manage)
        setupMenuItem(view.findViewById(R.id.item_about), "关于我们", android.R.drawable.ic_menu_info_details)
    }

    // 辅助方法：设置菜单项的文字和图标
    private fun setupMenuItem(menuView: View, title: String, iconResId: Int) {
        val tvName = menuView.findViewById<TextView>(R.id.tv_menu_name)
        val ivIcon = menuView.findViewById<ImageView>(R.id.iv_icon)

        tvName.text = title
        ivIcon.setImageResource(iconResId)

        // 设置点击事件
        menuView.setOnClickListener {
            startActivity(Intent(requireContext(),TaoLuMainActivity::class.java))
            Toast.makeText(context, "点击了: $title", Toast.LENGTH_SHORT).show()
        }
    }
}