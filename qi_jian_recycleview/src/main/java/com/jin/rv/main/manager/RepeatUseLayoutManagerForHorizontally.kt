package com.jin.rv.main.manager

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import kotlin.math.abs
import kotlin.math.max

/*
* 横向滚动
* */

class RepeatUseLayoutManagerForHorizontally : LayoutManager() {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    private val rectMap = mutableMapOf<Int,Rect>()
    var totalWidth = 0
    var halfItemWidth = 0

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        if (itemCount == 0) {
            return
        }
        val firstView = recycler.getViewForPosition(0)
        measureChildWithMargins(firstView,0,0)

        val itemWidth = getDecoratedMeasuredWidth(firstView)
        val itemHeight = getDecoratedMeasuredHeight(firstView)

        halfItemWidth = itemWidth / 2

        val startOffset = width / 2 - halfItemWidth
        Log.d("TAG", "onLayoutChildren:startOffset " + startOffset)

        var offsetX = 0
        for (i in 0 until itemCount) {
            val rect = Rect(offsetX + startOffset,0,offsetX + startOffset + itemWidth,itemHeight)
            rectMap[i] = rect
            offsetX += halfItemWidth
        }

        val visiableCount = getHorizontallyWidth() / halfItemWidth

        for (i in 0 until visiableCount) {
            val childView = recycler.getViewForPosition(i)
            addView(childView)
            measureChildWithMargins(childView,0,0)
            layoutDecorated(childView,rectMap[i]!!.left,rectMap[i]!!.top,rectMap[i]!!.right,rectMap[i]!!.bottom)
        }
        totalWidth = max(offsetX + startOffset + halfItemWidth * (visiableCount + 1) / 2 ,getHorizontallyWidth())
    }

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }


    var mSumDx = 0


    fun getCenterPosition(): Int {
        val centerPosition = mSumDx / halfItemWidth
        val remainder = mSumDx % halfItemWidth
        val result = if (remainder >= halfItemWidth / 2) {
            centerPosition + 1
        } else {
            centerPosition
        }
        return result
    }

    fun getFirstVisiabViewPosition(): Int {
        val firstView = getChildAt(0)
        return getPosition(firstView!!)
    }


    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State?
    ): Int {
        if (childCount <= 0) return dx
        var travel = dx
        if (mSumDx + dx < 0) {
            //到左边界
            travel = -mSumDx
        } else if (mSumDx + dx >= totalWidth - getHorizontallyWidth()) {
            //到右边界
            travel = totalWidth - getHorizontallyWidth() - mSumDx
        }
        mSumDx += travel
        val legalScreenRect = getLegalScreenRect()


        for (i in 0 until childCount) {
            val childView = getChildAt(i) ?: break
            if (dx > 0) {
                //从右往左滑动
                //回收左部的item
                if (getDecoratedRight(childView) - travel <= 0) {
                    removeAndRecycleView(childView,recycler)
                    continue
                }
            }else if (dx < 0){
                //从左往右滑动
                //回收底部的item
                if (getDecoratedLeft(childView) - travel >= width - paddingRight) {
                    removeAndRecycleView(childView,recycler)
                    continue
                }
            }
        }

        val firstView = getChildAt(0)
        val lastView = getChildAt(childCount - 1)

        detachAndScrapAttachedViews(recycler)

        //底部view复用
        if (travel > 0) {
            //复用底部
            if (firstView != null) {
                var position = getPosition(firstView)

                while (position < itemCount) {
                    if (Rect.intersects(rectMap[position]!!,legalScreenRect)) {
                        //存在交集，说明需要复用展示
                        val childView = recycler.getViewForPosition(position)
                        addView(childView)
                        measureChildWithMargins(childView,0,0)
                        layoutDecorated(childView,rectMap[position]!!.left  - mSumDx,rectMap[position]!!.top,rectMap[position]!!.right  - mSumDx,rectMap[position]!!.bottom)

                        val startOffset = width / 2 - halfItemWidth
                        handlerChildView(childView,rectMap[position]!!.left - mSumDx - startOffset)
                    }
                    position++
                }
            }
        }
        else {
            //复用顶部
            if (lastView != null) {
                var position = getPosition(lastView)
                while (position >= 0) {
                    if (rectMap[position] == null) break
                    if (Rect.intersects(rectMap[position]!!,legalScreenRect)) {
                        //存在交集，说明需要复用展示
                        val childView = recycler.getViewForPosition(position)
                        addView(childView,0)
                        measureChildWithMargins(childView,0,0)
                        layoutDecorated(childView,rectMap[position]!!.left - mSumDx,rectMap[position]!!.top,rectMap[position]!!.right - mSumDx,rectMap[position]!!.bottom)

                        val startOffset = width / 2 - halfItemWidth
                        handlerChildView(childView,rectMap[position]!!.left - mSumDx - startOffset)
                    }
                    position--
                }
            }
        }
        return dx
    }

    private fun handlerChildView(childView: View,moveX: Int) {
        var scale = 1 - abs(moveX * 1.0f / (8f * halfItemWidth))
        if (scale < 0) {
            scale = 0F
        }
        if (scale > 1) {
            scale = 1F
        }
        childView.scaleX = scale
        childView.scaleY = scale
    }



    private fun getLegalScreenRect(): Rect {
        return Rect(paddingLeft + mSumDx,  paddingTop,width - paddingRight + mSumDx, height - paddingBottom)
    }

    private fun getHorizontallyWidth(): Int {
        return width - paddingLeft - paddingRight
    }

}