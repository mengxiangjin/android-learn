package com.jin.slide_delete

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View

class CircleAvatarView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,defInt: Int = 0): View(context,attributeSet,defInt) {

    private val paint = Paint()

    private var bitmap: Bitmap

    init {
        bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.resources,R.drawable.img),500,500,false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        paint.color = Color.WHITE
        canvas.drawBitmap(bitmap,0f,0f,paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

        canvas.drawBitmap(getMaskBitmap(),0f,0f,paint)
        canvas.restore()
    }

    private fun getMaskBitmap():Bitmap {
        val createBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val center = bitmap.width / 2f
        val canvas = Canvas(createBitmap)
        val paint = Paint().apply {
            color = Color.WHITE
        }
        canvas.drawCircle(center,center,center,paint)
        return createBitmap
    }
}