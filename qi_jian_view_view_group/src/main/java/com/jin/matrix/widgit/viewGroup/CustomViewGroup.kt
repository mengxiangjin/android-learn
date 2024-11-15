package com.jin.matrix.widgit.viewGroup

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import kotlin.math.max

class CustomViewGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) : ViewGroup(context, attributeSet, defInt) {


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var maxWidth = 0
        var totalHeight = 0

        Log.d("zyz", "onMeasure:childCount " + childCount)

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child,widthMeasureSpec, heightMeasureSpec)

            Log.d("zyz", "onMeasure:measuredHeight " + child.measuredHeight)
            Log.d("zyz", "onMeasure:measuredWidth " + child.measuredWidth)

            maxWidth = max(child.measuredWidth, maxWidth)
            totalHeight += child.measuredHeight
        }
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getMode(heightMeasureSpec)

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(maxWidth,totalHeight)
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(maxWidth,heightSize)
        } else {
            setMeasuredDimension(widthSize,totalHeight)
        }
    }



    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = 0
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.layout(0, top, child.measuredWidth, top + child.measuredHeight)
            top += child.measuredHeight
        }
    }


}