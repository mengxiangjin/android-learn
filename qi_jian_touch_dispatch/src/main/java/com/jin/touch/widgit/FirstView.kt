package com.jin.touch.widgit

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

class FirstView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attributeSet, defInt) {


    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("TAG---FirstView", "onTouchEvent: ")
        return super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---FirstView", "dispatchTouchEvent: ")
        return super.dispatchTouchEvent(ev)
    }

}