package com.jin.learn.base

import android.content.IntentFilter
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.jin.learn.broadcast.ForceOfflineBroadcast

open class BaseActivity: AppCompatActivity() {

    private var forceOfflineBroadcast: ForceOfflineBroadcast? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        ActivityController.addActivity(this)
    }

    override fun onResume() {
        super.onResume()
        //注册广播
        val intentFilter = IntentFilter(ForceOfflineBroadcast.ACTION)
        forceOfflineBroadcast = ForceOfflineBroadcast()
        registerReceiver(forceOfflineBroadcast,intentFilter)
    }

    override fun onPause() {
        super.onPause()
        forceOfflineBroadcast?.let {
            unregisterReceiver(forceOfflineBroadcast)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        ActivityController.removeActivity(this)
    }
}