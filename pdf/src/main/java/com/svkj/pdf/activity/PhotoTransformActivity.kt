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
import com.bumptech.glide.Glide
import com.svkj.pdf.R
import com.svkj.pdf.adapter.PhotoTransformAdapter
import com.svkj.pdf.bean.PhotoBean
import com.svkj.pdf.databinding.ActivityPhotoTransformBinding
import com.svkj.pdf.dialog.LoadingDialog
import com.svkj.pdf.helper.PhotoDragTouchHelperCallback
import com.svkj.pdf.utils.PDFUtils


class PhotoTransformActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoTransformBinding
    private lateinit var photoTransformAdapter: PhotoTransformAdapter

    private var totalPhotoBeanCounts = 0
    private var selectedPhotoBeanList = mutableListOf<PhotoBean>()


    companion object {
        var photoBeanList = mutableListOf<PhotoBean>()

        fun launchPhotoOperationActivity(context: Context, selectedPDFList: MutableList<PhotoBean>) {
            val intent = Intent(context, PhotoTransformActivity::class.java)
            context.startActivity(intent)
            photoBeanList.clear()
            photoBeanList.addAll(selectedPDFList)
        }
    }

    private var selectedPDFBeanItemCounts = 0
        set(value) {
            field = value
            binding.tvSelectCounts.text = "已选${field}页"
            binding.tvTotalCounts.text = "共${totalPhotoBeanCounts}页"
            binding.tvSelectAll.isSelected = field == totalPhotoBeanCounts

            binding.tvConfirm.text = "完成"
            binding.tvConfirm.alpha = if (field == 0) {
                0.6f
            } else {
                1f
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoTransformBinding.inflate(layoutInflater)
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

        selectedPhotoBeanList.clear()
        selectedPhotoBeanList.addAll(photoBeanList)

        photoBeanList.forEach {
            Log.d("zyz", "原始id: " + it.id)
        }

        totalPhotoBeanCounts = photoBeanList.size
        selectedPDFBeanItemCounts = totalPhotoBeanCounts

        binding.backView.setOnClickListener {
            finish()
        }

        photoTransformAdapter = PhotoTransformAdapter(this, photoBeanList).apply {
            onSelectStatusChangeAction = {pdfBeanItems ->
                selectedPDFBeanItemCounts = pdfBeanItems.size
                selectedPhotoBeanList.clear()
                selectedPhotoBeanList.addAll(pdfBeanItems)
            }
        }
        binding.rvPdfItem.adapter = photoTransformAdapter
        binding.rvPdfItem.layoutManager = GridLayoutManager(this,2)
        val dragTouchHelper = ItemTouchHelper(PhotoDragTouchHelperCallback(binding.rvPdfItem,photoBeanList))
        dragTouchHelper.attachToRecyclerView(binding.rvPdfItem)

        binding.tvSelectAll.setOnClickListener {
            photoTransformAdapter.updateSelectAllStatus(selectedPDFBeanItemCounts != totalPhotoBeanCounts)
            selectedPDFBeanItemCounts = if (selectedPDFBeanItemCounts == totalPhotoBeanCounts) {
                0
            } else {
                totalPhotoBeanCounts
            }
        }

        binding.tvConfirm.setOnClickListener {
            startPreview()
        }

        binding.tvAdjust.setOnClickListener {
            isPreviewMode = false
        }

        isPreviewMode = true

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
                binding.tvTips.isVisible = it
                if (it) {
                    binding.tvConfirm.text = "图片转PDF(${selectedPhotoBeanList.size})"
                    binding.tvTitle.text = "预览"
                    binding.llPreview.removeAllViews()
                    selectedPhotoBeanList.forEach {
                        val pdfView = LayoutInflater.from(this).inflate(R.layout.item_preview,binding.root,false)
                        var imgPdf = pdfView.findViewById<ImageView>(R.id.img_pdf)
                        Glide.with(this@PhotoTransformActivity)
                            .load(it.filePath)
                            .into(imgPdf)

                        binding.llPreview.addView(pdfView)
                    }
                    binding.tvConfirm.setOnClickListener {
                        startConvertOperation()
                    }
                } else {
                    selectedPDFBeanItemCounts = selectedPDFBeanItemCounts
//                    binding.tvConfirm.text = "已选中${selectedPDFBeanItemCounts}/${totalPhotoBeanCounts}页"
                    binding.tvTitle.text = "选择要转换的页面"
                    binding.tvConfirm.setOnClickListener {
                        startPreview()
                    }
                }
            }
        }

    private fun startPreview() {
        if (selectedPDFBeanItemCounts == 0) return
        selectedPhotoBeanList.forEach {
            Log.d("zyz", "选中后的:it.id "  + it.id)
        }
        
        photoBeanList.forEach {
            Log.d("zyz", "选中后的原始id: " + it.id)
        }
        //原始的可能是被拖拽调换过顺序，需要进行转换
        val newPDFBeanItem = mutableListOf<PhotoBean>()
        photoBeanList.forEach {src ->
            val target = selectedPhotoBeanList.find {
                src.id == it.id
            }
            if (target != null) {
                newPDFBeanItem.add(target)
            }
        }
        selectedPhotoBeanList.clear()
        selectedPhotoBeanList.addAll(newPDFBeanItem)
        isPreviewMode = true
        newPDFBeanItem.forEach {
            Log.d("zyz", "最终选择的id: " + it.id)
        }
    }

    private fun startConvertOperation() {
        val loadingDialog = LoadingDialog(this)
        val filePaths = mutableListOf<String>()
        selectedPhotoBeanList.forEach {
            filePaths.add(it.filePath)
        }
        Thread {
            PDFUtils.mergePhotoToPDF(filePaths) {resultPath ->
                runOnUiThread {
                    loadingDialog.dismiss()
                    if (resultPath != null) {
                        // 主动请求媒体扫描器扫描文件,否则contentResolve会更新不及时
                        MediaScannerConnection.scanFile(this, arrayOf(resultPath), null, null)
                        val intent = Intent(this, PDFOperationResultActivity::class.java)
                        intent.putExtra(PDFUtils.PDF_OPERATION_KEY, PDFUtils.PICTURE_TO_PDF_OPERATION)
                        intent.putExtra(PDFUtils.FILE_PATH_KEY, resultPath)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "转换失败，请稍后再试~", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }.start()

    }


}