package com.jin.mvvm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jin.mvvm.base.BaseActivity
import com.jin.mvvm.data.Music
import com.jin.mvvm.databinding.ActivityMainBinding
import com.jin.mvvm.databinding.ActivityPlayerBinding
import com.jin.mvvm.model.PlayerModel
import com.jin.mvvm.play.IPlayerCallback
import com.jin.mvvm.play.PlayerPresenter

class PlayerActivity : BaseActivity(), IPlayerCallback {


    private lateinit var binding: ActivityPlayerBinding

    private val playerPresenter by lazy {
        PlayerPresenter(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        playerPresenter.registerCallback(this)
        initListener()
        initDataListener()
    }

    private fun initListener() {
        binding.playOrPause.setOnClickListener {
            playerPresenter.doPlayOrPause()
        }
        binding.next.setOnClickListener {
            playerPresenter.playNext()
        }
        binding.pre.setOnClickListener {
            playerPresenter.playPre()
        }
    }

    private fun initDataListener() {
        playerPresenter.currentMusic.addListener {
            binding.title.text = it?.name
        }
        playerPresenter.currentState.addListener {
           when(it) {
               PlayerPresenter.PlayState.PLAYING -> {
                   binding.playOrPause.text = "暂停"
               }
               PlayerPresenter.PlayState.PAUSE -> {
                   binding.playOrPause.text = "播放"
               }
               else -> {}
           }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        playerPresenter.unregisterCallback(this)
    }

    override fun onTitleChange(title: String) {
       binding.title.text = title
    }

    override fun onProgressChange(current: Int) {

    }

    override fun onCoverChange(cover: String) {
       println("封面更新" + cover)
    }

    override fun onPlaying() {
        binding.playOrPause.text = "暂停"
    }

    override fun onPlayingPause() {
        binding.playOrPause.text = "播放"
    }
}