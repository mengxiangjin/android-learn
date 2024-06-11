package com.jin.practice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jin.practice.databinding.ActivityMainBinding
import com.jin.practice.widgit.ArcView

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arcView.startAnim(10000)

    }
}