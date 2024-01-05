package com.wifi.anim.widgit

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView

class AutoUpdateTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) :
    AppCompatTextView(context, attributeSet, defInt) {



    //提供set方法，入参Point即object
    fun setTextChar(char: Int) {
        Log.d("lzy", "setTextChar: " + char)
        text = char.toChar().toString()
    }

    //提供set方法，入参Point即object
    fun setPoint(point: Point) {
        Log.d("lzy", "setPoint: " + point.toString())
        layout(point.x,point.y,point.x + width,point.y + height)
    }
}