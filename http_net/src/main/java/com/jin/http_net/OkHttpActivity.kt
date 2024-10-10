package com.jin.http_net

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.BufferedSink
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit

class OkHttpActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        applyPermission()
//        asyncOfGet()
//        asyncOfPost()
        asyncOfDownloadGet()
    }

    private fun applyPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE),200)
        }
    }

    private fun asyncOfGet() {
        val request = Request.Builder()
            .url("https://www.baidu.com")
            .method("GET",null)
            .build()

        val okhttpClient = OkHttpClient.Builder()
            .connectTimeout(2000,TimeUnit.MILLISECONDS)
            .readTimeout(2000,TimeUnit.MILLISECONDS)
            .writeTimeout(2000,TimeUnit.MILLISECONDS)
            .build()

        val newCall = okhttpClient.newCall(request)
        newCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("TAG", "onFailure: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("TAG", "onResponse: " + response.body?.string())
            }
        })
    }

    private fun asyncOfPost() {
        val requestBody = FormBody.Builder()
            .add("userId","1")
            .add("title","title~")
            .add("body","body~")
            .build()

//        val jsonStr = "{\"userId\" : \"1\",\"title\" : \"title~\",\"body\" : \"body~\"}"
//        val requestBody = jsonStr.toRequestBody("application/json;charset=utf-8".toMediaTypeOrNull())


        val request = Request.Builder().url("https://jsonplaceholder.typicode.com/posts")
            .header("Content-Type","multipart/form-data")
            .method("POST",requestBody)
            .build()

        val okhttpClient = OkHttpClient.Builder()
            .connectTimeout(2000,TimeUnit.MILLISECONDS)
            .readTimeout(2000,TimeUnit.MILLISECONDS)
            .writeTimeout(2000,TimeUnit.MILLISECONDS)
            .build()

        val newCall = okhttpClient.newCall(request)
        newCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("TAG", "onFailure: $e")
            }
            override fun onResponse(call: Call, response: Response) {
                Log.d("TAG", "onResponse: " + response.body?.string())
            }
        })
    }

    private fun asyncOfDownloadGet() {
        val request = Request.Builder()
            .url("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png")
            .method("GET",null)
            .header("Content-type","application/octet-stream")
            .build()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2000,TimeUnit.MILLISECONDS)
            .readTimeout(2000,TimeUnit.MILLISECONDS)
            .writeTimeout(2000,TimeUnit.MILLISECONDS)
            .build()

        val newCall = okHttpClient.newCall(request)
        newCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("TAG", "onFailure: $e")
            }

            override fun onResponse(call: Call, response: Response) {
                writeToSDCard(response)
            }
        })
    }

    /**
     * 将文件写入SD卡来保存
     */
    private fun writeToSDCard(response: Response) {
        val inputStream = response.body!!.byteStream()
        val dir = this@OkHttpActivity.getExternalFilesDir("img")?:return
        val file = File(dir,"a.png")

        val fileSize = response.body!!.contentLength()

        val fileOutputStream = FileOutputStream(file)
        val fileReader = ByteArray(1024)
        var read = inputStream.read(fileReader)


        var sum = 0L
        while (read != -1) {
            fileOutputStream.write(fileReader,0,read)
            sum += read.toLong()
            val progress = sum * 1.0 / fileSize * 100
            Log.d("TAG", "writeToSDCard: $progress")
            read = inputStream.read(fileReader)
        }
        fileOutputStream.flush()
        inputStream.close()
        fileOutputStream.close()
    }

}