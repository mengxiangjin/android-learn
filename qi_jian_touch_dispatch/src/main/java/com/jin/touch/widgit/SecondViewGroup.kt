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
        Log.d("TAG---SecondViewGroup", "onTouchEvent: ")
        return super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---SecondViewGroup", "dispatchTouchEvent: ")
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---SecondViewGroup", "onInterceptTouchEvent: ")
        return true
    }
}