package com.jin.matrix.widgit

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jin.matrix.R

class BitmapColorMatrixView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) :
    View(context, attributeSet, defInt) {

    override fun onDraw(canvas: Canvas) {
//        exampleDrawBlue(canvas)
        exampleColorMatrix(canvas)
        super.onDraw(canvas)
    }

    private fun exampleDrawBlue(canvas: Canvas) {
        val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
        val paint = Paint()
        canvas.drawBitmap(srcBitmap,0f,0f,paint)

        canvas.translate(0f,srcBitmap.height +200f)
        val colorMatrix = floatArrayOf(
            0f,0f,0f,0f,0f,
            0f,0f,0f,0f,0f,
            0f,0f,1f,0f,0f,
            0f,0f,0f,1f,0f)
        paint.colorFilter = ColorMatrixColorFilter(ColorMatrix(colorMatrix))
        canvas.drawBitmap(srcBitmap,0f,0f,paint)
    }

    private fun exampleColorMatrix(canvas: Canvas) {
        val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
        val paint = Paint()
        canvas.drawBitmap(srcBitmap,0f,0f,paint)

        canvas.translate(0f,srcBitmap.height +200f)
        val colorMatrix = floatArrayOf(
            1f,0f,0f,0f,0f,
            0f,1f,0f,0f,50f,
            0f,0f,1f,0f,0f,
            0f,0f,0f,1f,0f)
        paint.colorFilter = ColorMatrixColorFilter(ColorMatrix(colorMatrix))
        canvas.drawBitmap(srcBitmap,0f,0f,paint)
    }
}