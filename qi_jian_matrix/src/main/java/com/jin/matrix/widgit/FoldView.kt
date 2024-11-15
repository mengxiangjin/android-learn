package com.jin.matrix.widgit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import com.jin.matrix.R
import kotlin.math.sqrt

class FoldView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) :
    View(context, attributeSet, defInt) {

    private val counts = 8
    private var itemWidth = 0f  //未折叠的单个宽度
    private var itemFolderWidth = 0f //折叠后的单个宽度 垂直长度
    private val paint = Paint()
    private val srcMatrix = Matrix()
    private val factor = 0.8f   //折叠后的总宽度 = 折叠前的总宽度 * factor
    private val bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_scene)
    private var h = 0f

    private val lineaPaint = Paint()
    private val solidPaint = Paint()

    init {
        itemWidth = bitmap.width / 1f / counts
        itemFolderWidth = bitmap.width / 1f * factor / counts
        h = sqrt(itemWidth * itemWidth - itemFolderWidth * itemFolderWidth)

        val alpha = (255 * (1 - factor)).toInt()
        solidPaint.color = Color.argb(alpha * factor, 0f, 0f, 0f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (i in 0 until counts) {
            srcMatrix.reset()
            val srcPoints = floatArrayOf(
                itemWidth * i, 0f,
                itemWidth * (i + 1), 0f,
                itemWidth * (i + 1), bitmap.height.toFloat(),
                itemWidth * i, bitmap.height.toFloat()
            )
            val dstPoints = if (i % 2 == 0) {
                //偶
                floatArrayOf(
                    itemFolderWidth * i, 0f,
                    itemFolderWidth * (i + 1), h,
                    itemFolderWidth * (i + 1), bitmap.height + h,
                    itemFolderWidth * i, bitmap.height.toFloat()
                )

            } else {
                floatArrayOf(
                    itemFolderWidth * i, h,
                    itemFolderWidth * (i + 1), 0f,
                    itemFolderWidth * (i + 1), bitmap.height.toFloat(),
                    itemFolderWidth * i, bitmap.height + h
                )
            }
            srcMatrix.setPolyToPoly(srcPoints, 0, dstPoints, 0, srcPoints.size / 2)

            canvas.save()
            canvas.setMatrix(srcMatrix)
            canvas.clipRect(itemWidth * i, 0f, itemWidth * (i + 1), bitmap.height.toFloat())
            canvas.drawBitmap(bitmap, 0f, 0f, paint)

            if (i % 2 == 0) {
                //
                lineaPaint.shader = LinearGradient(
                    itemWidth * i,
                    0f,
                    itemWidth * (i + 1),
                    0f,
                    Color.BLACK,
                    Color.TRANSPARENT,
                    Shader.TileMode.CLAMP
                )
                canvas.drawRect(
                    itemWidth * i,
                    0f,
                    itemWidth * (i + 1),
                    bitmap.height.toFloat(),
                    lineaPaint
                )
            } else {
//                canvas.drawRect(itemWidth * i, 0f, itemWidth * (i + 1), bitmap.height.toFloat(),solidPaint)
            }
            canvas.restore()
        }


//        srcMatrix.reset()
//        val srcPoints = floatArrayOf(
//            itemWidth, 0f,
//            itemWidth * 2, 0f,
//            itemWidth, bitmap.height.toFloat(),
//            itemWidth * 2, bitmap.height.toFloat()
//        )
//        val dstPoints =  floatArrayOf(
//            itemFolderWidth, h,
//            itemFolderWidth * 2, 0f,
//            itemFolderWidth, bitmap.height + h,
//            itemFolderWidth * 2, bitmap.height.toFloat(),
//        )
//        srcMatrix.setPolyToPoly(srcPoints,0,dstPoints,0,srcPoints.size / 2)
//
//        canvas.save()
//        canvas.setMatrix(srcMatrix)
//        canvas.clipRect(itemWidth,0f,itemWidth * 2,bitmap.height.toFloat())
//        canvas.drawBitmap(bitmap,0f,0f,paint)
//
//        canvas.restore()
    }
}