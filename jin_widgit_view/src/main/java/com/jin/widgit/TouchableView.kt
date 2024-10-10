package com.jin.widgit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Scroller
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.max
import kotlin.math.min

class TouchableView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet?=null,defInt: Int = 0): View(context,attributeSet,defInt) {

    var scroller = Scroller(context)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.RED)
    }

    private var downX = 0f
    private var downY = 0f


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                val offsetX = event.x - downX
                val offsetY = event.y - downY

                //方式一：onLayout
                onLayoutOffset(offsetX,offsetY)

//                //方式一：改变LayoutParams中的leftMargin与topMargin
//                onParamsOffset(offsetX,offsetY)
//
//                //方式三：scrollBy函数
//                (parent as ViewGroup).scrollBy((-offsetX).toInt(), (-offsetY).toInt())

            }
            MotionEvent.ACTION_UP -> {
                scroller.startScroll(200,200,500,500,5000)
                invalidate()
            }
        }
        return true
    }

    private fun onLayoutOffset(offsetX: Float,offsetY: Float) {
        val mLeft = min(max(left + offsetX,0f),resources.displayMetrics.widthPixels - width.toFloat())
        val mTop = min(max(top + offsetY,0f),resources.displayMetrics.heightPixels - height.toFloat())

        layout(mLeft.toInt(), mTop.toInt(),
            (mLeft + width).toInt(),(mTop + height).toInt()
        )
    }

    private fun onParamsOffset(offsetX: Float,offsetY: Float) {
        val params = layoutParams as ConstraintLayout.LayoutParams
        val leftMargin = min(max(params.leftMargin + offsetX,0f),resources.displayMetrics.widthPixels - width.toFloat())
        val topMargin = min(max(params.topMargin + offsetY,0f),resources.displayMetrics.heightPixels - height.toFloat())
        params.leftMargin = leftMargin.toInt()
        params.topMargin = topMargin.toInt()
        layoutParams = params
    }


    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) {
            (parent as ViewGroup).scrollTo(-scroller.currX,-scroller.currY)
            invalidate()
        }
    }

}