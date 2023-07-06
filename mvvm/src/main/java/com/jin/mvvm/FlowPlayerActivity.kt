package com.jin.mvvm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jin.mvvm.databinding.ActivityFlowPlayerActiityBinding
import com.jin.mvvm.play.IPlayerCallback
import com.jin.mvvm.play.PlayerPresenter

class FlowPlayerActivity : AppCompatActivity(), IPlayerCallback {


    private lateinit var binding: ActivityFlowPlayerActiityBinding

    private val playerPresenter by lazy {
//        PlayerPresenter.instance
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowPlayerActiityBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        playerPresenter.registerCallback(this)
        initListener()
        initDataListener()
    }

    private fun initListener() {
        binding.playOrPause.setOnClickListener {
//            playerPresenter.doPlayOrPause()
        }
    }

    private fun initDataListener() {
//        playerPresenter.currentState.addListener {
//            if (it == PlayerPresenter.PlayState.PLAYING) {
//                binding.playOrPause.text = "暂停"
//            } else {
//                binding.playOrPause.text = "播放"
//            }
//        }
//        playerPresenter.currentMusic.addListener {
//
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        playerPresenter.unregisterCallback(this)
    }

    override fun onTitleChange(title: String) {
        TODO("Not yet implemented")
    }

    override fun onProgressChange(current: Int) {
        TODO("Not yet implemented")
    }

    override fun onCoverChange(cover: String) {
        TODO("Not yet implemented")
    }

    override fun onPlaying() {
        binding.playOrPause.text = "暂停"
    }

    override fun onPlayingPause() {
        binding.playOrPause.text = "播放"
    }
}