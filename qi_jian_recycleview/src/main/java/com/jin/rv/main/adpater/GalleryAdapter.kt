package com.jin.rv.main.adpater

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jin.rv.R
import com.jin.rv.databinding.ItemGalleryBinding

class GalleryAdapter(private val context: Context) : RecyclerView.Adapter<GalleryAdapter.MyHolder>() {

    var count = 0

    private var imgs = listOf(
        R.drawable.img_0,
        R.drawable.img_1,
        R.drawable.img_2,
        R.drawable.img_3,
        R.drawable.img_4,
        R.drawable.img_5,
        R.drawable.img_2,
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        count++
        Log.d("TAG", "onCreateViewHolder: ${count}")
        return MyHolder(ItemGalleryBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return 7
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        Log.d("TAG", "onBindViewHolder: ${count}")
        holder.bgView.text = "item ${position + 1}"
        holder.imgGallery.setImageResource(imgs[position % imgs.size])
    }


    class MyHolder(val binding: ItemGalleryBinding) : ViewHolder(binding.root) {
        val bgView = binding.viewBg
        val imgGallery = binding.imgGallery
    }


}