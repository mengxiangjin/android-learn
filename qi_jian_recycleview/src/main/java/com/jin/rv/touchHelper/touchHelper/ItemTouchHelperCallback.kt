package com.jin.rv.touchHelper.touchHelper

import android.graphics.Canvas
import android.graphics.Color
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.jin.rv.touchHelper.adapter.RvAdapter
import java.util.Collections
import kotlin.math.abs

class ItemTouchHelperCallback(var datas: MutableList<String>,var adapter: Adapter<*>): ItemTouchHelper.Callback() {


    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags,swipeFlags)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        Collections.swap(datas,viewHolder.adapterPosition,target.adapterPosition)
        adapter.notifyItemMoved(viewHolder.adapterPosition,target.adapterPosition)
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


    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (viewHolder == null || viewHolder !is RvAdapter.MyHolder) return
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            Log.d("TAG", "onSelectedChanged: ACTION_STATE_SWIPE")
            viewHolder.bgView.setBackgroundColor(Color.RED)
        } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            Log.d("TAG", "onSelectedChanged: ACTION_STATE_DRAG")
            viewHolder.bgView.setBackgroundColor(Color.BLUE)
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            Log.d("TAG", "onSelectedChanged: ACTION_STATE_IDLE")
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        if (viewHolder !is RvAdapter.MyHolder) return
        viewHolder.bgView.setBackgroundColor(Color.parseColor("#4CAF50"))
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        datas.removeAt(viewHolder.adapterPosition)
        adapter.notifyItemRemoved(viewHolder.adapterPosition)
    }


    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val alpha = 1 - abs(dX) / viewHolder.itemView.width
        viewHolder.itemView.alpha = alpha
        viewHolder.itemView.scaleX = alpha
        viewHolder.itemView.rotation = viewHolder.itemView.rotation + 1
    }


    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return super.getSwipeThreshold(viewHolder)
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return super.getSwipeEscapeVelocity(defaultValue)
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return super.getSwipeVelocityThreshold(defaultValue)
    }

    override fun canDropOver(
        recyclerView: RecyclerView,
        current: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return super.canDropOver(recyclerView, current, target)
    }

    override fun getBoundingBoxMargin(): Int {
        return super.getBoundingBoxMargin()
    }

    override fun getMoveThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return super.getMoveThreshold(viewHolder)
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}