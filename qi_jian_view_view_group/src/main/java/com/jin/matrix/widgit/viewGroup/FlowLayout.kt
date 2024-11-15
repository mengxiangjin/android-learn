package com.jin.matrix.widgit.viewGroup

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import kotlin.math.max


class FlowLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    ViewGroup(context, attributeSet, def) {


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var width = 0
        var height = 0
        var lineWidth = 0
        var lineHeight = 0

        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            measureChild(childView, widthMeasureSpec, heightMeasureSpec)
            val marginLayoutParams = childView.layoutParams as MarginLayoutParams
            val childWidth =
                childView.measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin
            val childHeight =
                childView.measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin

            //当前view是否需要换行
            if (lineWidth + childWidth > widthSize) {
                width = max(width, lineWidth)
                height += lineHeight

                lineWidth = childWidth
                lineHeight = childHeight
            } else {
                lineWidth += childWidth
                lineHeight = max(lineHeight, childHeight)
            }

            if (i == childCount - 1) {
                width = max(width, lineWidth)
                height += lineHeight
            }
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


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var lineWidth = 0
        var lineHeight = 0
        var left = 0
        var top = 0
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val marginLayoutParams = childView.layoutParams as MarginLayoutParams
            val childViewWidth =
                childView.measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin
            val childViewHeight =
                childView.measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin

            if (lineWidth + childViewWidth > measuredWidth) {
                //换行
                left = 0
                top += lineHeight

                lineWidth = childViewWidth
                lineHeight = childViewHeight
            } else {
                lineWidth += childViewWidth
                lineHeight = max(lineHeight, childViewHeight)
            }
            childView.layout(left + marginLayoutParams.leftMargin, top + marginLayoutParams.topMargin, left + childViewWidth, top + childViewHeight)
            left += childViewWidth
        }
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
        return MarginLayoutParams(p)
    }


}