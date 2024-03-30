package com.wifi.exchangefile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.wifi.exchangefile.bean.FileTypeBean
import com.wifi.exchangefile.databinding.ListitemFileTypeBinding

class FileTypeAdapter(val context: Context,val fileTypes: List<FileTypeBean>) : RecyclerView.Adapter<FileTypeAdapter.MyHolder>() {

    var currentSelectedIndex = 0
        set(value) {
            if (field <= fileTypes.size) {
                notifyItemChanged(field)
            }
            field = value
            if (field <= fileTypes.size) {
                notifyItemChanged(field)
            }
        }

    var onItemClickAction: ((FileTypeBean) -> Unit)? = null


    class MyHolder(val binding: ListitemFileTypeBinding) : ViewHolder(binding.root) {
        val bgView = binding.bgView
        val name = binding.tvName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflate = ListitemFileTypeBinding.inflate(LayoutInflater.from(context))
        return MyHolder(inflate)
    }

    override fun getItemCount(): Int {
        return fileTypes.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = fileTypes[position].name
        holder.name.isSelected = currentSelectedIndex == position
        holder.bgView.isSelected = currentSelectedIndex == position

        holder.itemView.setOnClickListener {
            onItemClickAction?.invoke(fileTypes[position])
        }
    }
}