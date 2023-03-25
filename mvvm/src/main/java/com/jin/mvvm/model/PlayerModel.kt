package com.jin.mvvm.model

import com.jin.mvvm.data.Music

class PlayerModel {
    fun getSongById(s: String): Music {
        return Music("name" + s,"cover" + s,"url" + s)
    }
}