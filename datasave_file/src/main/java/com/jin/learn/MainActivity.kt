package com.jin.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter


/*
* android 将数据保存到文件中，从文件中拿取数据
*
* */
class MainActivity : AppCompatActivity() {


    private lateinit var finish: Button
    private lateinit var content: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        finish = findViewById(R.id.finish)
        content = findViewById(R.id.data_content)

        var data = loadData()
        if (data.isNotEmpty()) {
            content.setText(data)
            Toast.makeText(this,"Restoring succeeded",Toast.LENGTH_LONG).show()
        }

        finish.setOnClickListener {
            //将输入的内容持久化到本地  openFileOutput  /data/data/包名/files/目录
            // MODE_PRIVATE 若存在则覆盖   MODE_APPEND 存在则追加
            val output = openFileOutput("data", MODE_PRIVATE)
            var writer = BufferedWriter(OutputStreamWriter(output))
            val content = content.text.toString()
            writer.use {
                it.write(content)
            }
            finish()
        }
    }

    private fun loadData(): String {
        //从文件读取数据
        val content = StringBuilder()
        try {
            val input = openFileInput("data")
            val reader = BufferedReader(InputStreamReader(input))
            reader.use {
                it.forEachLine {
                    content.append(it)
                }
            }
        }catch (e: IOException) {
            e.printStackTrace()
        }

        return content.toString()
    }



}