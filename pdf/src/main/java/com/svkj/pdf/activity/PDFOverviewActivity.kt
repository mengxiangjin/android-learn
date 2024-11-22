package com.svkj.pdf.activity

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.webkit.MimeTypeMap
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.svkj.pdf.adapter.PDFOverviewAdapter
import com.svkj.pdf.bean.PDFBean
import com.svkj.pdf.databinding.ActivityPdfOverviewBinding
import com.svkj.pdf.dialog.LoadingDialog
import com.svkj.pdf.utils.PDFUtils
import java.io.File
import java.io.IOException


class PDFOverviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfOverviewBinding

    private lateinit var pdfOverviewAdapter: PDFOverviewAdapter

    private var allPDFList = mutableListOf<PDFBean>()
    private var selectedPDFList = mutableListOf<PDFBean>()

    private var pdfOperationType = PDFUtils.PDF_MERGE_OPERATION
        set(value) {
            field = value
            binding.tvConfirm.isVisible = field == PDFUtils.PDF_MERGE_OPERATION
        }


    private val MAX_SELECT_PAGE_COUNTS = 500
    private var currentSelectPageCounts = 0
        set(value) {
            field = value
            binding.tvConfirm.text = "已选中${field}/${MAX_SELECT_PAGE_COUNTS}页"
            binding.tvConfirm.isSelected = field > 0
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        WindowCompat.setDecorFitsSystemWindows(window, true) // 关闭沉浸式
//        val params = window.attributes
//        params.layoutInDisplayCutoutMode =
//            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
//        window.attributes = params

        binding = ActivityPdfOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        getPDFFiles()
    }

    private fun initViews() {
        pdfOperationType = intent.getIntExtra(PDFUtils.PDF_OPERATION_KEY,PDFUtils.PDF_MERGE_OPERATION)

        currentSelectPageCounts = 0
        pdfOverviewAdapter = PDFOverviewAdapter(this, mutableListOf()).apply {
            onSelectStatusChangeAction = {selectedPdfs ->
                var selectPageCounts = 0
                selectedPdfs.forEach {
                    selectPageCounts += it.pageCounts
                }
                selectedPDFList.clear()
                selectedPDFList.addAll(selectedPdfs)
                currentSelectPageCounts = selectPageCounts

                when(pdfOperationType) {
                    PDFUtils.PDF_MERGE_OPERATION -> {

                    }
                    PDFUtils.PDF_SPLIT_OPERATION -> {
                        getPDFContentToOperation()
                    }
                    PDFUtils.PDF_TO_PICTURE_OPERATION -> {
                        getPDFContentToOperation()
                    }
                }
            }
        }
        binding.rvPdf.adapter = pdfOverviewAdapter
        binding.rvPdf.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        binding.tvConfirm.setOnClickListener {
            if (currentSelectPageCounts > MAX_SELECT_PAGE_COUNTS || currentSelectPageCounts <= 0) {
                return@setOnClickListener
            }
            getPDFContentToOperation()
        }
        binding.backView.setOnClickListener {
            finish()
        }
    }

    private fun getPDFFiles() {
        val loadingDialog = LoadingDialog(this)
        val uri = MediaStore.Files.getContentUri("external")
        // 定义要查询的列
        val selection = MediaStore.Files.FileColumns.MIME_TYPE + " = ?"
        val selectionArgs = arrayOf(MimeTypeMap.getSingleton().getMimeTypeFromExtension("pdf"))
        // 对查询结果进行排序
        val sortOrder = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"
        val pdfList = mutableListOf<PDFBean>()
        Thread {
            val cursor = contentResolver.query(uri, null, selection, selectionArgs, sortOrder)
            cursor?.let {
                while (it.moveToNext()) {
                    val filePath =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
                    val fileSize =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE))
                    val displayName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME))
                    val dateModified =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATE_MODIFIED))
                    val pageCount = getPageCount(filePath)
                    Log.d("zyz", "getPDFFiles:filePath " + filePath)
                    Log.d("zyz", "getPDFFiles:fileSize " + fileSize)
                    Log.d("zyz", "getPDFFiles:displayName " + displayName)
                    Log.d("zyz", "getPDFFiles:dateModified " + dateModified)
                    Log.d("zyz", "getPDFFiles:pageCount " + pageCount)
                    pdfList.add(PDFBean(pdfList.size,displayName,filePath,dateModified,pageCount,fileSize,
                        mutableListOf()
                    ))
                }
            }
            runOnUiThread {
                allPDFList.clear()
                allPDFList.addAll(pdfList)
                pdfOverviewAdapter.setNewDatas(allPDFList)
                loadingDialog.dismiss()
            }

        }.start()
    }



    private fun getPageCount(pdfFilePath: String): Int {
        var pageCount = 0
        val file = File(pdfFilePath)
        try {
            val parcelFileDescriptor =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(parcelFileDescriptor)
            pageCount = pdfRenderer.pageCount
            pdfRenderer.close()
            parcelFileDescriptor.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return pageCount
    }

    private fun getPDFContentToOperation() {
        var pdfBeanItemId = 0
        if (selectedPDFList.isNotEmpty()) {
            Thread{
                selectedPDFList.forEach {
                    try {
                        val parcelFileDescriptor =
                            ParcelFileDescriptor.open(File(it.filePath), ParcelFileDescriptor.MODE_READ_ONLY)
                        val pdfRenderer = PdfRenderer(parcelFileDescriptor)

                        val pdfItems = mutableListOf<PDFBean.PDFBeanItem>()


                        for (i in 0 until it.pageCounts) {
                            var page = pdfRenderer.openPage(i)
                            // 创建Bitmap，它的大小与页面一致
                            val bitmap =
                                Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                            val pdfBeanItem = PDFBean.PDFBeanItem(pdfBeanItemId,it.id,bitmap)
                            pdfBeanItemId++
                            pdfItems.add(pdfBeanItem)
                            page.close()
                        }
                        it.pdfItems = pdfItems
                        Log.d("zyz", "pdfItems.size: " + it.pdfItems.size)
                        // 关闭PdfRenderer
                        pdfRenderer.close();
                        parcelFileDescriptor.close();
                    }catch (e: IOException) {
                        Log.d("zyz", "IOException: " + e.toString())
                    }
                }
                runOnUiThread {
                    PDFOperationActivity.launchPDFOperationActivity(this,selectedPDFList,pdfOperationType)
                    finish()
                }
            }.start()
        }
    }
}