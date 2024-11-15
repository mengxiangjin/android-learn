package com.jin.http_net.retrofit

import com.jin.http_net.retrofit.bean.SystemSwitchResponse
import com.jin.http_net.retrofit.bean.TopicResponse
import com.jin.http_net.retrofit.bean.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Streaming
import retrofit2.http.Url

interface RetrofitBaseInterface {

    @GET("posts")
    fun getTopic(): Call<List<TopicResponse>>

    @GET("posts/{path}")
    fun getTopicFromPath(@Path("path") path: String): Call<TopicResponse>

    @GET
    fun getTopicFromUrl(@Url url: String): Call<TopicResponse>

    @GET("posts")
    fun getTopicForQuery(@Query("userId") userId: Int): Call<List<TopicResponse>>

    @GET("posts")
    fun getTopicForQueryMap(@QueryMap map: Map<String,@JvmSuppressWildcards Any>): Call<List<TopicResponse>>

    @POST("user/register")
    @FormUrlEncoded
    fun register(@Field("username") username: String,@Field("password") password: String,@Field("repassword") repassword: String): Call<UserResponse>

    @POST("user/register")
    @FormUrlEncoded
    fun registerForMap(@FieldMap map: Map<String,@JvmSuppressWildcards Any>): Call<UserResponse>

    @GET("system/getSwitch")
    fun getSystemSwitchForAddHead(@Header("channel")channel: Int,@Header("androidVersionCode")androidVersionCode: Int,@Header("packageName")packageName: String): Call<SystemSwitchResponse>

    @GET("system/getSwitch")
    @Headers("channel:1","androidVersionCode:1","packageName:com.huanji.android")
    fun getSystemSwitchForAddHeads(): Call<SystemSwitchResponse>

    @POST("wx/login")
    @Headers("channel:1","androidVersionCode:1","packageName:com.huanji.android")
    fun login(@Body body: RequestBody): Call<ResponseBody>

    @Streaming
    @GET("android-studio-2024.1.2.12-windows.exe")
    fun getResource(): Call<ResponseBody>

    @Multipart
    @POST("upload")
    fun uploadResource(@Part("fileName")fileName: RequestBody,@Part file: MultipartBody.Part): Call<ResponseBody>

}