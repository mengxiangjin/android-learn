package com.wifi.anim

import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Point
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.wifi.anim.databinding.ActivityPropertyAnimBinding

class ObjectAnimActivity : AppCompatActivity() {


    private lateinit var binding: ActivityPropertyAnimBinding

    private var animator: ValueAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPropertyAnimBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnStart.setOnClickListener {
            animator = ValueAnimator.ofInt(0, 100)
            animator!!.addUpdateListener {
                val animatedValue = it.animatedValue as Int
                binding.btnExample.layout(
                    animatedValue,
                    animatedValue,
                    animatedValue + binding.btnExample.width,
                    animatedValue + binding.btnExample.height
                )
            }
            animator!!.duration = 6000
            animator!!.repeatCount = ValueAnimator.INFINITE
            animator!!.repeatMode = ValueAnimator.REVERSE
            animator!!.start()
        }

        customPropertyOfObjectValueAnim()
    }


    @SuppressLint("Recycle")
    private fun guideIntroduce() {
        /*
        * binding.btnStart:targetView
        * alpha:propertyName
        * values 改变PropertyName的值
        * */
        val alphaAnim = ObjectAnimator.ofFloat(binding.btnStart, "alpha", 0f, 1f)
        val scaleAnim = ObjectAnimator.ofFloat(binding.btnStart, "scaleX", 0f, 1f)
        val rotateAnim = ObjectAnimator.ofFloat(binding.btnStart, "rotation", 0f, 1f)
        val translateAnim = ObjectAnimator.ofFloat(binding.btnStart, "translationX", 0f, 1f)
        alphaAnim.duration = 3000
        alphaAnim.start()


        /*
        * propertyName的值确定：
        * 1.targetObject必须有set方法，如"alpha" -> setAlpha
        * 2.ofFloat 代表 setAlpha(a: Float)
        * 即targetObject类存在 setAlpha(alpha: Float) 方法
        * */
        val anim = ObjectAnimator.ofFloat(binding.btnStart, "alpha", 0f, 1f)
    }

    /*
    * 改变binding.imgFallBall的point值，即调用setPoint()方法
    * */
    private fun customPropertyOfObjectValueAnim() {
        val anim = ObjectAnimator.ofObject(
            binding.imgFallBall,
            "point",
            object : TypeEvaluator<Point> {
                override fun evaluate(
                    fraction: Float,
                    startValue: Point,
                    endValue: Point
                ): Point {
                    val newX = startValue.x + (endValue.x - startValue.x) * fraction
                    val newY = if (fraction * 2 <= 1) {
                        startValue.y + (endValue.y - startValue.y) * fraction * 2
                    } else {
                        endValue.y
                    }
                    return Point(newX.toInt(), newY.toInt())
                }
            },
            Point(0, 0),
            Point(500, 500)
        )
        anim.interpolator = AccelerateInterpolator()
        anim.duration = 3000
        anim.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        animator?.cancel()
    }

}