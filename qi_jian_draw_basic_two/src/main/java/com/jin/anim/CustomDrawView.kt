package com.jin.anim

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View

class CustomDrawView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    View(context, attributeSet, def) {


    private val paint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        textSize = TypedValue.applyDimension(COMPLEX_UNIT_SP, 50f, resources.displayMetrics)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        introduceBaseLine(canvas)
//        introduceFontMetrics(canvas)
//        introduceGetTextBounds(canvas)
        drawTextDependOnTop(canvas)
    }

    private fun introduceBaseLine(canvas: Canvas) {
        val x = 0f
        val y = 300f
        paint.color = Color.RED
        canvas.drawLine(x, y, x + 1000, y, paint)
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("hello worldg", x, y, paint)
    }

    private fun introduceFontMetrics(canvas: Canvas) {
        val x = 0f
        val baseLineY = 300f
        paint.color = Color.RED
        canvas.drawLine(x, baseLineY, x + 1000, baseLineY, paint)
        val ascentY = paint.fontMetrics.ascent + baseLineY
        val descentY = paint.fontMetrics.descent + baseLineY
        val topY = paint.fontMetrics.top + baseLineY
        val bottomY = paint.fontMetrics.bottom + baseLineY
        canvas.drawLine(x, ascentY, x + 1000, ascentY, paint)
        canvas.drawLine(x, descentY, x + 1000, descentY, paint)
        canvas.drawLine(x, topY, x + 1000, topY, paint)
        canvas.drawLine(x, bottomY, x + 1000, bottomY, paint)
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.LEFT

        canvas.drawText("hello worldg", x, baseLineY, paint)
    }

    private fun introduceGetTextBounds(canvas: Canvas) {
        val baseLineY = 500f
        val startX = 0f
        //绘制最大矩形
        val maxRectY = paint.fontMetrics.top + baseLineY
        val measureTextWidth = paint.measureText("hello worldg")
        canvas.drawRect(0f,maxRectY,measureTextWidth,paint.fontMetrics.bottom + baseLineY,paint)

        //绘制最小矩形
        val minRect = Rect()
        paint.getTextBounds("hello worldg",0,"hello worldg".length,minRect)
        minRect.offset(0, baseLineY.toInt())
        canvas.drawRect(minRect,paint)

        canvas.drawText("hello worldg", startX, baseLineY, paint)
    }

    private fun drawTextDependOnTop(canvas: Canvas) {
        val top = 300f
        canvas.drawText("hello worldg", 0f, paint.fontMetrics.top - top, paint)
    }


}