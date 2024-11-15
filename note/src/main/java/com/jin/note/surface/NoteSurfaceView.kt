package com.jin.note.surface

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.jin.note.bean.InsertableImg
import com.jin.note.bean.InsertableObject
import java.util.LinkedList

class NoteSurfaceView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    SurfaceView(context, attributeSet, def), SurfaceHolder.Callback,Runnable {

    private var insertLists = LinkedList<InsertableObject>()

    private var isDrawing = false

    init {
        holder.addCallback(this)
    }

    private var paint =  Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.WHITE
    }


    fun addInsertableObject(item: InsertableObject) {
        insertLists.add(item)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.d("TAG", "surfaceCreated: ")
        val lockCanvas = holder.lockCanvas(Rect(0, 0, 1, 1))
        val clipBoundsRect = lockCanvas.clipBounds
        if (clipBoundsRect.width() == width && clipBoundsRect.height() == height) {
            lockCanvas.drawColor(Color.BLACK)
            holder.unlockCanvasAndPost(lockCanvas)
        } else {
            holder.unlockCanvasAndPost(lockCanvas)
        }
        isDrawing = true
        Thread(this).start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isDrawing = false
        Log.d("TAG", "surfaceDestroyed: ")
    }

    override fun run() {
        while (isDrawing) {
            Log.d("TAG", "run:1 ")
            val insertableObject = insertLists.poll()
            insertableObject?.let {
                val canvas = holder.lockCanvas()
                when(it) {
                    is InsertableImg -> {
                        canvas.drawBitmap(it.bitmap,0f,0f,paint)
                    }
                }
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }
}