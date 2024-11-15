package com.jin.draw.widgit

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.MotionEvent
import android.view.View
import com.jin.draw.R

class ShaderLayerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    View(context, attributeSet, def) {

    private var mSetShadow = false

    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL_AND_STROKE
        textSize = TypedValue.applyDimension(COMPLEX_UNIT_SP,12f,resources.displayMetrics)
    }

    private var path = Path()


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        introduceShadow(canvas)
//        introduceMaskFilter(canvas)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = TypedValue.applyDimension(COMPLEX_UNIT_DIP,20f,resources.displayMetrics)

        canvas.drawPath(path,paint)
    }

    private fun introduceMaskFilter(canvas: Canvas) {
        paint.color = Color.BLACK
        paint.maskFilter = BlurMaskFilter(10f,Blur.INNER)
        canvas.drawCircle(100f,800f,50f,paint)
        paint.maskFilter = BlurMaskFilter(10f,Blur.OUTER)
        canvas.drawCircle(300f,800f,50f,paint)
        paint.maskFilter = BlurMaskFilter(10f,Blur.NORMAL)
        canvas.drawCircle(500f,800f,50f,paint)
        paint.maskFilter = BlurMaskFilter(10f,Blur.SOLID)
        canvas.drawCircle(700f,800f,50f,paint)

        paint.maskFilter = BlurMaskFilter(10f,Blur.OUTER)
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = TypedValue.applyDimension(COMPLEX_UNIT_DIP,2f,resources.displayMetrics)
        paint.textSize = TypedValue.applyDimension(COMPLEX_UNIT_SP,40f,resources.displayMetrics)
        canvas.drawText("一休",50f,1200f,paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                paint.maskFilter = null
                path.moveTo(event.x,event.y)
                postInvalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x,event.y)
                postInvalidate()
            }
            MotionEvent.ACTION_UP -> {
                paint.maskFilter = BlurMaskFilter(10f,Blur.OUTER)
                postInvalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun introduceShadow(canvas: Canvas) {
        if (mSetShadow) {
            paint.setShadowLayer(3f,20f,20f,Color.RED)
        } else {
            paint.clearShadowLayer()
        }
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.avatar)
        canvas.drawBitmap(bitmap,0f,0f,paint)
        canvas.drawText("一休",50f,600f,paint)
        canvas.drawCircle(500f,600f,20f,paint)
    }

    fun setShadow(shadow: Boolean) {
        mSetShadow = shadow
        postInvalidate()
    }
}