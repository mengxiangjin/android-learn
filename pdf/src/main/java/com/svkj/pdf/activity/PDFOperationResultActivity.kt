package com.svkj.pdf.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.svkj.pdf.MainActivity
import com.svkj.pdf.databinding.ActivityPdfOpertationResultBinding
import com.svkj.pdf.utils.PDFUtils
import com.svkj.pdf.utils.PictureScanner
import java.io.File


class PDFOperationResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfOpertationResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfOpertationResultBinding.inflate(layoutInflater)
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
        val filePath = intent.getStringExtra(PDFUtils.FILE_PATH_KEY)
        val pdfOperationType = intent.getIntExtra(PDFUtils.PDF_OPERATION_KEY,PDFUtils.PDF_MERGE_OPERATION)

        when(pdfOperationType) {
            PDFUtils.PDF_MERGE_OPERATION,PDFUtils.PDF_SPLIT_OPERATION,PDFUtils.PICTURE_TO_PDF_OPERATION -> {
                binding.groupPdf.isVisible = true
                binding.groupPicture.isVisible = false
                filePath?.let {path ->
                    binding.tvName.text = File(path).name

                    binding.tvLook.setOnClickListener {
                        openPDFFile(getFileUri(File(path)))
                    }

                    binding.tvSend.setOnClickListener {
                        shareFile(getFileUri(File(path)),"application/pdf")
                    }
                }
            }
            PDFUtils.PDF_TO_PICTURE_OPERATION -> {
                binding.groupPdf.isVisible = false
                binding.groupPicture.isVisible = true
                binding.tvTitle.text = "PDF逐页转图"
                filePath?.let {filePath ->
                    val parentFile = File(filePath)
                    if (!parentFile.isDirectory) {
                        finish()
                        return
                    }
                    val listFiles = parentFile.listFiles()
                    if (listFiles.isNullOrEmpty()) {
                        finish()
                        return
                    }

                    binding.tvTips.text = "${listFiles.size}张照片导出成功"
                    binding.tvCounts.text = "${listFiles.size}"
                    Glide.with(this)
                        .load(listFiles[0])
                        .into(binding.imgPreview)

                    binding.tvSend.setOnClickListener {
                        shareFile(getFileUri(File(listFiles[0].path)),"image/png")
                    }

                    binding.tvLook.setOnClickListener {
                        PictureScanner(this, filePath)
                    }
                }
            }
        }



        binding.backView.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }



    private fun openPDFFile(pdfUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(pdfUri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    fun openPhotoFiles(uri: Uri) {
//        val clipData = ClipData.newRawUri("clip_label", Uri.parse("content://some/content/uri"))
//        for (item in paths) {
//            clipData.addItem(ClipData.Item(getFileUri(File(item))))
//        }
//        val intent = Intent()
//        intent.setAction(Intent.ACTION_VIEW)
//        intent.setType("image/*")
//        intent.clipData = clipData
//        startActivity(intent)

        val intent = Intent(Intent.ACTION_VIEW,uri)
//        intent.setDataAndType(uri, "image/*")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    private fun shareFile(uri: Uri,type: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType(type)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent,"Share PDF"))
    }

    private fun getFileUri(file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(this, "${packageName}.provider", file)
        } else {
            Uri.fromFile(file)
        }
    }




}