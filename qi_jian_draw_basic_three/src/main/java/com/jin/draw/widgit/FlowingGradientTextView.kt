package com.jin.draw.widgit

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_SP


/*
* 流水灯文本
* */
class FlowingGradientTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    androidx.appcompat.widget.AppCompatTextView(context, attributeSet, def) {

    private val content = "欢迎来到德莱联盟LoL"
    private val baseLine = 500f
    private val flowWidth = 400f
    private var startFlowX = -200f
    private val paint = Paint().apply {
        textSize = TypedValue.applyDimension(COMPLEX_UNIT_SP,20f,resources.displayMetrics)
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }

    init {
        val measureTextWidth = paint.measureText(content)
        ValueAnimator.ofFloat(0f,1f).apply {
            addUpdateListener(object : AnimatorUpdateListener {
                override fun onAnimationUpdate(animation: ValueAnimator) {
                    startFlowX = animation.animatedValue as Float * measureTextWidth - flowWidth / 2
                    invalidate()
                }
            })
            duration = 1000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val colors = intArrayOf(Color.BLACK,Color.RED,Color.BLACK)
        val points = floatArrayOf(0f,0.5f,1f)
        paint.shader = LinearGradient(startFlowX,0f,startFlowX + flowWidth,0f,colors,points,Shader.TileMode.CLAMP)
        canvas.drawText(content,0f,baseLine,paint)
    }
}