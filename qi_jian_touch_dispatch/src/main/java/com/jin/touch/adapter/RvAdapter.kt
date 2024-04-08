package com.jin.touch.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jin.touch.R

class RvAdapter(val context: Context, var datas: List<String>) :
    RecyclerView.Adapter<RvAdapter.MyHolder>() {


    class MyHolder(val rootView: View) : ViewHolder(rootView) {
        val tvTag = rootView.findViewById<TextView>(R.id.tv_tag)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false)
        return MyHolder(view)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.tvTag.text = datas[position]
        holder.tvTag.setBackgroundColor(Color.RED)
    }

    fun addDatas(datas: List<String>) {
        this.datas = datas
        notifyDataSetChanged()
    }
}