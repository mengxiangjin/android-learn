package com.jin.learn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jin.learn.databinding.ActivityMainBinding

/*
* launchMode = stander
* */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {
        binding.standard.setOnClickListener {
            /*
            * MainActivity的launchMode为默认标准模式，点击会一直自己创建自己，需要点击多次退出才能退出程序
            * onCreate，onCreate，onCreate，onCreate
            * */
            startActivity(Intent(this,MainActivity::class.java))
        }

        binding.jump.setOnClickListener {
            startActivity(Intent(this,SecondActivity::class.java))
        }
    }
}