package com.jin.draw.widigit

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class BufferSurfaceLockRectView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    SurfaceView(context, attributeSet, def), SurfaceHolder.Callback {

    private val paint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 30f
    }

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        draw()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }


    private fun draw() {
        Thread {
            while (true) {
                val lockCanvas = holder.lockCanvas(Rect(0, 0, 1, 1))
                val clipBoundsRect = lockCanvas.clipBounds
                if (clipBoundsRect.width() == width && clipBoundsRect.height() == height) {
                    lockCanvas.drawColor(Color.BLACK)
                    holder.unlockCanvasAndPost(lockCanvas)
                } else {
                    holder.unlockCanvasAndPost(lockCanvas)
                    break
                }
            }

            for (i in 0 until 10) {
                //大红圆
                if (i == 0) {
                    val lockCanvas = holder.lockCanvas(Rect(10,10,600,600))
                    lockCanvas.drawColor(Color.RED)
                    holder.unlockCanvasAndPost(lockCanvas)
                }

                //中绿圆
                if (i == 1) {
                    val lockCanvas = holder.lockCanvas(Rect(30,30,570,570))
                    lockCanvas.drawColor(Color.GREEN)
                    holder.unlockCanvasAndPost(lockCanvas)
                }

                //小蓝圆
                if (i == 2) {
                    val lockCanvas = holder.lockCanvas(Rect(60,60,540,540))
                    lockCanvas.drawColor(Color.BLUE)
                    holder.unlockCanvasAndPost(lockCanvas)
                }

                //小小白色圆
                if (i == 3) {
                    val lockCanvas = holder.lockCanvas(Rect(200,200,400,400))
                    paint.color = Color.WHITE
                    lockCanvas.drawCircle(300f,300f,100f,paint)
                    holder.unlockCanvasAndPost(lockCanvas)
                }

                //小小小文字
                if (i == 4) {
                    val lockCanvas = holder.lockCanvas(Rect(250,250,350,350))
                    paint.color = Color.RED
                    paint.style = Paint.Style.FILL_AND_STROKE
                    paint.textSize = 30f
                    paint.strokeWidth = 0f
                    lockCanvas.drawText("$i",300f,300f,paint)
                    holder.unlockCanvasAndPost(lockCanvas)
                }

                Thread.sleep(800)
            }
        }.start()
    }


}