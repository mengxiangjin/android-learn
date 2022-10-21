package com.jin.learn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class SecondActivity : AppCompatActivity() {

    private lateinit var title: TextView
    private lateinit var back: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_activity)
        initView()

        var name = intent.getStringExtra("name")
        title.text = name

        back.setOnClickListener {
            //回传数据给上一个activity
            val intent = Intent()
            intent.putExtra("age",18)
            setResult(200,intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("age",68)
        setResult(300,intent)
        //注意此行代码顺序
        super.onBackPressed()
    }

    fun initView() {
        title = findViewById(R.id.title)
        back = findViewById(R.id.back)
    }
}