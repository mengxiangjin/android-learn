package com.jin.basic_view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue

class AiGenderAvatarView@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, def : Int = 0)
    : androidx.appcompat.widget.AppCompatImageView(context,attrs,def) {

    private var mPaint = Paint()

    private var arcWith = dp2px(9f)

//    private var drawable: Drawable? = null

    init {
        mPaint.color = Color.RED
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = dp2px(9f).toFloat()

//        drawable = context.getDrawable(R.mipmap.ic_ai_gender_girl)
//        drawable.setBounds()
        drawable.bounds = Rect(drawable.bounds.left + dp2px(10f),drawable.bounds.top + dp2px(10f),drawable.bounds.right - dp2px(10f),drawable.bounds.bottom - dp2px(10f))

    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        var rect = drawable.bounds
        val rectF = RectF((rect.left - arcWith).toFloat(),
            rect.top.toFloat(), (rect.right + arcWith * 5).toFloat(), rect.bottom.toFloat()
        )


        canvas?.drawRect(rect,mPaint)
//        canvas?.drawArc(rectF,0f,360f,false,mPaint)
        val heart = drawable.bounds.centerX()

        Log.d("lzy", "onDraw: " + drawable.bounds.left)
        Log.d("lzy", "onDraw: " + drawable.bounds.top)
        Log.d("lzy", "onDraw: " + drawable.bounds.right)
        Log.d("lzy", "onDraw: " + drawable.bounds.bottom)



        canvas?.drawArc(rectF,0f,360f,false,mPaint)
        Log.d("lzy", "onDraw: " + drawable.intrinsicHeight)
        Log.d("lzy", "onDraw: " + drawable.intrinsicWidth)
    }

    fun dp2px(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp,
            resources.displayMetrics
        ).toInt()
    }

    private fun sp2px(sp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, sp,
            resources.displayMetrics
        ).toInt()
    }
}