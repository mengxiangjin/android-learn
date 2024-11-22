package com.svkj.pdf.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.svkj.pdf.R
import com.svkj.pdf.adapter.PDFOperationAdapter
import com.svkj.pdf.bean.PDFBean
import com.svkj.pdf.databinding.ActivityPdfOperationBinding
import com.svkj.pdf.dialog.LoadingDialog
import com.svkj.pdf.helper.PDFDragTouchHelperCallback
import com.svkj.pdf.utils.PDFUtils


class PDFOperationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfOperationBinding
    private lateinit var pdfOperationAdapter: PDFOperationAdapter

    private var totalPDFBeanItemCounts = 0

    private var selectedPDFBeanItemList = mutableListOf<PDFBean.PDFBeanItem>()
    private var pdfOperationType = PDFUtils.PDF_MERGE_OPERATION

    private var loadingDialog: LoadingDialog?= null


    companion object {
        var PDFItemList = mutableListOf<PDFBean.PDFBeanItem>()


        fun launchPDFOperationActivity(context: Context, selectedPDFList: MutableList<PDFBean>,pdfOperationType: Int) {
            val intent = Intent(context, PDFOperationActivity::class.java)
            intent.putExtra(PDFUtils.PDF_OPERATION_KEY,pdfOperationType)
            Log.d("zyz", "launchPDFOperationActivity: " + pdfOperationType)
            context.startActivity(intent)
            PDFItemList.clear()
            selectedPDFList.forEach {
                PDFItemList.addAll(it.pdfItems)
            }
        }

    }

    private var selectedPDFBeanItemCounts = 0
        set(value) {
            field = value
            binding.tvSelectCounts.text = "已选${field}页"
            binding.tvTotalCounts.text = "共${totalPDFBeanItemCounts}页"
            binding.tvSelectAll.isSelected = field == totalPDFBeanItemCounts

            if (pdfOperationType == PDFUtils.PDF_TO_PICTURE_OPERATION) {
                binding.tvConfirm.text = "逐页转图（${field}）"
            } else {
                binding.tvConfirm.text = "已选中${field}/${totalPDFBeanItemCounts}页"
            }
            binding.tvConfirm.alpha = if (field == 0) {
                0.6f
            } else {
                1f
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfOperationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
    }

    private fun initViews() {
        pdfOperationType = intent.getIntExtra(PDFUtils.PDF_OPERATION_KEY,PDFUtils.PDF_MERGE_OPERATION)

        selectedPDFBeanItemList.clear()
        selectedPDFBeanItemList.addAll(PDFItemList)

        totalPDFBeanItemCounts = PDFItemList.size
        selectedPDFBeanItemCounts = totalPDFBeanItemCounts

        binding.backView.setOnClickListener {
            finish()
        }
        pdfOperationAdapter = PDFOperationAdapter(this, PDFItemList).apply {
            onSelectStatusChangeAction = {pdfBeanItems ->
                selectedPDFBeanItemCounts = pdfBeanItems.size
                selectedPDFBeanItemList.clear()
                selectedPDFBeanItemList.addAll(pdfBeanItems)
            }
        }
        binding.rvPdfItem.adapter = pdfOperationAdapter
        binding.rvPdfItem.layoutManager = GridLayoutManager(this,2)
        val dragTouchHelper = ItemTouchHelper(PDFDragTouchHelperCallback(PDFItemList))
        dragTouchHelper.attachToRecyclerView(binding.rvPdfItem)

        binding.tvSelectAll.setOnClickListener {
            pdfOperationAdapter.updateSelectAllStatus(selectedPDFBeanItemCounts != totalPDFBeanItemCounts)
            selectedPDFBeanItemCounts = if (selectedPDFBeanItemCounts == totalPDFBeanItemCounts) {
                0
            } else {
                totalPDFBeanItemCounts
            }
        }

        binding.tvConfirm.setOnClickListener {
            startPreview()
        }

        binding.tvAdjust.setOnClickListener {
            isPreviewMode = false
        }

    }

    var isPreviewMode: Boolean? = null
        set(value) {
            field = value
            field?.let {
                binding.tvSelectAll.isVisible = !it
                binding.tvAdjust.isVisible = it
                binding.scrollView.isVisible = it
                binding.clOverview.isVisible = !it
                binding.rvPdfItem.isVisible = !it
                if (it) {
                    binding.tvConfirm.text = if (pdfOperationType == PDFUtils.PDF_TO_PICTURE_OPERATION) {
                        "逐页转图（${selectedPDFBeanItemList.size}）"
                    } else {
                        "合并生成PDF(${selectedPDFBeanItemList.size})"
                    }
                    binding.tvTitle.text = "预览"
                    binding.llPreview.removeAllViews()
                    selectedPDFBeanItemList.forEach {
                        val pdfView = LayoutInflater.from(this).inflate(R.layout.item_preview,binding.root,false)
                        var imgPdf = pdfView.findViewById<ImageView>(R.id.img_pdf)
                        imgPdf.setImageBitmap(it.bitmap)

                        binding.llPreview.addView(pdfView)
                    }
                    binding.tvConfirm.setOnClickListener {
                        startConvertOperation()
                    }
                } else {
                    selectedPDFBeanItemCounts = selectedPDFBeanItemCounts
//                    binding.tvConfirm.text = "已选中${selectedPDFBeanItemCounts}/${totalPDFBeanItemCounts}页"
                    binding.tvTitle.text = "选择要转换的页面"
                    binding.tvConfirm.setOnClickListener {
                        startPreview()
                    }
                }
            }
        }

    private fun startPreview() {
        if (selectedPDFBeanItemCounts == 0) return
        selectedPDFBeanItemList.forEach {
            Log.d("zyz", "选中后的:it.id "  + it.id)
        }
        PDFItemList.forEach {
            Log.d("zyz", "选中后的原始id: " + it.id)
        }
        //原始的可能是被拖拽调换过顺序，需要进行转换
        val newPDFBeanItem = mutableListOf<PDFBean.PDFBeanItem>()
        PDFItemList.forEach {src ->
            val target = selectedPDFBeanItemList.find {
                src.id == it.id
            }
            if (target != null) {
                newPDFBeanItem.add(target)
            }
        }
        selectedPDFBeanItemList.clear()
        selectedPDFBeanItemList.addAll(newPDFBeanItem)
        isPreviewMode = true
        newPDFBeanItem.forEach {
            Log.d("zyz", "最终选择的id: " + it.id)
        }
    }

    private fun startConvertOperation() {
        val bitmaps = mutableListOf<Bitmap>()
        selectedPDFBeanItemList.forEach {
            bitmaps.add(it.bitmap)
        }
        loadingDialog = LoadingDialog(this)

        when(pdfOperationType) {
            PDFUtils.PDF_MERGE_OPERATION,PDFUtils.PDF_SPLIT_OPERATION -> {
                startMergeOrSplit(bitmaps)
            }
            PDFUtils.PDF_TO_PICTURE_OPERATION -> {
                saveBitmapsFromPDF(bitmaps)
            }
        }
    }

    private fun startMergeOrSplit(bitmaps: List<Bitmap>) {
        Thread{
            PDFUtils.mergerBitmapsToPDF(bitmaps) { filePath ->
                runOnUiThread {
                    loadingDialog?.dismiss()
                    if (filePath != null) {
                        // 主动请求媒体扫描器扫描文件,否则contentResolve会更新不及时
                        MediaScannerConnection.scanFile(this, arrayOf(filePath), null, null);

                        val intent = Intent(this, PDFOperationResultActivity::class.java)
                        intent.putExtra(PDFUtils.PDF_OPERATION_KEY, pdfOperationType)
                        intent.putExtra(PDFUtils.FILE_PATH_KEY, filePath)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "合并失败，请稍后再试~", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }.start()
    }

    private fun saveBitmapsFromPDF(bitmaps: List<Bitmap>) {
        Thread{
            PDFUtils.saveBitmapsToGallery(bitmaps) { filePath ->
                runOnUiThread {
                    loadingDialog?.dismiss()
                    if (!filePath.isNullOrEmpty()) {
                        // 主动请求媒体扫描器扫描文件,否则contentResolve会更新不及时
                        MediaScannerConnection.scanFile(this,
                            arrayOf(filePath), null, null)

                        val intent = Intent(this, PDFOperationResultActivity::class.java)
                        intent.putExtra(PDFUtils.PDF_OPERATION_KEY, pdfOperationType)
                        intent.putExtra(PDFUtils.FILE_PATH_KEY, filePath)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "转图失败，请稍后再试~", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }.start()
    }


}