package com.jin.touch.widgit

import android.content.Context
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

class TouchImgView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) : androidx.appcompat.widget.AppCompatImageView(context, attributeSet, defInt) {

    private var startPointF = PointF()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("TAG", "onTouchEvent: " + left)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startPointF.set(event.x, event.y)
            }

            MotionEvent.ACTION_MOVE -> {
                val left = (event.x - startPointF.x).toInt() + left
                val top = (event.y - startPointF.y).toInt() + top
                layout(left, top, left + width, top + height)
            }
        }
        return true
    }


}