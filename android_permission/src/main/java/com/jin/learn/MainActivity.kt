package com.jin.learn

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jin.learn.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
    }

    private fun initListener() {
//        binding.call.setOnClickListener {
//            //ACTION_CALL危险权限，Android6.0以下需静态权限声明，否则崩溃
//            //    <uses-permission android:name="android.permission.CALL_PHONE"></uses-permission> 声明了也会崩溃
//            //需要动态权限申请
//            val intent = Intent(Intent.ACTION_CALL)
//            intent.data = Uri.parse("tel:10086")
//            startActivity(intent)
//        }

        binding.call.setOnClickListener {
            //运行时申请权限
            //检查是否已经授权
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE)
              != PackageManager.PERMISSION_GRANTED) {
                //申请权限 params：申请的权限列表，请求码（用于回调时进行判断）
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE),1)
            } else {
                call()
            }
        }

        binding.jump.setOnClickListener {
            startActivity(Intent(this,SecondActivity::class.java))
        }
    }



    /*
    * 申请权限结果的回调
    * params： 1.申请权限的请求码
    *         2.申请的权限列表
    *         3.申请结果
    * */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   call()
                } else {
                    Toast.makeText(this,"you denied the permission",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun call() {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:10086")
        startActivity(intent)
    }
}