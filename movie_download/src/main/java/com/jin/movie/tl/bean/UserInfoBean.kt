package com.jin.movie.tl.bean


import java.io.Serializable

// 最外层的响应结构
data class UserInfoResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: UserInfoData?
)

// 详细的用户信息 (data 字段)
data class UserInfoData(
    val userId: Int,
    val nickName: String?,
    val userLogo: String?,          // 头像
    val userSlogan: String?,        // 签名
    val userGender: Int,            // 1男 2女
    val userMobile: String?,

    // --- 状态数据 ---
    val followStatus: Int,          // 0未关注 1已关注
    val liveStatus: Int,            // 直播状态
    val svipStatus: Int,
    val guardStatus: Int,
    val voiceStatus: Int,
    val adminStatus: Int,
    val isCustomerService: Boolean,

    // --- 统计数据 ---
    val userFans: Int,              // 粉丝数
    val userFollows: Int,           // 关注数
    val userPraises: Int,           // 获赞数
    val collect: Int,               // 收藏数
    val userVieweds: Int,           // 被浏览数
    val userVideos: Int,            // 视频数
    val userLiveVideos: Int,        // 直播记录数
    val goodNumber: Int,            // 靓号?
    val dynamicNumber: Int,         // 动态数

    // --- 资产与等级 ---
    val userIntegral: Int,          // 积分
    val userCoin: Int,              // 金币
    val userConsumption: Int,       // 消费
    val totalCharge: String?,       // 总充值
    val totalAmount: String?,       // 总金额
    val userGrade: Int,             // 用户等级
    val wealthGrade: Int,           // 财富等级
    val nobleGrade: Int,            // 贵族等级
    val userGradeIcon: String?,     // 等级图标 URL

    // --- 视觉素材 ---
    val floatingBackgroundImage: String?, // 悬浮背景图(可能用于个人主页背景)
    val mountName: String?,
    val mountCoverImage: String?,
    val mountSwf: String?,
    val cardIcon: String?,
    val bubbleIcon: String?,
    val titleIcon: String?,

    // --- 榜单列表 (List<String> 代表头像URL列表) ---
    val contributionUserList: List<String>?,
    val guardUserList: List<String>?,
    val titleList: List<Any>?,      // 暂时不确定类型，用 Any? 或 List<String>?

    // --- 位置信息 ---
    val province: String?,
    val city: String?,
    val address: String?,
    val lng: String?,
    val lat: String?,

    // --- 其他 ---
    val imUserSig: String?,
    val rimUrl: String?,
    val rimName: String?,
    val mountSwfTime: String?,
    val userBirthday: String?,
    val autoReplyContent: String?,
    val chatCoin: Int,
    val kickTime: Long,
    val userEnterRoomEffect: String?,
    val paradeNumber: Int,
    val plotNumber: Int,
    val roomId: String?,
    val tables: Any?
) : Serializable