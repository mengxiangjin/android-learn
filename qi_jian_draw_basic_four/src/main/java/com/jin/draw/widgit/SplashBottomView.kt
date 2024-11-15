package com.jin.draw.widgit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Region
import android.util.AttributeSet
import android.view.View

class SplashBottomView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defInt: Int = 0): View(context,attributeSet,defInt){


    private var customWidth = 0
    private var customHeight = 0

    private var path = Path()
    private var bottomPath = Path()
    private var offsetY = 80f

    private var paint = Paint().apply {
        color = Color.parseColor("#FF5DB1FF")
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private var bgPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        path.reset()
        bottomPath.reset()

        path.moveTo(0f, offsetY)
        path.quadTo(customWidth / 4f,0f,customWidth / 2f,offsetY)
        path.quadTo(customWidth / 4f * 3,offsetY * 2,customWidth.toFloat(),0f)

        bottomPath.set(path)
        bottomPath.offset(0f,10f)
        bottomPath.lineTo(customWidth.toFloat(),10f)
        bottomPath.lineTo(customWidth.toFloat(),customHeight.toFloat())
        bottomPath.lineTo(0f,customHeight.toFloat())
        bottomPath.close()

        canvas.drawPath(path,paint)
        canvas.drawPath(bottomPath,bgPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        customWidth = w
        customHeight = h
    }
}