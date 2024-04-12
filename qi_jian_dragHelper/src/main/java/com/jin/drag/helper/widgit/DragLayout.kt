package com.jin.drag.helper.widgit

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import androidx.customview.widget.ViewDragHelper
import com.jin.drag.helper.R

class DragLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) :
    LinearLayout(context, attributeSet, defInt) {

    private lateinit var dragHelper: ViewDragHelper

    init {
        dragHelper = ViewDragHelper.create(this, 1f, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return child.id == R.id.view_one || child.id == R.id.view_two
            }

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                return left
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                return top
            }

            override fun getViewHorizontalDragRange(child: View): Int {
                return 1
            }

            override fun getViewVerticalDragRange(child: View): Int {
                return 1
            }

            override fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {
                Log.d("TAG", "onEdgeTouched: " + edgeFlags)
                super.onEdgeTouched(edgeFlags, pointerId)
            }

            override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
                Log.d("TAG", "onEdgeDragStarted: " + edgeFlags)
                dragHelper.captureChildView(findViewById(R.id.view_three),pointerId)
                super.onEdgeDragStarted(edgeFlags, pointerId)
            }

            override fun onEdgeLock(edgeFlags: Int): Boolean {
                return super.onEdgeLock(edgeFlags)
            }
        })
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT or ViewDragHelper.EDGE_TOP)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }
}