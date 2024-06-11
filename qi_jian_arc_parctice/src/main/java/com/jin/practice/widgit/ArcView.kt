package com.jin.practice.widgit

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.View
import com.jin.practice.bean.ArcBean
import kotlin.math.min

class ArcView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,defInt: Int = 0): View(context,attributeSet,defInt) {


    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    private var rect = RectF()   //绘制扇形所需要的矩形范围
    private var arcStrokeWidth = 0f

    private var startAngle = 0f


    private var paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
    }

    private var beans = listOf(
        ArcBean(Color.RED,30f),
        ArcBean(Color.GREEN,60f),
        ArcBean(Color.GRAY,50f),
        ArcBean(Color.BLUE,40f),
        ArcBean(Color.BLACK,120f),
        ArcBean(Color.YELLOW,60f),
        )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.translate(centerX,centerY)

        startAngle = 0f
        beans.forEach {
            val sweepAngle = min(currentAngle - startAngle,it.sweepAngle)
            if (sweepAngle > 0) {
                paint.color = it.color
                canvas.drawArc(rect,startAngle,sweepAngle,false,paint)
            }
            startAngle += it.sweepAngle
        }

        canvas.restore()
    }

    private var currentAngle = 0f


    fun startAnim(duration: Long) {
        val valueAnim = ValueAnimator.ofFloat(0f,360f)
        valueAnim.duration = duration
        valueAnim.repeatCount = 0
        valueAnim.addUpdateListener {
            currentAngle = it.animatedValue as Float
            invalidate()
        }
        valueAnim.start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val defaultWidth = TypedValue.applyDimension(COMPLEX_UNIT_DIP,50f,context.resources.displayMetrics).toInt()
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultWidth,defaultWidth)
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(defaultWidth,heightSize)
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize,defaultWidth)
        } else {
            setMeasuredDimension(widthSize,heightSize)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val min = min(w,h)
        radius = min / 2f
        centerX = w / 2f
        centerY = h / 2f

        arcStrokeWidth = radius * 0.3f

        paint.strokeWidth = arcStrokeWidth
        rect.set(-(radius - arcStrokeWidth / 2),-(radius - arcStrokeWidth / 2),radius - arcStrokeWidth / 2,radius - arcStrokeWidth / 2)
    }



}