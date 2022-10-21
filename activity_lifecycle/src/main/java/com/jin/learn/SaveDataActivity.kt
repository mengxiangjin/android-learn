package com.jin.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

//onSaveInstanceState
class SaveDataActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SaveDataActivity"
        const val DATA = "I am a new value"
    }

    private lateinit var finish: Button
    private lateinit var data: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_data)
        initView()
        initListener()
        var value = savedInstanceState?.getString("data")
        value?.let {
            data.text = it
        }

    }

    private fun initView() {
        finish = findViewById(R.id.finish)
        data = findViewById(R.id.data)
    }

    private fun initListener() {
        finish.setOnClickListener {
            //不会回调onSaveInstanceState，只有当activity系统回收时才会回调不会回调onSaveInstanceState
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("data", DATA)
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState: ")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d(TAG, "onRestoreInstanceState: ")
    }
}