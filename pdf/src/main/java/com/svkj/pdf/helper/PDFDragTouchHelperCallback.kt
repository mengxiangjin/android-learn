package com.svkj.pdf.helper

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.svkj.pdf.bean.PDFBean
import java.util.Collections

class PDFDragTouchHelperCallback(val datas:MutableList<PDFBean.PDFBeanItem>): ItemTouchHelper.Callback() {


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

//        Collections.swap(datas,viewHolder.adapterPosition,target.adapterPosition)
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
}