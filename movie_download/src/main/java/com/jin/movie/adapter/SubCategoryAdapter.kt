package com.jin.movie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.R
import com.jin.movie.bean.SmallCategory

class SubCategoryAdapter(
    private var list: List<SmallCategory>,
    private val onSubClick: (SmallCategory) -> Unit
) : RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {

    private var selectedPosition = 0

    // 【核心修改】更新数据时，自动校准选中位置
    fun updateData(newData: List<SmallCategory>) {
        this.list = newData

        // 1. 在新数据里找：谁的 isSelected 是 true？
        // (前提是你的 HtmlParseHelper 解析时正确设置了 isSelected)
        var newIndex = newData.indexOfFirst { it.isSelected }

        // 2. 如果解析没找到(index返回-1)，那我们就只能默认选第0个
        if (newIndex == -1) {
            newIndex = 0
        }

        // 3. 更新选中位置
        this.selectedPosition = newIndex
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_name)

        fun bind(item: SmallCategory, position: Int) {
            tvName.text = item.name

            // 选中状态控制颜色/背景
            tvName.isSelected = (position == selectedPosition)

            itemView.setOnClickListener {
                if (selectedPosition != position) {
                    // UI 立即响应：先变色
                    val oldPos = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(oldPos)
                    notifyItemChanged(selectedPosition)

                    // 回调去请求数据
                    onSubClick(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sub_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }

    override fun getItemCount() = list.size
}