package com.jin.rv

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jin.rv.databinding.ItemRvBinding

class RvAdapter(private val context: Context) : RecyclerView.Adapter<RvAdapter.MyHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ItemRvBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return 22
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.bgView.text = "第${position}个item"
    }


    class MyHolder(val binding: ItemRvBinding) : ViewHolder(binding.root) {
        val bgView = binding.viewBg
    }

}