package com.jin.http_net.retrofit

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.jin.http_net.databinding.ActivityRetrofitBinding
import com.jin.http_net.retrofit.bean.SystemSwitchResponse
import com.jin.http_net.retrofit.bean.TopicResponse
import com.jin.http_net.retrofit.bean.UserResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

class RetrofitActivity: AppCompatActivity() {

    private lateinit var retrofitBaseInterface: RetrofitBaseInterface
    private lateinit var binding: ActivityRetrofitBinding

    companion object {
        const val BASE_URL_ONE = "https://jsonplaceholder.typicode.com/"
        const val BASE_URL_TWO = "https://www.wanandroid.com/"
        const val BASE_URL_THREE = "http://192.144.215.52:2001/api/hj/"
        const val BASE_URL_FOUR = "https://r1---sn-ni57rn7y.gvt1-cn.com/edgedl/android/studio/install/2024.1.2.12/"

        const val TEST_GET_OF_NO_ARGUMENT = 0
        const val TEST_GET_OF_PATH = 1
        const val TEST_GET_OF_HAS_ARGUMENT = 2
        const val TEST_GET_OF_HAS_ARGUMENT_MAP = 3

        const val TEST_POST_OF_ARGUMENT = 4
        const val TEST_POST_OF_ARGUMENT_MAP = 5
        const val TEST_GET_OF_URL = 6

        const val TEST_GET_OF_HEADER = 7
        const val TEST_GET_OF_HEADERS = 8
        const val TEST_GET_OF_DOWNLOAD_STREAM = 9

        const val TEST_POST_OF_UPLOAD = 10
        const val TEST_POST_OF_BODY = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRetrofitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initListener()
    }

    private fun initListener() {
        binding.btnOne.setOnClickListener {
            changeRetrofitAttr(BASE_URL_ONE)
            startTest(TEST_GET_OF_NO_ARGUMENT)
        }
        binding.btnTwo.setOnClickListener {
            changeRetrofitAttr(BASE_URL_ONE)
            startTest(TEST_GET_OF_PATH)
        }
        binding.btnThree.setOnClickListener {
            changeRetrofitAttr(BASE_URL_ONE)
            startTest(TEST_GET_OF_HAS_ARGUMENT)
        }
        binding.btnFour.setOnClickListener {
            changeRetrofitAttr(BASE_URL_ONE)
            startTest(TEST_GET_OF_HAS_ARGUMENT_MAP)
        }

        binding.btnFive.setOnClickListener {
            changeRetrofitAttr(BASE_URL_TWO)
            startTest(TEST_POST_OF_ARGUMENT)
        }

        binding.btnSix.setOnClickListener {
            changeRetrofitAttr(BASE_URL_TWO)
            startTest(TEST_POST_OF_ARGUMENT_MAP)
        }

        binding.btnSeven.setOnClickListener {
            changeRetrofitAttr(BASE_URL_ONE)
            startTest(TEST_GET_OF_URL)
        }

        binding.btnEight.setOnClickListener {
            changeRetrofitAttr(BASE_URL_THREE)
            startTest(TEST_GET_OF_HEADER)
        }

        binding.btnNine.setOnClickListener {
            changeRetrofitAttr(BASE_URL_THREE)
            startTest(TEST_GET_OF_HEADERS)
        }

        binding.btnTen.setOnClickListener {
            changeRetrofitAttr(BASE_URL_FOUR)
            startTest(TEST_GET_OF_DOWNLOAD_STREAM)
        }

        binding.btnEleven.setOnClickListener {
            changeRetrofitAttr(BASE_URL_FOUR)
            startTest(TEST_POST_OF_UPLOAD)
        }

        binding.btnTwelve.setOnClickListener {
            changeRetrofitAttr(BASE_URL_THREE)
            startTest(TEST_POST_OF_BODY)
        }
    }

    private fun changeRetrofitAttr(baseUrl: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory()
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitBaseInterface = retrofit.create(RetrofitBaseInterface::class.java)
    }

    private fun startTest(testID: Int) {
        when(testID) {
            //无参GET请求
            TEST_GET_OF_NO_ARGUMENT -> {
                val hotkey = retrofitBaseInterface.getTopic()
                hotkey.enqueue(object : Callback<List<TopicResponse>> {
                    override fun onResponse(call: Call<List<TopicResponse>>, response: Response<List<TopicResponse>>) {
                        Log.d("TAG", "onResponse: " + response.headers().toString())
                        Log.d("TAG", "onResponse: $response")
                        Log.d("TAG", "onResponse: " + response.body()?.toString())
                        val sb = StringBuilder()
                        sb.append("响应头：" + response.headers().toString() + "\n")
                        sb.append("响应：$response\n")
                        sb.append("响应体：" + response.body()?.toString())
                        binding.tvContent.text = sb.toString()
                    }

                    override fun onFailure(call: Call<List<TopicResponse>>, t: Throwable) {
                        Log.d("TAG", "onFailure: ${t.message}")
                        binding.tvContent.text = t.message
                    }
                })
            }

            TEST_GET_OF_PATH -> {
                val hotkey = retrofitBaseInterface.getTopicFromPath("3")
                hotkey.enqueue(object : Callback<TopicResponse> {
                    override fun onResponse(call: Call<TopicResponse>, response: Response<TopicResponse>) {
                        Log.d("TAG", "onResponse: " + response.headers().toString())
                        Log.d("TAG", "onResponse: $response")
                        Log.d("TAG", "onResponse: " + response.body()?.toString())
                        val sb = StringBuilder()
                        sb.append("响应头：" + response.headers().toString() + "\n")
                        sb.append("响应：$response\n")
                        sb.append("响应体：" + response.body()?.toString())
                        binding.tvContent.text = sb.toString()
                    }

                    override fun onFailure(call: Call<TopicResponse>, t: Throwable) {
                        Log.d("TAG", "onFailure: ${t.message}")
                        binding.tvContent.text = t.message
                    }
                })
            }

            TEST_GET_OF_HAS_ARGUMENT -> {
                val hotkey = retrofitBaseInterface.getTopicForQuery(3)
                hotkey.enqueue(object : Callback<List<TopicResponse>> {
                    override fun onResponse(call: Call<List<TopicResponse>>, response: Response<List<TopicResponse>>) {
                        Log.d("TAG", "onResponse: " + response.headers().toString())
                        Log.d("TAG", "onResponse: $response")
                        Log.d("TAG", "onResponse: " + response.body()?.toString())
                        val sb = StringBuilder()
                        sb.append("响应头：" + response.headers().toString() + "\n")
                        sb.append("响应：$response\n")
                        sb.append("响应体：" + response.body()?.toString())
                        binding.tvContent.text = sb.toString()
                    }

                    override fun onFailure(call: Call<List<TopicResponse>>, t: Throwable) {
                        Log.d("TAG", "onFailure: ${t.message}")
                        binding.tvContent.text = t.message
                    }
                })
            }

            TEST_GET_OF_HAS_ARGUMENT_MAP -> {
                val hotkey = retrofitBaseInterface.getTopicForQueryMap(mapOf(Pair("userId","3"),Pair("id","4")))
                hotkey.enqueue(object : Callback<List<TopicResponse>> {
                    override fun onResponse(call: Call<List<TopicResponse>>, response: Response<List<TopicResponse>>) {
                        Log.d("TAG", "onResponse: " + response.headers().toString())
                        Log.d("TAG", "onResponse: $response")
                        Log.d("TAG", "onResponse: " + response.body()?.toString())
                        val sb = StringBuilder()
                        sb.append("响应头：" + response.headers().toString() + "\n")
                        sb.append("响应：$response\n")
                        sb.append("响应体：" + response.body()?.toString())
                        binding.tvContent.text = sb.toString()
                    }

                    override fun onFailure(call: Call<List<TopicResponse>>, t: Throwable) {
                        Log.d("TAG", "onFailure: ${t.message}")
                        binding.tvContent.text = t.message
                    }
                })
            }

            TEST_POST_OF_ARGUMENT -> {
                val register = retrofitBaseInterface.register("t1e2st", "123123", "123123")
                register.enqueue(object : Callback<UserResponse> {
                    override fun onResponse(
                        call: Call<UserResponse>,
                        response: Response<UserResponse>
                    ) {
                        Log.d("TAG", "onResponse: " + response.headers().toString())
                        Log.d("TAG", "onResponse: $response")
                        Log.d("TAG", "onResponse: " + response.body()?.toString())
                        val sb = StringBuilder()
                        sb.append("响应头：" + response.headers().toString() + "\n")
                        sb.append("响应：$response\n")
                        sb.append("响应体：" + response.body()?.toString())
                        binding.tvContent.text = sb.toString()
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        Log.d("TAG", "onFailure: ${t.message}")
                        binding.tvContent.text = t.message
                    }
                })
            }

            TEST_POST_OF_ARGUMENT_MAP -> {
                val register = retrofitBaseInterface.registerForMap(mapOf(Pair("username","t1e2s3t"),Pair("password","123123"),Pair("repassword","123123")))
                register.enqueue(object : Callback<UserResponse> {
                    override fun onResponse(
                        call: Call<UserResponse>,
                        response: Response<UserResponse>
                    ) {
                        Log.d("TAG", "onResponse: " + response.headers().toString())
                        Log.d("TAG", "onResponse: $response")
                        Log.d("TAG", "onResponse: " + response.body()?.toString())
                        val sb = StringBuilder()
                        sb.append("响应头：" + response.headers().toString() + "\n")
                        sb.append("响应：$response\n")
                        sb.append("响应体：" + response.body()?.toString())
                        binding.tvContent.text = sb.toString()
                    }

                    override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                        Log.d("TAG", "onFailure: ${t.message}")
                        binding.tvContent.text = t.message
                    }
                })
            }

            TEST_GET_OF_URL -> {
                val topicFromUrl = retrofitBaseInterface.getTopicFromUrl("posts/2")
                topicFromUrl.enqueue(object : Callback<TopicResponse> {
                    override fun onResponse(
                        call: Call<TopicResponse>,
                        response: Response<TopicResponse>
                    ) {
                        Log.d("TAG", "onResponse: " + response.headers().toString())
                        Log.d("TAG", "onResponse: $response")
                        Log.d("TAG", "onResponse: " + response.body()?.toString())
                        val sb = StringBuilder()
                        sb.append("响应头：" + response.headers().toString() + "\n")
                        sb.append("响应：$response\n")
                        sb.append("响应体：" + response.body()?.toString())
                        binding.tvContent.text = sb.toString()
                    }

                    override fun onFailure(call: Call<TopicResponse>, t: Throwable) {
                        Log.d("TAG", "onFailure: ${t.message}")
                        binding.tvContent.text = t.message
                    }
                })
            }

            TEST_GET_OF_HEADER -> {
                val systemSwitchForAddHead =
                    retrofitBaseInterface.getSystemSwitchForAddHead(1, 1, "com.huanji.android")
                systemSwitchForAddHead.enqueue(object : Callback<SystemSwitchResponse> {
                    override fun onResponse(
                        call: Call<SystemSwitchResponse>,
                        response: Response<SystemSwitchResponse>
                    ) {
                        Log.d("TAG", "onResponse: " + response.headers().toString())
                        Log.d("TAG", "onResponse: $response")
                        Log.d("TAG", "onResponse: " + response.body()?.toString())
                        val sb = StringBuilder()
                        sb.append("响应头：" + response.headers().toString() + "\n")
                        sb.append("响应：$response\n")
                        sb.append("响应体：" + response.body()?.toString())
                        binding.tvContent.text = sb.toString()
                    }

                    override fun onFailure(call: Call<SystemSwitchResponse>, t: Throwable) {
                        Log.d("TAG", "onFailure: ${t.message}")
                        binding.tvContent.text = t.message
                    }
                })
            }

            TEST_GET_OF_HEADERS -> {
                val systemSwitchForAddHead =
                    retrofitBaseInterface.getSystemSwitchForAddHeads()
                systemSwitchForAddHead.enqueue(object : Callback<SystemSwitchResponse> {
                    override fun onResponse(
                        call: Call<SystemSwitchResponse>,
                        response: Response<SystemSwitchResponse>
                    ) {
                        Log.d("TAG", "onResponse: " + response.headers().toString())
                        Log.d("TAG", "onResponse: $response")
                        Log.d("TAG", "onResponse: " + response.body()?.toString())
                        val sb = StringBuilder()
                        sb.append("响应头：" + response.headers().toString() + "\n")
                        sb.append("响应：$response\n")
                        sb.append("响应体：" + response.body()?.toString())
                        binding.tvContent.text = sb.toString()
                    }

                    override fun onFailure(call: Call<SystemSwitchResponse>, t: Throwable) {
                        Log.d("TAG", "onFailure: ${t.message}")
                        binding.tvContent.text = t.message
                    }
                })
            }

            TEST_GET_OF_DOWNLOAD_STREAM -> {
                val systemSwitchForAddHead =
                    retrofitBaseInterface.getResource()
                systemSwitchForAddHead.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        Log.d("TAG", "onResponse: " + response.headers().toString())
                        Log.d("TAG", "onResponse: $response")
                        Log.d("TAG", "onResponse: " + response.body()?.toString())
                        val sb = StringBuilder()
                        sb.append("响应头：" + response.headers().toString() + "\n")
                        sb.append("响应：$response\n")
                        sb.append("响应体：" + response.body()?.toString())
                        response.body()?.let {
                            saveToLocal(it)
                        }
                        binding.tvContent.text = sb.toString()
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("TAG", "onFailure: ${t.message}")
                        binding.tvContent.text = t.message
                    }
                })
            }

            TEST_POST_OF_UPLOAD -> {
                val fileNameRequestBody = "文件名称".toRequestBody("text/plain".toMediaTypeOrNull())
                val dir = this.getExternalFilesDir("download") ?: return
                val file = File(dir,"a.png")
                if (file.exists()) {
                    file.createNewFile()
                }
                val fileRequestBody = file.asRequestBody("image/png".toMediaTypeOrNull())
                val createFormData =
                    MultipartBody.Part.createFormData("file", "文件名称", fileRequestBody)
                retrofitBaseInterface.uploadResource(fileNameRequestBody,createFormData)
            }

            TEST_POST_OF_BODY -> {
                val jsonBody = Gson().toJson(mapOf(Pair("a", "a"), Pair("b", "b")))
                val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
                val call = retrofitBaseInterface.login(requestBody)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        Log.d("TAG", "onResponse: " + response.headers().toString())
                        Log.d("TAG", "onResponse: $response")
                        Log.d("TAG", "onResponse: " + response.body()?.byteStream())
                        val sb = StringBuilder()
                        sb.append("响应头：" + response.headers().toString() + "\n")
                        sb.append("响应：$response\n")
                        sb.append("响应体：" + response.body()?.byteStream())

//                        val jsonObject = JSONObject(response.body().toString())
//                        var optInt = jsonObject.optInt("code")
//                        var optString = jsonObject.optString("message")
//                        var optJSONObject = jsonObject.optJSONObject("content")

                        binding.tvContent.text = sb.toString()
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("TAG", "onFailure: ${t.message}")
                        binding.tvContent.text = t.message
                    }

                })
            }
        }
    }

    private fun saveToLocal(responseBody: ResponseBody) {
        val inputStream = responseBody.byteStream()
        val contentLength = responseBody.contentLength()

        val filesDir = this.getExternalFilesDir("download")?: return
        val fileName = "a.exe"
        val fileOutStream = FileOutputStream(File(filesDir,fileName))

        val fileReader = ByteArray(1024)
        var readLength = inputStream.read(fileReader)
        var sumProgress = 0L
        while (readLength != -1) {
            fileOutStream.write(fileReader,0,readLength)
            sumProgress += readLength.toLong()
            val progress = sumProgress * 1.0 / contentLength * 100
            readLength = inputStream.read(fileReader)
            Log.d("TAG", "saveToLocal: $progress")
        }
        fileOutStream.flush()
        inputStream.close()
        fileOutStream.close()
    }

}