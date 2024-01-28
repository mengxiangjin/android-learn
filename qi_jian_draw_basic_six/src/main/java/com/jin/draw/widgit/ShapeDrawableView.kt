package com.jin.draw.widgit

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.ArcShape
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.PathShape
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.View
import com.jin.draw.R
import kotlin.io.path.Path

class ShapeDrawableView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,defInt: Int = 0): View(context,attributeSet,defInt) {


    override fun onDraw(canvas: Canvas) {
//        drawRectShapeDrawable(canvas)
//        drawOvalShapeDrawable(canvas)
//        drawArcShapeDrawable(canvas)
//        drawRoundShapeDrawable(canvas)
//        drawPathShapeDrawable(canvas)
        drawBitmapShaderDrawable(canvas)
        super.onDraw(canvas)
    }


    private fun drawRectShapeDrawable(canvas: Canvas) {
        val rectShapeDrawable = ShapeDrawable(RectShape())
        rectShapeDrawable.setBounds(50,50,200,100)
        rectShapeDrawable.paint.color = Color.RED

        rectShapeDrawable.draw(canvas)
    }

    private fun drawOvalShapeDrawable(canvas: Canvas) {
        val ovalShapeDrawable = ShapeDrawable(OvalShape())
        ovalShapeDrawable.setBounds(50,50,200,100)
        ovalShapeDrawable.paint.color = Color.BLUE
        ovalShapeDrawable.draw(canvas)
    }

    private fun drawArcShapeDrawable(canvas: Canvas) {
        val arcShape = ArcShape(0f,300f)
        val arcShapeDrawable = ShapeDrawable(arcShape)
        arcShapeDrawable.setBounds(50,50,400,400)
        arcShapeDrawable.paint.color = Color.YELLOW
        arcShapeDrawable.draw(canvas)
    }

    private fun drawRoundShapeDrawable(canvas: Canvas) {
        val outerRadi = floatArrayOf(20f,20f,0f,0f,0f,0f,20f,20f)
        val inset = RectF(100f,100f,100f,100f)
        val innerRadii = floatArrayOf(0f,0f,20f,20f,20f,20f,0f,0f)

        val roundRectShape = RoundRectShape(outerRadi,inset,innerRadii)
        val roundRectShapeDrawable = ShapeDrawable(roundRectShape)
        roundRectShapeDrawable.setBounds(50,50,400,400)
        roundRectShapeDrawable.paint.color = Color.YELLOW
        roundRectShapeDrawable.paint.style = Paint.Style.STROKE
        roundRectShapeDrawable.draw(canvas)
    }

    private fun drawPathShapeDrawable(canvas: Canvas) {
        val path = Path()
        path.addRect(0f,0f,100f,200f,Path.Direction.CCW)
        val pathShape = PathShape(path,200f,200f)
        val pathShapeDrawable = ShapeDrawable(pathShape)
        pathShapeDrawable.setBounds(0,0,250,150)
        pathShapeDrawable.paint.color = Color.BLUE

        pathShapeDrawable.draw(canvas)
    }

    private fun drawBitmapShaderDrawable(canvas: Canvas) {
        val shapeDrawable = ShapeDrawable(RectShape())
        val bitmap = BitmapFactory.decodeResource(resources,R.drawable.img)
        val bitmapShader = BitmapShader(bitmap,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
        shapeDrawable.setBounds(100,100,900,900)
        shapeDrawable.paint.color = Color.BLUE
        shapeDrawable.paint.shader = bitmapShader

        shapeDrawable.draw(canvas)
    }
}