package com.wifi.anim.widgit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.wifi.anim.R
import kotlin.math.max

class ScaleImageView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    View(context, attributeSet, def) {


    private var lastX = 0f
    private var lastY = 0f
    private var bitmapRect = Rect()
    private var closeRect= Rect()
    private var scaleRect = Rect()
    private var touchEvent = TouchEvent.ACTION_MOVE

    private var sceneBitmap: Bitmap? = null
    private val closeBitmap = BitmapFactory.decodeResource(resources, R.drawable.close)
    private val scaleBitmap = BitmapFactory.decodeResource(resources, R.drawable.scale)

    private val lineInterval = 10f
    private val lineWidth = 30f
    private val linePaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.RED
        strokeWidth = 10f
    }

    private var currentWidth = 0f
    private var currentHeight = 0f

    fun setResourceID(resourceId: Int) {
        sceneBitmap = BitmapFactory.decodeResource(resources, resourceId)
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (sceneBitmap == null) return
        bitmapRect.set(
            closeBitmap.width / 2,
            closeBitmap.height / 2,
            (currentWidth - scaleBitmap.width / 2f).toInt(),
            (currentHeight - scaleBitmap.height / 2f).toInt()
        )

        closeRect.set(
            bitmapRect.left - closeBitmap.width / 2,
            bitmapRect.top - closeBitmap.height / 2,
            bitmapRect.left - closeBitmap.width / 2 + closeBitmap.width,
            bitmapRect.top - closeBitmap.height / 2 + closeBitmap.height
        )
        scaleRect.set(
            bitmapRect.right - scaleBitmap.width / 2,
            bitmapRect.bottom - scaleBitmap.height / 2,
            bitmapRect.right + scaleBitmap.width / 2,
            bitmapRect.bottom + scaleBitmap.height / 2
        )

        canvas.drawBitmap(sceneBitmap!!,null,bitmapRect,null)
        canvas.drawBitmap(closeBitmap,0f,0f,null)
        canvas.drawBitmap(scaleBitmap,currentWidth - scaleBitmap.width / 1f,currentHeight - scaleBitmap.height / 1f,null)

        val rolCounts = ((bitmapRect.width() + lineInterval) / (lineWidth + lineInterval)).toInt()
        for (i in 0 until rolCounts) {
            val x = i * (lineWidth + lineInterval) + lineInterval + bitmapRect.left
            canvas.drawLine(x, bitmapRect.top.toFloat(), x + lineWidth, bitmapRect.top.toFloat(), linePaint)
            canvas.drawLine(x, bitmapRect.bottom.toFloat(), x + lineWidth, bitmapRect.bottom.toFloat(), linePaint)
        }
        val colCounts = ((bitmapRect.height() + lineInterval) / (lineWidth + lineInterval)).toInt()
        for (i in 0 until colCounts) {
            val y = i * (lineWidth + lineInterval) + lineInterval + bitmapRect.top
            canvas.drawLine(bitmapRect.left.toFloat(), y, bitmapRect.left.toFloat(), y + lineWidth, linePaint)
            canvas.drawLine(bitmapRect.right.toFloat(), y, bitmapRect.right.toFloat(), y + lineWidth, linePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                // 记录触摸开始时的坐标偏移
                lastX = event.x
                lastY = event.y
                if (closeRect.contains(event.x.toInt(), event.y.toInt())) {
                    Log.d("TAG", "onCreate:closeRect ")
                    touchEvent = TouchEvent.ACTION_CLOSE
                } else if (scaleRect.contains(event.x.toInt(), event.y.toInt())) {
                    Log.d("TAG", "onCreate:scaleRect ")
                    touchEvent = TouchEvent.ACTION_SCALE
                } else {
                    Log.d("TAG", "onCreate:bitmapRect ")
                    touchEvent = TouchEvent.ACTION_MOVE
                }
            }
            MotionEvent.ACTION_MOVE -> {
                // 计算新位置并更新视图的布局
                val newX = event.x - lastX
                val newY = event.y - lastY
                if (touchEvent == TouchEvent.ACTION_MOVE) {
                    val realLeft = max(left + newX.toInt(),0)
                    val realTop = max(top + newY.toInt(),0)
                    val params = layoutParams as FrameLayout.LayoutParams
                    params.leftMargin = realLeft
                    params.topMargin = realTop
                    layoutParams = params
                    layout(
                        realLeft, realTop, (realLeft + currentWidth).toInt(),
                        (realTop + currentHeight).toInt()
                    )
                } else if (touchEvent == TouchEvent.ACTION_SCALE) {
                    val realRight = max(right + newX,left + 50f)
                    val realBottom = max(top + 50f,bottom + newY)

                    currentWidth = realRight - left
                    currentHeight = realBottom - top

                    val params = layoutParams as FrameLayout.LayoutParams
                    params.leftMargin = left
                    params.topMargin = top
                    params.width =  realRight.toInt() - left
                    params.height = realBottom.toInt() - top
                    layoutParams = params

                    layout(left, top, realRight.toInt(), realBottom.toInt())
                    lastX = event.x
                    lastY = event.y
                } else {

                }

            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (sceneBitmap == null) return
        if (currentWidth == 0f || currentHeight == 0f) {
            currentWidth = sceneBitmap!!.width + closeBitmap.width / 2f + scaleBitmap.width / 2f
            currentHeight = sceneBitmap!!.height + closeBitmap.height / 2f + scaleBitmap.height / 2f
        }
        setMeasuredDimension(currentWidth.toInt(),currentHeight.toInt())
    }

    enum class TouchEvent{
        ACTION_CLOSE,
        ACTION_MOVE,
        ACTION_SCALE
    }
}