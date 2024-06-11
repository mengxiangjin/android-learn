package com.jin.slide_delete.adapter

import android.content.Context
import android.util.Log
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jin.slide_delete.databinding.ItemDatasBinding
import kotlin.math.abs

class SlideAdapter(val context: Context,val datas: List<String>): RecyclerView.Adapter<SlideAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemDatasBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    var preX = 0f
    var preTranslate = 0f

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvContent.text = datas[position]

        holder.tvDeleter.setOnClickListener {
        }

        holder.tvRefresh.setOnClickListener {
        }
        val slideWidth = TypedValue.applyDimension(COMPLEX_UNIT_DIP,100f,context.resources.displayMetrics)
        holder.tvContent.setOnTouchListener { v, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    preX = event.x
                    preTranslate = 0f
                }
                MotionEvent.ACTION_MOVE -> {
                    val offset = event.x - preX
                    Log.d("TAG", "onBindViewHolder: " + offset)
                    if (offset > 0 && holder.tvContent.translationX + offset > 0) {
                        holder.tvContent.translationX = 0f
                    } else {
                        val translateX = if (abs(holder.tvContent.translationX + offset) > slideWidth) {
                            -slideWidth
                        } else {
                            holder.tvContent.translationX + offset
                        }
                        holder.tvContent.translationX = translateX
                    }
                }
                else -> {
                    if (abs(holder.tvContent.translationX) >= slideWidth / 2) {
                        holder.tvContent.translationX = -slideWidth
                    } else {
                        holder.tvContent.translationX = 0f
                    }
                }
            }
            true
        }



    }



    class MyViewHolder(val binding: ItemDatasBinding): ViewHolder(binding.root) {
        val tvDeleter = binding.tvDelete
        val tvRefresh = binding.tvRefresh
        val tvContent = binding.tvContent
        val llOperation = binding.llOperation
    }

}