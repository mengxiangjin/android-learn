package com.jin.rv.manager

import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import kotlin.math.max

class RepeatUseLayoutManager : LayoutManager() {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }


    private var rectMap = mutableMapOf<Int,Rect>()

    var totalHeight = 0


    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        if (itemCount == 0) {
            return
        }
        val view = recycler.getViewForPosition(0)
        measureChildWithMargins(view,0,0)

        val itemWidth = getDecoratedMeasuredWidth(view)
        val itemHeight = getDecoratedMeasuredHeight(view)

        var offsetY = 0
        for (i in 0 until itemCount) {
            val rect = Rect(0,offsetY,itemWidth,offsetY + itemHeight)
            offsetY += itemHeight
            rectMap[i] = rect
        }


        val visiableCount = getVerticalHeight() / itemHeight
        for (i in 0 until visiableCount) {
            val childView = recycler.getViewForPosition(i)
            addView(childView)
            measureChildWithMargins(childView,0,0)
            layoutDecorated(childView,rectMap[i]!!.left,rectMap[i]!!.top,rectMap[i]!!.right,rectMap[i]!!.bottom)
        }
        totalHeight = max(offsetY,getVerticalHeight())

    }

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }
    override fun canScrollVertically(): Boolean {
        return true
    }

    var sumOffset = 0
    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        var currentOffset = dy
        if (sumOffset + currentOffset <= 0) {
            //到顶
            currentOffset = -sumOffset
        } else if (sumOffset + currentOffset >= totalHeight - getVerticalHeight()) {
            //到底
            currentOffset = totalHeight - getVerticalHeight() - sumOffset
        }

        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            if (childView == null) break
            if (dy > 0) {
                //回收顶部的item
                if (getDecoratedBottom(childView!!) - currentOffset <= 0) {
                    removeAndRecycleView(childView,recycler)
                    continue
                }

            }
        }

        //底部view复用
        val legalScreenRect = getLegalScreenRect(currentOffset)

        val lastShowView = getChildAt(childCount - 1)
        if (lastShowView != null) {
            var position = getPosition(lastShowView) + 1
            while (position < itemCount) {
                if (rectMap[position] == null) break
                if (legalScreenRect.intersect(rectMap[position]!!)) {
                    //存在交集，说明需要复用展示
                    val childView = recycler.getViewForPosition(position)
                    addView(childView)
                    measureChildWithMargins(childView,0,0)
                    layoutDecorated(childView,rectMap[position]!!.left,rectMap[position]!!.top - sumOffset,rectMap[position]!!.right,rectMap[position]!!.bottom - sumOffset)
                } else {
                    break
                }
                position++
            }
        }

        offsetChildrenVertical(-currentOffset)
        sumOffset += currentOffset
        return dy
    }

    private fun getLegalScreenRect(offset: Int): Rect {
        return Rect(paddingLeft,sumOffset + offset + paddingTop,width + paddingRight,sumOffset + offset + getVerticalHeight() )
    }

    private fun getVerticalHeight(): Int {
        return height - paddingBottom - paddingTop
    }


}