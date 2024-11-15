package com.jin.draw

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jin.draw.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            binding.shadowView.setShadow(true)
        }

        binding.btnClear.setOnClickListener {
            binding.shadowView.setShadow(false)
        }
    }
}