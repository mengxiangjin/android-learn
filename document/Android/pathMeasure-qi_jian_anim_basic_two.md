### 动画篇

#### PathMeasure

##### PathMeasure用法

###### PathMeasure.setPath(path,boolean)

```kotlin
pathMeasure = PathMeasure()
pathMeasure!!.setPath(path,false)
```

- pathMeasure与path进行绑定
- boolean：是否将绑定的Path进行Close
- 注意：当boolean = true时，实际绑定的Path并未更改被close，而是PathMeasure计算的为close后的Path，该值只会影响PathMeasure计算出的值，对原Path无影响

```kotlin
private fun introducePathMeasure(canvas: Canvas) {
    canvas.translate(100f,100f)
    path.reset()
    path.moveTo(100f,0f)
    path.lineTo(100f,100f)
    path.lineTo(200f,100f)
    path.lineTo(200f,0f)
    
    pathMeasure = PathMeasure()
    /*
    * forceClosed:
    *   false:pathMeasure.length = 300
    *   true:pathMeasure.length = 400
    * */
    pathMeasure!!.setPath(path,false)
    pathMeasure!!.setPath(path,true) 
    canvas.drawPath(path,paint)
}
```

###### PathMeasure.getLength()

获取绑定的Path的当前曲线路径长度，而不是整个当前path的长度，详见PathMeasure.nextCountar()

###### PathMeasure.isClosed()

获取绑定的Path是否是闭合的，只与PathMeasure.setPath设置的boolean有关

###### PathMeasure.nextCountar()

移动到与PathMeasure绑定的下一个path上，如果存在返回true，否则false

- pathMeasure.length是当前path的路径长度，而非整个path长度
- pathMeasure.nextContour的顺序与path中添加的顺序相同
- 输出结果：2000 1200 400

```kotlin
private fun pathMeasureNextContour(canvas: Canvas) {
    path.reset()
    //添加3个非连续的路径Rect
    path.addRect(100f, 100f, 600f, 600f, Path.Direction.CW)
    path.addRect(200f, 200f, 500f, 500f, Path.Direction.CW)
    path.addRect(300f, 300f, 400f, 400f, Path.Direction.CCW)
    canvas.drawPath(path, paint)

    pathMeasure = PathMeasure(path, false)
    do {
        Log.d("lzy", "pathMeasureNextContour: " + pathMeasure!!.length)
    } while (pathMeasure!!.nextContour())
}
```

###### PathMeasure.getSegment(startD,stopD,dstPath,boolean)

- 分段截取PathMeasure所绑定的Path
- startD：截取的起始点[0,length]
- stopD：截取的终点[0,length]
- dstPath：截取的结果添加到该path上
- boolean：是否使用moveTo将截取的结果添加到dstPath上，dstPath可能之前就存在一些路径，moveTo=true即为将path移动到新的路径上，之前的路径与新路径可能会断开。moveTo=false，之前路径的终点与当前起点相连接
- moveTo=false时，dstPath之前已存在路径圆，会将dstPath的起始点与之前路径终点相连接

```kotlin
private fun pathMeasureOfSegment(canvas: Canvas) {
    path.reset()
    path.addRect(100f, 100f, 600f, 600f, Path.Direction.CCW)
    pathMeasure = PathMeasure(path,false)

    val dstPath = Path()
    dstPath.addCircle(50f,50f,20f,Path.Direction.CCW)
    pathMeasure!!.getSegment(0f,650f,dstPath,false)
    canvas.drawPath(dstPath,paint)
}
```

![](https://s2.loli.net/2024/01/05/poYbxRHtAgLml7B.jpg)

- 当moveTo=true时，dstPath之前已存在路径圆

![微信图片_20240105114337](https://s2.loli.net/2024/01/05/kQnP2Giac8JCg6z.jpg)

- 注意：dstPath绘制的顺序与PathMeasure绑定的path顺序相同
- 注意：当startD与stopD不合法时，dstPath不会被改变。方法返回false

###### PathMeasure与动画结合

```kotlin
init {
    val anim = ValueAnimator.ofFloat(0f,1f)
    anim.addUpdateListener { animation ->
        animValue = animation.animatedValue as Float
        invalidate()
    }
    anim.duration = 1000
    anim.repeatCount = ValueAnimator.INFINITE
    anim.repeatMode = ValueAnimator.RESTART
    anim.start()
}
```

```kotlin
private fun pathMeasureOfAnim(canvas: Canvas) {
    path.reset()
    path.addCircle(500f,500f,150f,Path.Direction.CW)
    val destPath = Path()
    pathMeasure = PathMeasure(path,false)
    pathMeasure!!.getSegment(0f,pathMeasure!!.length * animValue,destPath,true)
    canvas.drawPath(destPath,paint)
}
```

- 起始点不变，每次绘制改变stopD，当进度为1时，stopD即为pathMeasure.length

![a1f62453-43e7-42ca-8547-1a729516be77](https://s2.loli.net/2024/01/05/OMpoGXziw3SdmLK.gif)

###### PathMeasure.getPosTan(distance,pos[2],tan[2])

- 根据PathMeasure传入的路径距离distance，向pos[2]，tan[2]写入数据。
- pos[2]：返回值，当前distance对应点的坐标x，y。相对于屏幕坐标
- tan[2]：返回值，半径为1的圆，pos[2]点正好在圆上，采样位置的切线，距圆心距离x，y。相对于圆心坐标

```kotlin
init {
    bitmap = BitmapFactory.decodeResource(resources,R.drawable.arrow)
    val anim = ValueAnimator.ofFloat(0f,1f)
    anim.addUpdateListener { animation ->
        animValue = animation.animatedValue as Float
        invalidate()
    }
    anim.duration = 10000
    anim.repeatCount = ValueAnimator.INFINITE
    anim.repeatMode = ValueAnimator.RESTART
    anim.start()
}
```

```kotlin
private fun pathMeasureOfAnimWithArrow(canvas: Canvas) {
    path.reset()
    path.addCircle(500f,500f,150f,Path.Direction.CW)
    val destPath = Path()
    val posArray = FloatArray(2)
    val tanArray = FloatArray(2)
    val matrix = Matrix()
    pathMeasure = PathMeasure(path,false)
    pathMeasure!!.getSegment(0f,pathMeasure!!.length * animValue,destPath,true)
    pathMeasure!!.getPosTan(pathMeasure!!.length * animValue,posArray,tanArray)
    matrix.postRotate(Math.toDegrees(atan2(tanArray[1],tanArray[0]).toDouble()).toFloat(),bitmap!!.width / 2f,bitmap!!.height / 2f)
    matrix.postTranslate(posArray[0] - bitmap!!.width / 2f,posArray[1] - bitmap!!.height / 2f)

    canvas.drawBitmap(bitmap!!,matrix,paint)
    canvas.drawPath(destPath,paint)
}
```

![b8b88524-a70c-4c49-83bf-75da431c299d](https://s2.loli.net/2024/01/05/bdztU1HBla9qRPy.gif)

- 构造Bitmap箭头，将箭头平移到当前点的后面，以及旋转箭头的方向
- 平移的距离即为posArray[0],posArray[1]为中心点的坐标，故需要减少width/2,height/2
- 旋转的角度值为tan[1]/tan[0]的反正切值（atan2）,得到的是弧度值，需将弧度转换为角度即可，旋转中心点即为Bitmap中心点

###### PathMeasure.getMatrix(distance,matrix,flag)

- 根据distance自动计算出旋转的角度值赋予matrix
- flag取值PathMeasure.POSITION_MATRIX_FLAG，则赋予matrix即为位置信息（位置）
- flag取值PathMeasure.TANGENT_MATRIX_FLAG，则赋予matrix即为切边信息（角度）
- flag取值PathMeasure.TANGENT_MATRIX_FLAG or PathMeasure.POSITION_MATRIX_FLAG 即都赋予

```kotlin
init {
    bitmap = BitmapFactory.decodeResource(resources, R.drawable.arrow)
    val anim = ValueAnimator.ofFloat(0f, 1f)
    anim.addUpdateListener { animation ->
        animValue = animation.animatedValue as Float
        invalidate()
    }
    anim.duration = 10000
    anim.repeatCount = ValueAnimator.INFINITE
    anim.repeatMode = ValueAnimator.RESTART
    anim.start()
}
```

```kotlin
private fun pathMeasureOfAnimWithMatrix(canvas: Canvas) {
    path.reset()
    path.addCircle(500f, 500f, 150f, Path.Direction.CW)
    pathMeasure = PathMeasure(path, false)
    val dstPath = Path()
    val matrix = Matrix()
    pathMeasure!!.getSegment(0f, animValue * pathMeasure!!.length, dstPath, true)
    pathMeasure!!.getMatrix(
        animValue * pathMeasure!!.length, matrix,
        PathMeasure.POSITION_MATRIX_FLAG or PathMeasure.TANGENT_MATRIX_FLAG
    )
    matrix.preTranslate(-bitmap!!.width / 2f,-bitmap!!.height / 2f)
    canvas.drawPath(dstPath, paint)
    canvas.drawBitmap(bitmap!!,matrix,paint)
}
```

- flag已取值为PathMeasure.TANGENT_MATRIX_FLAG or PathMeasure.POSITION_MATRIX_FLAG，故只需平移宽度、高度的一半即可
- 效果相同，同手动计算getPosTan出来的位置信息，切边信息

###### 支付宝支付成功例子

- 先画圆，圆完成后再画勾
- PathMeasure与Path绑定，path包含二条路径，圆的路径与勾的路径

```kotlin
path.reset()
path.addCircle(500f, 500f, 150f, Path.Direction.CW)
path.moveTo(500f - 150f / 2, 500f)
path.lineTo(500f, 500f + 150f / 2)
path.lineTo(500f + 150f / 2, 500f - 150f / 2)
pathMeasure = PathMeasure(path, false)
```

- 动画重复一次，动画监听repeat中切换下一个路径PathMeasure.nextContour

```kotlin
val anim = ValueAnimator.ofFloat(0f, 1f)
anim.addUpdateListener { animation ->
    animValue = animation.animatedValue as Float
    invalidate()
}
anim.addListener(object : AnimatorListener {
    override fun onAnimationStart(animation: Animator) {

    }

    override fun onAnimationEnd(animation: Animator) {

    }

    override fun onAnimationCancel(animation: Animator) {
    }

    override fun onAnimationRepeat(animation: Animator) {
        pathMeasure!!.nextContour()
    }
})
anim.duration = 3000
anim.repeatCount = 1
anim.start()
```

- 分段绘制

```kotlin
private fun pathMeasureOfExampleAli(canvas: Canvas) {
    pathMeasure!!.getSegment(0f,animValue * pathMeasure!!.length,dstPath,true)
    canvas.drawPath(dstPath,paint)
}
```

- dstPath为全局变量，每次将新路径add之前的路径上，moveForce = true

![b7e1ed0c-6633-46a3-847a-65f0aecd379a](https://s2.loli.net/2024/01/05/cQs13vdKjGOuafV.gif)