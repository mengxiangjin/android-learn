package com.svkj.pdf.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.svkj.pdf.bean.PDFBean
import com.svkj.pdf.databinding.ItemPdfOperationBinding

class PDFOperationAdapter(val context: Context, val pdfBeanItem: MutableList<PDFBean.PDFBeanItem>): RecyclerView.Adapter<PDFOperationAdapter.PDFOperationHolder>() {

    companion object {
        const val ONLY_UPDATE_SELECT_STATUS = 200
    }

    val selectedPdfItemList = mutableListOf<PDFBean.PDFBeanItem>()
    var onSelectStatusChangeAction: ((MutableList<PDFBean.PDFBeanItem>) -> Unit)? = null


    init {
        pdfBeanItem.forEach {
            selectedPdfItemList.add(it)
        }
        Log.d("zyz", ":selectedPdfItemSet.size " + selectedPdfItemList.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDFOperationHolder {
        val inflate = ItemPdfOperationBinding.inflate(LayoutInflater.from(context),parent,false)
        return PDFOperationHolder(inflate)
    }

    override fun getItemCount(): Int {
        return pdfBeanItem.size
    }

    override fun onBindViewHolder(holder: PDFOperationHolder, position: Int) {
        holder.tvNum.text = "${holder.adapterPosition + 1}"
        holder.imgPdf.setImageBitmap(pdfBeanItem[holder.adapterPosition].bitmap)
        holder.imgStatus.isSelected = selectedPdfItemList.find {
            it.id == pdfBeanItem[holder.adapterPosition].id
        } != null
        holder.itemView.setOnClickListener {

            val item = selectedPdfItemList.find {
                it.id == pdfBeanItem[holder.adapterPosition].id
            }
            if (item == null) {
                selectedPdfItemList.add(pdfBeanItem[holder.adapterPosition])
            } else {
                selectedPdfItemList.remove(item)
            }
            notifyItemChanged(holder.adapterPosition, ONLY_UPDATE_SELECT_STATUS)
            onSelectStatusChangeAction?.invoke(selectedPdfItemList)
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
                holder.imgStatus.isSelected = selectedPdfItemList.find {
                    it.id == pdfBeanItem[holder.adapterPosition].id
                } != null
                return
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    fun setNewDatas(pdfList: MutableList<PDFBean.PDFBeanItem>) {
        this.pdfBeanItem.clear()
        this.pdfBeanItem.addAll(pdfList)
        notifyItemRangeChanged(0,pdfList.size)
    }

    fun updateSelectAllStatus(isSelectAll: Boolean) {
        selectedPdfItemList.clear()
        if (isSelectAll) {
            pdfBeanItem.forEach {
                selectedPdfItemList.add(it)
            }
        }
        notifyItemRangeChanged(0,pdfBeanItem.size,ONLY_UPDATE_SELECT_STATUS)
    }

    class PDFOperationHolder(val binding: ItemPdfOperationBinding): ViewHolder(binding.root) {
        val imgPdf = binding.imgPdf
        val imgStatus = binding.imgStatus
        val tvNum = binding.tvNum

    }
}