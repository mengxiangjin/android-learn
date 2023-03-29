package com.jin.note.edit

import android.content.Context
import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.jin.note.manager.IModelManager

class DoodleTouchLayer2(context: Context): View(context) {


    private var modelManager: IModelManager? = null

    companion object {
        const val TAG = "DoodleTouchLayer2"
    }


    fun setModelManager(modelManager: IModelManager) {
        this.modelManager = modelManager
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }


    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "dispatchTouchEvent: " + event)
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent: " + event)
        if (modelManager == null) return false
        return modelManager!!.onTouchEvent(event)
    }
}