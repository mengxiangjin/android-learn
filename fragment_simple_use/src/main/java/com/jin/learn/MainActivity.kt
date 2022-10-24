package com.jin.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.jin.learn.databinding.ActivityMainBinding
import com.jin.learn.fragment.AnotherFragment
import com.jin.learn.fragment.LeftFragment
import com.jin.learn.fragment.RightFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListener()
        //动态添加fragment
        replaceFragment(RightFragment())
    }

    private fun initListener() {
        var fragment = supportFragmentManager.findFragmentById(R.id.left_fragment)
        fragment?.let {
            val leftFragment  = it as LeftFragment
            leftFragment.button?.setOnClickListener {
                replaceFragment(AnotherFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        var transaction = supportFragmentManager.beginTransaction()
        //要替换的布局ID （ViewGroup）
        transaction.replace(R.id.right_fragment,fragment)
        //将此次操作加入到fragment回退栈中(back时是否回退fragment)，参数无意义，一般为null
        transaction.addToBackStack(null)
        transaction.commit()
    }
}