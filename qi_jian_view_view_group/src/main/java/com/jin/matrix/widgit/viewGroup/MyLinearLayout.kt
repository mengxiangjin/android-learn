package com.jin.matrix.widgit.viewGroup

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import kotlin.math.max

class MyLinearLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) :
    ViewGroup(context, attributeSet, defInt) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //测量子控件
        var height = 0
        var width = 0
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
            val marginLayoutParams = childView.layoutParams as MarginLayoutParams
            Log.d("TAG", "onMeasure:topMargin " + marginLayoutParams.topMargin)
            Log.d("TAG", "onMeasure:bottomMargin " + marginLayoutParams.bottomMargin)
            Log.d("TAG", "onMeasure:leftMargin " + marginLayoutParams.leftMargin)
            Log.d("TAG", "onMeasure:rightMargin " + marginLayoutParams.rightMargin)


            width = max(
                childView.measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin,
                width
            )
            height += childView.measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin
        }
        val realWidth = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            width
        }
        val realHeight = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            height
        }
        setMeasuredDimension(realWidth, realHeight)
    }


    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var top = 0
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val marginLayoutParams = childView.layoutParams as MarginLayoutParams

            val childWidth = childView.measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin
            val childHeight = childView.measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin
            childView.layout(
                0,
                top,
                childWidth,
                top +childHeight
            )
            top += childHeight
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

}