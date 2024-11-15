package com.jin.draw

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.jin.draw.databinding.ActivityBitmapBinding
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.min

class BitmapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBitmapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBitmapBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        downloadImg()
//        introduceJustDecodeBounds()
//        introduceSampleSize()
//        introduceExtraAlpha()
//        introduceExtraAlphaBlurMask()
//        exampleExtraAlphaLight()
        introduceSizeOfBitmap()
    }

    private fun introduceJustDecodeBounds() {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val bitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_alipay,options)
        Log.d("TAG", "onCreate: " + bitmap) //null
        Log.d("TAG", "onCreate: " + options.outWidth)   //38
        Log.d("TAG", "onCreate: " + options.outHeight)  //38
        Log.d("TAG", "onCreate: " + options.outMimeType)    //image/png
    }

    private fun introduceSampleSize() {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val bitmap = BitmapFactory.decodeResource(resources,R.drawable.girl,options)


        //根据目标宽高与bitmap宽高计算采样率
        val sampleWidth = options.outWidth / 200
        val sampleHeight = options.outHeight / 100
        options.inSampleSize = min(sampleWidth,sampleHeight)

        options.inJustDecodeBounds = false
        val realBitmap = BitmapFactory.decodeResource(resources,R.drawable.girl,options)

        binding.imgNet.setImageBitmap(realBitmap)
    }


    private fun downloadImg() {
        Thread {
            val byteArray = getImg()
            binding.imgNet.post {
                val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
                binding.imgNet.setImageBitmap(bitmap)
            }
        }.start()
    }

    private fun introduceExtraAlpha() {
        val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
        val bitmap = Bitmap.createBitmap(srcBitmap.width,srcBitmap.height,Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val extractAlphaBitmap = srcBitmap.extractAlpha()
        val paint = Paint()
        paint.color = Color.CYAN
        canvas.drawBitmap(extractAlphaBitmap,0f,0f,paint)
        binding.imgNet.setImageBitmap(bitmap)
    }

    private fun introduceExtraAlphaBlurMask() {
        val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
        val dstBitmap = Bitmap.createBitmap(srcBitmap.width,srcBitmap.height,Bitmap.Config.ARGB_8888)
        val paint = Paint().apply {
            maskFilter = BlurMaskFilter(28f,BlurMaskFilter.Blur.NORMAL)
            color = Color.CYAN
        }
        val offset = IntArray(2)
        val alphaBitmap = srcBitmap.extractAlpha(paint, offset)

        val canvas = Canvas(dstBitmap)
        canvas.drawBitmap(alphaBitmap,0f,0f,paint)

        binding.imgNet.setImageBitmap(dstBitmap)
    }


    private fun exampleExtraAlphaLight() {
        val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
        val dstBitmap = Bitmap.createBitmap(srcBitmap.width,srcBitmap.height,Bitmap.Config.ARGB_8888)

        val paint = Paint().apply {
            maskFilter = BlurMaskFilter(20f,BlurMaskFilter.Blur.NORMAL)
            color = Color.CYAN
        }
        val offset = IntArray(2)
        val alphaBitmap = srcBitmap.extractAlpha(paint, offset)

        val canvas = Canvas(dstBitmap)

        //绘制透明度图像
        canvas.drawBitmap(alphaBitmap,0f,0f,paint)

        //绘制源图像
        canvas.drawBitmap(srcBitmap,-offset[0].toFloat(),-offset[1].toFloat(),paint)
        binding.imgNet.setImageBitmap(dstBitmap)

    }

    private fun introduceSizeOfBitmap(): Int {
        val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            srcBitmap.allocationByteCount
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            srcBitmap.allocationByteCount
        } else {
            srcBitmap.rowBytes * srcBitmap.height
        }
    }

    private fun getImg(): ByteArray? {
        val url = URL("https://b.bdstatic.com/searchbox/icms/searchbox/img/cheng_girl.png")
        val urlConnection = url.openConnection() as HttpURLConnection
        urlConnection.readTimeout = 3000
        urlConnection.requestMethod = "GET"
        if (urlConnection.responseCode == 200) {
            val inputStream = urlConnection.inputStream
            val out = ByteArrayOutputStream()
            val byteBuffer = ByteArray(1024)
            var length = inputStream.read(byteBuffer)
            while (length != -1) {
                out.write(byteBuffer, 0, length)
                length = inputStream.read(byteBuffer)
            }
            inputStream.close()
            return out.toByteArray()
        }
        return null
    }
}