package com.jin.learn

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.jin.learn.adapter.UserTableAdapter
import com.jin.learn.bean.User
import com.jin.learn.databinding.ActivitySecondBinding
import com.jin.learn.provider.MyContentProvider
import com.jin.learn.provider.MyDBHelper
import java.io.File


class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    private lateinit var userTableAdapter: UserTableAdapter


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userTableAdapter = UserTableAdapter(this, emptyList())
        binding.rvUser.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvUser.adapter = userTableAdapter

        binding.query.setOnClickListener {
            queryData()
        }
        binding.insert.setOnClickListener {
            val name = binding.etName.text.toString()
            if (name.isNotBlank()) {
                insertData(name)
            }
        }

        binding.query2.setOnClickListener {
            queryDataFor2()
        }

        binding.send.setOnClickListener {
            //通过文件后缀名获取对应的mimeType
//            MimeTypeMap.getSingleton().getMimeTypeFromExtension()

            val externalFilesDir = getExternalFilesDir("")
            val file = File(externalFilesDir,"img.png")

            val intent = Intent(Intent.ACTION_VIEW)
            val uri =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, "com.jin.learn.provider", file)
            } else {
                 Uri.fromFile(file)
            }

            intent.setDataAndType(uri,"image/png")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(intent)
        }
    }

    private fun insertData(name: String) {
        var uri = Uri.parse("content://${MyContentProvider.AUTOHORITY}/${MyDBHelper.TABLE_NAME}")
        val contentValue = ContentValues()
        contentValue.put(MyDBHelper.column_name,name)
        contentResolver.insert(uri,contentValue)
    }

    private fun queryData() {
        val userDatas = mutableListOf<User>()

        var uri = Uri.parse("content://${MyContentProvider.AUTOHORITY}/${MyDBHelper.TABLE_NAME}")
        var cursor = contentResolver.query(uri, null, null, null, null, null)
        cursor?.let {
            while (it.moveToNext()) {
                var name = it.getString(it.getColumnIndex(MyDBHelper.column_name))
                var id = it.getInt(it.getColumnIndex(MyDBHelper.column_id))
                val user = User(id.toString(),name)
                userDatas.add(user)
            }
        }
        userTableAdapter?.updateDatas(userDatas)
    }

    private fun queryDataFor2() {
        val userDatas = mutableListOf<User>()
        var uri = Uri.parse("content://${MyContentProvider.AUTOHORITY}/${MyDBHelper.TABLE_NAME}/2")
        var cursor = contentResolver.query(uri, null, null, null, null, null)
        cursor?.let {
            while (it.moveToNext()) {
                var name = it.getString(it.getColumnIndex(MyDBHelper.column_name))
                var id = it.getInt(it.getColumnIndex(MyDBHelper.column_id))
                val user = User(id.toString(),name)
                userDatas.add(user)
            }
        }
        userTableAdapter?.updateDatas(userDatas)
    }
}