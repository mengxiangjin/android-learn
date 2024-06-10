package com.jin.rv.main.adpater

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jin.rv.databinding.ItemRvBinding
import com.jin.rv.databinding.ItemRvGroupBinding

class RvAdapter(private val context: Context) : RecyclerView.Adapter<ViewHolder>() {


    companion object {
        const val TYPE_OF_NORMAL = 0
        const val TYPE_OF_GROUP = 1
    }


    override fun getItemViewType(position: Int): Int {
        return if (position % 5 == 0) {
            TYPE_OF_GROUP
        } else {
            TYPE_OF_NORMAL
        }
    }

    var count = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        count++
        Log.d("TAG", "onCreateViewHolder: ${count}"  )
        return if (viewType == TYPE_OF_GROUP) {
            MyGroupHolder(ItemRvGroupBinding.inflate(LayoutInflater.from(context),parent,false))
        } else {
            MyHolder(ItemRvBinding.inflate(LayoutInflater.from(context),parent,false))
        }
    }

    override fun getItemCount(): Int {
        return 22
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("TAG", "onBindViewHolder: ${count}"  )

        if (holder is MyHolder) {
            holder.bgView.text = "第${position}个item"
            holder.bgView.setBackgroundColor(Color.WHITE)
        }
        if (holder is MyGroupHolder) {
            holder.bgView.text = "第${position}组"
            holder.bgView.setBackgroundColor(Color.GREEN)
        }
    }


    class MyHolder(val binding: ItemRvBinding) : ViewHolder(binding.root) {
        val bgView = binding.viewBg
    }

    class MyGroupHolder(val binding: ItemRvGroupBinding) : ViewHolder(binding.root) {
        val bgView = binding.viewBg
    }
}