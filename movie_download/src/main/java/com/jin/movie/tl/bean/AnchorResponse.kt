package com.jin.movie.tl.bean

import java.io.Serializable

/**
 * 主播列表的 API 响应包装类
 */
data class AnchorResponse(
    val success: Boolean,
    val code: Int,
    val message: String,
    val data: List<AnchorBean>?
) : Serializable

/**
 * 主播/用户实体类 (完整版)
 * 对应接口: /user/user/list
 */
data class AnchorBean(
    // --- 核心标识 ---
    val id: Int,
    val userName: String?,      // 用户名/账号 (e.g. "18255818127")
    val nickName: String?,      // 昵称 (e.g. "梦蝶女王S")

    // --- 个人资料 ---
    val userLogo: String?,      // 头像 URL
    val userSlogan: String?,    // 签名/Slogan (e.g. "私信有福利...")
    val userGender: Int,        // 性别: 1=男, 2=女
    val userMobile: String?,    // 手机号 (可能为空串)
    val userEmail: String?,     // 邮箱 (可能为空串)

    // --- 敏感信息 (虽然 JSON 返回了，但前端一般不展示) ---
    val userPassword: String?,  // 加密的密码 hash

    // --- 状态标志 ---
    val followStatus: Int,      // 关注状态: 0=未关注, 1=已关注
    val liveStatus: Int,        // 直播状态: 0=未直播, 1=直播中
    val onlineStatus: Int,      // 在线状态: 0=离线, 1=在线
    val userType: Int,          // 用户类型: 1 or 2 (可能是主播等级或角色)
    val userStatus: Int,        // 账号状态: 10 (正常?)

    // --- 位置信息 (JSON中多为 null) ---
    val province: String?,
    val city: String?,
    val address: String?,
    val lng: String?,           // 经度 (建议用String或Double?，防止解析错误)
    val lat: String?,           // 纬度

    // --- 时间信息 ---
    val addTime: String?,       // 注册时间
    val lastLoginTime: String?, // 最后登录时间
    val updatedTime: String?,   // 信息更新时间

    // --- 统计数据 ---
    val collect: Int,           // 收藏数/被收藏数

    // --- 预留字段 ---
    val tables: Any?            // JSON 返回 null，类型未知，暂时用 Any? 占位
) : Serializable