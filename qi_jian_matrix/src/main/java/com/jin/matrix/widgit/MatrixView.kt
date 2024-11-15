package com.jin.matrix.widgit

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.jin.matrix.R
import java.util.Arrays

class MatrixView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) : View(context,attributeSet,defInt) {

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.BLACK
    }


    override fun onDraw(canvas: Canvas) {
//        canvas.save()
//        val matrix = Matrix()
//        matrix.preTranslate(width / 2f,height / 2f)
//        canvas.setMatrix(matrix)
//        canvas.drawRect(0f,0f,400f,400f,paint)
//
//        matrix.preSkew(1f,0f)
//        canvas.setMatrix(matrix)
//        paint.color = Color.RED
//        canvas.drawRect(0f,0f,400f,400f,paint)
//        canvas.restore()
//        super.onDraw(canvas)

//        mapPoints(canvas)
//        mapRadius(canvas)
//        mapRect(canvas)

//        setPolyToPoly(canvas)
        setRectToRect(canvas)
    }

    private fun mapPoints(canvas: Canvas) {
        val matrix = Matrix()
        matrix.setScale(0.5f,1f)
        val srcPoints = floatArrayOf(100f,100f,200f,100f,100f,200f,200f,200f)
        val dstPoints = floatArrayOf()
        matrix.mapPoints(dstPoints,srcPoints)
    }

    private fun mapRadius(canvas: Canvas) {
        val matrix = Matrix()
        val srcRadius = 10f
        matrix.setScale(0.5f,1f)
        val result = matrix.mapRadius(srcRadius)
        Log.d("lzy", "mapRadius: " + result)
    }

    private fun mapRect(canvas: Canvas) {
        val rect = RectF(100f,500f,600f,1000f)
        val matrix = Matrix()
        matrix.setScale(0.5f,1f)
        matrix.mapRect(rect)
    }

    private fun setPolyToPoly(canvas: Canvas) {
        val srcPoints = floatArrayOf(100f,100f,400f,100f,100f,400f,400f,400f)
        val dstPoints = floatArrayOf(100f,300f,400f,200f,100f,400f,400f,300f)

        val matrix = Matrix()
        matrix.setPolyToPoly(srcPoints,0,dstPoints,0,3)
        matrix.mapPoints(srcPoints)
        Log.d("zyz", "setPolyToPoly:srcPoints " + Arrays.toString(srcPoints))
        Log.d("zyz", "setPolyToPoly:dstPoints " + Arrays.toString(dstPoints))
    }

    private fun setRectToRect(canvas: Canvas) {
        val matrix = Matrix()
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_scene)

        val bitmapRect = RectF(0f,0f,bitmap.width.toFloat(),bitmap.height.toFloat())
        val viewRect = RectF(0f,0f,width.toFloat(),height.toFloat())
        matrix.setRectToRect(bitmapRect,viewRect,Matrix.ScaleToFit.CENTER)

        canvas.drawBitmap(bitmap,matrix,paint)
    }



}