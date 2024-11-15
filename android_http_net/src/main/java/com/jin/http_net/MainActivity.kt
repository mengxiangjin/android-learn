package com.jin.http_net

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import org.apache.http.HttpVersion
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.params.BasicHttpParams
import org.apache.http.params.HttpConnectionParams
import org.apache.http.params.HttpProtocolParams
import org.apache.http.protocol.HTTP
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //HttpClient Start
//        httpClientForHttpGet("https://www.baidu.com")
//        httpClientForHttpPost("http://jsonplaceholder.typicode.com/posts")
        //HttpClient End

        //HttpUrlConnection Start
//        httpUrlConnectionForHttpGet("https://www.baidu.com")
        httpUrlConnectionForHttpPost("http://jsonplaceholder.typicode.com/posts", mapOf(Pair("userId", "1"),
            Pair("title","title~"), Pair("body","body~")))
        //HttpUrlConnection End
    }


    /*
    * 创建执行器HttpClient
    * */
    private fun createHttpClient(): HttpClient {
        val basicHttpParams = BasicHttpParams()

        //设置连接超时
        HttpConnectionParams.setConnectionTimeout(basicHttpParams,1500)
        //tcp非延迟
        HttpConnectionParams.setTcpNoDelay(basicHttpParams,true)
        //设置请求超时
        HttpConnectionParams.setSoTimeout(basicHttpParams,1500)
        HttpProtocolParams.setVersion(basicHttpParams,HttpVersion.HTTP_1_1)
        //设置持续连接
        HttpProtocolParams.setUseExpectContinue(basicHttpParams,true)
        HttpProtocolParams.setContentCharset(basicHttpParams, HTTP.UTF_8)

        return DefaultHttpClient(basicHttpParams)
    }

    private fun httpClientForHttpGet(url: String) {
        Thread {
            val httpClient = createHttpClient()

            //创建Get请求
            val httpGet = HttpGet(url)
            httpGet.addHeader("Connection","Keep-Alive")

            //执行器执行该请求处理返回结果
            val httpResponse = httpClient.execute(httpGet)
            val statusCode = httpResponse.statusLine.statusCode
            Log.d("HTTP-GET", "statusCode: " + statusCode)
            val inputStream = httpResponse.entity.content

            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            var content = bufferedReader.readLine()
            while (content != null) {
                Log.d("HTTP-GET", "content: " + content)
                content = bufferedReader.readLine()
            }
            inputStream.close()
            bufferedReader.close()
        }.start()
    }

    private fun httpClientForHttpPost(url: String) {
        Thread{
            val httpClient = createHttpClient()

            val httpPost = HttpPost(url)
            httpPost.addHeader("Connection","Keep-Alive")

            //POST请求携带参数NameValuePair形式
            val params = mutableListOf<NameValuePair>()

            params.add(BasicNameValuePair("userId","1"))
            params.add(BasicNameValuePair("title","title~"))
            params.add(BasicNameValuePair("body","body~"))

            httpPost.entity = UrlEncodedFormEntity(params)

            val httpResponse = httpClient.execute(httpPost)
            val statusCode = httpResponse.statusLine.statusCode
            Log.d("HTTP-POST", "statusCode: " + statusCode)
            val inputStream = httpResponse.entity.content

            val bufferedReader = BufferedReader(InputStreamReader(inputStream))

            var content = bufferedReader.readLine()
            while (content != null) {
                Log.d("HTTP-POST", "content: " + content)
                content = bufferedReader.readLine()
            }
            inputStream.close()
            bufferedReader.close()
        }.start()
    }

    private fun httpUrlConnectionForHttpGet(url: String) {
        Thread {
            val url = URL(url)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "GET"
            httpURLConnection.readTimeout = 1500
            httpURLConnection.connectTimeout = 150

            //是否向httpURLConnection输出
            httpURLConnection.doOutput = true
            //是否从httpURLConnection输入
            httpURLConnection.doInput = true
            //是否使用缓存
            httpURLConnection.useCaches = false
            //是否自动重定向
            httpURLConnection.instanceFollowRedirects = true

            httpURLConnection.connect()

            val responseCode = httpURLConnection.responseCode
            Log.d("HTTPURL-GET", "onCreate:responseCode " + responseCode)
            if (responseCode == 200) {
                //成功
                val bufferedReader = BufferedReader(InputStreamReader(httpURLConnection.inputStream))
                var content = bufferedReader.readLine()
                while (content != null) {
                    Log.d("HTTPURL-GET", "onCreate: " + content)
                    content = bufferedReader.readLine()
                }
                bufferedReader.close()
            }
            httpURLConnection.disconnect()
        }.start()
    }

    private fun httpUrlConnectionForHttpPost(url: String,params: Map<String,String>) {
        Thread{
            val url = URL(url)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.setRequestProperty("Connection","Keep-Alive")
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.connectTimeout = 1500
            httpURLConnection.readTimeout = 1500
            httpURLConnection.doOutput = true
            httpURLConnection.doInput = true

//            httpURLConnection.setRequestProperty("Content-Type","application/json")
            /*
            * 携带参数 body中
            * */
            val bufferedWriter = BufferedWriter(OutputStreamWriter(httpURLConnection.outputStream))
            val content = StringBuilder()

//            val jsonStr = "{\"userId\" : \"1\",\"title\" : \"title~\",\"body\" : \"body~\"}"
//            httpURLConnection.outputStream.write(jsonStr.toByteArray())
//            httpURLConnection.outputStream.flush()
//            httpURLConnection.outputStream.close()


            params.keys.forEach {
                if (content.isNotEmpty()) {
                    content.append("&")
                }
                content.append(URLEncoder.encode(it,"UTF-8"))
                content.append("=")
                content.append(URLEncoder.encode(params[it],"UTF-8"))
            }
            bufferedWriter.write(content.toString())
            bufferedWriter.flush()
            bufferedWriter.close()

            httpURLConnection.connect()
            val responseCode = httpURLConnection.responseCode
            Log.d("HTTPURL-POST", "onCreate:responseCode " + responseCode)
            if (responseCode == 200 || responseCode == 201) {
                //成功
                val bufferedReader = BufferedReader(InputStreamReader(httpURLConnection.inputStream))
                var content = bufferedReader.readLine()
                while (content != null) {
                    Log.d("HTTPURL-POST", "onCreate: " + content)
                    content = bufferedReader.readLine()
                }
                bufferedReader.close()
            }
            httpURLConnection.disconnect()


        }.start()
    }
}