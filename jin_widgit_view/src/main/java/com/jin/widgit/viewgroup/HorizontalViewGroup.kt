package com.jin.widgit.viewgroup

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewGroup
import android.widget.Scroller
import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.math.abs

class HorizontalViewGroup @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,defInt: Int = 0): ViewGroup(context,attributeSet,defInt) {

    private var currentShowChildIndex = 0
    private var scroller: Scroller
    private var velocityTracker: VelocityTracker

    init {
        scroller = Scroller(context)
        velocityTracker = VelocityTracker.obtain()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var left = 0
        for (i in 0 until  childCount) {
            val child = getChildAt(i)
            child.layout(left,0,left + child.measuredWidth,child.measuredHeight)
            left += child.measuredWidth
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        measureChildren(widthMeasureSpec,heightMeasureSpec)
        if (childCount == 0) {
            setMeasuredDimension(0,0)
            return
        }
        val childView = getChildAt(0)
        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(childView.measuredWidth * childCount,childView.measuredWidth)
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(childView.measuredWidth * childCount,heightSize)
        } else if (heightMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSize,childView.measuredHeight)
        }
    }


    var lastX = 0f
    var lastY = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var intercept = false
        when(ev.action) {
            MotionEvent.ACTION_DOWN -> {
                velocityTracker.addMovement(ev)
                if (!scroller.isFinished) {
                    scroller.abortAnimation()
                }
                lastTouchX = ev.x
            }
            MotionEvent.ACTION_MOVE -> {
                if (abs(lastX - ev.x) > abs(lastY - ev.y)) {
                    intercept = true
                }
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        lastX = ev.x
        lastY = ev.y
        return intercept
    }

    var lastTouchX = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastTouchX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                val offsetX = event.x - lastTouchX
                scrollBy(-offsetX.toInt(),0)
            }
            MotionEvent.ACTION_UP -> {
                val distance = scrollX - currentShowChildIndex * getChildAt(0).width
                Log.d("TAG", "ACTION_UP: " + distance)
                if (abs(distance)  > getChildAt(0).width / 2) {
                    Log.d("TAG", "ACTION_UP: ")
                    if (distance > 0) {
                        currentShowChildIndex++
                    } else{
                        currentShowChildIndex--
                    }
                } else {
                    Log.d("TAG", "快速滑动: ")
                    velocityTracker.computeCurrentVelocity(1000)
                    if (abs(velocityTracker.xVelocity)  > 50) {
                        //快速滑动
                        if (velocityTracker.xVelocity > 0) {
                            currentShowChildIndex--
                        } else {
                            currentShowChildIndex++
                        }
                    }
                }
                currentShowChildIndex = min(max(0,currentShowChildIndex),childCount - 1)
                val dx = currentShowChildIndex * width - scrollX
                Log.d("TAG", "onTouchEvent:scrollX " + scrollX)
                Log.d("TAG", "onTouchEvent:width " + width)
                Log.d("TAG", "onTouchEvent:currentShowChildIndex " + currentShowChildIndex)
                Log.d("TAG", "onTouchEvent:dx " + dx)
                scroller.startScroll(scrollX,scrollY,dx,0,1000)
                invalidate()
            }
        }
        lastTouchX = event.x
        return true
    }


    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX,scroller.currY)
            invalidate()
        }
    }


}