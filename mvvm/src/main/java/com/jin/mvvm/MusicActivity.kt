package com.jin.mvvm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jin.mvvm.base.BaseActivity
import com.jin.mvvm.controller.MusicPresenter
import com.jin.mvvm.databinding.ActivityMusicBinding

class MusicActivity : BaseActivity() {


    private lateinit var binding: ActivityMusicBinding

    private val musicPresenter by lazy {
        MusicPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initDataListener()
        initListener()
    }

    private fun initListener() {
        binding.songList.setOnClickListener {
            musicPresenter.getAllSong()
        }
    }

    private fun initDataListener() {
        musicPresenter.musicList.addListener {
            //非主线程更新UI会闪退
            binding.tips.text = it?.size.toString()
            println("加载状态" + it?.size)
            println(Thread.currentThread().name)
        }
        musicPresenter.loadState.addListener {
            println("加载状态" + it?.name)
            println(Thread.currentThread().name)
        }
    }
}