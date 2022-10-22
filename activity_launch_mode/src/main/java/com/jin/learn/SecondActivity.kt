package com.jin.learn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jin.learn.databinding.ActivitySecondBinding

/*
* launchMode = singleTop
* */
class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    companion object {
        const val TAG = "SecondActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {
        binding.singleTop.setOnClickListener {
            /*
            * SecondActivity launchMode = singleTop
            * activity回退栈在跳转的时候会查看栈顶是否已经存在需要跳转的activity，若存在，则不会onCreate新的activity，而是会复用之前
            * a -> b -> c -> 跳转到c -> (不会重新走c的onCreate)  --> a,b,c
            * a -> b -> c -> 跳转到b -> (会重新走b的onCreate)  --> a,b,c,b
            * */
            startActivity(Intent(this,SecondActivity::class.java))
        }

        binding.jump.setOnClickListener {
            startActivity(Intent(this,ThirdActivity::class.java))
        }
    }
}