package com.svkj.pdf.bean

import android.graphics.Bitmap

class PDFBean(val id: Int, val name: String, val filePath: String, val modifyTime: String, val pageCounts: Int, val fileSize: String,
              var pdfItems: MutableList<PDFBeanItem>) {



    class PDFBeanItem(val id: Int,val parentId: Int,val bitmap: Bitmap)
}