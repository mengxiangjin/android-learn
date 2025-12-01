package com.jin.movie.adapter


import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SidebarAdapter(
    private val items: List<String>,
    private val onItemClick: (String, Int) -> Unit
) : RecyclerView.Adapter<SidebarAdapter.ViewHolder>() {

    private var selectedPosition = 0

    fun setSelected(position: Int) {
        val old = selectedPosition
        selectedPosition = position
        notifyItemChanged(old)
        notifyItemChanged(selectedPosition)
    }

    inner class ViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv) {
        init {
            tv.setOnClickListener { onItemClick(items[bindingAdapterPosition], bindingAdapterPosition) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // 直接用代码创建 TextView，省去一个 layout 文件
        val tv = TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 120) // 高度
            gravity = Gravity.CENTER
            textSize = 14f
        }
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv.text = items[position]
        if (position == selectedPosition) {
            holder.tv.setTextColor(Color.parseColor("#E53935")) // 选中红
            holder.tv.setBackgroundColor(Color.parseColor("#12121A")) // 背景黑
        } else {
            holder.tv.setTextColor(Color.parseColor("#888888")) // 未选中灰
            holder.tv.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun getItemCount() = items.size
}