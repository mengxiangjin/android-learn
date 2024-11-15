package com.jin.camera.widgit

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Camera
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.BounceInterpolator
import android.widget.LinearLayout

class CameraRotateGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) :
    LinearLayout(context, attributeSet, defInt) {


    private val MAX_ROTATE_DEGRESS = 20f
    private val camera = Camera()
    private var centerX = 0f
    private var centerY = 0f
    private val matrix = Matrix()
    private var rotateX = 0f
    private var rotateY = 0f


    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        camera.save()
        matrix.reset()
        camera.rotateX(rotateX)
        camera.rotateY(rotateY)
        camera.getMatrix(matrix)

        matrix.preTranslate(-centerX,-centerY)
        matrix.postTranslate(centerX,centerY)
        canvas.setMatrix(matrix)
        super.dispatchDraw(canvas)
        canvas.restore()
        camera.restore()

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                calRotateDegress(event.x,event.y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                calRotateDegress(event.x,event.y)
                return true
            }
            MotionEvent.ACTION_UP -> {
                resetView()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun calRotateDegress(x: Float, y: Float) {
        var precentX =  (x - centerX) / (width / 2f)
        var precentY = (y - centerY) / (height / 2f)
        if (precentX > 1f) {
            precentX = 1f
        }
        if (precentX < -1f) {
            precentX = -1f
        }
        if (precentY > 1f) {
            precentY = 1f
        }
        if (precentY < -1f) {
            precentY = -1f
        }
        rotateX = precentY * MAX_ROTATE_DEGRESS
        rotateY = precentX * MAX_ROTATE_DEGRESS
        postInvalidate()
    }

    private fun resetView() {
        val rotateXPropertyHolder = PropertyValuesHolder.ofFloat("rotateX", rotateX, 0f)
        val rotateYPropertyHolder = PropertyValuesHolder.ofFloat("rotateY", rotateY, 0f)
        val valueAnimator =
            ValueAnimator.ofPropertyValuesHolder(rotateXPropertyHolder, rotateYPropertyHolder)
        valueAnimator.addUpdateListener {
            rotateX = it.getAnimatedValue("rotateX") as Float
            rotateY = it.getAnimatedValue("rotateY") as Float
            postInvalidate()
        }
        valueAnimator.duration = 3000
        valueAnimator.interpolator = BounceInterpolator()
        valueAnimator.start()
    }


}