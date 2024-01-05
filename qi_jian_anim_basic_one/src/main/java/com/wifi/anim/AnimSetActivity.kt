package com.wifi.anim

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.wifi.anim.databinding.ActivityAnimSetBinding
import kotlin.math.cos
import kotlin.math.sin

class AnimSetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnimSetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnimSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        playSequentially()
//        playSetBuilder()
//        testAnimSetListener()
//        testAnimSetStartDelay()
//        testExample()
        testPropertyHolder()
    }

    private fun testPropertyHolder() {

    }

    private fun testExample() {
        val radius = 400
        val animSet = AnimatorSet()
        val count = 5
        val angle = 90 / 4f
        val animList = mutableListOf<Animator>()
        val animTarget = listOf(
            binding.viewBlue,
            binding.viewBlack,
            binding.viewRed,
            binding.viewGray,
            binding.viewGreen
        )
        var isClose = true
        binding.viewYellow.setOnClickListener {
            isClose = !isClose
            for (i in 0 until count) {
                val translateX = cos(Math.toRadians(angle * i.toDouble())) * radius
                val translateY = sin(Math.toRadians(angle * i.toDouble())) * radius
                val translateXAnim = if (isClose) {
                    ObjectAnimator.ofFloat(animTarget[i], "translationX", -translateX.toFloat(), 0f)
                } else {
                    ObjectAnimator.ofFloat(animTarget[i], "translationX", 0f, -translateX.toFloat())
                }

                val translateYAnim = if (isClose) {
                    ObjectAnimator.ofFloat(animTarget[i], "translationY", -translateY.toFloat(), 0f)
                } else {
                    ObjectAnimator.ofFloat(animTarget[i], "translationY", 0f, -translateY.toFloat())
                }

                val scaleXAnim = if (isClose) {
                    ObjectAnimator.ofFloat(animTarget[i], "scaleX", 1f, 0f)
                } else {
                    ObjectAnimator.ofFloat(animTarget[i], "scaleX", 0f, 1f)
                }
                val scaleYAnim = if (isClose) {
                    ObjectAnimator.ofFloat(animTarget[i], "scaleY", 1f, 0f)
                } else {
                    ObjectAnimator.ofFloat(animTarget[i], "scaleY", 0f, 1f)
                }
                val alphaAnim = if (isClose) {
                    ObjectAnimator.ofFloat(animTarget[i], "alpha", 1f, 0f)
                } else {
                    ObjectAnimator.ofFloat(animTarget[i], "alpha", 0f, 1f)
                }
                animList.add(translateXAnim)
                animList.add(translateYAnim)
                animList.add(scaleXAnim)
                animList.add(scaleYAnim)
                animList.add(alphaAnim)
            }
            animSet.playTogether(animList)
            animSet.duration = 1000
            animSet.start()
        }


    }

    private fun testAnimSetStartDelay() {
        val translateOneAnim = ObjectAnimator.ofFloat(binding.btnOne, "translationY", 0f, 200f)
        val translateTwoAnim = ObjectAnimator.ofFloat(binding.btnTwo, "translationY", 0f, 200f)
        translateTwoAnim.startDelay = 3000

        val animSet = AnimatorSet()
        animSet.play(translateTwoAnim).with(translateOneAnim)
        animSet.duration = 3000
        animSet.startDelay = 3000
        animSet.start()

    }

    private fun testAnimSetListener() {
        val bgColorOneAnim =
            ObjectAnimator.ofInt(binding.btnOne, "backgroundColor", Color.BLACK, Color.RED)
        val translateOneAnim = ObjectAnimator.ofFloat(binding.btnOne, "translationY", 0f, 200f)
        val translateTwoAnim = ObjectAnimator.ofFloat(binding.btnTwo, "translationY", 0f, 200f)

        translateOneAnim.repeatCount = ObjectAnimator.INFINITE
        translateOneAnim.repeatMode = ObjectAnimator.REVERSE
        val animSet = AnimatorSet()
        animSet.play(translateOneAnim).with(translateTwoAnim).before(bgColorOneAnim)
        animSet.duration = 3000

        animSet.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.d("lzy", "onAnimationStart: ")
            }

            override fun onAnimationEnd(animation: Animator) {
                Log.d("lzy", "onAnimationEnd: ")
            }

            override fun onAnimationCancel(animation: Animator) {
                Log.d("lzy", "onAnimationCancel: ")
            }

            override fun onAnimationRepeat(animation: Animator) {
                //AnimatorSet的onAnimationRepeat永远不会被调用
                Log.d("lzy", "onAnimationRepeat: ")
            }
        })
        /*
        * 倘若animSet设置了duration、interpolator、setTarget
        *       会覆盖掉objectAnim 单个设置的值（startDelay不受影响)
        * */
        animSet.duration = 3000
        animSet.interpolator = LinearInterpolator()
        animSet.setTarget(binding.btnOne)
        animSet.startDelay = 3000
        animSet.start()
        Handler(Looper.getMainLooper()).postDelayed({
            animSet.cancel()
        }, 8000)

    }

    private fun playSetBuilder() {
        val bgColorOneAnim =
            ObjectAnimator.ofInt(binding.btnOne, "backgroundColor", Color.BLACK, Color.RED)
        val translateOneAnim = ObjectAnimator.ofFloat(binding.btnOne, "translationY", 0f, 200f)
        val translateTwoAnim = ObjectAnimator.ofFloat(binding.btnTwo, "translationY", 0f, 200f)

        /*
        * animSet.play(translateOneAnim) -> AnimatorSet.Builder对象
        * with(anim):同时
        * after(anim)：先播放anim动画，之后播放this代表动画
        * before(anim)：先播放this代表动画，之后播放anim动画
        * */
        val animSet = AnimatorSet()
        val playAnim = animSet.play(translateOneAnim)
        playAnim.with(translateTwoAnim).after(bgColorOneAnim)
        animSet.duration = 3000
        animSet.start()
    }

    private fun playSequentially() {
        val bgColorOneAnim =
            ObjectAnimator.ofInt(binding.btnOne, "backgroundColor", Color.BLACK, Color.RED)
        val translateOneAnim = ObjectAnimator.ofFloat(binding.btnOne, "translationY", 0f, 200f)
        val translateTwoAnim = ObjectAnimator.ofFloat(binding.btnTwo, "translationY", 0f, 200f)

        /*
        * playSequentially:顺序播放
        * playTogether:同时播放
        * AnimatorSet动画排队队列：bgColorOneAnim，translateOneAnim，translateTwoAnim
        * */
        val animSet = AnimatorSet()
        animSet.duration = 3000
//        animSet.playSequentially(bgColorOneAnim,translateOneAnim,translateTwoAnim)
        animSet.playTogether(bgColorOneAnim, translateOneAnim, translateTwoAnim)
        animSet.start()
    }
}