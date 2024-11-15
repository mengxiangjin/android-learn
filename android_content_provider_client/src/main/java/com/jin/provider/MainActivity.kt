package com.jin.provider

import android.content.ContentValues
import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.jin.provider.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.insert.setOnClickListener {
            val name = binding.etName.text.toString()
            if (name.isNotBlank()) {
                insertData(name)
            }
        }

        var uri = Uri.parse("content://com.jin.learn.mycontentprovider/user")
        contentResolver.registerContentObserver(uri,true,object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                Toast.makeText(this@MainActivity,"onChange1",Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun insertData(name: String) {
        var uri = Uri.parse("content://com.jin.learn.mycontentprovider/user")
        val contentValue = ContentValues()
        contentValue.put("name",name)
        var newDataUri = contentResolver.insert(uri, contentValue)
        if (newDataUri == null) {
            Toast.makeText(this,"插入失败",Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this,"插入成功",Toast.LENGTH_LONG).show()
        }
    }
}