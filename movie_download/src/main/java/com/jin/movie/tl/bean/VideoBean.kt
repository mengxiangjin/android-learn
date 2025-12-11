package com.jin.movie.tl.bean

import java.io.Serializable
//短视频
// 外层响应
data class VideoListResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: VideoListPageData?
)

// 分页数据容器
data class VideoListPageData(
    val records: List<VideoBean>?,
    val total: Int,
    val size: Int,
    val current: Int,
    val pages: Int
)

// 单个视频实体
data class VideoBean(
    val id: Int,
    val userId: Int,
    val nickName: String?,
    val userLogo: String?,

    val videoTitle: String?,     // 标题/文案
    val converImage: String?,    // 封面图
    val videoUrl: String?,       // 视频地址

    val videoPraises: Int,       // 点赞数
    val videoViews: Int,         // 浏览数
    val durationsTime: Int,      // 时长(秒)
    val addTime: String?
) : Serializable