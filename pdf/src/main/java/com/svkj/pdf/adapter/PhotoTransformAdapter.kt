package com.svkj.pdf.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.svkj.pdf.bean.PhotoBean
import com.svkj.pdf.databinding.ItemPhotoTransformBinding

class PhotoTransformAdapter(val context: Context, val photoBeanList: MutableList<PhotoBean>): RecyclerView.Adapter<PhotoTransformAdapter.PDFOperationHolder>() {

    companion object {
        const val ONLY_UPDATE_SELECT_STATUS = 200
    }

    val selectedPhotoBeanList = mutableListOf<PhotoBean>()
    var onSelectStatusChangeAction: ((MutableList<PhotoBean>) -> Unit)? = null


    init {
        photoBeanList.forEach {
            selectedPhotoBeanList.add(it)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDFOperationHolder {
        val inflate = ItemPhotoTransformBinding.inflate(LayoutInflater.from(context),parent,false)
        return PDFOperationHolder(inflate)
    }

    override fun getItemCount(): Int {
        return photoBeanList.size
    }

    override fun onBindViewHolder(holder: PDFOperationHolder, position: Int) {
        holder.tvNum.text = "${holder.adapterPosition + 1}"

        Glide.with(context)
            .load(photoBeanList[holder.adapterPosition].filePath)
            .into(holder.imgPdf)

        holder.imgStatus.isSelected = selectedPhotoBeanList.find {
            it.id == photoBeanList[holder.adapterPosition].id
        } != null
        holder.itemView.setOnClickListener {
            val item = selectedPhotoBeanList.find {
                it.id == photoBeanList[holder.adapterPosition].id
            }
            if (item == null) {
                selectedPhotoBeanList.add(photoBeanList[holder.adapterPosition])
            } else {
                selectedPhotoBeanList.remove(item)
            }
            notifyItemChanged(holder.adapterPosition, ONLY_UPDATE_SELECT_STATUS)
            onSelectStatusChangeAction?.invoke(selectedPhotoBeanList)
        }
    }


    override fun onBindViewHolder(
        holder: PDFOperationHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            var params = payloads[0]
            if (params is Int && params == ONLY_UPDATE_SELECT_STATUS) {
                holder.imgStatus.isSelected = selectedPhotoBeanList.find {
                    it.id == photoBeanList[holder.adapterPosition].id
                } != null
                return
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    fun setNewDatas(pdfList: MutableList<PhotoBean>) {
        this.photoBeanList.clear()
        this.photoBeanList.addAll(pdfList)
        notifyItemRangeChanged(0,pdfList.size)
    }

    fun updateSelectAllStatus(isSelectAll: Boolean) {
        selectedPhotoBeanList.clear()
        if (isSelectAll) {
            photoBeanList.forEach {
                selectedPhotoBeanList.add(it)
            }
        }
        notifyItemRangeChanged(0,photoBeanList.size,ONLY_UPDATE_SELECT_STATUS)
    }

    class PDFOperationHolder(val binding: ItemPhotoTransformBinding): ViewHolder(binding.root) {
        val imgPdf = binding.imgPdf
        val imgStatus = binding.imgStatus
        val tvNum = binding.tvNum

    }
}