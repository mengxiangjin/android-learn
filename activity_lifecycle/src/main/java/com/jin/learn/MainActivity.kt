package com.jin.learn

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var jumpCompleteActivity: Button
    private lateinit var jumpDialogActivity: Button
    private lateinit var jumpSaveDataActivity: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: ")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initListener()
    }

    private fun initView() {
        jumpCompleteActivity = findViewById(R.id.jump_complete_activity)
        jumpDialogActivity = findViewById(R.id.jump_dialog_activity)
        jumpSaveDataActivity = findViewById(R.id.jump_save_data_activity)
    }

    private fun initListener() {
        jumpCompleteActivity.setOnClickListener {
            //普通的activity  
            // onCreate -> onStart -> onResume ->点击跳转按钮
            // -> onPause -> onSaveInstanceState -> onStop -> 从CompleteActivity返回 -> onRestart -> onStart -> onResume
            startActivity(Intent(this, CompleteActivity::class.java))
        }
        jumpDialogActivity.setOnClickListener {
            //DialogActivity 为小窗口Activity，需在manifest声明其的主题为Dialog
            // onCreate -> onStart -> onResume ->点击跳转按钮
            // -> onPause -> 从CompleteActivity返回 -> onResume
            startActivity(Intent(this, DialogActivity::class.java))
        }
        jumpSaveDataActivity.setOnClickListener {
            startActivity(Intent(this, SaveDataActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState: ")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState: ")
    }


}