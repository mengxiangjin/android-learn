package com.jin.learn

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class OfflineBroadcast: BroadcastReceiver() {

    companion object {
        const val BROADCASTACTION = "OfflineBroadcast"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        AlertDialog.Builder(context).apply {
            setTitle("Warning")
            setMessage("You are forced to be offline. Please try to login again")
            setCancelable(false)
            setPositiveButton("ok") { _,_ ->
                ActivityController.clearActivity()
                val intent = Intent(context,LoginActivity::class.java)
                context?.startActivity(intent)
            }
            show()
        }
    }
}