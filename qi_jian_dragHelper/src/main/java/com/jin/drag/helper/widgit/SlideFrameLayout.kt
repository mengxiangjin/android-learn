package com.jin.drag.helper.widgit

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper
import kotlin.math.min

class SlideFrameLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) :
    FrameLayout(context, attributeSet, defInt) {

    private var mainView: View? = null
    private var slideView: View? = null

    private var slideWidth = 0
    private lateinit var dragHelper: ViewDragHelper


    init {
        dragHelper = ViewDragHelper.create(this,object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return child == mainView
            }

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                if (left < 0) {
                    return 0
                }
                return min(slideWidth,left)
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                return 0
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                if (releasedChild == mainView) {
                    if (mainView!!.left <= slideWidth / 2) {
                        dragHelper.smoothSlideViewTo(releasedChild,0,0)
                    } else {
                        dragHelper.smoothSlideViewTo(releasedChild,slideWidth,0)
                    }
                    invalidate()
                }
                super.onViewReleased(releasedChild, xvel, yvel)
            }

            override fun onViewPositionChanged(
                changedView: View,
                left: Int,
                top: Int,
                dx: Int,
                dy: Int
            ) {
                super.onViewPositionChanged(changedView, left, top, dx, dy)
                val scale = mainView!!.left / 1f / slideWidth
                setScale(scale)
            }
        })
    }

    fun setScale(showPercent: Float) {
        mainView!!.scaleX = (1 - 0.2 * showPercent).toFloat()
        mainView!!.scaleY = (1 - 0.2 * showPercent).toFloat()

        slideView!!.scaleX = (0.5 + 0.5 * showPercent).toFloat()
        slideView!!.scaleY = (0.5 + 0.5 * showPercent).toFloat()
        slideView!!.translationX = -slideWidth / 2 + slideWidth / 2 * showPercent
    }


    fun addCustomView(mainView: View,mainViewParams: ViewGroup.LayoutParams,slideView: View,slideViewParams: ViewGroup.LayoutParams) {
        this.slideView = slideView
        this.mainView = mainView
        this.slideWidth = slideViewParams.width
        addView(slideView,slideViewParams)
        addView(mainView,mainViewParams)
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            invalidate()
        }
        super.computeScroll()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    fun resetMainView() {
        if (mainView == null) return
        dragHelper.smoothSlideViewTo(mainView!!,0,0)
        invalidate()
    }


}