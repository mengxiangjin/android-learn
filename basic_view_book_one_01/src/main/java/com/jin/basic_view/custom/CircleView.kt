package com.jin.basic_view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View


class CircleView @JvmOverloads constructor
    (context: Context, attrs: AttributeSet? = null, def : Int = 0)
    : View(context,attrs,def){


    private var thumbBackPaint = Paint()
    private var thumbProgPaint = Paint()

    var mRectF = RectF()

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
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = Paint().apply {
            style = Paint.Style.STROKE
            color = Color.RED
        }

        canvas.drawArc(mRectF, 0F,360F,false,thumbBackPaint)
        canvas.drawArc(mRectF, 0F,180F,false,thumbProgPaint)
        canvas.drawRect(mRectF,paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewWide = measuredWidth - paddingLeft - paddingRight
        val viewHigh = measuredHeight - paddingTop - paddingBottom
        val mRectLength =
            ((if (viewWide > viewHigh) viewHigh else viewWide) - if (thumbBackPaint.getStrokeWidth() > thumbProgPaint.getStrokeWidth()) thumbBackPaint.getStrokeWidth() else thumbProgPaint.getStrokeWidth())
        val mRectL = paddingLeft + (viewWide - mRectLength) / 2
        val mRectT = paddingTop + (viewHigh - mRectLength) / 2
        mRectF = RectF(
            mRectL.toFloat(),
            mRectT.toFloat(),
            (mRectL + mRectLength).toFloat(),
            (mRectT + mRectLength).toFloat()
        )

    }

    fun dip2px(context: Context?, dipValue: Float): Int {
        val scale = context!!.resources.displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

}