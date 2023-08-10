package com.jin.basic_view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log

class ZoomDrawable(val context: Context): Drawable() {



    val longWidth = dip2px(context,32f)
    val longHeight = dip2px(context,4f)

    val shortWidth = dip2px(context,20f)
    val shortHeight = dip2px(context,2f)

    private var mPaint = Paint()
    private var mSrcX = 0
    private var mSrcY = 0

    var currentProgress = 5

    val min = 1
    val middlen = 5
    val max = 10


    init {
        mPaint.color = Color.WHITE
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.isAntiAlias = true
    }


    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        mSrcX = bounds.left
        mSrcY = bounds.top
        invalidateSelf()
    }


    override fun draw(canvas: Canvas) {
        canvas.rotate(-90f)
        canvas.translate(-intrinsicHeight.toFloat(), 0f)

        val itemHeight = intrinsicWidth / 9
        for (i in 0 until 10) {
            var height = 0
            if (i == currentProgress - 1) {
                mPaint.color = Color.parseColor("#FFFFE43D")
                height = longHeight
            } else {
                mPaint.color = Color.WHITE
                height = shortHeight
            }
            val yPoint = mSrcY + itemHeight * i
            val rect = Rect()

            if (i == min - 1 || i == middlen - 1 || i == max - 1) {
                rect.set(mSrcX,yPoint,mSrcX + longWidth,yPoint + height)
            } else {
                rect.set(mSrcX ,yPoint,mSrcX + shortWidth,yPoint +height)
            }
            canvas.drawRect(rect,mPaint)
        }
    }



    override fun setAlpha(alpha: Int) {
        invalidateSelf()
    }

    fun setProgress(progress: Int) {
        currentProgress = progress
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {}




    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun getIntrinsicWidth(): Int {
        return dip2px(context,180f)
    }

    override fun getIntrinsicHeight(): Int {
        return dip2px(context,50f)
    }


    override fun invalidateSelf() {
        super.invalidateSelf()
    }

    fun dip2px(context: Context?, dipValue: Float): Int {
        val scale = context!!.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }
}