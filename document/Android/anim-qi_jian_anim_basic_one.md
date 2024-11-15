### 动画篇

#### 概览

- 属性动画
- 视图动画：补间动画、逐帧动画

#### 补间动画

##### 缩放（Scale)

```kotlin
/*
* fromXScale -> toXScale
* fromYScale -> toYScale
* duration:动画持续时间
* fillBefore:动画结束后是否停留在起始状态（默认true）
* fillAfter:动画结束后是否停留在最终动画状态(默认false)
* repeatCount:动画重复次数（默认0不重复） infinite (循环)
* repeatMode:重复形式（repeatCount >0 或者 infinite 有效）  restart：从头开始 reverse:倒转
* interpolator：插值器
* startOffset：动画开始倒计时（每次都会）
* */
val animation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.scale_one)
```

```xml
<scale xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromXScale="0"
    android:toXScale="1.4"
    android:fromYScale="0"
    android:toYScale="1.4"
    android:pivotX="50%"
    android:pivotY="50%"
    android:duration="3000"
    android:fillBefore="true"
    android:fillAfter="true"
    android:repeatCount="infinite"
    android:repeatMode="restart"
    android:startOffset="3000"
    android:interpolator="@android:interpolator/accelerate_decelerate"
    >
</scale>
```

```kotlin
private fun starScaleAnim() {
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
```

##### 透明度(Alpha)

```xml
<alpha xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromAlpha="0"
    android:toAlpha="1"
    android:duration="7000"
    android:fillAfter="true"
    >
</alpha>
```

```kotlin
private fun starAlphaAnim() {
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
```

##### 旋转（Rotate）参数同Scale中的相似

```xml
<rotate xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromDegrees="0"
    android:toDegrees="45"
    android:pivotX="50%"
    android:pivotY="50%"
    android:duration="3000"
    android:fillAfter="true"
    >
</rotate>
```

```kotlin
private fun starRotateAnim() {
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
```

##### 平移（Translate）

```xml
<translate xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromXDelta="0"
    android:toXDelta="100"
    android:fromYDelta="0"
    android:toYDelta="100"
    android:fillAfter="true"
    android:duration="3000"
    >
</translate>
```

```kotlin
private fun starTranslateAnim() {
        val transAnimation = TranslateAnimation(Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,1f)
        transAnimation.duration = 700
        transAnimation.fillAfter = true
        transAnimation.repeatCount = Animation.INFINITE
        transAnimation.repeatMode = Animation.RESTART
        transAnimation.startOffset = 3000
        binding.imgDisplay.startAnimation(transAnimation)
    }
```

##### 组合动画（Set）

set中设置repeatCount无效，必须对每个动画分别设置

```xml
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <alpha
        android:fromAlpha="0f"
        android:toAlpha="1f"/>
    <rotate
        android:fromDegrees="0"
        android:toDegrees="45"
        android:pivotX="50%"
        android:pivotY="50%"/>
    <scale
        android:fromXScale="0.2f"
        android:toXScale="1.2f"
        android:fromYScale="0.2f"
        android:toYScale="1.2f"/>
</set>
```

```kotlin
private fun starSetAnim() {
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
```

#### 属性动画（ValueAnimator）

```kotlin
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
    valueAnim.repeatCount = ValueAnimator.INFINITE
    valueAnim.interpolator = LinearInterpolator()
    valueAnim.start()
}
```

##### ValueAnimator中的animatedValue从哪获得

1. Interpoator: 插值器(存在默认插值器如不设定)返回当前进度的参数fraction

1. Evalutar:计算器 通过返回的参数fraction进行值的计算

```kotlin
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
```

##### 自定义类型 ValueAnimator.ofObject

```kotlin
private fun ofObjectIntroduce() {
    val anim = ValueAnimator.ofObject(object : TypeEvaluator<Point> {
        override fun evaluate(fraction: Float, startValue: Point, endValue: Point): Point {
            val newX = startValue.x + (endValue.x - startValue.x) * fraction
            val newY = startValue.y + (endValue.y - startValue.y) * fraction
            return Point(newX.toInt(), newY.toInt())
        }
    },Point(20,20),Point(100,100))
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
```

#### 属性动画（ObjectAnimator）

ObjectAnimator继承于ValueAnimator，ValueAnimator动画方法ObjectAnimator都可以用

```kotlin
private fun guideIntroduce() {
        /*
        * binding.btnStart:targetView
        * alpha:propertyName
        * values 改变PropertyName的值
        * */
        val alphaAnim = ObjectAnimator.ofFloat(binding.btnStart,"alpha",0f,1f)
        val scaleAnim = ObjectAnimator.ofFloat(binding.btnStart,"scaleX",0f,1f)
        val rotateAnim = ObjectAnimator.ofFloat(binding.btnStart,"rotation",0f,1f)
        val translateAnim = ObjectAnimator.ofFloat(binding.btnStart,"translationX",0f,1f)
        alphaAnim.duration = 3000
        alphaAnim.start()
    }
```

##### ObjectAnimator的PropertyName参数

```kotlin
/*
* propertyName的值确定：
* 1.targetObject必须有set方法，如"alpha" -> setAlpha
* 2.ofFloat 代表 setAlpha(a: Float)
* 即targetObject类存在 setAlpha(alpha: Float) 方法
* */
val anim = ObjectAnimator.ofFloat(binding.btnStart,"alpha",0f,1f)
```

##### ObjectAnimator自定义PropertyName

```kotlin
//提供set方法，入参Point即object
fun setPoint(point: Point) {
    Log.d("lzy", "setPoint: " + point.toString())
    layout(point.x,point.y,point.x + width,point.y + height)
}
```

```kotlin
/*
* 改变binding.imgFallBall的point值，即调用setPoint()方法
* */
private fun customPropertyOfObjectValueAnim() {
    val anim = ObjectAnimator.ofObject(
        binding.imgFallBall,
        "point",
        object : TypeEvaluator<Point> {
            override fun evaluate(
                fraction: Float,
                startValue: Point,
                endValue: Point
            ): Point {
                val newX = startValue.x + (endValue.x - startValue.x) * fraction
                val newY = if (fraction * 2 <= 1) {
                    startValue.y + (endValue.y - startValue.y) * fraction * 2
                } else {
                    endValue.y
                }
                return Point(newX.toInt(), newY.toInt())
            }
        },
        Point(0, 0),
        Point(500, 500)
    )
    anim.interpolator = AccelerateInterpolator()
    anim.duration = 3000
    anim.start()
}
```

#### 组合动画（AnimatorSet）

```kotlin
private fun playSequentially() {
    val bgColorOneAnim = ObjectAnimator.ofInt(binding.btnOne,"backgroundColor",Color.BLACK,Color.RED)
    val translateOneAnim = ObjectAnimator.ofFloat(binding.btnOne,"translationY",0f,200f)
    val translateTwoAnim = ObjectAnimator.ofFloat(binding.btnTwo,"translationY",0f,200f)

    /*
    * playSequentially:顺序播放
    * playTogether:同时播放
    * AnimatorSet动画排队队列：bgColorOneAnim，translateOneAnim，translateTwoAnim
    * */
    val animSet = AnimatorSet()
    animSet.duration = 3000
    animSet.playSequentially(bgColorOneAnim,translateOneAnim,translateTwoAnim)
    animSet.playTogether(bgColorOneAnim,translateOneAnim,translateTwoAnim)
    animSet.start()
}
```

##### AnimatorSet的playSequentially，playTogether

playSequentially：顺序播放，首先播放队列的动画，当队列的动画播放完成，即顺序播放下一动画。注意：动画若设置无限重复模式，下一队列动画将永远不会得到播放

playTogether：同时播放，相当与栅栏放开，动画都按照自身逻辑进行播放

playSequentially，playTogether只负责到达时间点开始放开栅栏，至于动画逻辑，交由动画自身切处理。

##### AnimatorSet的duration

playSequentially：每个动画的播放时长

playTogether：总时长

##### A，B，C动画  A动画执行完，同时执行B，C动画

AnimatorSet().Builder类去实现

##### AnimatorSet().Builder

```kotlin
private fun playSetBuilder() {
    val bgColorOneAnim = ObjectAnimator.ofInt(binding.btnOne,"backgroundColor",Color.BLACK,Color.RED)
    val translateOneAnim = ObjectAnimator.ofFloat(binding.btnOne,"translationY",0f,200f)
    val translateTwoAnim = ObjectAnimator.ofFloat(binding.btnTwo,"translationY",0f,200f)

    /*
    * animSet.play(translateOneAnim) -> AnimatorSet.Builder对象
    * with(anim):同时
    * after(anim)：先播放anim动画，之后播放this代表动画
    * before(anim)：先播放this代表动画，之后播放anim动画
    * */
    val animSet = AnimatorSet()
    val playAnim = animSet.play(translateOneAnim)
    playAnim.with(translateTwoAnim).after(bgColorOneAnim)
    animSet.duration = 3000
    animSet.start()
}
```

##### AnimatorSet监听器

```kotlin
private fun testAnimSetListener() {
    val bgColorOneAnim = ObjectAnimator.ofInt(binding.btnOne,"backgroundColor",Color.BLACK,Color.RED)
    val translateOneAnim = ObjectAnimator.ofFloat(binding.btnOne,"translationY",0f,200f)
    val translateTwoAnim = ObjectAnimator.ofFloat(binding.btnTwo,"translationY",0f,200f)

    translateOneAnim.repeatCount = ObjectAnimator.INFINITE
    translateOneAnim.repeatMode = ObjectAnimator.REVERSE
    val animSet = AnimatorSet()
    animSet.play(translateOneAnim).with(translateTwoAnim).before(bgColorOneAnim)
    animSet.duration = 3000

    animSet.addListener(object : AnimatorListener {
        override fun onAnimationStart(animation: Animator) {
            Log.d("lzy", "onAnimationStart: ")
        }

        override fun onAnimationEnd(animation: Animator) {
            Log.d("lzy", "onAnimationEnd: ")
        }

        override fun onAnimationCancel(animation: Animator) {
            Log.d("lzy", "onAnimationCancel: ")
        }

        override fun onAnimationRepeat(animation: Animator) {
            //AnimatorSet的onAnimationRepeat永远不会被调用
            Log.d("lzy", "onAnimationRepeat: ")
        }
    })
    animSet.start()
    Handler(Looper.getMainLooper()).postDelayed({
        animSet.cancel()
    },8000)

}
```

- onAnimationRepeat永远不会被调用
- AnimatorSet到达duration 就会回调onAnimationEnd，无论其中的动画是否还在重复执行
- animSet.cancel() -> onAnimationCancel 回调

##### AnimatorSet公共方法

```kotlin
/*
* 倘若animSet设置了duration、interpolator、setTarget
*       会覆盖掉objectAnim 单个设置的值（startDelay不受影响)
* */
animSet.duration = 3000
animSet.interpolator = LinearInterpolator()
animSet.setTarget(binding.btnOne)
animSet.startDelay = 3000
animSet.start()
```

#### PropertyValuesHolder

##### PropertyValuesHolder实例

```kotlin
private fun createPropertyValuesHolder() {
    //实例创建
    PropertyValuesHolder.ofFloat("alpha",0f,1f)
    PropertyValuesHolder.ofInt("tranlationX",0,1)
    PropertyValuesHolder.ofObject("customProperty",object : TypeEvaluator<Pair<Int,Int>> {
        override fun evaluate(fraction: Float, startValue: Pair<Int,Int>?, endValue: Pair<Int,Int>?): Pair<Int,Int> {
            return Pair(20,20)
        }
    },Pair(10,10),Pair(20,20))
}
```

##### PropertyValuesHolder使用

- ObjectAnimator.ofPropertyValuesHolder(target,propertyValuesHodler.....)

  ```kotlin
  private fun testPropertyValuesHolderOfBasic() {
      val rotationPropertyValuesHolder = PropertyValuesHolder.ofFloat("Rotation", 0f,-40f, 40f)
      val alphaPropertyValuesHolder = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
      ObjectAnimator.ofPropertyValuesHolder(binding.view,rotationPropertyValuesHolder,alphaPropertyValuesHolder).apply {
          duration = 3000
          repeatCount = ObjectAnimator.INFINITE
          repeatMode = ObjectAnimator.REVERSE
          start()
      }
  }
  ```

  自定义属性TextChar，View当中存在setTextChar（char: Int）

  ```kotlin
  private fun testPropertyValuesHolderOfObject() {
      val charPropertyValuesHolder = PropertyValuesHolder.ofObject("TextChar", object : TypeEvaluator<Int> {
          override fun evaluate(fraction: Float, startValue: Int, endValue: Int): Int {
              val value = startValue + ((endValue - startValue) * fraction).toInt()
              Log.d("lzy", "evaluate: " + value)
              return value
          }
      }, 65, 97)
      ObjectAnimator.ofPropertyValuesHolder(binding.view,charPropertyValuesHolder).apply {
          duration = 3000
          repeatCount = 0
          start()
      }
  }
  ```

##### KeyFrame帧

```kotlin
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
        duration = 1000
        start()
    }
}
```

##### KeyFrame的插值器

```kotlin
//从上一帧frame4到frame5的过程中使用此插值器，其他阶段默认插值器
//同理，给第一帧设置插值器是无效的，没有上一帧
frame5.interpolator = LinearInterpolator()
```

#### ViewGroup动画（子view进入退出动画）

##### animateLayoutChanges（无法自定义动画）

ViewGroup当中的属性值，默认为false无动画

```xml
<LinearLayout
    android:id="@+id/ll_container"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    android:animateLayoutChanges="true"
    android:orientation="vertical"
    android:layout_marginTop="20dp"
    app:layout_constraintTop_toBottomOf="@id/btn_add"/>
```

![example](https://s2.loli.net/2024/01/04/R51yrNGEjvc3AUn.gif)

##### LayoutTransition（支持自定义动画）

###### ViewGroup存在setLayoutTransition(LayoutTransition)方法

1. 创建LayoutTransition对象

  ```kotlin
val layoutTransition = LayoutTransition()
  ```

​	2.自定义动画，targetView = null

```kotlin
val enterAnim = ObjectAnimator.ofFloat(null,"rotationY",0f,360f,0f)
```

​	3.将对话设置给layoutTransition，将layoutTransition设置给ViewGroup

```kotlin
layoutTransition.setAnimator(LayoutTransition.APPEARING,enterAnim)
binding.llContainer.layoutTransition = layoutTransition
```

###### layoutTransition.setAnimator（）

```kotlin
private fun introduceSetAnimator() {
        val layoutTransition = LayoutTransition()
        /*
        * Int:动画的类型
        *   LayoutTransition.APPEARING:view入场（add）
        *   LayoutTransition.DISAPPEARING:view退场（remove）
        *   LayoutTransition.CHANGE_APPEARING:view入场时(add),其他已有控件需要移动的动画
        *   LayoutTransition.CHANGE_DISAPPEARING:view退场时(remove),其他已有控件需要移动的动画
        * Animator:具体动画(valueAnim,objectAnim)
        * */
        val enterAnim = ObjectAnimator.ofFloat(null,"rotationY",0f,360f,0f)
        layoutTransition.setDuration(3000)
        layoutTransition.setInterpolator(LayoutTransition.APPEARING,LinearInterpolator())
        layoutTransition.addTransitionListener(object : LayoutTransition.TransitionListener {
            override fun startTransition(
                transition: LayoutTransition?,
                container: ViewGroup?,
                view: View?,
                transitionType: Int
            ) {
                
            }

            override fun endTransition(
                transition: LayoutTransition?,
                container: ViewGroup?,
                view: View?,
                transitionType: Int
            ) {
            }

        })
        layoutTransition.setAnimator(LayoutTransition.APPEARING,enterAnim)
    }
```

###### 示例效果

```kotlin
private fun addAnimOfViewGroup() {
    val layoutTransition = LayoutTransition()
    val enterAnim = ObjectAnimator.ofFloat(null,"rotationY",0f,360f,0f)
    val exitAnim = ObjectAnimator.ofFloat(null,"rotation",0f,90f,0f)
    layoutTransition.setAnimator(LayoutTransition.APPEARING,enterAnim)
    layoutTransition.setAnimator(LayoutTransition.DISAPPEARING,exitAnim)
    binding.llContainer.layoutTransition = layoutTransition
}
```

![example](https://s2.loli.net/2024/01/04/SdUpuD9n2o3v1rl.gif)

###### 注意事项

- LayoutTransition.CHANGE_APPEARING，LayoutTransition.CHANGE_DISAPPEARING需要与PropertyValuesHolder动画使用，否则无效
- 其中PropertyValuesHolder中需要动画属性"left","top"是必填的，不需要也要更改为0,0
- 上述二type问题存在较多，暂不建议