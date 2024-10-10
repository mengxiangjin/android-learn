package com.jin.rv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jin.rv.databinding.ActivityRulerBinding


class RulerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRulerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRulerBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}