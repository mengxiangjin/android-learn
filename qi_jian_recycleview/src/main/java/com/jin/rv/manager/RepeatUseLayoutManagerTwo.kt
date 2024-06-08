package com.jin.rv.manager

import android.graphics.Rect
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import kotlin.math.max

class RepeatUseLayoutManagerTwo : LayoutManager() {

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

    var mSumDy = 0
    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if (childCount <= 0) return dy
        var travel = dy
        if (mSumDy + dy < 0) {
            //到顶
            travel = -mSumDy
        } else if (mSumDy + dy >= totalHeight - getVerticalHeight()) {
            //到底
            travel = totalHeight - getVerticalHeight() - mSumDy
        }

        for (i in 0 until childCount) {
            val childView = getChildAt(i) ?: break
            if (dy > 0) {
                //从下往上滑动
                //回收顶部的item
                if (getDecoratedBottom(childView) - travel <= 0) {
                    removeAndRecycleView(childView,recycler)
                    continue
                }
            }else if (dy < 0){
                //从上往下滑动
                //回收底部的item
                if (getDecoratedTop(childView) - travel >= height - paddingBottom) {
                    Log.d("TAG", "scrollVerticallyBy:回收底部的 " + i)
                    removeAndRecycleView(childView,recycler)
                    continue
                }
            }
        }

        val firstView = getChildAt(0)
        val lastView = getChildAt(childCount - 1)

        detachAndScrapAttachedViews(recycler)
        mSumDy += travel

        //底部view复用
        val legalScreenRect = getLegalScreenRect()

        if (travel > 0) {
            //复用底部
            if (firstView != null) {
                var position = getPosition(firstView)
                while (position < itemCount) {
                    if (rectMap[position] == null) break
                    if (legalScreenRect.intersect(rectMap[position]!!)) {
                        //存在交集，说明需要复用展示
                        val childView = recycler.getViewForPosition(position)
                        addView(childView)
                        measureChildWithMargins(childView,0,0)
                        layoutDecorated(childView,rectMap[position]!!.left,rectMap[position]!!.top - mSumDy,rectMap[position]!!.right,rectMap[position]!!.bottom - mSumDy)
                    }
                    position++
                }
            }
        } else {
            //复用顶部
            if (lastView != null) {
                var position = getPosition(lastView)
                while (position >= 0) {
                    if (rectMap[position] == null) break
                    if (legalScreenRect.intersect(rectMap[position]!!)) {
                        //存在交集，说明需要复用展示
                        Log.d("TAG", "scrollVerticallyBy:复用顶部 " + position)
                        val childView = recycler.getViewForPosition(position)
                        addView(childView,0)
                        measureChildWithMargins(childView,0,0)
                        layoutDecorated(childView,rectMap[position]!!.left,rectMap[position]!!.top - mSumDy,rectMap[position]!!.right,rectMap[position]!!.bottom - mSumDy)
                    }
                    position--
                }
            }
        }
        return dy
    }

    private fun getLegalScreenRect(): Rect {
        return Rect(paddingLeft,mSumDy  + paddingTop,width + paddingRight,mSumDy  + getVerticalHeight())
    }

    private fun getVerticalHeight(): Int {
        return height - paddingBottom - paddingTop
    }
}