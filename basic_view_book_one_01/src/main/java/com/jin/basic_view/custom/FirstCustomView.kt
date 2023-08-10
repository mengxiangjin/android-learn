package com.jin.basic_view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View


/*
* Paint Color Rect 相关
* */
class FirstCustomView @JvmOverloads constructor
    (context: Context,attrs: AttributeSet? = null, def : Int = 0)
    : View(context,attrs,def) {


    private var mPaint: Paint = Paint()


    init {
        mPaint.color = Color.RED
        mPaint.strokeWidth = 50f
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.isAntiAlias = true   //抗锯齿 可以让所画图形四周更加圆滑
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawCircle(150f,150f,150f,mPaint)
        mPaint.color = Color.BLUE
        canvas?.drawCircle(150f,150f,100f,mPaint)
//        canvas?.drawColor(Color.RED)  //画布上色
        canvas?.drawLine(500f,500f,800f,800f, mPaint)

        //矩形描边
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 20f
        mPaint.color = Color.GREEN
        canvas?.drawRect(300f,300f,400f,400f,mPaint)

        //矩形填充
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.strokeWidth = 20f
        mPaint.color = Color.GREEN
        canvas?.drawRect(500f,300f,600f,400f,mPaint)

        //圆角矩形
        mPaint.style = Paint.Style.STROKE
        canvas?.drawRoundRect(700f,300f,800f,400f,30f,30f,mPaint)


        //矩形相交     (避免在onDraw创建对象)
        val redRect = RectF(900f,300f,1000f,400f)
        val greenRect = RectF(980f,300f,1200f,400f)
        val yellowRect = RectF(900f,450f,1000f,500f)
        mPaint.strokeWidth = 10f
        mPaint.color = Color.RED
        canvas?.drawRect(redRect,mPaint)
        mPaint.color = Color.GREEN
        canvas?.drawRect(greenRect,mPaint)
        mPaint.color = Color.YELLOW
        canvas?.drawRect(yellowRect,mPaint)

        //判断矩形是否相交,若相交会改变 redRect原矩形 不相交则不变
        val intersectOne = redRect.intersect(greenRect)
        val intersectTwo = redRect.intersect(yellowRect)
        Log.d("lzy", "intersectOne: $intersectOne")
        Log.d("lzy", "redRect left: ${redRect.left}")
        Log.d("lzy", "redRect top: ${redRect.top}")
        Log.d("lzy", "redRect right: ${redRect.right}")
        Log.d("lzy", "redRect bottom: ${redRect.bottom}")
        Log.d("lzy", "intersectTwo: $intersectTwo")

        //单纯判断是否相交，不会改变原矩形
        var intersectThree = RectF.intersects(greenRect,redRect)
        var intersectFour = redRect.intersects(greenRect.left,greenRect.top,greenRect.right,greenRect.bottom)

        mPaint.color = Color.BLACK
        //合并二个矩形 取最小左上角与最大右小角
        yellowRect.union(greenRect)
        canvas?.drawRect(yellowRect,mPaint)


        //颜色 4个字节存储 8位 argb Int(0~255)
        //alpha << 24 | red << 16 | green << 8 | blue
        Color.argb(255,255,255,255)

        val mPath = Path()
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE
        val newRect = RectF(20F,400F,180F,600F)
        mPath.addRect(newRect, Path.Direction.CW)

        canvas?.drawPath(mPath,mPaint)
    }


}