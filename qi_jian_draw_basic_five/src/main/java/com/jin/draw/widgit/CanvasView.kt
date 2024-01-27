package com.jin.draw.widgit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View

class CanvasView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) : View(context, attributeSet, defInt) {


    val mPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.RED
    }



    override fun onDraw(canvas: Canvas) {
        introduceSaveLayer(canvas)
        super.onDraw(canvas)
    }

    private fun introduceSaveLayer(canvas: Canvas) {
        val srcBitmap = Bitmap.createBitmap(400,400,Bitmap.Config.ARGB_8888)
        val dstBitmap = Bitmap.createBitmap(400,400,Bitmap.Config.ARGB_8888)

        mPaint.color = Color.RED
        val srcCanvas = Canvas(srcBitmap)
        srcCanvas.drawRect(0f,0f,400f,400f,mPaint)

        mPaint.color = Color.BLUE
        val dstCanvas = Canvas(dstBitmap)
        dstCanvas.drawOval(0f,0f,400f,400f,mPaint)

        canvas.drawColor(Color.GREEN)

        val saveLayerID = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), mPaint)
        canvas.drawBitmap(dstBitmap,0f,0f,mPaint)

        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(srcBitmap,200f,200f,mPaint)

        canvas.restoreToCount(saveLayerID)
    }


}