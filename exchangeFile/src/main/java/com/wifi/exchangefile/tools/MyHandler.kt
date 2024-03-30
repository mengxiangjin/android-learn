package com.wifi.exchangefile.tools

import android.os.Handler
import android.os.Message
import android.widget.Toast
import com.wifi.exchangefile.MainActivity
import com.wifi.exchangefile.MyApplication

class MyHandler : Handler {


    private constructor()

    companion object {
        private var myHandler: MyHandler? = null
            get() {
                if (field == null) {
                    field =  MyHandler()
                }
                return field
            }

        fun getImstance(): MyHandler {
            return myHandler!!
        }


        const val CODE_START_LISTEN = 200
        const val CODE_CONNECT_SUCCESS = 201


    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        when (msg.what) {
            CODE_START_LISTEN -> {
                Toast.makeText(MyApplication.getmInstance(),"服务端开始监听",Toast.LENGTH_LONG).show()
            }
            CODE_CONNECT_SUCCESS -> {
                Toast.makeText(MyApplication.getmInstance(),"客户端已连接",Toast.LENGTH_LONG).show()
            }
        }

    }
}