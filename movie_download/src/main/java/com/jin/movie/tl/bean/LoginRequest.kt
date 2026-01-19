package com.jin.movie.tl.bean

// 1. 请求体：对应 Python 的 json_data
data class LoginRequest(
    val userName: String,
    val password: String,
    val verifyCode: String = ""
)

// 2. 响应体外层：对应 Python 的 resp.json()
data class LoginResponse(
    val success: Boolean,
    val code: Int,        // 这里对应 20000
    val message: String,
    val data: TokenData?  // data 可能为空，所以用 ?
)

// 3. 响应体内部数据：对应 data 字段
data class TokenData(
    val token: String
)