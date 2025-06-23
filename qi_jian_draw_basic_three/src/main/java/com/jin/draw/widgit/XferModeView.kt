package com.jin.draw.widgit

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.jin.draw.R

class XferModeView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    View(context, attributeSet, def) {

    val bitmapHeight = 300f
    val bitmapWidth = 300f

    var srcBitmap: Bitmap? = null
    var dstBitmap: Bitmap? = null

    var paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
    }

    var eraserSrcBitmap: Bitmap? = null
    var eraserDstBitmap: Bitmap? = null
    var bonusBitmap: Bitmap? = null
    val bouncedMatrix = Matrix()



    val wavePath = Path()
    val mitemWaveLength = 1000
    var dx = 0f

    init {
        initSrcBitmap()
        initDstBitmap()


        eraserSrcBitmap = BitmapFactory.decodeResource(resources, R.drawable.girl)
        eraserDstBitmap =
            Bitmap.createBitmap(
                eraserSrcBitmap!!.width,
                eraserSrcBitmap!!.height,
                Bitmap.Config.ARGB_8888
            )
        bonusBitmap =
            BitmapFactory.decodeResource(resources, R.drawable.avatar)

        //将中奖图片缩放到与遮罩图相同大小
        bouncedMatrix.setScale(
            eraserSrcBitmap!!.width / 1f / bonusBitmap!!.width,
            eraserSrcBitmap!!.height / 1f / bonusBitmap!!.height
        )

        ValueAnimator.ofFloat(0f,mitemWaveLength.toFloat()).apply {
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener {
                dx  = it.animatedValue as Float
                postInvalidate()
            }
            duration = 3000
            start()
        }
    }


    private fun initSrcBitmap() {
        srcBitmap =
            Bitmap.createBitmap(bitmapWidth.toInt(), bitmapHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(srcBitmap!!)
        paint.color = Color.BLUE
        canvas.drawRect(0f, 0f, bitmapWidth, bitmapHeight, paint)
    }

    private fun initDstBitmap() {
        dstBitmap =
            Bitmap.createBitmap(bitmapWidth.toInt(), bitmapHeight.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(dstBitmap!!)
        paint.color = Color.RED
        canvas.drawOval(0f, 0f, bitmapWidth, bitmapHeight, paint)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        introduceXfermode(canvas)
//        invertedExample(canvas)
        eraserExample(canvas)
//        textMask(canvas)
    }

    private fun introduceXfermode(canvas: Canvas) {
        paint.style = Paint.Style.FILL_AND_STROKE
        val saveLayerID = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        canvas.drawBitmap(dstBitmap!!, 0f, 0f, paint)

        //xfermode设置前为目标对象，即给谁应用xfermode
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)

        //xfermode设置后为源对象，拿什么应用xfermode
        canvas.drawBitmap(srcBitmap!!, bitmapWidth / 2f, bitmapHeight / 2f, paint)
        paint.xfermode = null
        canvas.restoreToCount(saveLayerID)
    }

    private fun invertedExample(canvas: Canvas) {
        //绘制原图像
        val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.girl)
        canvas.drawBitmap(srcBitmap, 0f, 0f, paint)

        val saveLayerID = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        //源图像下方绘制遮罩作为目标图像
        val dstBitmap = BitmapFactory.decodeResource(resources, R.drawable.shader)
        canvas.drawBitmap(dstBitmap, 0f, srcBitmap!!.height.toFloat(), paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

        //绘制倒过来的原图像（作为源图像） PorterDuff.Mode.SRC_IN应用此源图像
        val newMatrix = Matrix()
        newMatrix.setScale(1f, -1f)
        newMatrix.postTranslate(0f, srcBitmap.height * 2f)
        canvas.drawBitmap(srcBitmap, newMatrix, paint)

        paint.xfermode = null
        canvas.restoreToCount(saveLayerID)
    }


    val path = Path()
    var preX = 0f
    var preY = 0f
    private fun eraserExample(canvas: Canvas) {
        paint.strokeWidth = 30f

        //绘制奖励图片（最底层）放开即为刮刮乐效果
        canvas.drawBitmap(bonusBitmap!!,bouncedMatrix,paint)
        val saveLayerID = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), paint)

        val bitmapCanvas = Canvas(eraserDstBitmap!!)
        paint.style = Paint.Style.STROKE

        bitmapCanvas.drawPath(path, paint)
        canvas.drawBitmap(eraserDstBitmap!!, 0f, 0f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        canvas.drawBitmap(eraserSrcBitmap!!, 0f, 0f, paint)

        paint.xfermode = null
        canvas.restoreToCount(saveLayerID)
    }


    private fun textMask(canvas: Canvas) {
        //使用dst_in的话
        // 文本源图像
        val srcTextBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
        val textCanvas = Canvas(srcTextBitmap)
        val mPaint = Paint().apply {
            textSize = 30f
            style = Paint.Style.FILL_AND_STROKE
            color = Color.BLACK
            textAlign = Paint.Align.CENTER
        }
        val text = "CSDN AND OTHER BLOG"
        val baseLineY =
            (mPaint.fontMetrics.descent - mPaint.fontMetrics.ascent) / 2 - mPaint.fontMetrics.descent
        textCanvas.drawText(text, srcTextBitmap.width / 2f, srcTextBitmap.height / 2f - baseLineY, mPaint)
        canvas.drawBitmap(srcTextBitmap, 0f, 0f, paint)

        //遮罩目标图像
        val maskBitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888)
//
        generatePath(400)
        canvas.drawPath(wavePath,paint)
    }

    private fun generatePath(height: Int) {
        path.reset()
        val originY = height / 2f
        val halfWaveLen = 1000 / 2f
        wavePath.moveTo(-mitemWaveLength + dx, originY);
        var i = -mitemWaveLength
        while (i <= width + mitemWaveLength) {
            wavePath.rQuadTo(halfWaveLen / 2, -50f, halfWaveLen, 0f);
            wavePath.rQuadTo(halfWaveLen / 2, 50f, halfWaveLen, 0f);
            i += mitemWaveLength
        }
        wavePath.lineTo(height.toFloat(), height.toFloat());
        wavePath.lineTo (0f , height.toFloat()) ;
        wavePath.close ();
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                preX = event.x
                preY = event.y
                path.moveTo(preX, preY)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val endPointX = (preX + event.x) / 2
                val endPointY = (preY + event.y) / 2

                path.quadTo(preX, preY, endPointX, endPointY)

                preX = event.x
                preY = event.y
                postInvalidate()
            }

            MotionEvent.ACTION_UP -> {
            }
        }
        return super.onTouchEvent(event)
    }

}