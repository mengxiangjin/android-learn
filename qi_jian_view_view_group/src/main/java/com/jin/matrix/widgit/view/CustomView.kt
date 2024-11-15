package com.jin.matrix.widgit.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.jin.matrix.R

class CustomView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) : View(context, attributeSet, defInt) {

    private val paint = Paint()
    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_scene)


    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap,0f,0f,paint)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getMode(heightMeasureSpec)

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(bitmap.width,bitmap.height)
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(bitmap.width,heightSize)
        } else {
            setMeasuredDimension(widthSize,bitmap.height)
        }
    }

}