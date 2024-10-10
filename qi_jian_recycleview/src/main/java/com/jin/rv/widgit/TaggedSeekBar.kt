package com.jin.rv.widgit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.jin.rv.R
import kotlin.math.abs
import kotlin.math.sqrt

class TaggedSeekBar @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,defInt: Int = 0): View(context,attributeSet,defInt) {

    private var tagHeight = 0f  //上方文字框的高度
    private var indicatorHeight = 0f    //上方三角形的高度
    private var thumbRadius = 0f    //小圆的半径
    private var progress = 0f
    private var progressHeight = 0f //SeekBar的高度
    private var progressCorner = 0f //SeekBar的圆角
    private var thumbStrokeWidth = 0f   //大圆绘制的画笔大小
    private var progressColor = 0
    private var bgColor = 0 //默认颜色



    private var marginHor = 0f  //seekBar设置margin，防止圆环显示不完整

    private var maxProgress = 100

    private var bgPaint = Paint()
    private var progressPaint = Paint()
    private var thumbBigPaint = Paint()
    private var thumbSmallPaint = Paint()
    private var pathPaint = Paint()
    private var textPaint = Paint()

    private var centerX = 0f
    private var centerY =  0f

    private var path = Path()


    init {
        val taggedSeekBar = context.obtainStyledAttributes(attributeSet, R.styleable.TaggedSeekBar)
        tagHeight = taggedSeekBar.getDimension(R.styleable.TaggedSeekBar_tagHeight,20f)
        indicatorHeight = taggedSeekBar.getDimension(R.styleable.TaggedSeekBar_indicatorHeight,20f)
        thumbRadius = taggedSeekBar.getDimension(R.styleable.TaggedSeekBar_thumbRadius,20f)
        progress = taggedSeekBar.getFloat(R.styleable.TaggedSeekBar_progress,0f)
        thumbStrokeWidth = taggedSeekBar.getDimension(R.styleable.TaggedSeekBar_thumbStrokeWidth,20f)
        progressHeight = taggedSeekBar.getDimension(R.styleable.TaggedSeekBar_progressHeight,20f)
        progressCorner = taggedSeekBar.getDimension(R.styleable.TaggedSeekBar_progressCorner,20f)

        progressColor = taggedSeekBar.getColor(R.styleable.TaggedSeekBar_progressColor,Color.RED)
        bgColor = taggedSeekBar.getColor(R.styleable.TaggedSeekBar_bgColor,Color.GRAY)
        taggedSeekBar.recycle()

        bgPaint.color = bgColor
        bgPaint.style = Paint.Style.FILL_AND_STROKE

        progressPaint.color = progressColor
        progressPaint.style = Paint.Style.FILL_AND_STROKE

        thumbBigPaint.color = progressColor
        thumbBigPaint.style = Paint.Style.STROKE
        thumbBigPaint.strokeWidth = thumbStrokeWidth

        thumbSmallPaint.color = Color.WHITE
        thumbSmallPaint.style = Paint.Style.FILL_AND_STROKE

        pathPaint.color = progressColor
        pathPaint.style = Paint.Style.FILL_AND_STROKE

        textPaint.color = Color.WHITE
        textPaint.textSize = 30f
        textPaint.textAlign = Paint.Align.CENTER
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val height = paddingTop + paddingBottom + (thumbRadius + thumbStrokeWidth) * 2 + indicatorHeight + tagHeight
        if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, height.toInt())
        } else {
            setMeasuredDimension(widthSize,heightSize)
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        path.reset()
        val marginHor = if (indicatorHeight * 2 > thumbRadius + thumbStrokeWidth) {
            indicatorHeight * 2
        } else {
            thumbRadius + thumbStrokeWidth
        }
        if (progress > 100) {
            progress = 100f
        }
        if (progress < 0) {
            progress = 0f
        }

        //进度条底色
        val bgLeft = paddingLeft + marginHor
        val bgRight = width - paddingRight - marginHor
        val bgBottom = height - paddingBottom - (thumbRadius + thumbStrokeWidth) + progressHeight / 2
        val bgTop = bottom - progressHeight
        canvas.drawRoundRect(bgLeft,bgTop,bgRight,bgBottom,progressCorner,progressCorner,bgPaint)

        //进度条颜色
        val progressLeft = bgLeft
        val progressRight = bgLeft + (bgRight -bgLeft) * (progress/ 1f / maxProgress)
        val progressBottom  = bgBottom
        val progressTop = bgTop
        canvas.drawRoundRect(progressLeft,progressTop,progressRight,progressBottom,progressCorner,progressCorner,progressPaint)

        //thumb绘制
        centerX = progressRight
        centerY = (progressBottom + progressTop) / 2
        canvas.drawCircle(centerX,centerY,thumbRadius + thumbStrokeWidth,thumbBigPaint)
        canvas.drawCircle(centerX,centerY,thumbRadius,thumbSmallPaint)

        //绘制三角形下标
        val tagLeft = centerX - tagHeight
        val tagRight = centerX + tagHeight
        val tagBottom = centerY - thumbRadius - thumbStrokeWidth - indicatorHeight
        val tagTop = tagBottom - tagHeight
        path.addRoundRect(tagLeft,tagTop,tagRight,tagBottom,20f,20f,Path.Direction.CW)

        path.moveTo(centerX - indicatorHeight / 2,centerY - thumbRadius - thumbStrokeWidth - indicatorHeight)
        path.lineTo(centerX,centerY - thumbRadius - thumbStrokeWidth)
        path.lineTo(centerX + indicatorHeight / 2,centerY - thumbRadius - thumbStrokeWidth - indicatorHeight)
        canvas.drawPath(path,pathPaint)

        //绘制文字
        val text = "${progress.toInt()}%"
        val distance = (textPaint.fontMetrics.descent - textPaint.fontMetrics.ascent) / 2 - textPaint.fontMetrics.descent
        val baseLineY = (tagTop + tagBottom) / 2 + distance
        canvas.drawText(text,centerX,baseLineY,textPaint)

    }

    private var lastPointX = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastPointX = event.x
                return isPressThumbRange(event.x,event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                calculProgressByDrag(event.x)
                lastPointX = event.x
                invalidate()
            }
            MotionEvent.ACTION_UP -> {

            }
        }
        return true
    }

    private fun calculProgressByDrag(x: Float) {
        //progressBar长度
        val startX = paddingLeft + marginHor
        val endX = width - paddingRight - marginHor
        val itemWidth = (endX - startX) / maxProgress
        val calDistance = x - lastPointX
        progress += calDistance / itemWidth
    }

    private fun isPressThumbRange(x: Float,y: Float): Boolean {
        val distance = sqrt(abs(centerX - x) * abs(centerX - x) + abs(centerY - y) * abs(centerY - y))
        val result = distance <= thumbRadius + thumbStrokeWidth
        return result
    }
}