package com.jin.touch.widgit

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import kotlin.math.abs
import kotlin.math.sqrt

class ZoomTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attributeSet, defInt) {

    //当前屏幕手指的数量
    private var pointerCounts = 0
    //双指缩放前的距离
    private var moveBeforeDistance = 0f
    //当前的文本大小
    private var textSize = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (textSize == 0f) {
            textSize = getTextSize()
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                pointerCounts++
                moveBeforeDistance = 0f
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                pointerCounts++
                //计算二个手指伸缩之前的距离
                moveBeforeDistance = calDistance(event)
            }

            MotionEvent.ACTION_POINTER_UP -> {

            }

            MotionEvent.ACTION_UP -> {
                moveBeforeDistance = 0f
            }

            MotionEvent.ACTION_MOVE -> {
                if (pointerCounts >= 2) {
                    val newDistance = calDistance(event)
                    if (newDistance != -1f && abs(newDistance - moveBeforeDistance) > 50) {
                        zoom(newDistance / moveBeforeDistance)
                        moveBeforeDistance = newDistance
                    }
                }
            }
        }
        return true
    }

    private fun zoom(zoom: Float) {
        textSize *= zoom
        setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize)
    }

    private fun calDistance(event: MotionEvent): Float {
        try {
            val x = event.getX(0) - event.getX(1)
            val y = event.getY(0) - event.getY(1)
            return sqrt(x * x + y * y).toFloat()
        }catch (e: Exception) {

        }
        return -1f
    }


}