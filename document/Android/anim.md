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