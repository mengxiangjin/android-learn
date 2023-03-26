package com.jin.navigation.fragment

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.jin.navigation.R
import com.jin.navigation.base.BaseFragment

class AvatarVerifyFragment: BaseFragment() {
    override fun getResourceId(): Int {
        return R.layout.fragment_avatar_verify
    }

    override fun initView(root: View) {
        root.findViewById<Button>(R.id.avatarVerifySuccess).setOnClickListener {
            findNavController().navigate(R.id.to_login_page)
        }
    }
}