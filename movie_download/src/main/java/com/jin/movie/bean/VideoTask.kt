package com.jin.movie.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_tasks")
data class VideoTask(
    @PrimaryKey val url: String, // 用 URL 做主键
    val title: String,
    val coverUrl: String,
    var localPath: String,     // m3u8 本地路径
    var progress: Int = 0,     // 0-100
    var state: Int = STATE_WAIT, // 状态
    var totalTs: Int = 0,      // 总切片数
    var currentTs: Int = 0     // 当前下载切片数
) {
    companion object {
        const val STATE_WAIT = 0
        const val STATE_DOWNLOADING = 1
        const val STATE_PAUSE = 2
        const val STATE_SUCCESS = 3
        const val STATE_FAIL = 4
        const val STATE_MERGING = 5 // 【新增】合并中
    }
}