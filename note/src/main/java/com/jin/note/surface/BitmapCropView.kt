package com.jin.note.surface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.sqrt

class BitmapCropView@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) :
    View(context, attributeSet, def) {


        private var circleRadius = 8f


    private var controlPointF = mutableListOf<CropPoint>()

    private var cropRectF = RectF()
    private var cropLocationRectF = RectF()

    private var path = Path()

    private val minWidth = 160f
    private val minHeight = 160f

    private var circlePaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.WHITE
    }

    private var circleStrokePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLUE
        strokeWidth = 2f
    }

    private var linePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLUE
        strokeWidth = 3f
    }

    private var srcLayoutParams:SrcLayoutParams?  = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawPath(path,linePaint)
        controlPointF.forEach {
            canvas.drawCircle(it.x,it.y,circleRadius,circlePaint)
            canvas.drawCircle(it.x,it.y,circleRadius,circleStrokePaint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initControlPointF(w, h)
    }

    var downCropPoint: CropPoint? = null
    var lastX = 0f
    var lastY = 0f
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                downCropPoint = getDownCropPoint(event.x, event.y)
                if (downCropPoint != null) {
                    //有效
                    lastX = event.rawX
                    lastY = event.rawY
                    return true
                } else {
                    return false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val offsetX = event.rawX - lastX
                val offsetY = event.rawY - lastY

                if (srcLayoutParams == null) return false

                val newLeft = if (left + offsetX < srcLayoutParams!!.left) {
                    srcLayoutParams!!.left
                } else if (right - (left + offsetX) < minWidth) {
                    right - minWidth
                } else {
                    left + offsetX
                }
                val newTop = if (top + offsetY < srcLayoutParams!!.top) {
                    srcLayoutParams!!.top
                } else if (bottom - (top + offsetY) < minHeight) {
                    bottom - minHeight
                }else {
                    top + offsetY
                }

                val newRight = if (right + offsetX > srcLayoutParams!!.right) {
                    srcLayoutParams!!.right
                } else if (right + offsetX - left < minWidth) {
                    left + minWidth
                }else {
                    right + offsetX
                }

                val newBottom = if (bottom + offsetY > srcLayoutParams!!.bottom) {
                    srcLayoutParams!!.bottom
                }else if (bottom + offsetY - top < minHeight) {
                    top + minHeight
                } else {
                    bottom + offsetY
                }

                when(downCropPoint!!.dragEffectEdge) {
                    0 -> {
                        layout(newLeft.toInt(), newTop.toInt(),right,bottom)
                    }
                    1 -> {
                        Log.d("zyz", "onTouchEvent: " + newTop)
                        layout(left,newTop.toInt(),right,bottom)
                    }
                    2 -> {
                        layout(left,newTop.toInt(),newRight.toInt(),bottom)
                    }
                    3 -> {
                        layout(left,top,newRight.toInt(),bottom)
                    }
                    4 -> {
                        layout(left,top,newRight.toInt(),newBottom.toInt())
                    }
                    5 -> {
                        layout(left,top,right,newBottom.toInt())
                    }
                    6 -> {
                        layout(newLeft.toInt(),top,right,newBottom.toInt())
                    }
                    7 -> {
                        layout(newLeft.toInt(),top,right,bottom)
                    }
                }
                lastX = event.rawX
                lastY = event.rawY
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }

    private fun initControlPointF(w: Int, h: Int) {
        if (srcLayoutParams == null) {
            srcLayoutParams = SrcLayoutParams(left,top,right,bottom)
        }

        path.reset()
        controlPointF.clear()
        cropRectF.set(paddingLeft.toFloat(),paddingTop.toFloat(),w.toFloat() - paddingEnd,h.toFloat() - paddingBottom)

        if (cropLocationRectF.isEmpty) {
            cropLocationRectF.set(0f,0f, (width - paddingLeft - paddingEnd).toFloat(),
                (height - paddingTop - paddingBottom).toFloat()
            )
        }
        cropRectF.set(paddingLeft.toFloat(),paddingTop.toFloat(),w.toFloat() - paddingEnd,h.toFloat() - paddingBottom)


        controlPointF.add(CropPoint(cropRectF.left, cropRectF.top,0))
        controlPointF.add(CropPoint(cropRectF.left + cropRectF.width() / 2f, cropRectF.top,1))
        controlPointF.add(CropPoint(cropRectF.right, cropRectF.top,2))
        controlPointF.add(CropPoint(cropRectF.right, cropRectF.height() / 2f,3))
        controlPointF.add(CropPoint(cropRectF.right, cropRectF.bottom,4))
        controlPointF.add(CropPoint(cropRectF.left + cropRectF.width() / 2f,  cropRectF.bottom,5))
        controlPointF.add(CropPoint( cropRectF.left,  cropRectF.bottom,6))
        controlPointF.add(CropPoint( cropRectF.left, cropRectF.height() / 2f,7))
        controlPointF.forEachIndexed { index, pointF ->
            if (index == 0) {
                path.moveTo(pointF.x,pointF.y)
            } else {
                path.lineTo(pointF.x,pointF.y)
            }
        }
        path.close()
    }

    private fun getDownCropPoint(x: Float,y: Float): CropPoint? {
        controlPointF.forEachIndexed { index, cropPoint ->
            val distance = sqrt((cropPoint.x - x) * (cropPoint.x - x) + (cropPoint.y - y) * (cropPoint.y - y))
            if (distance <= circleRadius) {
                return cropPoint
            }
        }
        return null
    }

    fun getCropRegion(): Rect? {
        if (srcLayoutParams != null) {
            val realRight = if (srcLayoutParams!!.right - right <= 0) {
                srcLayoutParams!!.right - srcLayoutParams!!.left - paddingLeft - paddingEnd
            } else {
                right - srcLayoutParams!!.left - paddingLeft - paddingEnd
            }

            val realBottom =  if (srcLayoutParams!!.bottom - bottom <= 0) {
                srcLayoutParams!!.bottom - srcLayoutParams!!.top - paddingTop - paddingBottom
            } else {
                bottom - srcLayoutParams!!.top - paddingTop - paddingBottom
            }
            val rect = Rect(left - srcLayoutParams!!.left,
                abs( top - srcLayoutParams!!.top) ,realRight,realBottom)
            if (rect.isEmpty) {
                return Rect(0,0,width - paddingLeft - paddingEnd,height - paddingTop - paddingBottom)
            }
            return rect
        }
        return null
    }


    class CropPoint(var x:Float,var y :Float,var dragEffectEdge :Int) {
    }

    class SrcLayoutParams(var left: Int,var top: Int,var right: Int,var bottom: Int){}



}