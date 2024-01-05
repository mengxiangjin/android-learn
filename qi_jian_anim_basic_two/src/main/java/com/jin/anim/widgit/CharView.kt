package com.jin.anim.widgit

import android.content.Context
import android.util.AttributeSet
import android.util.Log

class CharView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    androidx.appcompat.widget.AppCompatTextView(context, attributeSet, def) {


    fun setTextChar(char: Int) {
        Log.d("lzy", "setTextChar: " + char)
        text = char.toChar().toString()
    }

    fun setTest(char: Int) {
        Log.d("lzy", "setTextChar: " + char)
        text = char.toChar().toString()
    }
}