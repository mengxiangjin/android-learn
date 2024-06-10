package com.jin.rv.main.widgit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class CustomView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defInt: Int = 0): View(context,attributeSet,defInt) {

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        Log.d("zyz", "CustomView-----dispatchTouchEvent: ${event?.action}")
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d("zyz", "CustomView----onTouchEvent: ${event?.action}")
        return super.onTouchEvent(event)
    }
}