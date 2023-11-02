package com.wifi.basic_view_book_one_canvas.custom

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.wifi.basic_view_book_one_canvas.R


/*
* 画布相关操作 裁剪圆形头像
* clip 相关函数必须禁用硬件加速
* setLayerType(LAYER_TYPE_SOFTWARE,null)
* */
class CustomOfCanvasTwo @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0)
    :View(context,attributeSet,def){


    private val paint = Paint()

    private var avatarBmp: Bitmap? = null
    private var mPath: Path? = null

    init {
        paint.style = Paint.Style.STROKE
        paint.color = Color.GREEN
        avatarBmp = BitmapFactory.decodeResource(resources, R.drawable.ic_bird)
        mPath = Path()
        mPath!!.moveTo(0f,0f,)
        Log.d("lzy", ": " + avatarBmp!!.width)
        Log.d("lzy", ": " + avatarBmp!!.height)

        mPath!!.addCircle((avatarBmp!!.width / 2).toFloat(),
            (avatarBmp!!.height / 2).toFloat(), 100f,Path.Direction.CCW)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        setLayerType(LAYER_TYPE_SOFTWARE,null)
        canvas.clipPath(mPath!!)
//        canvas.drawPath(mPath!!,paint)
//        canvas.drawColor(Color.RED)
        canvas.drawBitmap(avatarBmp!!,0f,0f,paint)
    }
}