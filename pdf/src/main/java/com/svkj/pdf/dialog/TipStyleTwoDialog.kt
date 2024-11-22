package com.svkj.pdf.dialog

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.svkj.pdf.databinding.DialogTipStyleTwoBinding

class TipStyleTwoDialog(private val context: Context) {
    var dialog: Dialog? = null
        private set
    lateinit var binding: DialogTipStyleTwoBinding
        private set

    var titleColor: Int? = null

    private fun init() {
        if (dialog != null) return
        binding = DialogTipStyleTwoBinding.inflate(LayoutInflater.from(context))
        dialog = Dialog(context).apply {
            setContentView(binding.root)
            setCancelable(true)
            setCanceledOnTouchOutside(false)
        }

        binding.tvCancel.setOnClickListener { dialog!!.dismiss() }
    }

    /**
     * @param tip 若为空将只展示标题
     */
    fun show(
        title: String,
        tip: String?,
        listener: OnEventListener,
        cancelTip: String? = null,
        confirmTip: String? = null
    ) {
        init()
        binding.tvTitle.text = title
        if (tip == null) {
            binding.tvTip.visibility = View.GONE
        } else {
            binding.tvTip.visibility = View.VISIBLE
            binding.tvTip.text = tip
        }
        cancelTip?.let {
            binding.tvCancel.text = it
        }
        confirmTip?.let {
            binding.tvConfirm.text = it
        }
        binding.tvConfirm.setOnClickListener {
            dialog!!.dismiss()
            listener.onConfirm()
        }
        titleColor?.let { binding.tvTitle.setTextColor(it) }
        dialog!!.show()
        dialog!!.window?.let {
            it.setBackgroundDrawable(null)
            it.attributes.let { params ->
                params.width = WindowManager.LayoutParams.MATCH_PARENT
                it.attributes = params
            }
        }
    }

    interface OnEventListener {
        fun onConfirm()
    }
}