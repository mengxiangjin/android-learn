package com.jin.mvvm.play

import com.jin.mvvm.MusicPlayer
import com.jin.mvvm.data.Music
import com.jin.mvvm.lifecycle.IlifeCycle
import com.jin.mvvm.lifecycle.LifecycleOwner
import com.jin.mvvm.listener.DataListener
import com.jin.mvvm.model.PlayerModel

class PlayerPresenter(owner: LifecycleOwner){


    var currentMusic = DataListener<Music>()
    var currentState = DataListener<PlayState>()

    private val playerModel by lazy {
        PlayerModel()
    }

    private val musicPlayer by lazy {
        MusicPlayer()
    }

    init {

    }


    enum class PlayState{
        NONE,PLAYING,PAUSE,LOADING
    }

//
//    private var callbacks = ArrayList<IPlayerCallback>()
//
//    fun registerCallback(callback: IPlayerCallback) {
//        if (!callbacks.contains(callback)) {
//            callbacks.add(callback)
//        }
//    }
//
//    fun unregisterCallback(callback: IPlayerCallback) {
//        if (callbacks.contains(callback)) {
//            callbacks.remove(callback)
//        }
//    }

    fun doPlayOrPause() {
        if (currentMusic == null) {
            currentMusic.value = playerModel.getSongById("123")
        }
        musicPlayer.playMusic(currentMusic.value!!)
//        dispatchTitleChanged("当前播放的标题")
//        dispatchCoverChanged("当前播放的封面")
//        if (currentState.value != PlayState.PLAYING) {
//            currentState.value = PlayState.PLAYING
//            dispatchPlayingState()
//        } else {
//            currentState.value = PlayState.PAUSE
//            dispatchPauseState()
//        }
    }

//    private fun dispatchPlayingState() {
//        callbacks.forEach {
//            it.onPlaying()
//        }
//    }
//
//    private fun dispatchPauseState() {
//        callbacks.forEach {
//            it.onPlayingPause()
//        }
//    }
//
//    private fun dispatchTitleChanged(title: String) {
//        callbacks.forEach {
//            it.onTitleChange(title)
//        }
//    }
//
//    private fun dispatchCoverChanged(cover: String) {
//        callbacks.forEach {
//            it.onCoverChange(cover)
//        }
//    }

    fun playNext() {
        currentMusic.value = playerModel.getSongById("下一首 bbb")
        currentState.value =PlayState.PLAYING
//        dispatchTitleChanged("切换到下一首，标题变化了")
//        dispatchCoverChanged("切换到下一首，封面变化了")
    }

    fun playPre() {
        currentMusic.value = playerModel.getSongById("上一首 aaa")
        currentState.value =PlayState.PLAYING
//        dispatchTitleChanged("切换到上一首，标题变化了")
//        dispatchCoverChanged("切换到上一首，封面变化了")
    }

    inner class ViewCycleImpl: IlifeCycle {
        override fun onCreate() {
        }

        override fun onStart() {
            println("监听网络变化")
        }

        override fun onResume() {
        }

        override fun onPause() {
        }

        override fun onStop() {
            println("停止监听")
        }

        override fun onDestroy() {
        }
    }



}