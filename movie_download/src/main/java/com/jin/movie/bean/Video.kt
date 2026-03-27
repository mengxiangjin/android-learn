package com.jin.movie.bean

import com.jin.movie.dog.DogMainActivity

data class Video(
    val detailUrl: String,  //详情页
    val title: String,       // 标题
    val coverUrl: String,    // 封面图链接
    val duration: String,    // 时长
    val playCount: String,   // 播放量
    // 新增：默认值为 0，方便后续赋值
    var page: Int = 0
) {

    // 扩展属性：通过封面图地址推算 m3u8 地址
    // 逻辑：去掉最后一个 "/" 后的内容，替换为 "index.m3u8"
    val movieUrl: String
        get() = if (detailUrl.startsWith("${DogMainActivity.BASE_URL}/download/")) {
            detailUrl
        }else if(coverUrl.contains("/")) {
            coverUrl.substringBeforeLast("/") + "/index.m3u8"
        } else {
            ""
        }

    override fun toString(): String {
        return "Video(detailUrl='$detailUrl', title='$title', coverUrl='$coverUrl', duration='$duration', playCount='$playCount', page=$page, movieUrl='$movieUrl')"
    }


}
