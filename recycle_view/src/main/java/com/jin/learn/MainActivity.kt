package com.jin.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.jin.learn.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val fruitList = ArrayList<Fruit>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var fruitAdapter: FruitAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initFruits() // 初始化水果数据
        initView()
    }

    private fun initView() {
        fruitAdapter = FruitAdapter(fruitList,this)
        linearLayoutManager = LinearLayoutManager(this)
        binding.recvList.layoutManager = linearLayoutManager
        binding.recvList.adapter = fruitAdapter
    }

    private fun initFruits() {
        fruitList.add(Fruit("Apple", R.drawable.apple_pic))
        fruitList.add(Fruit("Banana", R.drawable.banana_pic))
        fruitList.add(Fruit("Orange", R.drawable.orange_pic))
        fruitList.add(Fruit("Watermelon", R.drawable.watermelon_pic))
        fruitList.add(Fruit("Pear", R.drawable.pear_pic))
        fruitList.add(Fruit("Grape", R.drawable.grape_pic))
        fruitList.add(Fruit("Pineapple", R.drawable.pineapple_pic))
        fruitList.add(Fruit("Strawberry", R.drawable.strawberry_pic))
        fruitList.add(Fruit("Cherry", R.drawable.cherry_pic))
        fruitList.add(Fruit("Mango", R.drawable.mango_pic))
    }
}