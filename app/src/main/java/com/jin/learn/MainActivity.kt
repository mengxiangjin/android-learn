package com.jin.learn

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {


    companion object {
        private val REQUEST_CODE = 100
    }


    private lateinit var jumpObvious: Button
    private lateinit var jumpHide: Button
    private lateinit var jumpBrowse: Button
    private lateinit var jumpMineBrowse: Button
    private lateinit var jumpData: Button
    private lateinit var jumpDataReceive: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initListener()
    }

    private fun initView() {
        jumpObvious = findViewById(R.id.obvious_jump)
        jumpHide = findViewById(R.id.hide_jump)
        jumpBrowse = findViewById(R.id.browse_jump)
        jumpMineBrowse = findViewById(R.id.mine_browse_jump)
        jumpData = findViewById(R.id.data_jump)
        jumpDataReceive = findViewById(R.id.receive_data_jump)
    }

    private fun initListener() {
        jumpObvious.setOnClickListener {
            //显式跳转 意图
            val intent = Intent(this,SecondActivity::class.java)
            startActivity(intent)
        }
        jumpHide.setOnClickListener {
            //隐式跳转 意图
            //params: AndroidManifest中定义跳转的activity的<intent-filter> 下的 <action name>
            val intent = Intent("com.jin.learn.HIDE_ACTIVITY")
            intent.addCategory("com.jin.learn.CATEGORY")

//            <intent-filter>
//               <action android:name="com.jin.learn.HIDE_ACTIVITY"/>
//               <category android:name="android.intent.category.DEFAULT"/>
//               <category android:name="com.jin.learn.CATEGORY"/>
//            </intent-filter>
            startActivity(intent)
        }
        jumpBrowse.setOnClickListener {
            //跳转浏览器
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.baidu.com")
            startActivity(intent)
        }
        jumpMineBrowse.setOnClickListener {
            //跳转mine浏览器
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.baidu.com")
            intent.addCategory("android.intent.category.BROWSABLE")
            startActivity(intent)
        }
        jumpData.setOnClickListener {
            //数据跳转（向上传递数据）
            val intent = Intent(this,SecondActivity::class.java)
            intent.putExtra("name","zhangsan")
            startActivity(intent)
        }
        jumpDataReceive.setOnClickListener {
            val intent = Intent(this,SecondActivity::class.java)
            intent.putExtra("name","zhangsan")
            //REQUEST_CODE，用于onActivityResult中监听请求者
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    //startActivityForResult 上一个activity返回过来的回调
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_CODE -> {
                var age = data?.getIntExtra("age",0);
                jumpDataReceive.text = age.toString()
            }
        }
    }

    //Android Menu 选择事件回调
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.mine -> {
                Toast.makeText(this,"CLICK MINE",Toast.LENGTH_SHORT).show()
            }
            R.id.more -> {
                Toast.makeText(this,"CLICK MORE",Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /*
    * Android Menu 创建回调
    *  return ture -> showMenu false -> dismissMenu
    * */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.setting_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}