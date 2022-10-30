package com.jin.learn

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jin.learn.broadcast.BatteryBroadcast


/*
* 动态注册、静态注册广播接收者
* */
class MainActivity : AppCompatActivity() {

    private val receiver = BatteryBroadcast()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dynamicRegisterBroadcast()
    }

    private fun dynamicRegisterBroadcast() {
        //动态注册广播接受者 Intent.ACTION_BATTERY_CHANGED -> 手机电量发生变化
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(receiver,intentFilter)
    }
}