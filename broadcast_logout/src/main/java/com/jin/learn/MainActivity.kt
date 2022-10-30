package com.jin.learn

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class MainActivity : BaseActivity() {

    private lateinit var offline: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        offline = findViewById(R.id.force_offline)
        offline.setOnClickListener {
            val intent = Intent(OfflineBroadcast.BROADCASTACTION)
            intent.setPackage(packageName)
            sendBroadcast(intent)
        }
    }
}