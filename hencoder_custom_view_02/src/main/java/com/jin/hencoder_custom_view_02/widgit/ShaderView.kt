package com.jin.hencoder_custom_view_02.widgit

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Xfermode
import android.util.AttributeSet
import android.view.View
import com.jin.hencoder_custom_view_02.R

class ShaderView
    @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {


    private val mPaint: Paint = Paint()

    init {
        mPaint.style = Paint.Style.STROKE
    }


    /*
    * LinearGradient   线性渐变
    * RadialGradient 中心向外辐射渐变
    * SweepGradient 扫描渐变
    * */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        mPaint.shader = LinearGradient(100f,100f,500f,500f,Color.RED,Color.GREEN,Shader.TileMode.CLAMP)
//        mPaint.shader = RadialGradient(300f,300f,200f,Color.RED,Color.GREEN,Shader.TileMode.CLAMP)
//        mPaint.shader = SweepGradient(300f,300f,Color.RED,Color.GREEN)
//        bitmapShader(canvas)
//        canvas.drawCircle(300f,300f,200f,mPaint)

        drawWithXfermode(canvas)
    }

    private fun bitmapShader(canvas: Canvas) {
        val bitmapShader =  BitmapShader(
            BitmapFactory.decodeResource(resources, R.drawable.ic_function_bg_five),
            Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )
        mPaint.shader = bitmapShader
        canvas.drawCircle(100f,100f,200f,mPaint)
    }

    private fun drawWithXfermode(canvas: Canvas) {
        val rectf = RectF(100f,100f,300f,300f)
        canvas.drawRoundRect(rectf,35f,35f,mPaint)

        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        val path = Path()
        path.moveTo(150f,300f)
        path.lineTo(200f,400f)
        path.lineTo(250f,300f)
//        path.close()
        canvas.drawPath(path,mPaint)
    }

}