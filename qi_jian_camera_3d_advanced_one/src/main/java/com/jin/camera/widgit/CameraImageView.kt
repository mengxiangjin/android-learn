package com.jin.camera.widgit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import com.jin.camera.R

class CameraImageView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) :
    androidx.appcompat.widget.AppCompatImageView(context, attributeSet, defInt) {

    private var bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.img)
    private var paint = Paint()

    private val camera = Camera()
    private var progress = 1f


    override fun onDraw(canvas: Canvas) {
//        exampleRotate(canvas)
        canvas.save()
        camera.save()
        paint.alpha = 100
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        camera.rotateX(progress)
//        camera.translate(0f,0f,-progress * 2)
        camera.applyToCanvas(canvas)
        camera.restore()
        super.onDraw(canvas)
        canvas.restore()
    }

    private fun exampleRotate(canvas: Canvas) {
        canvas.save()
        camera.save()

        paint.alpha = 100
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        val matrix = Matrix()
        camera.rotateZ(progress)
        camera.getMatrix(matrix)

        val centerX = width / 2f
        val centerY = height / 2f
        matrix.preTranslate(-centerX,-centerY)
        matrix.postTranslate(centerX,centerX)
        canvas.setMatrix(matrix)

        camera.restore()
        super.onDraw(canvas)
        canvas.restore()
    }

    private fun exampleCameraApply(canvas: Canvas) {
        canvas.save()
        camera.save()

        val matrix = Matrix()
        camera.translate(0f,0f,10f)
        camera.getMatrix(matrix)
        canvas.setMatrix(matrix)

        camera.restore()
        canvas.restore()
    }

    fun setProgress(progress: Int) {
        this.progress = progress.toFloat()
        postInvalidate()
    }
}