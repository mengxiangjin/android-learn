package com.jin.navigation.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import com.jin.navigation.R
import com.jin.navigation.base.BaseFragment

class LoginFragment: BaseFragment() {
    override fun getResourceId(): Int {
        return R.layout.fragment_login
    }

    override fun initView(root: View) {
        super.initView(root)
        root.findViewById<Button>(R.id.to_register_page).setOnClickListener {
            val name = root.findViewById<EditText>(R.id.login_name).text.toString()
            val bundle = Bundle()
            bundle.putString("name",name)
            findNavController().navigate(R.id.to_register_page,bundle)
        }
        root.findViewById<Button>(R.id.to_forget_page).setOnClickListener {
            findNavController().navigate(R.id.to_forget_page)
        }
        //跳转到另一个activity
        root.findViewById<Button>(R.id.to_agreement_page).setOnClickListener {
            findNavController().navigate(R.id.to_agreement_page)
        }

    }
}