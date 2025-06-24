package com.jin.movie.utils

import android.content.Context
import android.os.Environment
import com.jin.movie.bean.PlayBackItem
import java.io.File
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicInteger

object Utils {

    var TASK_COUNTS_ITEM = AtomicInteger(0)

    fun getRootFile(): File {
        return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"movie")
    }

    fun getPathForMovieName(playBackItem: PlayBackItem): String {
        return getPathForUserRoot(playBackItem) + File.separator + playBackItem.videoTitle
    }

    fun getPathForUserRoot(playBackItem: PlayBackItem): String {
        return getRootFile().path + File.separator + playBackItem.nickName
    }

    fun getPathForM3U8File(playBackItem: PlayBackItem): String {
        return getPathForMovieName(playBackItem) + File.separator + "temp.m3u8"
    }


    fun getPathForFFMpegFile(playBackItem: PlayBackItem): String {
        return getPathForMovieName(playBackItem) + File.separator + "ffmpeg.txt"
    }




}