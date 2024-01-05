package com.jin.anim.wigit

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.View
import com.jin.anim.R
import kotlin.math.atan2

class PathMeasureView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    View(context, attributeSet, def) {

    private val path = Path()
    private var pathMeasure: PathMeasure? = null
    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth =
            TypedValue.applyDimension(COMPLEX_UNIT_DIP, 5f, context.resources.displayMetrics)
    }


    private var animValue = 0f

    private var bitmap: Bitmap? = null

    private val dstPath = Path()

    init {
        path.reset()
        path.addCircle(500f, 500f, 150f, Path.Direction.CW)
        path.moveTo(500f - 150f / 2, 500f)
        path.lineTo(500f, 500f + 150f / 2)
        path.lineTo(500f + 150f / 2, 500f - 150f / 2)
        pathMeasure = PathMeasure(path, false)

        bitmap = BitmapFactory.decodeResource(resources, R.drawable.arrow)
        val anim = ValueAnimator.ofFloat(0f, 1f)
        anim.addUpdateListener { animation ->
            animValue = animation.animatedValue as Float
            invalidate()
        }
        anim.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {

            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
                pathMeasure!!.nextContour()
            }
        })
        anim.duration = 3000
        anim.repeatCount = 1
        anim.start()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        introducePathMeasure(canvas)
//        pathMeasureOfNextContour(canvas)
//        pathMeasureOfSegment(canvas)
//        pathMeasureOfAnim(canvas)
//        pathMeasureOfAnimWithArrow(canvas)
//        pathMeasureOfAnimWithMatrix(canvas)
        pathMeasureOfExampleAli(canvas)
    }

    private fun introducePathMeasure(canvas: Canvas) {
        canvas.translate(100f, 100f)
        path.reset()
        path.moveTo(100f, 0f)
        path.lineTo(100f, 100f)
        path.lineTo(200f, 100f)
        path.lineTo(200f, 0f)

        pathMeasure = PathMeasure()
        /*
        * forceClosed:
        *   false:pathMeasure.length = 300
        *   true:pathMeasure.length = 400
        * */
        pathMeasure!!.setPath(path, false)
        pathMeasure!!.setPath(path, true)
        canvas.drawPath(path, paint)
    }

    private fun pathMeasureOfNextContour(canvas: Canvas) {
        path.reset()
        //添加3个非连续的路径Rect
        path.addRect(100f, 100f, 600f, 600f, Path.Direction.CW)
        path.addRect(200f, 200f, 500f, 500f, Path.Direction.CW)
        path.addRect(300f, 300f, 400f, 400f, Path.Direction.CCW)
        canvas.drawPath(path, paint)

        pathMeasure = PathMeasure(path, false)
        do {
            Log.d("lzy", "pathMeasureNextContour: " + pathMeasure!!.length)
        } while (pathMeasure!!.nextContour())
    }

    private fun pathMeasureOfSegment(canvas: Canvas) {
        path.reset()
        path.addRect(100f, 100f, 600f, 600f, Path.Direction.CCW)
        pathMeasure = PathMeasure(path, false)

        val dstPath = Path()
        dstPath.addCircle(50f, 50f, 20f, Path.Direction.CCW)
        pathMeasure!!.getSegment(0f, 650f, dstPath, true)
        canvas.drawPath(dstPath, paint)
    }

    private fun pathMeasureOfAnim(canvas: Canvas) {
        path.reset()
        path.addCircle(500f, 500f, 150f, Path.Direction.CW)
        val destPath = Path()
        pathMeasure = PathMeasure(path, false)
        pathMeasure!!.getSegment(0f, pathMeasure!!.length * animValue, destPath, true)
        canvas.drawPath(destPath, paint)
    }

    private fun pathMeasureOfAnimWithArrow(canvas: Canvas) {
        path.reset()
        path.addCircle(500f, 500f, 150f, Path.Direction.CW)
        val destPath = Path()
        val posArray = FloatArray(2)
        val tanArray = FloatArray(2)
        val matrix = Matrix()
        pathMeasure = PathMeasure(path, false)
        pathMeasure!!.getSegment(0f, pathMeasure!!.length * animValue, destPath, true)
        pathMeasure!!.getPosTan(pathMeasure!!.length * animValue, posArray, tanArray)
        matrix.postRotate(
            Math.toDegrees(atan2(tanArray[1], tanArray[0]).toDouble()).toFloat(),
            bitmap!!.width / 2f,
            bitmap!!.height / 2f
        )
        matrix.postTranslate(posArray[0] - bitmap!!.width / 2f, posArray[1] - bitmap!!.height / 2f)

        canvas.drawBitmap(bitmap!!, matrix, paint)
        canvas.drawPath(destPath, paint)
    }

    private fun pathMeasureOfAnimWithMatrix(canvas: Canvas) {
        path.reset()
        path.addCircle(500f, 500f, 150f, Path.Direction.CW)
        pathMeasure = PathMeasure(path, false)
        val dstPath = Path()
        val matrix = Matrix()
        pathMeasure!!.getSegment(0f, animValue * pathMeasure!!.length, dstPath, true)
        pathMeasure!!.getMatrix(
            animValue * pathMeasure!!.length, matrix,
            PathMeasure.POSITION_MATRIX_FLAG or PathMeasure.TANGENT_MATRIX_FLAG
        )
        matrix.preTranslate(-bitmap!!.width / 2f, -bitmap!!.height / 2f)
        canvas.drawPath(dstPath, paint)
        canvas.drawBitmap(bitmap!!, matrix, paint)
    }

    private fun pathMeasureOfExampleAli(canvas: Canvas) {
        pathMeasure!!.getSegment(0f,animValue * pathMeasure!!.length,dstPath,true)
        canvas.drawPath(dstPath,paint)
    }



}