package com.jin.rv.main.recycleView

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.jin.rv.main.manager.RepeatUseLayoutManagerForHorizontally

class GalleryRecycleView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,defInt: Int = 0): RecyclerView(context,attributeSet,defInt) {


    init {
        isChildrenDrawingOrderEnabled = true
    }


    fun getCustomLayoutManager(): RepeatUseLayoutManagerForHorizontally? {
        if (layoutManager != null) {
            return layoutManager as RepeatUseLayoutManagerForHorizontally
        }
        return layoutManager
    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        val manager = getCustomLayoutManager()
        manager?.let {
            val centerIndex = it.getCenterPosition() - it.getFirstVisiabViewPosition()

            var order = 0
            if (centerIndex == i) {
                order = childCount - 1
            } else if (centerIndex > i) {
                order = i
            } else {
                order = centerIndex + childCount - 1 - i
            }
            return order
        }
        return super.getChildDrawingOrder(childCount, i)
    }


}