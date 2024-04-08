package com.jin.touch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView
import com.jin.touch.adapter.RvAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var rvList: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvList = findViewById(R.id.rv_list)
        val rvAdapter = RvAdapter(this, emptyList())
        val gridManager = GridLayoutManager(this,3)
        gridManager.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                Log.d("lzy", "getSpanSize: " + position)
                return if (position % 2 == 0) {
                    1
                } else {
                    2
                }
            }

        }
        rvList.layoutManager = gridManager
        rvList.adapter = rvAdapter

        val datas = listOf("zero","one","two","three","four","five","six","seven","eight","nine")
        rvAdapter.addDatas(datas)

    }
}