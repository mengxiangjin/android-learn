package com.jin.movie.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jin.movie.R
import com.jin.movie.bean.BigCategory


class BigCategoryAdapter(
    private var categories: List<BigCategory>,
    private val onCategoryClick: (BigCategory) -> Unit // 点击回调
) : RecyclerView.Adapter<BigCategoryAdapter.ViewHolder>() {

    // 记录当前选中的位置，默认选中第0个
    private var selectedPosition = 0

    // 更新数据的方法
    fun updateData(newData: List<BigCategory>) {
        categories = newData
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_category_name)

        fun bind(category: BigCategory, position: Int) {
            tvName.text = category.name

            // --- 核心选中逻辑 ---
            val isSelected = (position == selectedPosition)

            // 1. 触发 XML 里的颜色选择器 (变白/变灰)
            tvName.isSelected = isSelected

            if (isSelected) {
                // 选中：字体变大 (18sp)，加粗
                tvName.textSize = 18f
                tvName.typeface = Typeface.DEFAULT_BOLD
            } else {
                // 未选中：字体恢复 (15sp)，正常粗细
                tvName.textSize = 15f
                tvName.typeface = Typeface.DEFAULT
            }

            // 点击事件
            itemView.setOnClickListener {
                if (selectedPosition == position) return@setOnClickListener

                // 记录旧位置和新位置
                val oldPosition = selectedPosition
                selectedPosition = position

                // 局部刷新 UI (只刷新变动的两个 Item，性能最高)
                notifyItemChanged(oldPosition)
                notifyItemChanged(selectedPosition)

                // 回调
                onCategoryClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_big_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position], position)
    }

    override fun getItemCount() = categories.size
}