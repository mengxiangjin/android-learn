package com.jin.rv.main.manager

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import kotlin.math.max

class CustomLayoutManager : LayoutManager() {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    var sumDy = 0
    var totalHeight = 0
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        var offsetY = 0
        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val viewWidth = getDecoratedMeasuredWidth(view)
            val viewHeight = getDecoratedMeasuredHeight(view)
            layoutDecorated(view, 0, offsetY, viewWidth, offsetY + viewHeight)
            offsetY += viewHeight
        }
        totalHeight = max(offsetY, getVerticalHeight())
    }

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }
    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        var tempDy = dy
        if (sumDy + tempDy <= 0) {
            //到顶
            tempDy = -sumDy
        }
        if (sumDy + tempDy >= totalHeight - getVerticalHeight()) {
            //到底
            tempDy = totalHeight - sumDy - getVerticalHeight()
        }
        sumDy += tempDy
        offsetChildrenVertical(-tempDy)
        return dy
    }

    private fun getVerticalHeight(): Int {
        return height - paddingBottom - paddingTop
    }


}