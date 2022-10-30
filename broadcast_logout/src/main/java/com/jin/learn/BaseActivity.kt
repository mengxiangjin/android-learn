package com.jin.learn

import android.content.IntentFilter
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity: AppCompatActivity() {

    private var offlineBroadcast: OfflineBroadcast? = null


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        ActivityController.addActivity(this)
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        offlineBroadcast = OfflineBroadcast()
        intentFilter.addAction(OfflineBroadcast.BROADCASTACTION)
        registerReceiver(offlineBroadcast,intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(offlineBroadcast)
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityController.removeActivity(this)
    }

}