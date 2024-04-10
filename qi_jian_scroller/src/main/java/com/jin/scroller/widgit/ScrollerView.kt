package com.jin.scroller.widgit

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.Scroller

class ScrollerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) :
    LinearLayout(context, attributeSet, defInt) {


    private var scrollerView: Scroller? = null

    init {
        scrollerView = Scroller(context, LinearInterpolator(context, null))
    }


    override fun computeScroll() {
        if (scrollerView!!.computeScrollOffset()) {
            scrollTo(scrollerView!!.currX, scrollerView!!.currY)
        }
        invalidate()
    }

    fun scroll(startX: Int, dx: Int) {
        scrollerView!!.startScroll(startX, 0, dx, 0, 3000)
        invalidate()
    }


}