package com.jin.draw.widgit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View

class LinearGradientView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) : View(context, attributeSet, def) {

    private var paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        introduceHorLinearGradient(canvas)
//        introduceLinearGradientOfMode(canvas)
        textOfLinearGradient(canvas)
    }

    private fun textOfLinearGradient(canvas: Canvas) {
        paint.textSize = TypedValue.applyDimension(COMPLEX_UNIT_SP,20f,resources.displayMetrics)
        paint.style = Paint.Style.FILL_AND_STROKE
        val measureTextWidth = paint.measureText("This is a example of LinearGradient")
        val linearGradient = LinearGradient(0f,0f,measureTextWidth,0f,Color.RED,Color.BLUE,Shader.TileMode.CLAMP)
        paint.shader = linearGradient
        canvas.drawText("This is a example of LinearGradient",0f,500f,paint)
    }

    private fun introduceHorLinearGradient(canvas: Canvas) {
        val showRectF = RectF(0f,0f,width.toFloat(),500f)
        paint.shader = LinearGradient(0f,0f,showRectF.width(),0f,Color.RED,Color.WHITE,Shader.TileMode.CLAMP)
        canvas.drawRect(showRectF,paint)
    }

    private fun introduceLinearGradientOfMode(canvas: Canvas) {
        val showRectF = RectF(0f,0f,width.toFloat(),1000f)
        paint.shader = LinearGradient(0f,0f,showRectF.width() / 2,0f,Color.WHITE,Color.RED,Shader.TileMode.MIRROR)
        canvas.drawRect(showRectF,paint)
    }
}