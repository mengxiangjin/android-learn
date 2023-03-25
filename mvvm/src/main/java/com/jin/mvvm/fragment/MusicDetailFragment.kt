package com.jin.mvvm.fragment

import com.jin.mvvm.play.PlayerPresenter


class MusicDetailFragment: BaseFragment() {

    private val playerPresenter by lazy {
        PlayerPresenter.instance
    }

    init {
        lifecycleProvider.addLifeCycleListener(playerPresenter)
    }
}