package com.jin.draw.widgit

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable

class CustomDrawable(val bitmap: Bitmap) : Drawable() {

    private var bitmapShader: BitmapShader? = null
    private var mPaint = Paint().apply {

    }
    private var rect = RectF()


    override fun draw(canvas: Canvas) {
        canvas.drawRoundRect(rect, 10f, 10f, mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        mPaint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun getIntrinsicHeight(): Int {
        return bitmap.height
    }

    override fun getIntrinsicWidth(): Int {
        return bitmap.width
    }


    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        bitmapShader = BitmapShader(
            Bitmap.createScaledBitmap(bitmap, right - left, bottom - top, false),
            Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )
        mPaint.shader = bitmapShader
        rect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }
}