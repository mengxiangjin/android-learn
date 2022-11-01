package com.jin.learn

import android.content.Intent
import android.os.Bundle
import com.jin.learn.base.BaseActivity
import com.jin.learn.broadcast.ForceOfflineBroadcast
import com.jin.learn.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.finish.setOnClickListener {
            val intent = Intent(ForceOfflineBroadcast.ACTION)
            intent.setPackage(packageName)
            sendBroadcast(intent)
        }
    }
}