package com.jin.basic_view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class SecondCustomView @JvmOverloads constructor
    (context: Context, attrs: AttributeSet? = null, def : Int = 0)
    : View(context,attrs,def){


    private var mPaint: Paint = Paint()

    private var mPath: Path = Path()

    init {
        mPaint.color = Color.RED
        mPaint.strokeWidth = 5f
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true   //抗锯齿 可以让所画图形四周更加圆滑
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //起始点
        mPath.moveTo(550f,550f)
        mPath.lineTo(550f,800f)
        mPath.lineTo(800f,800f)
        //闭合
        mPath.close()
        canvas.drawPath(mPath,mPaint)

        //圆弧 canvas直接画
        val rect = RectF(200f,600f,400f,800f)
        canvas.drawArc(rect,0f,90f,false,mPaint)
        mPaint.style = Paint.Style.FILL_AND_STROKE
        //带二边的圆弧
        canvas.drawArc(rect,0f,90f,true,mPaint)

        //圆弧 canvas直接画
        mPath.moveTo(300f,1000f)
        val rect1 = RectF(500f,1000f,800f,1500f)
        mPath.arcTo(rect1,0f,90f,true)
        canvas.drawPath(mPath,mPaint)

        //path对象与canvas类似，可添加矩形，弧等路径
        mPath.moveTo(60f,60f)
        val positiveRect = RectF(200f,1000f,400f,1200f)
        val negativeRect = RectF(600f,1000f,800f,1200f)
        mPath.addRect(positiveRect,Path.Direction.CCW)
        mPath.addRect(negativeRect,Path.Direction.CW)
        canvas.drawPath(mPath,mPaint)

        val content = "这是一段用来测试路径方向的文字这是一段用来测试路径方向的文字这是一段用来测试路径方向的文字这是一段用来测试路径方向的文字"
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 0.5f
        mPaint.isAntiAlias = false
        val ccwPath = Path()
        ccwPath.addRect(positiveRect,Path.Direction.CCW)
        val cwPath = Path()
        cwPath.addRect(negativeRect,Path.Direction.CW)
        canvas.drawTextOnPath(content,ccwPath,0f,18f,mPaint)
        canvas.drawTextOnPath(content,cwPath,0f,18f,mPaint)
    }

}