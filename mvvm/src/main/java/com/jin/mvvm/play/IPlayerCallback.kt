package com.jin.mvvm.play

interface IPlayerCallback {

    fun onTitleChange(title: String)
    fun onProgressChange(current: Int)
    fun onCoverChange(cover: String)
    fun onPlaying()
    fun onPlayingPause()
}