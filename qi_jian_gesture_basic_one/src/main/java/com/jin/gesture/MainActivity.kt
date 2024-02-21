package com.jin.gesture

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.OnDoubleTapListener
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.jin.gesture.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

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
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.tvExample.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
        }
    }


}