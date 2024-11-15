package com.wifi.anim

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.TypeEvaluator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.wifi.anim.databinding.ActivityPropertyValueBinding

class PropertyValueAnimActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPropertyValueBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPropertyValueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.view.setOnClickListener {
            startActivity(Intent(this,GroupAnimActivity::class.java))
        }

//        testPropertyValuesHolderOfBasic()
//        testPropertyValuesHolderOfObject()
//        testPropertyValuesHolderOfKeyFrame()
        testPropertyValuesHolderOfKeyFrameOfCustom()
    }

    private fun createPropertyValuesHolder() {
        //实例创建
        PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
        PropertyValuesHolder.ofInt("tranlationX", 0, 1)
        PropertyValuesHolder.ofObject("customProperty", object : TypeEvaluator<Pair<Int, Int>> {
            override fun evaluate(
                fraction: Float,
                startValue: Pair<Int, Int>?,
                endValue: Pair<Int, Int>?
            ): Pair<Int, Int> {
                return Pair(20, 20)
            }
        }, Pair(10, 10), Pair(20, 20))
    }

    private fun testPropertyValuesHolderOfBasic() {
        val rotationPropertyValuesHolder = PropertyValuesHolder.ofFloat("Rotation", 0f, -40f, 40f)
        val alphaPropertyValuesHolder = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
        ObjectAnimator.ofPropertyValuesHolder(
            binding.view,
            rotationPropertyValuesHolder,
            alphaPropertyValuesHolder
        ).apply {
            duration = 3000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }
    }

    private fun testPropertyValuesHolderOfObject() {
        val charPropertyValuesHolder =
            PropertyValuesHolder.ofObject("TextChar", object : TypeEvaluator<Int> {
                override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
                    val value = startValue + ((endValue - startValue) * fraction).toInt()
                    Log.d("lzy", "evaluate: " + value)
                    return value
                }
            }, 65, 97)
        ObjectAnimator.ofPropertyValuesHolder(binding.view, charPropertyValuesHolder).apply {
            duration = 3000
            repeatCount = 0
            start()
        }
    }

    private fun testPropertyValuesHolderOfKeyFrame() {
        /*
        * Keyframe 关键帧
        * keyframe1(0,20)  keyframe2(0.5,30) keyframe3(1,10)
        * 进度0时属性值=20，进度0.5时属性值30，进度1时属性值10  进度值【0,1】
        * */
        val frame1 = Keyframe.ofFloat(0f, 0f)
        val frame2 = Keyframe.ofFloat(0.1f, 20f)
        val frame3 = Keyframe.ofFloat(0.2f, -20f)
        val frame4 = Keyframe.ofFloat(0.3f, 20f)
        val frame5 = Keyframe.ofFloat(0.4f, -20f)
        val frame6 = Keyframe.ofFloat(0.5f, 20f)
        val frame7 = Keyframe.ofFloat(0.6f, -20f)
        val frame8 = Keyframe.ofFloat(0.7f, 20f)
        val frame9 = Keyframe.ofFloat(0.8f, -20f)
        val frame10 = Keyframe.ofFloat(0.9f, 20f)
        val frame11 = Keyframe.ofFloat(1f, 0f)

        /*
        *
        * */
        frame5.interpolator = LinearInterpolator()

        val keyframePropertyValuesHolder = PropertyValuesHolder.ofKeyframe(
            "rotation",
            frame1,
            frame2,
            frame3,
            frame4,
            frame5,
            frame6,
            frame7,
            frame8,
            frame9,
            frame10,
            frame11
        )
        ObjectAnimator.ofPropertyValuesHolder(binding.view,keyframePropertyValuesHolder).apply {
            duration = 10000
            start()
        }
    }

    private fun testPropertyValuesHolderOfKeyFrameOfCustom() {
        val keyFrameOne = Keyframe.ofObject(0f,65)
        val keyFrameTwo = Keyframe.ofObject(0.5f,82)
        val keyFrameThree = Keyframe.ofObject(1f,96)
        val ofKeyframePropertyValuesHolder =
            PropertyValuesHolder.ofKeyframe("test", keyFrameOne, keyFrameTwo, keyFrameThree)
        ofKeyframePropertyValuesHolder.setEvaluator(object : TypeEvaluator<Int> {
            override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
                val value = startValue + (endValue - startValue) * fraction.toInt()
                Log.d("lzy", "evaluate: " + value)
                return value
            }
        })

        ObjectAnimator.ofPropertyValuesHolder(binding.view,ofKeyframePropertyValuesHolder).apply {
            duration = 3000
            start()
        }

    }
}