package com.jin.draw.widgit

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.OvershootInterpolator
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tan


class DragView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    def: Int = 0
) : View(context, attributeSet, def){


    /* 固定圆的圆心 */
    private val stableCenter = PointF(200f, 200f)

    /* 固定圆的半径 */
    private val stableRadius = 30f

    /* 拖拽圆的圆心 */
    private val dragCenter = PointF(100f, 100f)

    /* 拖拽圆的半径 */
    private val dragRadius = 40f

    /*拖拽圆最大拖拽距离*/
    private val maxDragDistance = 500f
    /*固定圆最小半径*/
    private val minStableRadius = 5f

    /*是否拖拽超出最大距离范围*/
    private var isOutOfMaxDistanceRange = false

    /*是否所有视图都消失*/
    private var isAllDisappear = false

    private val path = Path()

    private val paint = Paint().apply {
        color = Color.RED
        style = Paint.Style.FILL_AND_STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val distance = calDistance(stableCenter,dragCenter)
        val precent = distance / maxDragDistance
        var tempRadius = stableRadius + (minStableRadius - stableRadius) * precent
        if (tempRadius < minStableRadius) {
            tempRadius = minStableRadius
        }

        if (!isAllDisappear) {
            if (!isOutOfMaxDistanceRange) {
                canvas.drawCircle(stableCenter.x,stableCenter.y,tempRadius,paint)
                canvas.drawCircle(dragCenter.x,dragCenter.y,dragRadius,paint)
                path.reset()

                val controlPointF = PointF((stableCenter.x + dragCenter.x) / 2,(stableCenter.y + dragCenter.y) / 2)

                val divided = Math.toRadians(atan((stableCenter.y - dragCenter.y) / (stableCenter.x - dragCenter.x)).toDouble())

                val dragPointOne = PointF((dragCenter.x + sin(divided) * dragRadius).toFloat(),
                    (dragCenter.y - cos(divided) * dragRadius).toFloat()
                )

                val dragPointTwo = PointF((dragCenter.x - sin(divided) * dragRadius).toFloat(),
                    (dragCenter.y + cos(divided) * dragRadius).toFloat()
                )

                val stablePointOne = PointF((stableCenter.x + sin(divided) * tempRadius).toFloat(),
                    (stableCenter.y - cos(divided) * tempRadius).toFloat()
                )

                val stablePointTwo = PointF((stableCenter.x - sin(divided) * tempRadius).toFloat(),
                    (stableCenter.y + cos(divided) * tempRadius).toFloat()
                )

                path.moveTo(dragPointOne.x,dragPointOne.y)
                path.quadTo(controlPointF.x,controlPointF.y,stablePointOne.x,stablePointOne.y)
                path.lineTo(stablePointTwo.x,stablePointTwo.y)
                path.quadTo(controlPointF.x,controlPointF.y,dragPointTwo.x,dragPointTwo.y)
                canvas.drawPath(path,paint)
            } else {
                canvas.drawCircle(dragCenter.x,dragCenter.y,dragRadius,paint)
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                isOutOfMaxDistanceRange = false
                isAllDisappear = false
                dragCenter.set(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                dragCenter.set(event.x, event.y)
                if (calDistance(stableCenter,dragCenter) > maxDragDistance) {
                    isOutOfMaxDistanceRange = true
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                if (isOutOfMaxDistanceRange) {
                    //Move超出，判断Up是否超出最大距离
                    if (calDistance(PointF(event.x,event.y),dragCenter) > maxDragDistance) {
                        isAllDisappear = true
                    } else {
                        dragCenter.set(stableCenter.x,stableCenter.y)
                    }
                } else {
                    //均未超出最大距离（Move、Up）
                    val tempPointF = PointF(dragCenter.x,dragCenter.y)
                    val distance = calDistance(stableCenter,dragCenter)
                    val anim = ValueAnimator.ofFloat(distance,0f).apply {
                        duration = 3000
                        interpolator = OvershootInterpolator(3f)
                        addUpdateListener {
                            val animatedFraction = it.animatedFraction
                            Log.d("lzy", "onTouchEvent: " + it.animatedFraction)
                            val tempX = tempPointF.x + (stableCenter.x - tempPointF.x) * animatedFraction
                            val tempY = tempPointF.y + (stableCenter.y - tempPointF.y) * animatedFraction
                            dragCenter.set(tempX,tempY)
                            invalidate()
                        }
                    }
                    anim.start()
                }
                invalidate()
            }
        }
        return true
    }


    private fun calDistance(startPoint: PointF,endPoint: PointF): Float {
        return sqrt((startPoint.x - endPoint.x) * (startPoint.x - endPoint.x) + (startPoint.y - endPoint.y) * (startPoint.y - endPoint.y))
    }

}