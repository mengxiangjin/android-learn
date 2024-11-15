package com.wifi.exchangefile.tools

import android.util.Log
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.Socket

class ClientService {


    private var socket: Socket? = null

    private constructor()

    companion object {
        const val port = 5001
        private var instance: ClientService? = null
            get() {
                if (field == null) {
                    field = ClientService()
                }
                return field
            }

        fun getImstance(): ClientService {
            return instance!!
        }
    }

    var files = mutableListOf<File>()

    fun sendFiles(files: List<File>) {
        this.files.clear()
        this.files.addAll(files)
    }

    fun startConnect(serverIp: String) {
        Thread {
            try {
                socket = Socket(serverIp, port)
                socket!!.keepAlive = true
                while (true) {
                    //发文件
                    if (files.isNotEmpty()) {
                        val file = files[0]
                        val out = DataOutputStream(socket!!.getOutputStream())

                        //发送文件名称
                        out.writeUTF(file.name)
                        Log.d("zyz", "clientSend: " + file.name)
                        out.flush()

//                        //发送文件长度
                        out.writeLong(file.length())
                        out.flush()

                        val inputStream = FileInputStream(file)
                        out.write(file.readBytes())

                        out.flush()
                        inputStream.close()
                        files.removeAt(0)
                    }

                }
            } catch (e: Exception) {
                Log.d("zyz", "serverException:" + e)
            }

        }.start()
    }

}