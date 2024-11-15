package com.wifi.exchangefile.tools

import android.util.Log
import com.wifi.exchangefile.MyApplication
import com.wifi.exchangefile.OnProgressListener
import java.io.DataInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.ServerSocket
import java.net.Socket

/*
* 收文件
* */
class ServerService {

    var serverSocket: ServerSocket? = null
    var clientSocket: Socket? = null


    private constructor()

    companion object {
        const val port = 5001
        var onProgressListener: OnProgressListener? = null

        private var instance: ServerService? = null
            get() {
                if (field == null) {
                    field = ServerService()
                }
                if (onProgressListener == null) {
                    onProgressListener = object : OnProgressListener {
                        override fun onProgressChanged(progress: Float) {
                            Log.d("zyz", "progress: " + (progress).toInt())
                        }

                    }
                }
                return field
            }

        fun getImstance(): ServerService {
            return instance!!
        }


    }

    var files = mutableListOf<File>()
    fun startServer() {
        if (serverSocket == null) {
            serverSocket = ServerSocket(port)
        }
        Thread {
            clientSocket = serverSocket!!.accept()
            Log.d("zyz", "startServer:客户端已连接 ")
            try {
                while (true) {
                    val inStream = DataInputStream(clientSocket!!.getInputStream())
                    val fileName = inStream.readUTF()
                    Log.d("zyz", "serverReceive: " + fileName)

                    val length = inStream.readLong()
                    val totalBytes = ByteArray(length.toInt())
                    val file = File(
                        MyApplication.getmInstance().getExternalFilesDir("")!!.path + "/" + fileName
                    )
                    val fileOut = FileOutputStream(file.absoluteFile)

                    val newBytes = ByteArray(1024)
                    Log.d("zyz", "serverReceive: " + totalBytes.size)
                    //每次读取1024字节 1kb
                    val counts = totalBytes.size / newBytes.size
                    val residue = totalBytes.size % newBytes.size
                    if (counts <= 0) {
                        inStream.readFully(totalBytes)
                        fileOut.write(totalBytes)
                        onProgressListener?.onProgressChanged(100f)
                    } else {
                        for (i in 0 until counts) {
                            inStream.readFully(newBytes)
                            fileOut.write(newBytes)
                            onProgressListener?.onProgressChanged(newBytes.size * (i + 1) / 1f / totalBytes.size * 100)
                        }
                        if (residue != 0) {
                            val temBytes = ByteArray(residue)
                            inStream.readFully(temBytes)
                            fileOut.write(temBytes)
                            onProgressListener?.onProgressChanged(100f)
                        }
                    }
                    fileOut.flush()
                    fileOut.close()
                }
            } catch (e: Exception) {
                Log.d("zyz", "serverException: " + e)
            }
        }.start()
    }

}