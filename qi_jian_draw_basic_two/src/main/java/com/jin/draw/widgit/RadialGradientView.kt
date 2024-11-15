package com.jin.draw.widgit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View

class RadialGradientView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    View(context, attributeSet, def) {

    private val paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        introduceRadialGradient(canvas)
        introduceRadialGradientOfTileMode(canvas)
    }

    private fun introduceRadialGradientOfTileMode(canvas: Canvas) {
        paint.shader = RadialGradient(
            width / 2f,
            height / 2f,
            200f,
            Color.RED,
            Color.GREEN,
            Shader.TileMode.REPEAT
        )
        canvas.drawRect(0f,0f,width.toFloat(), height.toFloat(), paint)
    }

    private fun introduceRadialGradient(canvas: Canvas) {
        paint.shader = RadialGradient(
            width / 2f,
            height / 2f,
            200f,
            Color.RED,
            Color.GREEN,
            Shader.TileMode.CLAMP
        )
        canvas.drawCircle(width / 2f, height / 2f, 200f, paint)
    }
}