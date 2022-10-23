package com.jin.learn

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout

class TitleLayout(context: Context,attribute: AttributeSet):
    LinearLayout(context,attribute){

        init {
            //TitleLayout的布局
            LayoutInflater.from(context).inflate(R.layout.title_layout, this)
        }
}