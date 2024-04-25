package com.jin.rv.manager

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import kotlin.math.max

class CustomLayoutManager : LayoutManager() {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.MATCH_PARENT
        )
    }

    private var mapRect = mutableMapOf<Int, Rect>()

    var totalHeight = 0
    var sumDy = 0


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
//        if (itemCount == 0) {
//            detachAndScrapAttachedViews(recycler)
//            //剥离当前屏幕上的所有View
//            return
//        }
//        detachAndScrapAttachedViews(recycler)
//
//        val view = recycler.getViewForPosition(0)
//        measureChildWithMargins(view, 0, 0)
//        val viewWidth = getDecoratedMeasuredWidth(view)
//        val viewHeight = getDecoratedMeasuredHeight(view)
//
//        val visiableCount = getVerticalHeight() / viewHeight
//        var offsetY = 0
//        for (i in 0 until itemCount) {
//            val rect = Rect(0, offsetY, viewWidth, offsetY + viewHeight)
//            mapRect[i] = rect
//            offsetY += viewHeight
//        }
//
//        for (i in 0 until visiableCount) {
//            val childView = recycler.getViewForPosition(i)
//            addView(childView)
//            measureChildWithMargins(childView,0,0)
//            layoutDecorated(childView,mapRect[i]!!.left,mapRect[i]!!.top,mapRect[i]!!.right,mapRect[i]!!.bottom)
//        }


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
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
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