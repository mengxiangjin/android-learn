package com.jin.learn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.jin.learn.databinding.ActivityMainBinding
import com.jin.learn.db.DBHelper
import com.jin.learn.sp.LoginActivity
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.io.Reader

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
//            dbHelper?.readableDatabase
        }

        binding.write.setOnClickListener {
            //将输入的内容持久化到本地  openFileOutput  /data/data/包名/files/目录 (手机需要root用户才可查看)
            //context.filesDir
            // MODE_PRIVATE 若存在则覆盖   MODE_APPEND 存在则追加
            val fileOutputStream = openFileOutput("data", MODE_APPEND)
            fileOutputStream.write("这是我写入的数据".toByteArray())
            fileOutputStream.flush()
            fileOutputStream.close()
            finish()
        }

        binding.sp.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }

        loadData()
    }

    private fun loadData() {
        try {
            val fileInputStream = openFileInput("data")
            val content = StringBuilder()

            val bf = BufferedReader(InputStreamReader(fileInputStream))
            bf.forEachLine {
                content.append(it)
            }
            binding.data.text = content.toString()
        }catch (exception: FileNotFoundException) {
            Toast.makeText(this,"exception:$exception",Toast.LENGTH_LONG).show()
        }
    }
}