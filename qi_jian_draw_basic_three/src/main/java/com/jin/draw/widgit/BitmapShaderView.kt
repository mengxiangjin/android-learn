package com.jin.draw.widgit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Shader
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.jin.draw.R

class BitmapShaderView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    View(context, attributeSet, def) {

    private var srcBitmap: Bitmap? = null

    private var centerX = 0f
    private var centerY = 0f


    private val paint = Paint().apply {
        color = Color.GRAY
    }

    init {
        srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.avatar)
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.shader = BitmapShader(srcBitmap!!,Shader.TileMode.MIRROR,Shader.TileMode.MIRROR)
//        paint.shader = BitmapShader(srcBitmap!!,Shader.TileMode.REPEAT,Shader.TileMode.REPEAT)
//        paint.shader = BitmapShader(srcBitmap!!,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)


    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        telescopeView(canvas)
        drawAvatar(canvas)

//        canvas.drawRect(0f,0f,width.toFloat(),height.toFloat(),paint)
    }

    private fun drawAvatar(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        val scale = srcBitmap!!.width / width.toFloat()
        val matrix = Matrix()
        matrix.setScale(scale,scale)
        val bitmapShader = BitmapShader(srcBitmap!!,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
        bitmapShader.setLocalMatrix(matrix)
        paint.shader = bitmapShader
        canvas.drawCircle(srcBitmap!!.width / 2f,srcBitmap!!.width / 2f,srcBitmap!!.width / 2f,paint)
    }

    private fun telescopeView(canvas: Canvas) {
        //望远镜效果
        canvas.drawBitmap(srcBitmap!!,null, Rect(0,0,width,height),paint)
        canvas.drawColor(Color.BLACK)

        if (centerX != 0f || centerY != 0f) {
            paint.shader = BitmapShader(srcBitmap!!,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
            canvas.drawCircle(centerX,centerY,200f,paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                centerX = event.x
                centerY = event.y
                postInvalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                centerX = event.x
                centerY = event.y
            }
            MotionEvent.ACTION_UP -> {
                centerX = 0f
                centerY = 0f
            }
        }
        postInvalidate()

        return true
    }
}