package com.jin.draw.widigit

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class BufferSurfaceView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    SurfaceView(context, attributeSet, def), SurfaceHolder.Callback {


    private val paint = Paint().apply {
        color = Color.RED
        textSize = 50f
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL_AND_STROKE
    }


    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawText()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }


    private fun drawText() {
        Thread {
            for (i in 0 until 10) {
                val lockCanvas = holder.lockCanvas()
                lockCanvas.drawText("$i", width / 2f, (i + 1) * 50f, paint)
                holder.unlockCanvasAndPost(lockCanvas)
                Thread.sleep(800)
            }
        }.start()
    }


}