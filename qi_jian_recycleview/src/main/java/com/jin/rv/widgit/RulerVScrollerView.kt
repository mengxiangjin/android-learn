package com.jin.rv.widgit

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import com.jin.rv.R

class RulerVScrollerView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,defInt: Int = 0): NestedScrollView(context,attributeSet,defInt) {


    private lateinit var llLines: LinearLayout
    private lateinit var llTexts: LinearLayout

    private val minValue = 100
    private val maxValue = 240

    private val bigLineWidth = 100
    private val bigLineHeight = 5

    private val smallLineWidth = 70
    private val smallLineHeight = 2

    private val lineTop = 25

    override fun onFinishInflate() {
        super.onFinishInflate()
        llLines = findViewById(R.id.ll_lines)
        llTexts = findViewById(R.id.ll_texts)
        initViews()
    }


    private fun initViews() {
        for (i in minValue .. maxValue) {
            val lineView = View(context)
            lineView.setBackgroundColor(Color.parseColor("#D5D5D5"))
            if (i % 5 == 0) {
                val layoutParams = LinearLayout.LayoutParams(bigLineWidth,bigLineHeight)
                layoutParams.topMargin = lineTop
                lineView.layoutParams = layoutParams

                val textView = TextView(context)
                textView.text = i.toString()
                textView.setTextColor(Color.RED)
                textView.gravity = Gravity.CENTER
                val textLayoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                if (i != minValue) {
                    textLayoutParams.topMargin = lineTop * 3
                }

                textView.layoutParams = textLayoutParams
                llTexts.addView(textView)
            } else {
                val layoutParams = LinearLayout.LayoutParams(smallLineWidth,smallLineHeight)
                layoutParams.topMargin = lineTop
                lineView.layoutParams = layoutParams
            }
            llLines.addView(lineView)
        }
    }
}