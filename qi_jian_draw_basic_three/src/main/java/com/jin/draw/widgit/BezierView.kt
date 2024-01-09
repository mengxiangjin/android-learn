package com.jin.draw.widgit

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator

class BezierView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) : View(context, attributeSet, def) {

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = TypedValue.applyDimension(COMPLEX_UNIT_DIP, 6f, resources.displayMetrics)
        color = Color.BLACK
    }

    private val path = Path()
    private val frontPath = Path()

    private var viewHeight = 0
    private var viewWidth = 0

    private var dx = 0f

    private val waveLength = 1030f
    private val waveHeight = 100f

    init {
        ValueAnimator.ofFloat(0f, waveLength).apply {
            repeatCount = ValueAnimator.INFINITE
            duration = 2500
            addUpdateListener { animation ->
                dx = animation.animatedValue as Float
                postInvalidate()
            }
            interpolator = LinearInterpolator()
            start()
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        drawBezier(canvas)
//        canvas.drawPath(path,paint)
//        introduceQuadTo(canvas)
        drawWave(canvas)
    }


    private fun drawWave(canvas: Canvas) {
        paint.style = Paint.Style.FILL
        paint.color = Color.GREEN
        path.reset()
        path.moveTo(-waveLength + dx, 400f)

        val halfWaveLength = waveLength / 2
        var loop = -waveLength
        while (loop < viewWidth + waveLength) {
            path.rQuadTo(halfWaveLength / 2, -waveHeight, halfWaveLength, 0f)
            path.rQuadTo(halfWaveLength / 2, waveHeight, halfWaveLength, 0f)
            loop += waveLength
        }
        path.lineTo(viewWidth.toFloat(), viewHeight.toFloat())
        path.lineTo(0f, viewHeight.toFloat())
        path.close()
        canvas.drawPath(path, paint)

        paint.alpha = 100
        frontPath.reset()
        frontPath.moveTo(-(waveLength + dx * 2),400f)
        loop = -waveLength
        while (loop < viewWidth + waveLength) {
            frontPath.rQuadTo(halfWaveLength / 2, -waveHeight * 2, halfWaveLength, 0f)
            frontPath.rQuadTo(halfWaveLength / 2, waveHeight * 2, halfWaveLength, 0f)
            loop += waveLength
        }
        frontPath.lineTo(viewWidth.toFloat(), viewHeight.toFloat())
        frontPath.lineTo(0f, viewHeight.toFloat())
        frontPath.close()
        canvas.drawPath(frontPath, paint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
    }


    private fun introduceQuadTo(canvas: Canvas) {
        path.moveTo(50f, 500f)   //起始点
//        path.quadTo(350f,60f,950f,730f)x
        path.rQuadTo(350f - 50f, 60f - 500f, 950f - 50f, 730f - 500f)
        canvas.drawPath(path, paint)
    }

    private var prePointX = 0f
    private var prePointY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("lzy", "ACTION_DOWN: " + event.x + " " + event.y)
                prePointX = event.x
                prePointX = event.y
                path.moveTo(event.x, event.y)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d("lzy", "ACTION_MOVE: " + event.x + " " + event.y)

                val endX = (event.x + prePointX) / 2
                val endY = (event.y + prePointY) / 2

                path.quadTo(prePointX, prePointY, endX, endY)
                path.rQuadTo(20f, -30f, 40f, -50f)

                prePointX = event.x
                prePointY = event.y
                postInvalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun drawBezier(canvas: Canvas) {
        path.moveTo(50f, 500f)   //起始点
        path.lineTo(350f, 60f)   //控制点
        path.lineTo(950f, 730f)  //结束点

        val resultPath = getBezierPoints(Point(50, 500), Point(950, 730), Point(350, 60), 5000)
        canvas.drawPath(path, paint)
        canvas.drawPath(resultPath, paint)
    }


    private fun getBezierPoints(
        startPoint: Point,
        endPoint: Point,
        controlPoint: Point,
        resultCounts: Int
    ): Path {
        val resultPath = Path()
        for (i in 0..resultCounts) {
            val time = i * 1f / resultCounts
            val resultPoint = calcPoint(startPoint, endPoint, controlPoint, time)
            if (i == 0) {
                resultPath.moveTo(resultPoint.x, resultPoint.y)
            } else {
                resultPath.lineTo(resultPoint.x, resultPoint.y)
            }
        }
        return resultPath
    }

    private fun calcPoint(
        startPoint: Point,
        endPoint: Point,
        controlPoint: Point,
        time: Float
    ): PointF {
        val newLineStartX = startPoint.x + (controlPoint.x - startPoint.x) * time
        val newLineStartY = startPoint.y + (controlPoint.y - startPoint.y) * time
        val newLineEndX = controlPoint.x + (endPoint.x - controlPoint.x) * time
        val newLineEndY = controlPoint.y + (endPoint.y - controlPoint.y) * time
        return PointF(
            (newLineStartX + (newLineEndX - newLineStartX) * time),
            (newLineStartY + (newLineEndY - newLineStartY) * time)
        )
    }


}