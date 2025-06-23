package com.jin.nixiang.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface HttpReq {

    @POST("/login")
    @FormUrlEncoded
    fun postLogin(@Field("username")username: String, @Field("password")password: String, @Field("sign")rememberMe: String): Call<ResponseBody>


    @GET("/film")
    fun getFilm(): Call<ResponseBody>
}