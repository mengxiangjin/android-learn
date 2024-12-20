package com.jin.scroller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Scroller
import com.jin.scroller.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnScrollTo.setOnClickListener {
            binding.scrollView.scroll(0,100)
        }
        binding.btnScrollBy.setOnClickListener {
            binding.llAction.scrollBy(10,0)
        }
        binding.btnReset.setOnClickListener {
            binding.scrollView.scroll(100,-100)

        }
    }

}