package com.jin.movie.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.R
import com.jin.movie.bean.FixedCategory

class SortAdapter(
    private var list: List<FixedCategory>,
    private val onSortClick: (FixedCategory) -> Unit
) : RecyclerView.Adapter<SortAdapter.ViewHolder>() {

    private var selectedPosition = 0

    fun updateData(newData: List<FixedCategory>) {
        this.list = newData
        // 默认选中第0个（通常是“最新”）
        // 也可以根据 isSelected 属性来判断
        val index = newData.indexOfFirst { it.isSelected }
        this.selectedPosition = if (index != -1) index else 0
        notifyDataSetChanged()
    }

    // 新增：重置选中状态到第一个
    fun resetSelection() {
        if (selectedPosition != 0) {
            selectedPosition = 0
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_name)

        fun bind(item: FixedCategory, position: Int) {
            tvName.text = item.name
            tvName.isSelected = (position == selectedPosition)

            itemView.setOnClickListener {
                if (selectedPosition != position) {
                    val oldPos = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(oldPos)
                    notifyItemChanged(selectedPosition)
                    onSortClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            // 复用 item_sub_category 布局即可，长得一样
            .inflate(R.layout.item_sub_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount() = list.size
}