package com.svkj.pdf.utils

import android.graphics.Bitmap
import android.os.Environment
import android.os.Environment.DIRECTORY_DCIM
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import com.itextpdf.io.IOException
import com.itextpdf.io.image.ImageData
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.property.UnitValue
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object PDFUtils {


    const val PDF_MERGE_OPERATION = 0
    const val PDF_SPLIT_OPERATION = 1
    const val PDF_TO_PICTURE_OPERATION = 2
    const val PICTURE_TO_PDF_OPERATION = 3


    const val FILE_PATH_KEY = "FILE_PATH_KEY"
    const val FILE_PATH_LIST_KEY = "FILE_PATH_LIST_KEY"
    const val PDF_OPERATION_KEY = "PDF_OPERATION_KEY"

    const val PHOTO_BEANS_KEY = "PHOTO_BEANS_KEY"


    fun mergerBitmapsToPDF(bitmaps: List<Bitmap>,onResultCallback: ((String?) -> Unit)? = null) {
        var writer: PdfWriter? = null
        try {
            val pdfName = DataUtils.changeTimeToStrBySecond(System.currentTimeMillis().toString()).replace(" ","")  + ".pdf"
            val dir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
            val file = File(dir,"PDF合并_${pdfName.replace(":","-")}")
            val fos = FileOutputStream(file)
            writer = PdfWriter(fos)

            val pdf = PdfDocument(writer)
            for (bitmap in bitmaps) {
                val imageData: ImageData = ImageDataFactory.create(getByteArrayFromBitmap(bitmap))
                val newPage =
                    pdf.addNewPage(PageSize(imageData.width,imageData.height))
                val canvas = PdfCanvas(newPage)
                canvas.addImage(imageData, 0f, 0f, imageData.width, true)
            }
            onResultCallback?.invoke(file.path)
            pdf.close()
            writer.close()
            Log.d("zyz", "mergerBitmapsToPDF:success ")
        } catch (e: IOException) {
            Log.d("zyz", "mergerBitmapsToPDF: " + e.toString())
            onResultCallback?.invoke(null)
            e.printStackTrace()
        }
    }

    fun saveBitmapsToGallery(bitmaps: List<Bitmap>, onResultCallback: ((String?) -> Unit)? = null) {
        val parentDirName = "PDF逐页转图_" + DataUtils.changeTimeToStrBySecond(System.currentTimeMillis().toString()).replace(" ","")
        val saveDir =
            File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM), parentDirName)
        if (!saveDir.exists()) {
            saveDir.mkdir()
        }
        bitmaps.forEachIndexed { index, bitmap ->
            try {
                val file = File(saveDir,"${index}.png")
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream)
                outputStream.flush()
                outputStream.close()
            }catch (e: java.io.IOException) {
                Log.d("zyz", "saveBitmapsToGallery: " + e.toString())
                onResultCallback?.invoke(null)
                return
            }
        }
        onResultCallback?.invoke(saveDir.path)
    }

    //图片文件地址
    fun mergePhotoToPDF(filePaths: List<String>, onResultCallback: ((String?) -> Unit)? = null) {
        try {
            val pdfName = DataUtils.changeTimeToStrBySecond(System.currentTimeMillis().toString())  + ".pdf"
            val dir = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
            val file = File(dir,"图片转PDF_${pdfName.replace(":","-")}")
            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            filePaths.forEach {
                val img = Image(ImageDataFactory.create(it))
                img.setWidth(UnitValue.createPercentValue(100f));
                Log.d("zyz", "mergePhotoToPDF: " + it)
//                img.setAutoScale(true)
                // 将图片添加到PDF
                document.add(img)
                document.add(Paragraph("\n")); // 在图片之间添加空行
            }
            // 关闭文档
            document.close()
            onResultCallback?.invoke(file.path)
        }catch (e: Exception) {
            onResultCallback?.invoke(null)
        }


    }

    private fun getByteArrayFromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

}