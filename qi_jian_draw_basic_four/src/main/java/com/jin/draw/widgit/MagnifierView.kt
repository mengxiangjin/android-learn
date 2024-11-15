package com.jin.draw.widgit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.jin.draw.R

class MagnifierView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,defInt: Int = 0): View(context,attributeSet,defInt) {

    private val radius = 100
    private val magnifierFactory = 3
    private var shapeDrawable: ShapeDrawable? = null
    val translateMatrix = Matrix()

    private var srcBitmap: Bitmap? = null
    private var mPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
    }


    override fun onDraw(canvas: Canvas) {
        if (srcBitmap == null) {
            val tempBitmap = BitmapFactory.decodeResource(resources,R.drawable.girl)
            srcBitmap = Bitmap.createScaledBitmap(tempBitmap,width,height,false)

            shapeDrawable = ShapeDrawable(OvalShape())
            shapeDrawable!!.setBounds(0,0,radius * 2,radius * 2)

            val scaleBitmap = Bitmap.createScaledBitmap(srcBitmap!!,srcBitmap!!.width * magnifierFactory,srcBitmap!!.height * magnifierFactory,true)
            val bitmapShader = BitmapShader(scaleBitmap!!,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
            shapeDrawable!!.paint.shader = bitmapShader

        }
        canvas.drawBitmap(srcBitmap!!,0f,0f,mPaint)
        shapeDrawable!!.draw(canvas)
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        translateMatrix.reset()
        val x = event.x
        val y = event.y

        val dx = radius - x * magnifierFactory
        val dy = radius  -y * magnifierFactory
        Log.d("TAG", "onTouchEvent:realX " + dx)
        Log.d("TAG", "onTouchEvent:realY " + dy)

        translateMatrix.setTranslate(dx ,dy)
        shapeDrawable!!.paint.shader.setLocalMatrix(translateMatrix)
        shapeDrawable!!.setBounds((x - radius).toInt(), (y - radius).toInt(), (x + radius).toInt(),
            (y + radius).toInt()
        )
        postInvalidate()
        if (event.action == MotionEvent.ACTION_DOWN) return true
        return super.onTouchEvent(event)
    }
}