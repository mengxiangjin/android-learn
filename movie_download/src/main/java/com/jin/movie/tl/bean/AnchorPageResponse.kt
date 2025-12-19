package com.jin.movie.tl.bean

// 这个类用来对应 "data": { ... } 这一层
data class AnchorPageResponse(
    val success: Boolean,
    val message: String,
    val data: AnchorPageData? // 注意这里 data 是一个对象
)

// 这个类用来对应 data 内部的字段
data class AnchorPageData(
    val records: List<AnchorBean>?, // 真正的数据在这里
    val total: Int?,
    val size: Int?,
    val current: Int?
)