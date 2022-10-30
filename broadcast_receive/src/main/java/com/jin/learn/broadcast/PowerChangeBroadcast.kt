package com.jin.learn.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class PowerChangeBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Toast.makeText(context,"Receive Power Change",Toast.LENGTH_LONG).show()
    }
}