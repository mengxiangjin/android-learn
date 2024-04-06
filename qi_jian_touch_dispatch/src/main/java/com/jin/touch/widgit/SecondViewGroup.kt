package com.jin.touch.widgit

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout

class SecondViewGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) : LinearLayout(context, attributeSet, defInt) {


    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("TAG---SecondViewGroup", "onTouchEvent: " + event.action)
        return true
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---SecondViewGroup", "dispatchTouchEvent: " + ev.action)
        when (ev.action) {
            MotionEvent.ACTION_MOVE -> {
                return true
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---SecondViewGroup", "onInterceptTouchEvent: " + ev.action)
        return super.onInterceptTouchEvent(ev)
    }

}