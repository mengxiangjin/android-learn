package com.wifi.anim

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
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
            val animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.set_two)
            binding.imgDisplay.startAnimation(animation)

            startPropertyAnim()
            startColorAnim()
        }

        binding.btnPropertyAnim.setOnClickListener {
            startActivity(Intent(this@MainActivity,ObjectAnimActivity::class.java))
        }

        binding.btnAnimSet.setOnClickListener {
            startActivity(Intent(this@MainActivity,AnimSetActivity::class.java))
        }

        binding.btnPropertyValueAnim.setOnClickListener {
            startActivity(Intent(this@MainActivity,PropertyValueAnimActivity::class.java))
        }

        binding.btnGroupAnim.setOnClickListener {
            startActivity(Intent(this@MainActivity,GroupAnimActivity::class.java))
        }

        ofObjectIntroduce()
    }

    private fun startPropertyAnim() {
        //监听属性动画的进度值，改变view属性从而触发动画效果
        val valueAnim = ValueAnimator.ofInt(0,200,0)
        valueAnim.addUpdateListener {
            //ofInt -> Int
            //ofFloat -> Float
            val animValue = it.animatedValue as Int
            binding.imgDisplay.layout(animValue,animValue,animValue + binding.imgDisplay.width,animValue + binding.imgDisplay.height)
        }
        valueAnim.duration = 3000
        valueAnim.repeatMode = ValueAnimator.RESTART
//        valueAnim.repeatCount = ValueAnimator.INFINITE
        valueAnim.interpolator = LinearInterpolator()
        valueAnim.start()
    }

    private fun ofObjectIntroduce() {
        val anim = ValueAnimator.ofObject(object : TypeEvaluator<Point> {
            override fun evaluate(fraction: Float, startValue: Point, endValue: Point): Point {
                val newX = startValue.x + (endValue.x - startValue.x) * fraction
                val newY = startValue.y + (endValue.y - startValue.y) * fraction
                return Point(newX.toInt(), newY.toInt())
            }
        },Point(0,0),Point(500,500))
        anim.interpolator = object : Interpolator {
            override fun getInterpolation(input: Float): Float {
                return input
            }
        }
        anim.addUpdateListener {
            val pointValue = it.animatedValue as Point
            binding.imgDisplay.layout(pointValue.x,pointValue.y,pointValue.x + binding.imgDisplay.width,pointValue.y + binding.imgDisplay.height)
        }
        anim.duration = 3000
        anim.start()
    }

    private fun interpolatorIntroduce() {
        val valueAnim = ValueAnimator.ofInt(0,200)

        /*
        * 自定义插值器，实现Interpolator接口，返回fraction
        *  input取值 0~1
        *  此插值器返回1 - input 即逆向
        * */
        valueAnim.interpolator = object : Interpolator {
            override fun getInterpolation(input: Float): Float {
                return 1 - input
            }
        }
        /*
        * 自定义计算器，属性动画监听获取到的值即为evaluate返回值
        * 实现TypeEvaluator<T>接口,T为动画值的参数类型 ofInt，ofFloat
        * fraction: interpolator接口返回的值
        * */
        valueAnim.setEvaluator(object : TypeEvaluator<Int> {
            override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
                //动画监听一直返回20
//                return 20
                return (startValue + (endValue - startValue) * fraction).toInt()
            }

        })

        valueAnim.duration = 3000
        valueAnim.repeatMode = ValueAnimator.RESTART
        valueAnim.repeatCount = ValueAnimator.INFINITE
        valueAnim.start()
    }

    private fun startColorAnim() {
        val anim = ValueAnimator.ofObject(object : TypeEvaluator<Char> {
            override fun evaluate(fraction: Float, startValue: Char, endValue: Char): Char {
                return (startValue.toInt() + ((endValue.toInt() - startValue.toInt()) * fraction)).toChar()
            }
        },'A','Z')
        anim.addUpdateListener {
            val character = it.animatedValue as Char
            Log.d("lzy", "startColorAnim: ${character}")
            binding.btnChar.text = character.toString()
        }
        anim.duration = 3000
        anim.start()
    }

    private fun startScaleAnim() {
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

    private fun startAlphaAnim() {
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

    private fun startRotateAnim() {
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

    private fun startTranslateAnim() {
        val transAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f)
        transAnimation.duration = 700
        transAnimation.fillAfter = true
        transAnimation.repeatCount = Animation.INFINITE
        transAnimation.repeatMode = Animation.RESTART
        transAnimation.startOffset = 3000
        binding.imgDisplay.startAnimation(transAnimation)
    }

    private fun startSetAnim() {
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