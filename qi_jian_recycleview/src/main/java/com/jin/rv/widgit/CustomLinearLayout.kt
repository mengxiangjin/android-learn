package com.jin.rv.widgit

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout

class CustomLinearLayout @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,defInt: Int = 0): LinearLayout(context,attributeSet,defInt) {

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.d("zyz", "dispatchTouchEvent: " + ev?.action)
        return super.dispatchTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("zyz", "onTouchEvent: " + event?.action)
        return super.onTouchEvent(event)
    }
}