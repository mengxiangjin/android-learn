package com.jin.touch.widgit

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.MotionEvent
import android.widget.ScrollView

class CustomScrollView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) : ScrollView(context, attributeSet, defInt) {

    private var downY = 0f

    private var textViewHeight = TypedValue.applyDimension(COMPLEX_UNIT_DIP,50f,context.resources.displayMetrics)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---CustomScrollView", "onInterceptTouchEvent: " + ev.action)
        when(ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = ev.y
                Log.d("TAG", "onInterceptTouchEvent:downY " + downY)
                Log.d("TAG", "onInterceptTouchEvent:textViewHeight " + textViewHeight)
            }
            MotionEvent.ACTION_MOVE -> {
                return downY >= textViewHeight
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---CustomScrollView", "dispatchTouchEvent: " + ev.action)
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---CustomScrollView", "onTouchEvent: " + ev.action)
        return super.onTouchEvent(ev)
    }


}