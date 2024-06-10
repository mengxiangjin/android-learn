package com.jin.rv

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.jin.rv.databinding.ActivityItemTouchHelperBinding
import com.jin.rv.touchHelper.adapter.RvAdapter
import com.jin.rv.touchHelper.touchHelper.ItemTouchHelperCallback

class RvItemTouchHelperActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemTouchHelperBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemTouchHelperBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rv.layoutManager = layoutManager

        val datas = mutableListOf<String>()
        for (i in 1 until 20) {
            datas.add("这是第${i}个item")
        }
        val adapter = RvAdapter(this,datas)
        binding.rv.adapter = adapter

        val touchHelper = ItemTouchHelper(ItemTouchHelperCallback(datas,adapter))

        adapter.onOperationTouchAction = {
            touchHelper.startSwipe(it)
        }
        touchHelper.attachToRecyclerView(binding.rv)


    }
}