package com.jin.movie.bean


data class Actor(
    val name: String,
    val avatarUrl: String,
    val detailUrl: String, // 点击跳转的详情页链接
    // 可选字段，有的网站会有
    val videoCount: String = ""
)