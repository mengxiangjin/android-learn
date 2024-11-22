package com.svkj.pdf.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.svkj.pdf.bean.PhotoBean
import com.svkj.pdf.databinding.ItemPhotoOverviewBinding

class PhotoOverviewAdapter(val context: Context, val photoList: MutableList<PhotoBean>): RecyclerView.Adapter<PhotoOverviewAdapter.PhotoOverviewHolder>() {

    companion object {
        const val ONLY_UPDATE_SELECT_STATUS = 200
    }

    val selectedPhotoSet = mutableSetOf<PhotoBean>()
    var onSelectStatusChangeAction: ((MutableSet<PhotoBean>) -> Unit)? = null
    var onItemClickAction: ((PhotoBean) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoOverviewHolder {
        val inflate = ItemPhotoOverviewBinding.inflate(LayoutInflater.from(context),parent,false)
        return PhotoOverviewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return photoList.size
    }

    override fun onBindViewHolder(holder: PhotoOverviewHolder, position: Int) {
        holder.imgStatus.isSelected = selectedPhotoSet.find {
            it.id == photoList[position].id
        } != null

        Glide.with(context)
            .load(photoList[holder.adapterPosition].filePath)
            .into(holder.imgPhoto)


        holder.imgStatus.setOnClickListener {
            var PhotoBean = selectedPhotoSet.find {
                it.id == photoList[position].id
            }
            if (PhotoBean == null) {
                selectedPhotoSet.add(photoList[position])
            } else {
                selectedPhotoSet.remove(PhotoBean)
            }
            selectedPhotoSet.forEach {
                Log.d("zyz", "onBindViewHolder: " + it)
            }
            notifyItemChanged(position,ONLY_UPDATE_SELECT_STATUS)
            onSelectStatusChangeAction?.invoke(selectedPhotoSet)
        }

        holder.itemView.setOnClickListener {
            onItemClickAction?.invoke(photoList[holder.adapterPosition])
        }
    }


    override fun onBindViewHolder(
        holder: PhotoOverviewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            var params = payloads[0]
            if (params is Int && params == ONLY_UPDATE_SELECT_STATUS) {
                holder.imgStatus.isSelected = selectedPhotoSet.find {
                    it.id == photoList[position].id
                } != null
                return
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    fun setNewDatas(photoList: MutableList<PhotoBean>) {
        this.photoList.clear()
        this.photoList.addAll(photoList)
        notifyItemRangeChanged(0,photoList.size)
    }

    class PhotoOverviewHolder(val binding: ItemPhotoOverviewBinding): ViewHolder(binding.root) {
        val imgPhoto = binding.imgPhoto
        val imgStatus = binding.imgStatus
    }
}