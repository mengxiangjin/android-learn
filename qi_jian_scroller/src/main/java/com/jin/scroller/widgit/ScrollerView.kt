package com.jin.scroller.widgit

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Scroller

class ScrollerView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) :
    View(context, attributeSet, defInt) {


    private var scrollerView: Scroller? = null
    init {
        scrollerView = Scroller(context,LinearInterpolator(context,null))
        scrollerView!!.startScroll(100,100,-50,-50,3000)
        invalidate()
    }



    override fun computeScroll() {
        if (scrollerView!!.computeScrollOffset()) {
            scrollBy(scrollerView!!.currX,scrollerView!!.currY)
        }
        invalidate()
    }


}