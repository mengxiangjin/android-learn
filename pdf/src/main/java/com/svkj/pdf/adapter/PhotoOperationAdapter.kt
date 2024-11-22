package com.svkj.pdf.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.svkj.pdf.bean.PhotoBean
import com.svkj.pdf.databinding.ItemPhotoOperationBinding

class PhotoOperationAdapter(val context: Context, val photoList: MutableList<PhotoBean>): RecyclerView.Adapter<PhotoOperationAdapter.PhotoOverviewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoOverviewHolder {
        val inflate = ItemPhotoOperationBinding.inflate(LayoutInflater.from(context),parent,false)
        return PhotoOverviewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: PhotoOverviewHolder, position: Int) {
        Glide.with(context)
            .load(photoList[holder.adapterPosition].filePath)
            .into(holder.imgPhoto)
    }


    fun setNewDatas(photoList: MutableList<PhotoBean>) {
        this.photoList.clear()
        this.photoList.addAll(photoList)
        notifyItemRangeChanged(0,photoList.size)
    }

    class PhotoOverviewHolder(val binding: ItemPhotoOperationBinding): ViewHolder(binding.root) {
        val imgPhoto = binding.imgPhoto
    }
}