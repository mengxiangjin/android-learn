package com.jin.movie.tl.net
import com.jin.movie.tl.bean.AnchorPageResponse
import com.jin.movie.tl.bean.AnchorResponse
import com.jin.movie.tl.bean.ApiResponse
import com.jin.movie.tl.bean.LoginRequest
import com.jin.movie.tl.bean.LoginResponse
import com.jin.movie.tl.bean.UserInfoResponse
import com.jin.movie.tl.bean.VideoListResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface VideoApi {
    // 将 /1/10 改为 /{page}/{size}
    @POST("live/live/video/v2/globalRecommend/{page}/{size}")
    fun getRecommendVideos(
        @Path("page") page: Int,
        @Path("size") size: Int,
        @QueryMap params: Map<String, String>,
        @Body requestBody: Map<String,  @JvmSuppressWildcards Any>
    ): Call<ApiResponse>


    @POST("/live/live/video/list/{page}/{size}")
    fun searchVideos(
        @Path("page") page: Int,
        @Path("size") size: Int,
        @QueryMap queryParams: Map<String, String>, // 签名参数
        @Body requestBody: Map<String,  @JvmSuppressWildcards Any>
    ): Call<ApiResponse>


    // 【新增】短剧接口
    @POST("/user/plot/video/list/{page}/{size}")
    fun getPlotVideos(
        @Path("page") currentPage: Int,
        @Path("size") pageSize: Int,
        @QueryMap queryParams: Map<String, String>,
        @Body body: Map<String, @JvmSuppressWildcards Any>
    ): Call<ApiResponse>

    @POST("/user/user/list") // 确保这里的路径是对的
    fun searchAnchors(
        @QueryMap signParams: Map<String, String>,
        @Body body: Map<String, String>
    ): Call<AnchorResponse>


    // 【新增】获取主播个人主页详情
    @GET("user/userInfo/getUserInfo")
    fun getUserInfo(
        @QueryMap queryParams: Map<String, String>
    ): Call<UserInfoResponse>

    // VideoApi.kt


    // 【新增】获取指定用户的短视频列表
    @POST("/user/video/list/{page}/{size}")
    fun getUserVideoList(
        @Path("page") page: Int,
        @Path("size") size: Int,
        @QueryMap queryParams: Map<String, String>, // 签名放在 URL 参数里
        @Body requestBody: Map<String, String>      // userId 放在 Body 里
    ): Call<VideoListResponse>

    // VideoApi.kt 中添加

    // 【新增】获取主播回放列表
    // 接口地址: /live/live/video/anchor
    // 参数: page, size, anchorUserId, sign...
    @GET("/live/live/video/anchor/{page}/{size}")
    fun getAnchorReplayList(
        @Path("page") page: Int,
        @Path("size") size: Int,
        @QueryMap queryParams: Map<String, String>
    ): Call<ApiResponse>


    // 在 VideoApi.kt 中添加以下内容

    // 1. 获取我的关注列表
    // 参数放在 path 中: page, size
    // 同时也需要 sign (queryParams) 和可能的 userId (body)
    @GET("/user/follow/followList/{page}/{size}")
    fun getFollowList(
        @Path("page") page: Int,
        @Path("size") size: Int,
        @QueryMap queryParams: Map<String, String>,
    ): Call<AnchorPageResponse> // 这里假设返回结构和 SearchVideos 类似，是 ApiResponse<Page<AnchorBean>>

    // 2. 获取我的粉丝列表 (请核对 URL)
    @POST("/user/follow/fansList/{page}/{size}")
    fun getFansList(
        @Path("page") page: Int,
        @Path("size") size: Int,
        @QueryMap queryParams: Map<String, String>,
    ): Call<AnchorPageResponse>


    @POST("oauth/oauth/login")
    fun login(
        @QueryMap params: Map<String, String>, // 对应 Python 的 params
        @Body request: LoginRequest            // 对应 Python 的 json_data
    ): Call<LoginResponse>                     // 返回类型改为刚才定义的 LoginResponse
}