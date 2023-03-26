package com.jin.navigation.fragment

import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.jin.navigation.R
import com.jin.navigation.base.BaseFragment

class RegisterFragment: BaseFragment() {
    override fun getResourceId(): Int {
        return R.layout.fragment_register
    }

    override fun initView(root: View) {
        super.initView(root)
        root.findViewById<Button>(R.id.to_avatar_verify).setOnClickListener {
            findNavController().navigate(R.id.to_avatar_verify_fragment)
        }
        val name = arguments?.getString("name")
        root.findViewById<EditText>(R.id.register_name).setText(name)


    }

}