package com.jin.touch.widgit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class TouchView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) : View(context, attributeSet, defInt) {

    //当前屏幕是否存在第二根手指
    private var hasSecondPointer = false
    //第二个手指触摸点
    private var secondPoint = PointF()

    private var paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (hasSecondPointer) {
            canvas.drawCircle(secondPoint.x, secondPoint.y, 20f, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val actionIndex = event.actionIndex
        Log.d("TAG", "onTouchEvent: " + actionIndex)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                Log.d("TAG---", "ACTION_DOWN ")
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                Log.d("TAG---", "ACTION_POINTER_DOWN ")
                if (event.getPointerId(actionIndex) == 1) {
                    //第二个手指down
                    hasSecondPointer = true
                    secondPoint.set(event.getX(actionIndex),event.getY(actionIndex))
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (hasSecondPointer) {
                    try {
                        val pointerIndex = event.findPointerIndex(1)
                        secondPoint.set(event.getX(pointerIndex),event.getY(pointerIndex))
                    }catch (e: Exception) {
                        hasSecondPointer = false
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                Log.d("TAG---", "ACTION_UP ")
                hasSecondPointer = false
            }

            MotionEvent.ACTION_POINTER_UP -> {
                Log.d("TAG---", "ACTION_POINTER_UP ")
                if (event.getPointerId(actionIndex) == 1) {
                    //
                    hasSecondPointer = false
                }
            }
        }
        invalidate()
        return true
    }


}