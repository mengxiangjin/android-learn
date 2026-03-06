package com.jin.movie.tl.bean


import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * API 响应包装类
 */
data class ApiResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: DataBean
) : Serializable {
    override fun toString(): String {
        return "ApiResponse(success=$success, code=$code, message='$message', data=$data)"
    }
}

/**
 * 数据载荷类
 * 包含分页信息和记录列表
 */
data class DataBean(
    val records: List<VideoRecord>,
    val total: Int,
    val size: Int,
    val current: Int,
    val pages: Int,
    val optimizeCountSql: Boolean,
    val hitCount: Boolean,
    val searchCount: Boolean
    // val orders: List<Any>? // JSON中为空数组，暂时忽略或用 List<Any>
) : Serializable {
    override fun toString(): String {
        return "DataBean(records=$records, total=$total, size=$size, current=$current, pages=$pages, optimizeCountSql=$optimizeCountSql, hitCount=$hitCount, searchCount=$searchCount)"
    }
}

/**
 * 视频记录实体类
 */
data class VideoRecord(
    // --- 标识符 ---
    val id: Int,
    val anchorUserId: Int,
    val liveId: String?,
    val fileId: String?,

    // --- 用户信息 ---
    val nickName: String?,
    val userLogo: String?,
    val userSlogan: String?,

    // --- 状态与排序 ---
    val buyStatus: Int,
    val videoStatus: Int, // e.g., 10
    val s3Status: Int,    // e.g., 1
    val videoSort: Int,
    val globalRecommendSort: Int,
    val globalRecommend: Boolean,

    // --- 推荐相关 (JSON中有多个类似字段) ---
    // 1. 大数值 (e.g., 1754636248)
    val isRecommend: Long,
    // 2. 整数值 (e.g., 2, 4, 5) - 这是 JSON 中新增发现的字段
    val recommend: Int,
    // 3. 可空字段 (JSON 中为 null)
    val isRecommended: Int?,

    // --- 视频内容 ---
    val videoTitle: String?,
    val title: String?, // JSON 中通常为 null
    val descs: String?, // 描述

    // --- 封面 ---
    // JSON 字段名为 "converImage" (拼写错误)，映射为 coverImage
    @SerializedName(value = "coverImage", alternate = ["converImage"])
    val coverImage: String?,

    // --- 播放地址 ---
    val videoUrl: String?,
    val s3VideoUrl: String?,

    // --- 预览相关 ---
    val preview: String?, // 逗号分隔的图片 URL 字符串
    val previewVideoUrl: String?,
    val previewVideoCoverImage: String?,

    // --- 时间与时长 ---
    val startTime: String?,
    val endTime: String?,
    val addTime: String?,
    val updatedTime: String?,

    // JSON 字段名为 "durationsTime"，映射为 durationTime
    @SerializedName("durationsTime")
    val durationTime: Long,

    // --- 数据统计 ---
    val heat: Int,          // 热度
    val videoViews: Int,    // 观看数
    val videoSize: Long,    // 文件大小
    val praise: Int,        // 点赞数
    val collect: Int,       // 收藏数
    val comment: Int,       // 评论数
    val share: Int,         // 分享数
    val download: Int,      // 下载数
    val buy: Int,           // 购买数

    // --- 货币/费用 ---
    val videoCoin: Int,
    val downloadCoin: Int,

    // --- 用户交互状态 (大部分为 null) ---
    val whetherDownload: Boolean,
    val isPraise: Int?,
    val isCollect: Int?,
    val isDownLoad: Int?,
    val isFollow: Int?,
    val isProduct: Int?,

    // --- 【核心修复】标签列表 ---
    // 之前是 List<String> 导致崩溃，现在改为 List<VideoTag>
    // 即使 JSON 是 []，Gson 也能正确解析为空列表
    val videoTags: List<VideoTag>?

) : Serializable {
    override fun toString(): String {
        return "VideoRecord(id=$id, anchorUserId=$anchorUserId, liveId=$liveId, fileId=$fileId, nickName=$nickName, userLogo=$userLogo, userSlogan=$userSlogan, buyStatus=$buyStatus, videoStatus=$videoStatus, s3Status=$s3Status, videoSort=$videoSort, globalRecommendSort=$globalRecommendSort, globalRecommend=$globalRecommend, isRecommend=$isRecommend, recommend=$recommend, isRecommended=$isRecommended, videoTitle=$videoTitle, title=$title, descs=$descs, coverImage=$coverImage, videoUrl=$videoUrl, s3VideoUrl=$s3VideoUrl, preview=$preview, previewVideoUrl=$previewVideoUrl, previewVideoCoverImage=$previewVideoCoverImage, startTime=$startTime, endTime=$endTime, addTime=$addTime, updatedTime=$updatedTime, durationTime=$durationTime, heat=$heat, videoViews=$videoViews, videoSize=$videoSize, praise=$praise, collect=$collect, comment=$comment, share=$share, download=$download, buy=$buy, videoCoin=$videoCoin, downloadCoin=$downloadCoin, whetherDownload=$whetherDownload, isPraise=$isPraise, isCollect=$isCollect, isDownLoad=$isDownLoad, isFollow=$isFollow, isProduct=$isProduct, videoTags=$videoTags)"
    }
}

/**
 * 视频标签实体类
 * 对应 JSON: {"id": 15, "name": "黄金", "type": 0, ...}
 */
data class VideoTag(
    val id: Int,
    val name: String?,
    val type: Int,
    val sort: Int,
    val status: Int,
    val addTime: String?,
    val updatedTime: String?
) : Serializable {
    override fun toString(): String {
        return "VideoTag(id=$id, name=$name, type=$type, sort=$sort, status=$status, addTime=$addTime, updatedTime=$updatedTime)"
    }
}