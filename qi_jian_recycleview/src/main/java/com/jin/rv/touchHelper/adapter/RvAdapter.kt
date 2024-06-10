package com.jin.rv.touchHelper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jin.rv.databinding.ItemRvBinding
import com.jin.rv.databinding.ItemTouchHelperRvBinding

class RvAdapter(private val context: Context,private val datas: List<String>) : RecyclerView.Adapter<RvAdapter.MyHolder>() {


    var onOperationTouchAction: ((MyHolder) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ItemTouchHelperRvBinding.inflate(LayoutInflater.from(context),parent,false))

    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.bgView.text = datas[position]

        holder.imgOperation.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                onOperationTouchAction?.invoke(holder)
            }
            false
        }
    }


    class MyHolder(val binding: ItemTouchHelperRvBinding) : ViewHolder(binding.root) {
        val bgView = binding.viewBg
        val imgOperation = binding.imgOperation
    }


}