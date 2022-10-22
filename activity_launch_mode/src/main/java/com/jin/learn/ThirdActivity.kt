package com.jin.learn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jin.learn.databinding.ActivityThirdBinding


/*
* launchMode = singleTask
* */
class ThirdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThirdBinding
    
    companion object {
        const val TAG  ="ThirdActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThirdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(TAG, "onCreate: ")
        initListener()
    }

    private fun initListener() {
        binding.singleTask.setOnClickListener {
            /*
            * ThirdActivity launchMode = singleTask
            * activity回退栈在跳转的时候会查看栈(非栈顶)是否已经存在需要跳转的activity，若存在，则弹出所在activity上面的activity
            * a -> b -> c -> 跳转到c -> (不会重新走c的onCreate)  --> a,b,c
            * a -> b -> c -> d -> 跳转到c -> (d会出栈)  --> a,b,c
            * */
            startActivity(Intent(this,SecondActivity::class.java))
        }
        binding.jump.setOnClickListener {
            startActivity(Intent(this,FourthActivity::class.java))
        }
    }
}