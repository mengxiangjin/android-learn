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
            val intent = Intent()
            intent.putExtra("age",back.text)
            setResult(200,intent)
        }
    }

    fun initView() {
        title = findViewById(R.id.title)
        back = findViewById(R.id.back)
    }
}