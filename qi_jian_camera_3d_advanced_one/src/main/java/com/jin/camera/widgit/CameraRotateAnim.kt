package com.jin.camera.widgit

import android.graphics.Camera
import android.view.animation.Animation
import android.view.animation.Transformation

class CameraRotateAnim(private val fromDegress: Int, private val toDegress: Int,private val isReverse: Boolean = false) : Animation() {


    private var viewCenterX = 0f
    private var viewCenterY = 0f
    private var camera = Camera()

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        viewCenterX = width / 2f
        viewCenterY = height / 2f
    }


    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val currentDegress = fromDegress + (toDegress - fromDegress) * interpolatedTime
        camera.save()

        val translateZ = if (isReverse) {
            400 * (1 - interpolatedTime)
        } else {
            400 * interpolatedTime
        }

        camera.translate(0f,0f,translateZ)
        camera.rotateY(currentDegress)
        camera.getMatrix(t.matrix)

        t.matrix.preTranslate(-viewCenterX, -viewCenterY)
        t.matrix.postTranslate(viewCenterX, viewCenterY)

        camera.restore()
        super.applyTransformation(interpolatedTime, t)
    }
}