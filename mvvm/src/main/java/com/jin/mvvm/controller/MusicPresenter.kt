package com.jin.mvvm.controller

import com.jin.mvvm.data.Music
import com.jin.mvvm.listener.DataListener
import com.jin.mvvm.model.MusicModel

class MusicPresenter {

    enum class LoadState {
        LOADING,SUCCESS,ERROR
    }

    private var currentPage = 1
    private var pageSize = 20

    var musicList = DataListener<List<Music>>()
    var loadState = DataListener<LoadState>()


    private val musicModel by lazy {
        MusicModel()
    }

    fun getAllSong() {
        loadState.value = LoadState.LOADING
        musicModel.getSongList(currentPage,pageSize,object : MusicModel.OnMusicLoadingResult {
            override fun onSuccess(result: List<Music>) {
                musicList.value = result
                loadState.value = LoadState.SUCCESS
            }

            override fun onError(msg: String, code: Int) {
                loadState.value = LoadState.ERROR
            }

        })
    }
}