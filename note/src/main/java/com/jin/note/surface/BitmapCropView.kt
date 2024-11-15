package com.jin.note.surface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Region
import android.util.AttributeSet

class BitmapCropView@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    androidx.appcompat.widget.AppCompatImageView(context, attributeSet, def) {


        private var circleRadius = 8f
        private var circleStrokeRadius = 12f


    private var controlPointF = mutableListOf<PointF>()

    private var path = Path()

    private var circlePaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.WHITE
    }

    private var circleStrokePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLUE
        strokeWidth = 2f
    }

    private var linePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLUE
        strokeWidth = 3f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path,linePaint)
        controlPointF.forEach {
            canvas.drawCircle(it.x,it.y,circleRadius,circlePaint)
            canvas.drawCircle(it.x,it.y,circleRadius,circleStrokePaint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setPadding((circleRadius).toInt(),
            (circleRadius).toInt(), (circleRadius).toInt(), (circleRadius).toInt()
        )
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initControlPointF(w, h)
    }

    private fun initControlPointF(w: Int, h: Int) {
        path.reset()
        controlPointF.add(PointF(paddingLeft.toFloat(), paddingTop.toFloat()))
        controlPointF.add(PointF(w / 2f, paddingTop.toFloat()))
        controlPointF.add(PointF(w.toFloat() - paddingEnd, paddingTop.toFloat()))
        controlPointF.add(PointF(w.toFloat() - paddingEnd, h / 2f))
        controlPointF.add(PointF(w.toFloat() - paddingEnd, h.toFloat() - paddingBottom))
        controlPointF.add(PointF(w / 2f, h.toFloat() - paddingBottom))
        controlPointF.add(PointF(paddingLeft.toFloat(), h.toFloat() - paddingBottom))
        controlPointF.add(PointF(paddingLeft.toFloat(), h / 2f))
        controlPointF.forEachIndexed { index, pointF ->
            if (index == 0) {
                path.moveTo(pointF.x,pointF.y)
            } else {
                path.lineTo(pointF.x,pointF.y)
            }
        }
        path.close()
    }



}