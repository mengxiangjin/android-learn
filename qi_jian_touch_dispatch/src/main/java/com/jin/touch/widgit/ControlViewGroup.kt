package com.jin.touch.widgit

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.jin.touch.R

class ControlViewGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) : LinearLayout(context, attributeSet, defInt) {


    private var downX = 0f
    private var downY = 0f

    init {
        LayoutInflater.from(context).inflate(R.layout.control_view,this)
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when(ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = ev.x
                downY = ev.y
            }
            MotionEvent.ACTION_MOVE -> {
                return true
            }
        }
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("TAG---ControlViewGroup", "onTouchEvent: " + event.action)
        when(event.action) {
            MotionEvent.ACTION_MOVE -> {
                val layoutParams = layoutParams as ConstraintLayout.LayoutParams
                layoutParams.leftMargin = (event.x - downX).toInt()
                layoutParams.topMargin = (event.y - downY).toInt()
                setLayoutParams(layoutParams)
                return true
            }
        }
        return true
    }
}