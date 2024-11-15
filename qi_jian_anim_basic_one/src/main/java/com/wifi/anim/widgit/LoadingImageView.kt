package com.wifi.anim.widgit

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.animation.Interpolator
import androidx.appcompat.widget.AppCompatImageView
import com.wifi.anim.R

class LoadingImageView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null, def: Int = 0
) :
    AppCompatImageView(context, attributeSet, def) {

    private var srcTop = 0
    private var currentIndex = 0

    init {
        val anim = ValueAnimator.ofInt(0, 100, 0)

        val drawableList = listOf(
            R.drawable.avatar,
            R.drawable.display,
            R.mipmap.ic_launcher
        )

        anim.addUpdateListener {
            val animValue = it.animatedValue as Int
            Log.d("zyz", ": ${animValue} ")
            top = srcTop - animValue
        }

        anim.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                setImageResource(drawableList[currentIndex % drawableList.size])
            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
                currentIndex++
                setImageResource(drawableList[currentIndex % drawableList.size])
            }

        })

        anim.repeatCount = ValueAnimator.INFINITE
        anim.repeatMode = ValueAnimator.REVERSE
        anim.duration = 2000
        anim.interpolator = Interpolator { input -> 1 - input }
        anim.setEvaluator { fraction, startValue, endValue -> 20 }
        anim.start()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        srcTop = top
    }
}