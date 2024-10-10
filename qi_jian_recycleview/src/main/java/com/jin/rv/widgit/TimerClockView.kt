package com.jin.rv.widgit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class TimerClockView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defInt: Int = 0): View(context,attributeSet,defInt) {

    private var bigMarkPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 8f
        style = Paint.Style.STROKE
    }

    private var smallMarkPaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private var textMarkPaint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL_AND_STROKE
        textSize = 38f
        textAlign = Paint.Align.CENTER
    }

    private var centerCirclePaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL_AND_STROKE
    }

    private var hourPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLUE
    }

    private var minutePaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }

    private var secondPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.RED
    }


    private var bigOffset = 50
    private var smallOffset = 20
    private var textRect = Rect()
    private var textOffset = 20


    private var currentSecond = 0


    private var halfHourWidth = 14f
    private var halfMinuteWidth = 8f
    private var halfSecondWidth = 4f

    private var timerHandler: TimerHandler = TimerHandler(this)

    init {
        timerHandler.sendEmptyMessageDelayed(0x86,3000)
    }


    var flag = false
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val center = width / 2f
        val radius = center - 8f
        canvas.drawCircle(center,center,radius,bigMarkPaint)

        val hourHeight = radius - 120f
        val minuteHeight = radius - 50f
        val secondHeight = radius - 10f

        //刻度
        canvas.save()
        for (i in 1 .. 60) {
            canvas.rotate(6f,center,center)
            textRect.setEmpty()
            if (i % 5 == 0) {
                canvas.drawLine(center,center - radius,center,center - radius + bigOffset,bigMarkPaint)
                canvas.save()
                canvas.rotate(i * -6f,center,center)

                val textX = center + sin(Math.toRadians((6 * i).toDouble())) * (radius - bigOffset - textOffset)
                val textY = center - cos(Math.toRadians((6 * i).toDouble())) * (radius - bigOffset - textOffset) + 18f
                val time = "${i / 5}"
//                textMarkPaint.getTextBounds(time,0,time.length,textRect)
//                val baseLine = (center - radius + bigOffset + textOffset + textRect.height() / 2) + (textMarkPaint.fontMetrics.bottom - textMarkPaint.fontMetrics.top) / 2 - textMarkPaint.fontMetrics.bottom
                canvas.drawText(time, textX.toFloat(), textY.toFloat(),textMarkPaint)
                canvas.restore()
            } else {
                canvas.drawLine(center,center - radius,center,center - radius + smallOffset,smallMarkPaint)
            }
        }
        canvas.restore()

        //圆心
        canvas.drawCircle(center,center,30f,centerCirclePaint)

        //3682
        val hour = if (currentSecond / 60 / 60 > 12) {
            currentSecond / 60 / 60 - 12
        } else {
            currentSecond / 60 / 60
        }
        val minute =  currentSecond / 60 % 60
        val second = currentSecond % 60
        Log.d("TAG", "onDraw: minute" + minute)
        Log.d("TAG", "onDraw: second" + second)

        //时针
        canvas.save()
        canvas.rotate(30f * hour + minute / 60f * 30,center,center)
        val hourRectF = RectF(center - halfHourWidth,center - hourHeight / 4f * 3,center + halfHourWidth,center + hourHeight / 4f)
        canvas.drawRoundRect(hourRectF,hourHeight,hourHeight,hourPaint)
        canvas.restore()

        //分针
        canvas.save()
        canvas.rotate(6f * minute + second / 60f * 6,center,center)
        val minuteRectF = RectF(center - halfMinuteWidth,center - minuteHeight / 4f * 3,center + halfMinuteWidth,center + minuteHeight / 4f)
        canvas.drawRoundRect(minuteRectF,minuteHeight,minuteHeight,minutePaint)
        canvas.restore()

        canvas.save()
        canvas.rotate(6f * second,center,center)
        //秒针
        val secondRectF = RectF(center - halfSecondWidth,center - secondHeight / 4f * 3,center + halfSecondWidth,center + secondHeight / 4f)
        canvas.drawRoundRect(secondRectF,secondHeight,secondHeight,secondPaint)
        canvas.restore()

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val minSize = min(widthSize,heightSize)
        setMeasuredDimension(minSize,minSize)
    }

    class TimerHandler(val view: TimerClockView): Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when(msg.what) {
                0x86 -> {
                    view.currentSecond += 1
                    view.invalidate()
                    sendEmptyMessageDelayed(0x86,10)
                }
            }
        }
    }
}