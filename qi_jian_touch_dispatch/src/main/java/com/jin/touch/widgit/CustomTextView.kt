package com.jin.touch.widgit

import android.content.Context
import android.text.method.ScrollingMovementMethod
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

class CustomTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) : androidx.appcompat.widget.AppCompatTextView(context, attributeSet, defInt) {

    init {
        movementMethod = ScrollingMovementMethod.getInstance()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        Log.d("TAG---CustomTextView", "dispatchTouchEvent: " + event.action)
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("TAG---CustomTextView", "onTouchEvent: " + event.action)
        return super.onTouchEvent(event)
    }


}