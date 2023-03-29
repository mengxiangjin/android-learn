package com.jin.note.edit

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.AttrRes
import com.jin.note.manager.IModelManager
import com.jin.note.manager.ModelManager
import com.jin.note.visual.IVisualManager
import com.jin.note.visual.VisualManagerImpl

class DoodleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val mDoodleTouchLayer: DoodleTouchLayer2
    private val mVisualManager: IVisualManager
    private val modelManager: IModelManager

    companion object {
        const val TAG = "DoodleView"
    }

    init {
        mDoodleTouchLayer = DoodleTouchLayer2(context)
        mVisualManager = VisualManagerImpl(context)
        modelManager = ModelManager(context)
        modelManager.setTouchEventListener(mVisualManager)
        mDoodleTouchLayer.setModelManager(modelManager)
        addView(
            mDoodleTouchLayer,
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        )
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        Log.d(TAG, "dispatchTouchEvent: " + ev)
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        //拦截事件，交给doodleTouchLayer处理
        Log.d(TAG, "onInterceptTouchEvent: " + ev)
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.d(TAG, "onTouchEvent: " + event)
        return mDoodleTouchLayer.onTouchEvent(event)
    }


}