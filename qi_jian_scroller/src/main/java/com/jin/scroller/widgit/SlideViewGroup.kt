package com.jin.scroller.widgit

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Scroller
import androidx.core.content.res.ResourcesCompat
import com.jin.scroller.R
import kotlin.math.abs

class SlideViewGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) :
    ViewGroup(context, attributeSet, defInt) {


    //滑块宽度
    private var mSlideViewWidth = 0
    //滑块滚动到边界所需的宽度为正值
    private var mScrollWidth = 0
    //当前开关状态
    private var isOpen = false

    private var mScroller: Scroller

    private var lastTouchX = 0f


    init {
        mScroller = Scroller(context)
        background =  ResourcesCompat.getDrawable(resources,R.drawable.bg,null)

        val slideView = ImageView(context)
        slideView.scaleType = ImageView.ScaleType.CENTER_CROP
        slideView.setImageResource(R.drawable.slide_two)
        slideView.setOnClickListener {
            isOpen = !isOpen
            if (isOpen) {
                mScroller.startScroll(0,0,-mScrollWidth,0,500)
            } else {
                mScroller.startScroll(-mScrollWidth,0,mScrollWidth,0,500)
            }
            invalidate()
        }

        addView(slideView,background.intrinsicWidth / 2,background.intrinsicHeight)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val drawable = ResourcesCompat.getDrawable(resources,R.drawable.bg,null)

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(drawable!!.intrinsicWidth, drawable.intrinsicHeight)
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(drawable!!.intrinsicWidth,heightSize)
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize,drawable!!.intrinsicHeight)
        } else {
            setMeasuredDimension(widthSize,heightSize)
        }
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG", "onInterceptTouchEvent: " + ev.action)
        lastTouchX = ev.x
        when(ev.action) {
            MotionEvent.ACTION_DOWN -> {
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                return true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = lastTouchX - event.x
                //边界进行处理
                if (scrollX + dx < -mScrollWidth) {
                    //右边界处理
                    scrollTo(-mScrollWidth,0)
                } else if (scrollX + dx > 0) {
                    //左边界
                    scrollTo(0,0)
                } else {
                    scrollBy(dx.toInt(),0)
                }
            }
            MotionEvent.ACTION_UP -> {
                smoothScroll()
            }
        }
        lastTouchX = event.x
        return true
    }



    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val childView = getChildAt(0)
        mSlideViewWidth = measuredWidth / 2
        mScrollWidth = measuredWidth - mSlideViewWidth
        //初始布局左边铺满
        childView.layout(0,0,mSlideViewWidth,measuredHeight)
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX,mScroller.currY)
            invalidate()
        }
    }

    private fun smoothScroll() {
        val halfWidth = measuredWidth / 2 - mSlideViewWidth / 2
        isOpen = if (abs(scrollX) > halfWidth) {
            Log.d("TAG", "smoothScroll:右 ")
            Log.d("TAG", "scrollX: ${scrollX}")
            Log.d("TAG", "mScrollWidth: ${mScrollWidth}")
            //滑到右边界
            mScroller.startScroll(scrollX,0,-(mScrollWidth + scrollX),0,500)
            true
        } else {
            //滑到左边界
            Log.d("TAG", "smoothScroll:左 ")
            mScroller.startScroll(scrollX,0,-scrollX,0,500)
            false
        }
        invalidate()
    }



}