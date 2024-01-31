package com.jin.draw.widigit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.jin.draw.R

class CustomSurfaceView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, def: Int = 0) :
    SurfaceView(context, attributeSet, def), SurfaceHolder.Callback {


    private var dstBitmap: Bitmap? = null
    private var mPaint = Paint()
    private var mCanvas: Canvas? = null

    private var isDestroy = false
    private var moveX = 0f
    private var moveDirection = MoveDirection.LEFT //画布移动

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isDestroy = false
        drawAnim()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isDestroy = true
    }

    private fun drawAnim() {
        val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
        val totalWidth = width * 3 / 2
        dstBitmap =
            Bitmap.createScaledBitmap(srcBitmap, totalWidth, srcBitmap.height, false)
        Thread {
            while (!isDestroy) {
                mCanvas = holder.lockCanvas()
                drawView()
                holder.unlockCanvasAndPost(mCanvas)
                Thread.sleep(50)
            }
        }.start()
    }

    private fun drawView() {
        mCanvas!!.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR)
        mCanvas!!.drawBitmap(dstBitmap!!,moveX,0f,mPaint)

        when (moveDirection) {
            MoveDirection.LEFT -> {
                moveX -=1
            }
            MoveDirection.RIGHT -> {
                moveX +=1
            }
        }
        if (moveX <= -width / 2f) {
            moveDirection = MoveDirection.RIGHT
        }

        if (moveX >= 0) {
            moveDirection = MoveDirection.LEFT
        }
    }

    enum class MoveDirection {
        LEFT,RIGHT
    }

}