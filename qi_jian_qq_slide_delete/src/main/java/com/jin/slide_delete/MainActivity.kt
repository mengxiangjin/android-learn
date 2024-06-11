package com.jin.slide_delete

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.jin.slide_delete.adapter.SlideAdapter
import com.jin.slide_delete.databinding.ActivityMainBinding
import com.jin.slide_delete.helper.SlideTouchHelperCallback

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val datas = mutableListOf<String>()
        for (i in 1 until 20) {
            datas.add("这是第${i}个item")
        }

        binding.rvSlide.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvSlide.adapter = SlideAdapter(this,datas)

//        val itemTouchHelper = ItemTouchHelper(SlideTouchHelperCallback())
//        itemTouchHelper.attachToRecyclerView(binding.rvSlide)
    }
}