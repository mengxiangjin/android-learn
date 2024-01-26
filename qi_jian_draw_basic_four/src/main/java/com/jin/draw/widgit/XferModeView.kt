package com.jin.draw.widgit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class XferModeView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    View(context, attributeSet, def) {

    private var srcBitmap: Bitmap? = null
    private var dstBitmap: Bitmap? = null

    private var bitmapWidth = 300
    private var bitmapHeight = 300

    var index = 0
    var lineCount = 3

    private val mPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 5f
        color = Color.RED
    }

    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLUE
        textSize = 20f
        textAlign = Paint.Align.CENTER
    }

    init {
        initSrcBitmap()
        initDstBitmap()
    }

    private val proterDuffModes = mapOf(
        PorterDuff.Mode.CLEAR to "clear",
        PorterDuff.Mode.SRC to "src",
        PorterDuff.Mode.DST to "dst",
        PorterDuff.Mode.SRC_OVER to "src_over",
        PorterDuff.Mode.DST_OVER to "dst_over",
        PorterDuff.Mode.DST_IN to "dst_in",
        PorterDuff.Mode.SRC_OUT to "src_out",
        PorterDuff.Mode.DST_OUT to "dst_out",
        PorterDuff.Mode.SRC_ATOP to "src_atop",
        PorterDuff.Mode.DST_ATOP to "dst_atop",
        PorterDuff.Mode.LIGHTEN to "lighten",
        PorterDuff.Mode.XOR to "xor",
        PorterDuff.Mode.DARKEN to "darken",
        PorterDuff.Mode.MULTIPLY to "multiply",
        PorterDuff.Mode.SCREEN to "screen",
        PorterDuff.Mode.ADD to "add",
        PorterDuff.Mode.OVERLAY to "overlay",
        PorterDuff.Mode.SRC_IN to "src_in",
    )


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        drawAll(canvas)
        val saveLayerID = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        canvas.drawBitmap(dstBitmap!!,0f,0f,mPaint)
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(srcBitmap!!,bitmapWidth / 2f ,bitmapWidth / 2f,mPaint)
        canvas.restoreToCount(saveLayerID)
    }


    private fun initSrcBitmap() {
        srcBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(srcBitmap!!)
        mPaint.color = Color.BLUE
        canvas.drawRect(0f, 0f, bitmapWidth.toFloat(), bitmapHeight.toFloat(), mPaint)
    }

    private fun initDstBitmap() {
        dstBitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(dstBitmap!!)
        mPaint.color = Color.YELLOW
        canvas.drawOval(0f, 0f, bitmapWidth.toFloat(), bitmapHeight.toFloat(), mPaint)
    }


    private fun drawAll(canvas: Canvas) {
        if (srcBitmap == null || dstBitmap == null) return

        val rectList = mutableListOf<RectF>()
        val gridWidth = width / lineCount
        val rawCount = height / 250

        for (i in 1..rawCount) {
            val startY = i * 250f
            canvas.drawLine(0f, startY, width.toFloat(), startY, mPaint)
            for (j in 0..2) {
                val x = j * gridWidth.toFloat()
                val rect = RectF(x, startY - 250f, x + gridWidth, startY)
                rectList.add(rect)
            }
        }
        for (i in 1..3) {
            val x = i * gridWidth
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), height.toFloat(), mPaint)
        }

        proterDuffModes.forEach { key, value ->
            if (index >= rectList.size) return@forEach
            val rectF = rectList[index]
            val saveID = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
            canvas.drawBitmap(dstBitmap!!, rectF.left, rectF.top, mPaint)
            mPaint.xfermode = PorterDuffXfermode(key)
            canvas.drawBitmap(
                srcBitmap!!,
                rectF.left + bitmapWidth / 2f,
                rectF.top + bitmapWidth / 2f,
                mPaint
            )
            canvas.drawText(value, rectF.left + rectF.width() / 2, rectF.bottom - 20f, textPaint)
            canvas.restoreToCount(saveID)
            index++
        }
    }

}