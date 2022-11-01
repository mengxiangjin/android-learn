package com.jin.learn.broadcast

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.jin.learn.LoginActivity
import com.jin.learn.base.ActivityController

class ForceOfflineBroadcast: BroadcastReceiver() {



    companion object {
        const val ACTION = "force_offline_broadcast"
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        AlertDialog.Builder(context).apply {
            setCancelable(false)
            setTitle("Warning")
            setMessage("You are forced to be offline. Please try to login again")
            setPositiveButton("ok") { _, _ ->
                ActivityController.clearActivity()
                val intent = Intent(context, LoginActivity::class.java)
                context?.startActivity(intent)
            }.show()
        }
    }
}