package com.jin.basic_view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class AnalysisView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def : Int = 0)
    : View(context,attrs,def) {


    private var mLinesPaint: Paint = Paint()
    private var mContentPaint: Paint = Paint()

    private var mCenterX = 0f
    private var mCenterY = 0f

    private var mCount = 6
    private var mPath = Path()
    private var mTotalRadius = 0f
    private var mAngle = Math.toRadians(60.0)

    init {
        mLinesPaint.color = Color.RED
        mLinesPaint.strokeWidth = 3f
        mLinesPaint.style = Paint.Style.STROKE

        mContentPaint.color = Color.GREEN
        mContentPaint.strokeWidth = 3f
        mContentPaint.style = Paint.Style.FILL_AND_STROKE
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        mTotalRadius = min(w,h) / 2 * 0.8f
        mCenterX = (w / 2).toFloat()
        mCenterY = (h / 2).toFloat()
        postInvalidate()
        super.onSizeChanged(w, h, oldw, oldh)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawPolygon(it)
            drawLines(it)
        }
    }

    private fun drawPolygon(canvas: Canvas) {
        val radius = mTotalRadius / mCount
        for (i in 1 .. mCount) {
            val currentRadius = i * radius
            mPath.reset()
            for (j in 0 until mCount) {
                if (j == 0) {
                    mPath.moveTo(mCenterX + currentRadius,mCenterY)
                } else {
                    val x = mCenterX + currentRadius * cos((mAngle * j).toFloat())
                    val y = mCenterY + currentRadius * sin((mAngle * j).toFloat())
                    mPath.lineTo(x,y)
                }
            }
            mPath.close()
            canvas.drawPath(mPath,mLinesPaint)
        }
    }

    private fun drawLines(canvas: Canvas) {

    }


}