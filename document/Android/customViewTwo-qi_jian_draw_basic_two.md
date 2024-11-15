### 绘图进阶

#### 贝塞尔曲线

##### 一阶贝塞尔曲线

- 平常都直线相关，moveTo，LineTo即可

  起始点与结束点之间做随时间变化的匀速运动

- x = startX + （endX - startX）* time    time[0,1]

- y = startY + （endY - startY）* time    time[0,1]

##### 二阶贝塞尔曲线

- 一个控制点：控制曲线绘制 P
- 起始点A、结束点B

###### 贝塞尔曲线上点如何确定

- t时刻，曲线上的点坐标为x，y
- 连接AP（起始点与控制点），取出AP线段在t时刻运动到的位置D(x0，y0)
- 连接PB（控制点与结束点），取出PB线段在t时刻运动到的位置C(x1，y1)
- 连接DC，取出DC线段在t时刻运动到的位置，即为曲线上的点x，y

###### 手写贝塞尔曲线

resultCounts：想要获得曲线上点的个数，数值越大越平滑

```kotlin
private fun drawBezier(canvas: Canvas) {
    path.moveTo(50f,500f)   //起始点
    path.lineTo(350f,60f)   //结束点
    path.lineTo(950f,730f)  //控制点

    val resultPath = getBezierPoints(Point(50,500),Point(950,730),Point(350,60),5000)
    canvas.drawPath(path,paint)
    canvas.drawPath(resultPath,paint)
}
```

```kotlin
private fun getBezierPoints(startPoint: Point,endPoint: Point,controlPoint: Point,resultCounts: Int): Path {
    val resultPath = Path()
    for (i in 0..resultCounts) {
        val time = i * 1f / resultCounts
        val resultPoint = calcPoint(startPoint,endPoint,controlPoint,time)
        if (i == 0) {
            resultPath.moveTo(resultPoint.x,resultPoint.y)
        } else {
            resultPath.lineTo(resultPoint.x,resultPoint.y)
        }
    }
    return resultPath
}
```

```kotlin
private fun calcPoint(startPoint: Point,endPoint: Point,controlPoint: Point,time: Float): PointF {
    val newLineStartX = startPoint.x + (controlPoint.x - startPoint.x) * time
    val newLineStartY = startPoint.y + (controlPoint.y - startPoint.y) * time
    val newLineEndX = controlPoint.x + (endPoint.x - controlPoint.x) * time
    val newLineEndY = controlPoint.y + (endPoint.y - controlPoint.y) * time
    return PointF((newLineStartX + (newLineEndX - newLineStartX) * time),
        (newLineStartY + (newLineEndY - newLineStartY) * time)
    )
}
```

![微信图片_20240108160221](https://s2.loli.net/2024/01/08/xy6a9UPAuBHbzhM.jpg)

###### Android二阶贝塞尔曲线相关

默认以path的上一个lineTo作为起始点，没有则为坐标原点

```kotlin
path.quadTo(controlPointX,controlPointY,endX,endY)
```

- 20f：控制点X坐标相对于上一个结束点偏移量
- -30f：控制点Y坐标相对于上一个结束点偏移量
- 40f：结束点X坐标相对于上一个结束点偏移量
- -50f：结束点Y坐标相对于上一个结束点偏移量

```kotlin
path.rQuadTo(20f,-30f,40f,-50f)
```

等价quadTo

```kotlin
private fun introduceQuadTo(canvas: Canvas) {
        path.moveTo(50f,500f)   //起始点
//        path.quadTo(350f,60f,950f,730f)
        path.rQuadTo(350f-50f,60f - 500f,950f - 50f,730f - 500f)
        canvas.drawPath(path,paint)
}
```

###### 水波纹绘制

###### 阴影效果绘制Paint.setShadowLayer()

```kotlin
paint.setShadowLayer(3f,20f,20f,Color.RED)
```

- 3f：绘制阴影模糊的半径，半径越大越模糊（高斯模糊，取周围半径的RGB求平均）
- 20f：绘制阴影模糊X方向上的偏移量
- 20f：绘制阴影模糊Y方向上的偏移量
- Color.Red：绘制阴影颜色

```kotlin
override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    if (mSetShadow) {
        paint.setShadowLayer(3f,20f,20f,Color.RED)
    } else {
        paint.clearShadowLayer()
    }
    canvas.drawText("一休",50f,1200f,paint)
    canvas.drawCircle(500f,1200f,20f,paint)
    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.avatar)
    canvas.drawBitmap(bitmap,0f,0f,paint)
}

fun setShadow(shadow: Boolean) {
    mSetShadow = shadow
    postInvalidate()
}
```

![tutieshi_640x1422_8s (1)](https://s2.loli.net/2024/01/09/vCltjEuicsXmP3Y.gif)

- 图片设置模糊，画笔的颜色设置无效，复制的是图片的副本
- 设置模糊，只是模糊其边界

###### Paint.maskFilter（BlurMaskFilter）

设置发光样式

```kotlin
BlurMaskFilter(10f,Blur.INNER)
```

- 10f：发光模糊半径，同阴影一样。高斯模糊
- Blur.INNER：内发光
- Blur.OUTER：仅展示发光效果，其余的变为透明
- Blur.NORMAL：内外发光
- Blur.SOLID：外发光

![微信图片_20240109135635.jpg](https://s2.loli.net/2024/01/09/wEGcnV4vlzkZrQO.jpg)

##### Paint.setShadow(Shadow())

- Shadow子类：BitmapShadow,LinearGradient,RadialGradient

###### BitmapShadow

当绘制的Bitmap不足以容纳控件全部大小时，设置的图像模式

```kotlin
 BitmapShader(srcBitmap!!,Shader.TileMode.MIRROR,Shader.TileMode.MIRROR)
```

- srcBitmap：绘制的Bitmap
- Shader.TileMode.MIRROR：X方向上绘制模式，Y方向上绘制模式。镜像模式
- Shader.TileMode.REPEAT：重复图片模式
- Shader.TileMode.CLAMP：填充模式，图片的四周边缘颜色值填充剩余空白部分

```kotlin
canvas.drawRect(0f,0f,width.toFloat(),height.toFloat(),paint)
```

```kotlin
paint.shader = BitmapShader(srcBitmap!!,Shader.TileMode.MIRROR,Shader.TileMode.MIRROR)
paint.shader = BitmapShader(srcBitmap!!,Shader.TileMode.REPEAT,Shader.TileMode.REPEAT)
paint.shader = BitmapShader(srcBitmap!!,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
```

###### 望远镜效果

- 将Bitmap铺满View的宽高
- 绘制手指移动的圆形区域
- 设置BitmapShadow（）

```kotlin
paint.shader = BitmapShader(srcBitmap!!,Shader.TileMode.MIRROR,Shader.TileMode.MIRROR)
```

```kotlin
override fun onTouchEvent(event: MotionEvent): Boolean {
    centerX = event.x
    centerY = event.y
    postInvalidate()
    return true
}
```

```
canvas.drawBitmap(srcBitmap!!,null, Rect(0,0,width,height),paint)
canvas.drawColor(Color.BLACK)

if (centerX != 0f || centerY != 0f) {
    paint.shader = BitmapShader(srcBitmap!!,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
    canvas.drawCircle(centerX,centerY,500f,paint)
}
```

![tutieshi_640x1422_11s.gif](https://s2.loli.net/2024/01/09/CA6HBmk8fSdTsMO.gif)

###### 绘制圆形头像

```kotlin
private fun drawAvatar(canvas: Canvas) {
    canvas.drawColor(Color.BLACK)
    val scale = srcBitmap!!.width / width.toFloat()
    val matrix = Matrix()
    matrix.setScale(scale,scale)
    val bitmapShader = BitmapShader(srcBitmap!!,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
    bitmapShader.setLocalMatrix(matrix)
    paint.shader = bitmapShader
    canvas.drawCircle(srcBitmap!!.width / 2f,srcBitmap!!.width / 2f,srcBitmap!!.width / 2f,paint)
}
```

![微信图片_20240109155302.jpg](https://s2.loli.net/2024/01/09/mhuZwqKWyoAOJMk.jpg)

###### LinearGradient

- 垂直方向渐变。左上角 ->左下角

```kotlin
val showRectF = RectF(0f,0f,width.toFloat(),500f)
paint.shader = LinearGradient(0f,0f,0f,500f,Color.RED,Color.WHITE,Shader.TileMode.CLAMP)
canvas.drawRect(showRectF,paint)
```

- 水平方向渐变：左上角 -> 右上角

```kotlin
val showRectF = RectF(0f,0f,width.toFloat(),500f)
paint.shader = LinearGradient(0f,0f,showRectF.width(),0f,Color.RED,Color.WHITE,Shader.TileMode.CLAMP)
canvas.drawRect(showRectF,paint)
```

- TileMode对渐变的影响

1.渐变点：左上角 -> Point(width/2,0)

2.ShaderMode = Shader.TileMode.CLAMP

控件未显示完全的部分按照控件四周边缘颜色进行填充 白色->红色

控件宽度的一半是白色到红色，剩余一半填充，右边为红色,故用红色填充

```kotlin
val showRectF = RectF(0f,0f,width.toFloat(),1000f)
paint.shader = LinearGradient(0f,0f,showRectF.width() / 		      			2,0f,Color.WHITE,Color.RED,Shader.TileMode.CLAMP)
canvas.drawRect(showRectF,paint)
```

![微信图片_20240109171710.jpg](https://s2.loli.net/2024/01/09/RMJFtchPH1zba3n.jpg)

3.ShaderMode = Shader.TileMode.REPEAT

![微信图片_20240109172229.jpg](https://s2.loli.net/2024/01/09/pqoz3cB9VSm6ELU.jpg)

3.ShaderMode = Shader.TileMode.MIRROR

![微信图片_20240109172356.jpg](https://s2.loli.net/2024/01/09/tLuOvhqCGckFfbl.jpg)

###### 文本渐变效果

```kotlin
private fun textOfLinearGradient(canvas: Canvas) {
    paint.textSize = TypedValue.applyDimension(COMPLEX_UNIT_SP,20f,resources.displayMetrics)
    paint.style = Paint.Style.FILL_AND_STROKE
    val measureTextWidth = paint.measureText("This is a example of LinearGradient")
    val linearGradient = LinearGradient(0f,0f,measureTextWidth,0f,Color.RED,Color.BLUE,Shader.TileMode.CLAMP)
    paint.shader = linearGradient
    canvas.drawText("This is a example of LinearGradient",0f,500f,paint)
}
```

![微信图片_20240109173011.jpg](https://s2.loli.net/2024/01/09/lB4jCZG1iXDykx2.jpg)

###### 文字跑马灯动画效果

```kotlin
private val content = "欢迎来到德莱联盟LoL"
private val baseLine = 500f
private val flowWidth = 400f
private var startFlowX = -200f
private val paint = Paint().apply {
    textSize = TypedValue.applyDimension(COMPLEX_UNIT_SP,20f,resources.displayMetrics)
    style = Paint.Style.FILL_AND_STROKE
    color = Color.BLACK
}
```

```kotlin
override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val colors = intArrayOf(Color.BLACK,Color.RED,Color.BLACK)
    val points = floatArrayOf(0f,0.5f,1f)
    paint.shader = LinearGradient(startFlowX,0f,startFlowX + flowWidth,0f,colors,points,Shader.TileMode.CLAMP)
    canvas.drawText(content,0f,baseLine,paint)
}
```

```kotlin
init {
    val measureTextWidth = paint.measureText(content)
    ValueAnimator.ofFloat(0f,1f).apply {
        addUpdateListener(object : AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                startFlowX = animation.animatedValue as Float * measureTextWidth - flowWidth / 2
                invalidate()
            }
        })
        duration = 1000
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.RESTART
        start()
    }
}
```

- 动画改变渐变的开始点startFlowX

![tutieshi_640x1422_8s.gif](https://s2.loli.net/2024/01/09/uK3Pfw9OVb6csXY.gif)

###### RadialGradient（中心扩散渐变）

```kotlin
RadialGradient(width / 2f,height /2f,200f,Color.RED,Color.GREEN,Shader.TileMode.CLAMP)
```

- width / 2f：渐变扩散的中心点X坐标
- height /2f：渐变扩散的中心点Y坐标
- 200f：中心绘制半径
- Color.RED：渐变开始颜色
- Color.GREEN：渐变结束颜色
- Shader.TileMode.CLAMP：控件超出绘制的填充模式。此例子绘制的区域为半径200f的圆，若canvas绘制超出此圆的范围，填充规则

```kotlin
paint.shader = RadialGradient(width / 2f,height /2f,200f,Color.RED,Color.GREEN,Shader.TileMode.CLAMP)
canvas.drawCircle(width / 2f,height / 2f,200f,paint)
```

![微信图片_20240109182252.jpg](https://s2.loli.net/2024/01/09/9RcZH2tDWz4p5wS.jpg)

- canvas绘制超出shader的区域，填充模式

- ```kotlin
  paint.shader = RadialGradient(
      width / 2f,
      height / 2f,
      200f,
      Color.RED,
      Color.GREEN,
      Shader.TileMode.CLAMP
  )
  canvas.drawRect(0f,0f,width.toFloat(), height.toFloat(), paint)
  ```

Shader.TileMode.CLAMP：超出边缘填充为Shader四周边缘颜色

![微信图片_20240109184345.jpg](https://s2.loli.net/2024/01/09/92hylfpDbv7xoBS.jpg)

Shader.TileMode.MIRROR：超出部分镜像填充

![微信图片_20240109184550.jpg](https://s2.loli.net/2024/01/09/WgKyn9T4fokheuX.jpg)

Shader.TileMode.REPEAT：超出部分原图填充

![微信图片_20240109184556.jpg](https://s2.loli.net/2024/01/09/mWD4IapPykeYjRu.jpg)
