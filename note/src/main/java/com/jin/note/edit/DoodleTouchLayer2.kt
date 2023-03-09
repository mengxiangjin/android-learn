package com.jin.note.edit

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import android.view.View

class DoodleTouchLayer2(context: Context): View(context) {


    companion object {
        const val TAG = "DoodleTouchLayer2"
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }


    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "dispatchTouchEvent: " + event)
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent: " + event)
        return super.onTouchEvent(event)
    }
}