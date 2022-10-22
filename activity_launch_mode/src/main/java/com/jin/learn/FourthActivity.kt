package com.jin.learn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jin.learn.databinding.ActivityFourthBinding
import com.jin.learn.databinding.ActivityMainBinding
import com.jin.learn.databinding.ActivityThirdBinding


/*
* launchMode = singleInstance
*
* 此activity启动会在单独的一个栈中(c为singleInstance)
* a -> b -> c -> d  回退 d -> b -> a -> c    (一个栈回退完,再回退另一个栈)
* */
class FourthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFourthBinding

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFourthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(ThirdActivity.TAG, "onCreate: ")
        initListener()
    }
    private fun initListener() {
        binding.jump.setOnClickListener {
            startActivity(Intent(this,SecondActivity::class.java))
        }
    }

}