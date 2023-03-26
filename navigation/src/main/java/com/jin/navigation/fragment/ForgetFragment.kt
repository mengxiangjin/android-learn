package com.jin.navigation.fragment

import android.view.View
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.jin.navigation.R
import com.jin.navigation.base.BaseFragment

class ForgetFragment: BaseFragment() {
    override fun getResourceId(): Int {
        return R.layout.fragment_forget
    }

    override fun initView(root: View) {
        root.findViewById<Button>(R.id.back).setOnClickListener {
            //都可以进行返回
//            findNavController().popBackStack()
            findNavController().navigateUp()
        }
    }
}