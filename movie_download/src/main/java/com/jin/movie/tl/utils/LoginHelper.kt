package com.jin.movie.tl.utils

import android.content.Context
import android.util.Log
import com.jin.movie.MyApp
import com.jin.movie.tl.bean.LoginRequest
import com.jin.movie.tl.bean.LoginResponse
import com.jin.movie.tl.net.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object LoginHelper {

    /**
     * @param phone 手机号
     * @param pass 密码
     * @param signString 必须从外部传入计算好的签名 (对应 Python 的 get_token_sign())
     */
    fun login(phone: String, pass: String,callback: ((String) -> Unit)) {

        // 1. 准备 Body (JSON 数据)
        val requestBody = LoginRequest(
            userName = phone,
            password = pass,
            verifyCode = ""
        )

        val queryParams = SignUtils.getSignedParams("/oauth/oauth/login")



        // 3. 发起请求
        RetrofitClient.apiService.login(queryParams, requestBody)
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    val body = response.body()

                    // Python返回: code: 20000 代表成功
                    if (response.isSuccessful && body != null && body.code == 20000) {
                        val token = body.data?.token

                        if (!token.isNullOrEmpty()) {
                            Log.d("LoginHelper", "登录成功! Token: $token")

                            // 4. 【核心】自动保存 Token 到 SP
                            saveToken(token)
                            callback.invoke(token)
                        } else {
                            Log.e("LoginHelper", "Token 为空")
                            callback.invoke("Token 为空")
                        }
                    } else {
                        Log.e("LoginHelper", "登录失败: ${body?.message}, Code: ${body?.code}")
                        callback.invoke("登录失败: ${body?.message}, Code: ${body?.code}")
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("LoginHelper", "网络请求异常", t)
                    callback.invoke("网络请求异常")
                }
            })
    }

    private fun saveToken(token: String) {
        // 使用 MyApplication 全局上下文
        val sp = MyApp.instance.getSharedPreferences("my_app_sp", Context.MODE_PRIVATE)
        sp.edit().apply {
            // 存入的 Key 必须和 RetrofitClient 拦截器里读取的 Key 一致
            putString("token", token) // 注意：我看你之前的代码Token有前缀 "aiya_"，如果接口返回没有，这里可能需要手动拼接，如果返回自带就直接存 token
            // 如果 Python 返回的只是 '276ce...' 而你需要 'aiya_276ce...'，记得在这里拼接
            // 比如: putString("token", "aiya_$token")
            // 或者: putString("token", token)
            apply()
        }
        Log.i("LoginHelper", "Token 已更新至 SharedPreferences")
    }
}