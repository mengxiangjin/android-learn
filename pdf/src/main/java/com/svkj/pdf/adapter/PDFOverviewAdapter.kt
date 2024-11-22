package com.svkj.pdf.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.svkj.pdf.bean.PDFBean
import com.svkj.pdf.databinding.ItemPdfOverviewBinding
import com.svkj.pdf.utils.DataUtils

class PDFOverviewAdapter(val context: Context,val pdfList: MutableList<PDFBean>): RecyclerView.Adapter<PDFOverviewAdapter.PDFOverviewHolder>() {

    companion object {
        const val ONLY_UPDATE_SELECT_STATUS = 200
    }

    val selectedPdfSet = mutableSetOf<PDFBean>()
    var onSelectStatusChangeAction: ((MutableSet<PDFBean>) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDFOverviewHolder {
        val inflate = ItemPdfOverviewBinding.inflate(LayoutInflater.from(context),parent,false)
        return PDFOverviewHolder(inflate)
    }

    override fun getItemCount(): Int {
        return pdfList.size
    }

    override fun onBindViewHolder(holder: PDFOverviewHolder, position: Int) {
        holder.tvName.text = pdfList[position].name
        val modifyTime = DataUtils.changeTimeToStrByDay(pdfList[position].modifyTime)
        val mb = DataUtils.changeByteToMB(pdfList[position].fileSize)
        holder.tvDesc.text = "${modifyTime} ${mb}MB ${pdfList[position].pageCounts}页"

        holder.imgStatus.isSelected = selectedPdfSet.find {
            it.id == pdfList[position].id
        } != null
        holder.itemView.setOnClickListener {
            var pdfBean = selectedPdfSet.find {
                it.id == pdfList[position].id
            }
            if (pdfBean == null) {
                selectedPdfSet.add(pdfList[position])
            } else {
                selectedPdfSet.remove(pdfBean)
            }
            notifyItemChanged(position,ONLY_UPDATE_SELECT_STATUS)
            onSelectStatusChangeAction?.invoke(selectedPdfSet)
        }
    }


    override fun onBindViewHolder(
        holder: PDFOverviewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            var params = payloads[0]
            if (params is Int && params == ONLY_UPDATE_SELECT_STATUS) {
                holder.imgStatus.isSelected = selectedPdfSet.find {
                    it.id == pdfList[position].id
                } != null
                return
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    fun setNewDatas(pdfList: MutableList<PDFBean>) {
        this.pdfList.clear()
        this.pdfList.addAll(pdfList)
        notifyItemRangeChanged(0,pdfList.size)
    }

    class PDFOverviewHolder(val binding: ItemPdfOverviewBinding): ViewHolder(binding.root) {
        val tvName = binding.tvName
        val tvDesc = binding.tvDesc
        val imgStatus = binding.imgStatus
    }
}