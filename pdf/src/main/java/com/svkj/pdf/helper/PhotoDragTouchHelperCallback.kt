package com.svkj.pdf.helper

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_IDLE
import androidx.recyclerview.widget.RecyclerView
import com.svkj.pdf.bean.PhotoBean
import java.util.Collections
import kotlin.math.abs
import kotlin.math.min


class PhotoDragTouchHelperCallback(val recyclerView: RecyclerView,val datas:MutableList<PhotoBean>): ItemTouchHelper.Callback() {


    private var startPosition: Int = 0
    private var endPosition: Int = 0

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
//        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags,0)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        if (viewHolder.adapterPosition < target.adapterPosition) {
            //从上往下拖动，每滑动一个item，都将list中的item向下交换，向上滑同理。
            var startPosition = viewHolder.adapterPosition
            while (startPosition < target.adapterPosition) {
                Collections.swap(datas,startPosition,startPosition + 1)
                startPosition++
            }
        } else {
            var startPosition = viewHolder.adapterPosition
            while (startPosition > target.adapterPosition) {
                Collections.swap(datas,startPosition,startPosition - 1)
                startPosition--
            }
        }
        //坑
        // Collections.swap(datas,viewHolder.adapterPosition,target.adapterPosition)
        recyclerView.adapter?.notifyItemMoved(viewHolder.adapterPosition,target.adapterPosition)
        //决定是否回调onMoved函数
        return true
    }

    override fun onMoved(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        fromPos: Int,
        target: RecyclerView.ViewHolder,
        toPos: Int,
        x: Int,
        y: Int
    ) {

        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }



    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

//        if (viewHolder != null && actionState != ACTION_STATE_IDLE) {
//            // 非闲置状态下，记录下起始 position
//            startPosition = viewHolder.adapterPosition
//        }
//        if (actionState == ACTION_STATE_IDLE) {
//            // 当手势抬起时刷新，endPosition 是在 onMove() 回调中记录下来的
//            if (recyclerView.adapter != null) {
//                recyclerView.adapter!!.notifyItemRangeChanged(
//                    min(startPosition.toDouble(), endPosition.toDouble())
//                        .toInt(), (abs((startPosition - endPosition).toDouble()) + 1).toInt()
//                )
//            }
//        }
    }
}