package com.wifi.basic_view_book_one_canvas.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View


/*
* 画布相关操作 平移等
* translate rotate scale
* 1.画布进行一系列操作是不可逆的，除非调用save，restore等恢复
* 2.对画布的操作，不会影响到前面步骤所画的区域
* 3.每次draw的时候，都是一张新的画布映射到屏幕上
* */
class CustomOfCanvasOne @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0)
    :View(context,attributeSet,def){


    private val paint = Paint()

    init {
        paint.style = Paint.Style.STROKE
        paint.color = Color.GREEN
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setLayerType(LAYER_TYPE_SOFTWARE,null)

        canvas.drawColor(Color.RED)
        canvas.save()
        val rect = Rect(0,0,100,100)
        canvas.drawRect(rect,paint)

        //画布偏移x,y 不可变的操作 可通过save restore 对画布的操作不会影响到前面的draw
        //相当于每次draw都是在一个全新的画布，然后映射的屏幕中
        canvas.translate(200f,200f)
        paint.color = Color.RED
        canvas.drawRect(rect,paint)

        //不会影响到前面所画出的 旋转
        canvas.rotate(30f)
        canvas.translate(200f,200f)
        canvas.drawRect(rect,paint)

        //缩放
        canvas.translate(200f,200f)
        canvas.drawRect(rect,paint)
        canvas.scale(0.5f,0.5f)
        canvas.drawRect(rect,paint)

        canvas.restore()
        canvas.clipRect(Rect(200,200,600,600))
        canvas.drawColor(Color.BLUE)


    }



}