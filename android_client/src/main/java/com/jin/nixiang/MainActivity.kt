package com.jin.nixiang

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.gson.JsonParser
import com.jin.nixiang.databinding.ActivityMainBinding
import com.jin.nixiang.interceptor.CommonInterceptor
import com.jin.nixiang.retrofit.HttpReq
import com.jin.nixiang.utils.Utils
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnLogin.setOnClickListener {
            binding.editName.text?.toString()?.let { it1 ->
                binding.editPassword.text?.toString()?.let { it2 ->
//                    login(it1, it2)
                    loginByRetrofit(it1, it2)
                }
            }
        }



    }

    private fun login(name: String, password: String) {
        Thread{
            val client = OkHttpClient.Builder()
                .addInterceptor(CommonInterceptor())
                .build()
            val form = FormBody.Builder()
                .add("username", name)
                .add("password", Utils.md5(password))
                .add("sign",Utils.md5("${name}jin"))
                .build()
            val request = Request.Builder()
                .url("http://192.168.2.206:8080/login")
                .post(form)
                .build()
            val call = client.newCall(request)
            try {
                val response = call.execute()
                response.body?.let {
                    val response = it.string()
                    Log.d("TAG", response)
                    val jsonObject = JsonParser().parse(response).asJsonObject
                    val msg = jsonObject.get("msg").asString
                    val code = jsonObject.get("code").asInt
                    Log.d("TAG", "msg " + msg)
                    Log.d("TAG", "code " + code)
                    when(code) {
                        100 -> {
                            val token = jsonObject.get("token").asString
                            showToast("${msg}token${token}")
                        }
                        101 -> {
                            showToast(msg)
                        }
                        else -> {
                            showToast(msg)
                        }
                    }

                }
            }catch (e: Exception) {
                Log.d("TAG", "login: $e")
            }
        }.start()

    }

    private fun loginByRetrofit(name: String, password: String) {
        Thread{
            var retrofit = Retrofit.Builder()
                .client(OkHttpClient.Builder().addInterceptor(CommonInterceptor()).build())
                .baseUrl("http://192.168.2.206:8080")
                .build()
            var body =
                retrofit.create(HttpReq::class.java)
                    .postLogin(name, Utils.md5(password), Utils.md5("${name}jin"))
                    .execute().body()
            body?.let {
                showToast(it.string())
            }
        }.start()

    }

    private fun showToast(content: String) {
        runOnUiThread {
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show()
        }
    }
}