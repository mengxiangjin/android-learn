package com.wifi.anim

import android.animation.TypeEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Matrix
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.wifi.anim.databinding.ActivityMainBinding
import com.wifi.anim.widgit.ScaleImageView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnScaleOne.setOnClickListener {
            //从xml中映射动画
            val animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.set_two)
            binding.imgDisplay.startAnimation(animation)

            startPropertyAnim()
            startColorAnim()
        }

        binding.btnPropertyAnim.setOnClickListener {
            startActivity(Intent(this@MainActivity,ObjectAnimActivity::class.java))
        }

        binding.btnAnimSet.setOnClickListener {
            startActivity(Intent(this@MainActivity,AnimSetActivity::class.java))
        }

        binding.btnPropertyValueAnim.setOnClickListener {
            startActivity(Intent(this@MainActivity,PropertyValueAnimActivity::class.java))
        }

        var i = 0
        binding.btnGroupAnim.setOnClickListener {
//            startActivity(Intent(this@MainActivity,GroupAnimActivity::class.java))

            val scaleView = ScaleImageView(this@MainActivity)
            val index = i % 3
            scaleView.setResourceID(resources.getIdentifier("ic_book_${index}", "drawable", this@MainActivity.packageName))
            i++
            binding.flContainer.addView(scaleView)
        }

//        ofObjectIntroduce()


        var lastX = 0f
        var lastY = 0f
        binding.scene.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    // 记录触摸开始时的坐标偏移
                    lastX = event.x
                    lastY = event.y
                    Log.d("TAG", "onCreate:down ")
                }
                MotionEvent.ACTION_MOVE -> {
                    // 计算新位置并更新视图的布局
                    val newX = event.x - lastX
                    val newY = event.y - lastY
                    v.layout(v.left + newX.toInt(),v.top + newY.toInt(),v.right + newX.toInt(),newY.toInt() + v.bottom)
                    binding.close.layout(binding.close.left + newX.toInt(),binding.close.top + newY.toInt(),binding.close.right + newX.toInt(),newY.toInt() +binding.close.bottom)
                    binding.scale.layout(binding.scale.left + newX.toInt(),binding.scale.top + newY.toInt(),binding.scale.right + newX.toInt(),newY.toInt() + binding.scale.bottom)
                    binding.bgView.layout(binding.bgView.left + newX.toInt(),binding.bgView.top + newY.toInt(),binding.bgView.right + newX.toInt(),newY.toInt() + binding.bgView.bottom)
                    Log.d("TAG", "onCreate:move ")
                }
                MotionEvent.ACTION_UP -> {
                    Log.d("TAG", "onCreate:up ")
                }
            }
            true
        }

        var downX = 0f
        var downY = 0f
        var imgMatrix = Matrix()

        binding.scene.scaleType = ImageView.ScaleType.MATRIX



        var srcWidth = 0
        var srcHeight = 0
        binding.scene.post {
            srcWidth = binding.scene.width
            srcHeight = binding.scene.height

            val layoutParams1 = binding.scene.layoutParams

            Log.d("TAG", "onCreate:srcWidth " + srcWidth)
            Log.d("TAG", "onCreate:srcHight " + srcHeight)
        }




        binding.scale.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    // 记录触摸开始时的坐标偏移
                    downX = event.x
                    downY = event.y
                    Log.d("TAG", "onCreate:down ")
                }
                MotionEvent.ACTION_MOVE -> {
                    val newX = event.x - downX
                    val newY = event.y - downY
                    Log.d("TAG", "onCreate:ACTION_MOVE newX " + newX / downX)
                    Log.d("TAG", "onCreate:ACTION_MOVE newY " + newY / downY)
                    v.layout(v.left + newX.toInt(),v.top + newY.toInt(),v.right + newX.toInt(),newY.toInt() + v.bottom)
                    binding.bgView.layout(binding.bgView.left + newX.toInt(),binding.bgView.top + newY.toInt(),binding.bgView.right + newX.toInt(),newY.toInt() + binding.bgView.bottom)
                    binding.close.layout(binding.close.left + newX.toInt(),binding.close.top + newY.toInt(),binding.close.right + newX.toInt(),newY.toInt() +binding.close.bottom)
                    binding.scene.layout(binding.scene.left + newX.toInt(),binding.scene.top + newY.toInt(),binding.scene.right + newX.toInt(),newY.toInt() + binding.scene.bottom)


                    val layoutParams = binding.scene.layoutParams
                    layoutParams.width = (srcWidth * 1.5).toInt()
                    layoutParams.height = (srcHeight * 1.5).toInt()
                    binding.scene.layoutParams = layoutParams

                    imgMatrix.reset()
                    imgMatrix.setScale(1.5f,1.5f)
                    binding.scene.imageMatrix = imgMatrix
                }
                MotionEvent.ACTION_UP -> {
                }
            }
            true
        }

    }

    private fun startPropertyAnim() {
        //监听属性动画的进度值，改变view属性从而触发动画效果
        val valueAnim = ValueAnimator.ofInt(0,200,0)
        valueAnim.addUpdateListener {
            //ofInt -> Int
            //ofFloat -> Float
            val animValue = it.animatedValue as Int
            binding.imgDisplay.layout(animValue,animValue,animValue + binding.imgDisplay.width,animValue + binding.imgDisplay.height)
        }
        valueAnim.duration = 3000
        valueAnim.repeatMode = ValueAnimator.RESTART
//        valueAnim.repeatCount = ValueAnimator.INFINITE
        valueAnim.interpolator = LinearInterpolator()
        valueAnim.start()
    }

    private fun ofObjectIntroduce() {
        val anim = ValueAnimator.ofObject(object : TypeEvaluator<Point> {
            override fun evaluate(fraction: Float, startValue: Point, endValue: Point): Point {
                val newX = startValue.x + (endValue.x - startValue.x) * fraction
                val newY = startValue.y + (endValue.y - startValue.y) * fraction
                return Point(newX.toInt(), newY.toInt())
            }
        },Point(0,0),Point(500,500))
        anim.interpolator = object : Interpolator {
            override fun getInterpolation(input: Float): Float {
                return input
            }
        }
        anim.addUpdateListener {
            val pointValue = it.animatedValue as Point
            binding.imgDisplay.layout(pointValue.x,pointValue.y,pointValue.x + binding.imgDisplay.width,pointValue.y + binding.imgDisplay.height)
        }
        anim.duration = 3000
        anim.start()
    }

    private fun interpolatorIntroduce() {
        val valueAnim = ValueAnimator.ofInt(0,200)

        /*
        * 自定义插值器，实现Interpolator接口，返回fraction
        *  input取值 0~1
        *  此插值器返回1 - input 即逆向
        * */
        valueAnim.interpolator = object : Interpolator {
            override fun getInterpolation(input: Float): Float {
                return 1 - input
            }
        }
        /*
        * 自定义计算器，属性动画监听获取到的值即为evaluate返回值
        * 实现TypeEvaluator<T>接口,T为动画值的参数类型 ofInt，ofFloat
        * fraction: interpolator接口返回的值
        * */
        valueAnim.setEvaluator(object : TypeEvaluator<Int> {
            override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
                //动画监听一直返回20
//                return 20
                return (startValue + (endValue - startValue) * fraction).toInt()
            }

        })

        valueAnim.duration = 3000
        valueAnim.repeatMode = ValueAnimator.RESTART
        valueAnim.repeatCount = ValueAnimator.INFINITE
        valueAnim.start()
    }

    private fun startColorAnim() {
        val anim = ValueAnimator.ofObject(object : TypeEvaluator<Char> {
            override fun evaluate(fraction: Float, startValue: Char, endValue: Char): Char {
                return (startValue.toInt() + ((endValue.toInt() - startValue.toInt()) * fraction)).toChar()
            }
        },'A','Z')
        anim.addUpdateListener {
            val character = it.animatedValue as Char
            Log.d("lzy", "startColorAnim: ${character}")
            binding.btnChar.text = character.toString()
        }
        anim.duration = 3000
        anim.start()
    }

    private fun startScaleAnim() {
        val scaleAnimation = ScaleAnimation(
            0f,
            1.4f,
            0f,
            1.4f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        scaleAnimation.duration = 700
        scaleAnimation.fillAfter = true
        scaleAnimation.repeatCount = Animation.INFINITE
        scaleAnimation.repeatMode = Animation.RESTART
        scaleAnimation.startOffset = 3000
        binding.imgDisplay.startAnimation(scaleAnimation)
    }

    private fun startAlphaAnim() {
        /*
        * fromAlpha,toAlpha
        * */
        val alphaAnimation = AlphaAnimation(0f,1f)
        alphaAnimation.duration = 700
        alphaAnimation.fillAfter = true
        alphaAnimation.repeatCount = Animation.INFINITE
        alphaAnimation.repeatMode = Animation.RESTART
        alphaAnimation.startOffset = 3000
        binding.imgDisplay.startAnimation(alphaAnimation)
    }

    private fun startRotateAnim() {
        /*
        * 旋转中心点
        * */
        val rotateAnimation = RotateAnimation(0f,45f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
        rotateAnimation.duration = 700
        rotateAnimation.fillAfter = true
        rotateAnimation.repeatCount = Animation.INFINITE
        rotateAnimation.repeatMode = Animation.RESTART
        rotateAnimation.startOffset = 3000
        binding.imgDisplay.startAnimation(rotateAnimation)
    }

    private fun startTranslateAnim() {
        val transAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f)
        transAnimation.duration = 700
        transAnimation.fillAfter = true
        transAnimation.repeatCount = Animation.INFINITE
        transAnimation.repeatMode = Animation.RESTART
        transAnimation.startOffset = 3000
        binding.imgDisplay.startAnimation(transAnimation)
    }

    private fun startSetAnim() {
        val transAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f)
        val rotateAnimation = RotateAnimation(0f,45f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f)
        val alphaAnimation = AlphaAnimation(0f,1f)
        /*
        * AnimationSet构造函数：
        * true：动画共用一个插值器
        * */
        val animationSet = AnimationSet(true)
        animationSet.addAnimation(transAnimation)
        animationSet.addAnimation(rotateAnimation)
        animationSet.addAnimation(rotateAnimation)
        animationSet.duration = 7000
        animationSet.repeatCount = Animation.INFINITE
        animationSet.repeatMode = Animation.RESTART
    }
}