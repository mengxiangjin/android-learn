package com.jin.camera

import android.content.Intent
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.DecelerateInterpolator
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.jin.camera.databinding.ActivityMainBinding
import com.jin.camera.widgit.CameraRotateAnim

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnExample.setOnClickListener {
            startActivity(Intent(this,CameraRotateActivity::class.java))
        }

        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvProgress.text = "$progress"
                binding.cameraView.setProgress(progress)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        initReverse()
    }

    private var isOpen = false
    private fun initReverse() {
        val openAnim = CameraRotateAnim(0, 90)
        openAnim.duration = 2000
        openAnim.fillAfter = true
        openAnim.interpolator = AccelerateInterpolator()

        openAnim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.imgOne.isVisible = false
                binding.imgTwo.isVisible = true
                val anim = CameraRotateAnim(90,180,true)
                anim.duration = 2000
                anim.fillAfter = true
                anim.interpolator = DecelerateInterpolator()


                binding.llContent.startAnimation(anim)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

        })

        val closeAnim = CameraRotateAnim(180, 90)
        closeAnim.duration = 2000
        closeAnim.fillAfter = true
        closeAnim.interpolator = AccelerateInterpolator()
        closeAnim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.imgOne.isVisible = true
                binding.imgTwo.isVisible = false
                val anim = CameraRotateAnim(90,0,true)
                anim.duration = 2000
                anim.fillAfter = true
                anim.interpolator = DecelerateInterpolator()

                binding.llContent.startAnimation(anim)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

        })


        binding.btnReverse.setOnClickListener {
            isOpen = !isOpen
            if (isOpen) {
                binding.llContent.startAnimation(openAnim)
            } else {
                binding.llContent.startAnimation(closeAnim)
            }

        }
    }
}