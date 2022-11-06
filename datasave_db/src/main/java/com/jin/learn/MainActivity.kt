package com.jin.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jin.learn.databinding.ActivityMainBinding
import com.jin.learn.db.DBHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var dbHelper: DBHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        dbHelper = DBHelper(this,"BookStore.db",1)
        //回调upgrade方法
        dbHelper = DBHelper(this,"BookStore.db",2)
        binding.create.setOnClickListener {
            dbHelper?.writableDatabase
        }
    }
}