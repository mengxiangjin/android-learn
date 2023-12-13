package com.wifi.anim

import android.animation.AnimatorSet
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import com.wifi.anim.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnScaleOne.setOnClickListener {
            //从xml中映射动画
            val animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.rotate_one)
            binding.imgDisplay.startAnimation(animation)
        }
    }


    private fun starScaleAnim() {
        val scaleAnimation = ScaleAnimation(
            0f,
            1.4f,
            0f,
            1.4f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        scaleAnimation.duration = 700
        scaleAnimation.fillAfter = true
        scaleAnimation.repeatCount = Animation.INFINITE
        scaleAnimation.repeatMode = Animation.RESTART
        scaleAnimation.startOffset = 3000
        binding.imgDisplay.startAnimation(scaleAnimation)
    }


    private fun starAlphaAnim() {
        /*
        * fromAlpha,toAlpha
        * */
        val alphaAnimation = AlphaAnimation(0f,1f)
        alphaAnimation.duration = 700
        alphaAnimation.fillAfter = true
        alphaAnimation.repeatCount = Animation.INFINITE
        alphaAnimation.repeatMode = Animation.RESTART
        alphaAnimation.startOffset = 3000
        binding.imgDisplay.startAnimation(alphaAnimation)
    }


    private fun starRotateAnim() {
        /*
        * 旋转中心点
        * */
        val rotateAnimation = RotateAnimation(0f,45f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
        rotateAnimation.duration = 700
        rotateAnimation.fillAfter = true
        rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.repeatMode = Animation.RESTART
        rotateAnimation.startOffset = 3000
        binding.imgDisplay.startAnimation(rotateAnimation)
    }

    private fun starTranslateAnim() {
        val transAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f)
        transAnimation.duration = 700
        transAnimation.fillAfter = true
        transAnimation.repeatCount = Animation.INFINITE
        transAnimation.repeatMode = Animation.RESTART
        transAnimation.startOffset = 3000
        binding.imgDisplay.startAnimation(transAnimation)
    }

    private fun starSetAnim() {
        val transAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f)
        val rotateAnimation = RotateAnimation(0f,45f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
        val alphaAnimation = AlphaAnimation(0f,1f)
        /*
        * AnimationSet构造函数：
        * true：动画共用一个插值器
        * */
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(transAnimation)
        animationSet.addAnimation(rotateAnimation)
        animationSet.addAnimation(rotateAnimation)
        animationSet.duration = 7000
        animationSet.repeatCount = Animation.INFINITE
        animationSet.repeatMode = Animation.RESTART
    }
}