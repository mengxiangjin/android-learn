package com.jin.mvvm.model

import com.jin.mvvm.data.Music

class MusicModel {



    fun getSongList(currentPage: Int, pageSize: Int,callback: OnMusicLoadingResult) {
        Thread() {
            val result = mutableListOf<Music>()
            for (i in 0 ..20) {
                result.add(Music("xx${i}","cover${i}","url${i}"))
            }
            callback.onSuccess(result)
        }.start()


    }

    interface OnMusicLoadingResult {
        fun onSuccess(result: List<Music>)
        fun onError(msg: String,code: Int)
    }
}