package com.jin.nixiang.interceptor

import com.jin.nixiang.utils.Utils
import okhttp3.Interceptor
import okhttp3.Response


class CommonInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {


        // 每次发送请求，都在请求头中带sign
        //1  sign 的加密方式，当前时间使用md5加密
        val ctime = (System.currentTimeMillis() / 1000).toString()
        val sign = Utils.md5(ctime)

        // 2 把当前时间和sign都加到请求头中
        val request  =
            chain.request().newBuilder().addHeader("ctime", ctime).addHeader("sign", sign).build()

        // 3 继续往下执行,执行下面的拦截器，返回响应对象
        val response = chain.proceed(request)

        return response
    }

}