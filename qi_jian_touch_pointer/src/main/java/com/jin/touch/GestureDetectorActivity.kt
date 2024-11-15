package com.jin.touch

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.OnDoubleTapListener
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class GestureDetectorActivity : AppCompatActivity() {



    private var gestureListener = object : GestureDetector.OnGestureListener {
        override fun onDown(e: MotionEvent): Boolean {
            Log.d("TAG", "onDown: ")
            return true
        }

        override fun onShowPress(e: MotionEvent) {
            Log.d("TAG", "onShowPress: ")
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            Log.d("TAG", "onSingleTapUp: ")
            return true
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            Log.d("TAG", "onScroll: ")
            return true
        }

        override fun onLongPress(e: MotionEvent) {
            Log.d("TAG", "onLongPress: ")
        }

        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            Log.d("TAG", "onFling: ")
            return true
        }


    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gesture_detector)

        val gestureDetector = GestureDetector(this, gestureListener)

        gestureDetector.setOnDoubleTapListener(object : OnDoubleTapListener {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                Log.d("TAG", "onSingleTapConfirmed: ")
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                Log.d("TAG", "onDoubleTap: ")
                return true
            }

            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                Log.d("TAG", "onDoubleTapEvent: ")
                return true
            }
        })
        findViewById<View>(R.id.tv_example).setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
        }
    }


}