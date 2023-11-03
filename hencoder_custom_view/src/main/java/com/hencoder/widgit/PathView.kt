package com.hencoder.widgit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class PathView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {


    private val mPaint: Paint = Paint()
    private var mPath = Path()

    init {
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.color = Color.BLACK
        mPaint.strokeWidth = 5f
        mPaint.isAntiAlias = true
        // 使用 path 对图形进行描述（这段描述代码不必看懂）
        mPath.addArc(200f, 200f, 400f, 400f, -225f, 225f);
        mPath.arcTo(400f, 200f, 600f, 400f, -180f, 225f, false);
        mPath.lineTo(400f, 542f);

    }


    /*
    * path.arcTo：圆弧连接(非扇形)
    * forceMoveTo = true 画笔非平滑连接，即断开重新画
    * forceMoveTo = false 画笔平滑连接，即从画笔的末位置（lineTo(200,200)） 平滑连接
    *
    * 实际上arcTo(forceMoveTo = true) == addArc()
    * */
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        mPath.reset()
//        mPath.lineTo(100f,100f)
//        //先画一条直线，再画一段圆弧，path 非扇形圆弧
//        mPath.arcTo(100f,100f,200f,200f,-90f,90f,true)
//        canvas.drawPath(mPath,mPaint)
//    }


    /*
    * mPath.fillType:相交路径的填充方式
    *    Path.FillType.WINDING:默认 全填充(若二个图形绘制方向不同，则其实效果也是交叉填充 Path.Direction.CCW)
    *    Path.FillType.INVERSE_WINDING 反向全填充
    *    Path.FillType.EVEN_ODD:    交叉填充
    *    Path.FillType.INVERSE_EVEN_ODD 反向交叉填充
    * */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mPaint.strokeWidth = 0f
        mPath.reset()
        mPath.fillType = Path.FillType.WINDING
        mPath.addCircle(100f,100f,50f,Path.Direction.CCW)
        mPath.addCircle(150f,100f,50f,Path.Direction.CCW)
        canvas.drawPath(mPath,mPaint)
    }
}