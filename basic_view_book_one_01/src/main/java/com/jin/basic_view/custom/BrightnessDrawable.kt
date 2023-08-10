package com.jin.basic_view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.Log
import kotlin.math.cos
import kotlin.math.sin

class BrightnessDrawable(val context: Context): Drawable() {

    private var thumbBackPaint = Paint()
    private var thumbProgPaint = Paint()

    private var smallCircleBackPaint = Paint()
    private var smallCircleProgPaint = Paint()

    private var wholeRectF: RectF = RectF()
    private var bigCircleRectF: RectF = RectF()
    private var dp2 = 0

    private var smallCircleRadius = dip2px(context,2f)

    private var mAngle = Math.toRadians(-45.0)

    private var currentProgress = 5
    private var minProgress = 0
    private var maxProgress = 10

    private var circleWidth = 0
    private var circleHeight = 0

    init {
        // 初始化背景圆环画笔
        thumbBackPaint.style = Paint.Style.STROKE // 只描边，不填充
        thumbBackPaint.strokeCap = Paint.Cap.ROUND // 设置圆角
        thumbBackPaint.isAntiAlias = true // 设置抗锯齿
        thumbBackPaint.isDither = true // 设置抖动
        thumbBackPaint.strokeWidth = dip2px(context,5f).toFloat()
        thumbBackPaint.color = Color.parseColor("#FFB0B0B0")

        // 初始化进度圆环画笔
        thumbProgPaint.style = Paint.Style.STROKE // 只描边，不填充
        thumbProgPaint.strokeCap = Paint.Cap.ROUND // 设置圆角
        thumbProgPaint.isAntiAlias = true // 设置抗锯齿
        thumbProgPaint.isDither = true // 设置抖动
        thumbProgPaint.strokeWidth = dip2px(context,5f).toFloat()
        thumbProgPaint.color = Color.parseColor("#FFFDB904")

        // 初始化背景圆环画笔
        smallCircleBackPaint.style = Paint.Style.FILL_AND_STROKE
        smallCircleBackPaint.isAntiAlias = true // 设置抗锯齿
        smallCircleBackPaint.isDither = true // 设置抖动
        smallCircleBackPaint.strokeWidth = dip2px(context,5f).toFloat()
        smallCircleBackPaint.color = Color.parseColor("#FFB0B0B0")

        // 初始化进度圆环画笔
        smallCircleProgPaint.style = Paint.Style.FILL_AND_STROKE
        smallCircleProgPaint.isAntiAlias = true // 设置抗锯齿
        smallCircleProgPaint.isDither = true // 设置抖动
        smallCircleProgPaint.strokeWidth = dip2px(context,5f).toFloat()
        smallCircleProgPaint.color = Color.parseColor("#FFFDB904")

        circleWidth = dip2px(context,25f)
        circleHeight = dip2px(context,25f)
    }


    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        dp2 = dip2px(context, 2f)
        val dp25 = dip2px(context, 50f)
        wholeRectF.left = bounds.left.toFloat()
        wholeRectF.right = bounds.right.toFloat()
        wholeRectF.top = bounds.top.toFloat()
        wholeRectF.bottom = bounds.bottom.toFloat()

        bigCircleRectF.left = bounds.left.toFloat() + dip2px(context,15f)
        bigCircleRectF.right = bounds.right.toFloat() - dip2px(context,15f)
        bigCircleRectF.top = bounds.top.toFloat() + dip2px(context,15f)
        bigCircleRectF.bottom = bounds.bottom.toFloat() - dip2px(context,15f)


        invalidateSelf()
    }


    override fun draw(canvas: Canvas) {
        val paint = Paint().apply {
            style = Paint.Style.STROKE
            color = Color.RED
        }

        drawBigCircle(canvas)
        drawSmallCircle(canvas)

        canvas.drawRect(wholeRectF,paint)
        paint.color = Color.GREEN
        canvas.drawRect(bigCircleRectF,paint)
    }

    private fun drawBigCircle(canvas: Canvas) {
        canvas.drawArc(bigCircleRectF, 0F,360F,false,thumbBackPaint)
        canvas.drawArc(bigCircleRectF,0F,((currentProgress - minProgress) / 1.0f / maxProgress * 360),false,thumbProgPaint)
    }

    private fun drawSmallCircle(canvas: Canvas) {
        //应该画多少半圆
        val count = ((currentProgress - minProgress) / 1.0f / maxProgress * 16).toInt()
        var temCount = count
        val mCenterX = bigCircleRectF.centerX()
        val mCenterY = bigCircleRectF.centerY()
        for (i in -2 until 6) {

            val x = mCenterX  + (bigCircleRectF.width() / 2 + smallCircleRadius * 4)  * cos(mAngle * i)
            val y = mCenterY  + (bigCircleRectF.width() / 2 + smallCircleRadius * 4) * sin(mAngle * i)
            if (count == 0) {
                drawItemCircle(canvas,
                    RectF((x-smallCircleRadius).toFloat(),
                        (y - smallCircleRadius).toFloat(), (x + smallCircleRadius).toFloat(), (y + smallCircleRadius).toFloat()
                    )
                )
                continue
            }
            if (i == -2) {
                drawItemCircle(canvas,
                    RectF((x-smallCircleRadius).toFloat(),
                        (y - smallCircleRadius).toFloat(), (x + smallCircleRadius).toFloat(), (y + smallCircleRadius).toFloat()
                    ),false
                )
                temCount -= 1
                continue
            }
            if (temCount <= 0) {
                drawItemCircle(canvas,
                    RectF((x-smallCircleRadius).toFloat(),
                        (y - smallCircleRadius).toFloat(), (x + smallCircleRadius).toFloat(), (y + smallCircleRadius).toFloat()
                    )
                )
            } else {
                temCount -= 2
                if (temCount < 0) {
                    drawItemCircle(canvas,
                        RectF((x-smallCircleRadius).toFloat(),
                            (y - smallCircleRadius).toFloat(), (x + smallCircleRadius).toFloat(), (y + smallCircleRadius).toFloat()
                        ),
                        false
                    )
                } else {
                    drawItemCircle(canvas,
                        RectF((x-smallCircleRadius).toFloat(),
                            (y - smallCircleRadius).toFloat(), (x + smallCircleRadius).toFloat(), (y + smallCircleRadius).toFloat()
                        ),true,true
                    )
                }

            }

        }
    }

    fun drawItemCircle(canvas: Canvas,rectF: RectF,isTotal: Boolean = true,isProgPaint: Boolean = false) {
        val paint = Paint().apply {
            style = Paint.Style.STROKE
            color = Color.BLACK
        }
        canvas.drawRect(rectF,paint)

        if (isTotal) {
            if (isProgPaint) {
                canvas.drawCircle(rectF.centerX(),rectF.centerY(),rectF.width() / 2,smallCircleProgPaint)
            } else {
                canvas.drawCircle(rectF.centerX(),rectF.centerY(),rectF.width() / 2,smallCircleBackPaint)
            }
        } else {
            canvas.drawArc(rectF, 0F,360F,false,smallCircleBackPaint)
            canvas.drawArc(rectF,-90F,180F,false,smallCircleProgPaint)
        }
    }


    override fun setAlpha(alpha: Int) {
        thumbBackPaint.alpha = alpha
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
        return dip2px(context,60f)
    }

    override fun getIntrinsicHeight(): Int {
        return dip2px(context,60f)
    }


    override fun invalidateSelf() {
        super.invalidateSelf()
    }

    fun dip2px(context: Context?, dipValue: Float): Int {
        val scale = context!!.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }
}