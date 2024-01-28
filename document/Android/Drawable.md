### Drawable

#### ShapeDrawable

####  RectShape

- **ShapeDrawable**通过**RectShape**构造（矩形区域）

```kotlin
val rectShapeDrawable = ShapeDrawable(RectShape())
```

- **ShapeDrawable**对应的并不是资源文件中的shape标签，shape标签对应的是**GradientDrawable**
- 绘制出**ShapeDrawable**
- **setBounds**设置矩形所在控件的位置，非相对于屏幕
- paint.color = Color.RED 通过ShapeDrawable获取paint设置颜色更改矩形的颜色
- **ShapeDrawable**.draw(canvas)将drawable绘制出来

```kotlin
private fun drawRectShapeDrawable(canvas: Canvas) {
    val rectShapeDrawable = ShapeDrawable(RectShape())
    rectShapeDrawable.setBounds(50,50,200,100)
    rectShapeDrawable.paint.color = Color.RED
    rectShapeDrawable.draw(canvas)
}
```

- setBounds  根据当前View的位置设置而非屏幕

```xml
<com.jin.draw.widgit.ShapeDrawableView
    android:layout_width="250dp"
    android:layout_height="150dp"
    android:layout_marginStart="50dp"
    android:layout_marginTop="50dp"
    android:background="@color/black"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintStart_toStartOf="parent"/>
```

![image-20240128142631851](https://s2.loli.net/2024/01/28/oMEkDC3SceUl4RN.png)

- **ShapeDrawable**当更改其自带的Paint属性后，会立马在该ShapeDrawable做出响应，重新绘制ShapeDrawable，而只有调用ShapeDrawable.draw(cancas)才会将其绘制于画布中

#### OvalShape

- 椭圆形状
- 在指定的矩形区域绘制椭圆

```kotlin
private fun drawOvalShapeDrawable(canvas: Canvas) {
    val ovalShapeDrawable = ShapeDrawable(OvalShape())
    ovalShapeDrawable.setBounds(50,50,200,100)
    ovalShapeDrawable.paint.color = Color.BLUE
    ovalShapeDrawable.draw(canvas)
}
```

![image-20240128161940117](https://s2.loli.net/2024/01/28/uXWcIKS3zDqNgvo.png)

#### ArcShape

- 扇形图像
- 在指定的矩形绘制椭圆后截取出的扇形
- **ArcShape（startAngle，sweepAngle）开始的角度与扇过的角度**。x轴正方向为起始0°

```kotlin
private fun drawArcShapeDrawable(canvas: Canvas) {
    val arcShape = ArcShape(0f,300f)
    val arcShapeDrawable = ShapeDrawable(arcShape)
    arcShapeDrawable.setBounds(50,50,400,400)
    arcShapeDrawable.paint.color = Color.YELLOW
    arcShapeDrawable.draw(canvas)
}
```

![image-20240128162650422](https://s2.loli.net/2024/01/28/7yKDVqvzepZnblo.png)

#### RoundRectShape

- 圆角矩形图像

- RoundRectShape构造函数

- ```java
  RoundRectShape (float[] outerRadii, @Nullable   		RectF inset,
          @Nullable float[] innerRadii)
  ```

- outerRadii：长度为8的数组，每二个数为一组，代表外矩形边框四个角的x，y弧度半径（可为null）

- inset：内矩形距离外矩形的left，top，right，bottom（可为null）

- innerRadii：同outerRadii，内部矩形框的四个圆角

- 如下图：外边框左上角、左下角为圆角20f。内边框右上角、右下角

```kotlin
private fun drawRoundShapeDrawable(canvas: Canvas) {
        val outerRadi = floatArrayOf(20f,20f,0f,0f,0f,0f,20f,20f)
        val inset = RectF(100f,100f,100f,100f)
        val innerRadii = floatArrayOf(0f,0f,20f,20f,20f,20f,0f,0f)

        val roundRectShape = RoundRectShape(outerRadi,inset,innerRadii)
        val roundRectShapeDrawable = ShapeDrawable(roundRectShape)
        roundRectShapeDrawable.setBounds(50,50,400,400)
        roundRectShapeDrawable.paint.color = Color.YELLOW
        roundRectShapeDrawable.paint.style = Paint.Style.STROKE
        roundRectShapeDrawable.draw(canvas)
    }
```

![image-20240128165343121](https://s2.loli.net/2024/01/28/5McAeBmz1RG3VId.png)

#### PathShape

- 路径图像
- PathShape（path，宽带上总共所占的份额，高度上总共所占的份额）

```kotlin
val pathShape = PathShape(path,200f,200f)
```

- view的宽度更改为**px**单位，width = 250，height = 150

```xml
<com.jin.draw.widgit.ShapeDrawableView
    android:layout_width="250px"
    android:layout_height="150px"
    android:layout_marginStart="50dp"
    android:layout_marginTop="50dp"
    android:background="@color/black"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintStart_toStartOf="parent"/>
```

- **path添加一个宽 = 200份，高 = 200份的矩形**
- 通过**path**构造**PathShape**，PathShape的第二、三个参数代表总共的份数
- **path添加的矩形宽高份数正好达到为PathShape所设置的总份数**
- **故此path会铺满**

```kotlin
private fun drawPathShapeDrawable(canvas: Canvas) {
    val path = Path()
    path.addRect(0f,0f,200f,200f,Path.Direction.CCW)
    val pathShape = PathShape(path,200f,200f)
    val pathShapeDrawable = ShapeDrawable(pathShape)
    pathShapeDrawable.setBounds(0,0,250,150)
    pathShapeDrawable.paint.color = Color.BLUE

    pathShapeDrawable.draw(canvas)
}
```

![image-20240128171410743](https://s2.loli.net/2024/01/28/iZ4jktY7uWRV6Hg.png)

- **倘若修改Path中Rect宽度高度所占的份数(宽度所占份数为100份)，由于总份数PathShape（path，200f,200f）故宽度实际为ShapeDrawable的setbound所占的一半****、高度仍然占满**

```kotlin
path.addRect(0f,0f,100f,200f,Path.Direction.CCW)
```

![image-20240128171715411](https://s2.loli.net/2024/01/28/DuYI82n4K7x6wsH.png)

#### ShapeDrawable中getPaint().setShader

- **ShapeDrawable**有自带的画笔画笔功能，通过setShader可设置画笔的Shader
- layout_width = 1000px,layout_height="1000px"

```xml
<com.jin.draw.widgit.ShapeDrawableView
    android:layout_width="1000px"
    android:layout_height="1000px"
    android:layout_marginStart="50dp"
    android:layout_marginTop="50dp"
    android:background="@color/black"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintStart_toStartOf="parent"/>
```

- 通过ShapeDrawable.getPaint设置BitmaShader
- BitmapShader是从ShapeDrawable.setBounds构造的区域左上角开始绘制的，而不是view的左上角或者屏幕的左上角。

```kotlin
private fun drawBitmapShaderDrawable(canvas: Canvas) {
    val shapeDrawable = ShapeDrawable(RectShape())
    val bitmap = BitmapFactory.decodeResource(resources,R.drawable.img)
    val bitmapShader = BitmapShader(bitmap,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
    shapeDrawable.setBounds(100,100,900,900)
    shapeDrawable.paint.color = Color.BLUE
    shapeDrawable.paint.shader = bitmapShader

    shapeDrawable.draw(canvas)
}
```

![image-20240128174726996](https://s2.loli.net/2024/01/28/pewW6bms5qLTtZu.png)

### 放大镜效果

- 将需要放大的图像**SrcBitmap**铺满整个屏幕
- 构造BitmapShader，构造的Bitmap为SrcBitmap放大3倍的图像.magnifierFactory 放大倍数

```kotlin
val scaleBitmap = Bitmap.createScaledBitmap(srcBitmap!!,srcBitmap!!.width * magnifierFactory,srcBitmap!!.height * magnifierFactory,true)
```

- **ShapeDrawable绘制圆形区域，将其Paint.setShader(BitmapShader)**
- 该圆形区域即为SrcBitmap放大3倍图像的区域即可
- **bitmapShader绘制的区域oval，当bitmap未超出oval区域时，会以相应的mode进行填充。oval绘制的区域是从bitmap左上角开始的**
- **故当手指点击原图的x，y处坐标，实际放大的位置是放大后的图像的3x，3y处，要使绘制的区域oval以3x，3y处作为左上角开始绘制，需对bitmapShader进行平移**
- **因为oval区域绘制bitmapShader是从bitmap左上角开始的，所以首先将bitmap向左移动3x，向上移动3y，此时（3x，3y）即为bitmapShader对应区域的左上角，之后将bitmapShader向右移动区域的radius、向下移动区域的radius。故dx = -3x + radius，dy=-3y+radius**

```kotlin
shapeDrawable!!.paint.shader = bitmapShader
```

```kotlin
private val radius = 100
private val magnifierFactory = 3
private var shapeDrawable: ShapeDrawable? = null
val translateMatrix = Matrix()

private var srcBitmap: Bitmap? = null
private var mPaint = Paint().apply {
    style = Paint.Style.FILL_AND_STROKE
}
```

```kotlin
override fun onDraw(canvas: Canvas) {
    if (srcBitmap == null) {
        val tempBitmap = BitmapFactory.decodeResource(resources,R.drawable.girl)
        srcBitmap = Bitmap.createScaledBitmap(tempBitmap,width,height,false)

        shapeDrawable = ShapeDrawable(OvalShape())
        shapeDrawable!!.setBounds(0,0,radius * 2,radius * 2)

        val scaleBitmap = Bitmap.createScaledBitmap(srcBitmap!!,srcBitmap!!.width * magnifierFactory,srcBitmap!!.height * magnifierFactory,true)
        val bitmapShader = BitmapShader(scaleBitmap!!,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
        shapeDrawable!!.paint.shader = bitmapShader

    }
    canvas.drawBitmap(srcBitmap!!,0f,0f,mPaint)
    shapeDrawable!!.draw(canvas)
    super.onDraw(canvas)
}
```

```kotlin
override fun onTouchEvent(event: MotionEvent): Boolean {
    translateMatrix.reset()
    val x = event.x
    val y = event.y

    val dx = radius - x * magnifierFactory
    val dy = radius  -y * magnifierFactory
    Log.d("TAG", "onTouchEvent:realX " + dx)
    Log.d("TAG", "onTouchEvent:realY " + dy)

    translateMatrix.setTranslate(dx ,dy)
    shapeDrawable!!.paint.shader.setLocalMatrix(translateMatrix)
    shapeDrawable!!.setBounds((x - radius).toInt(), (y - radius).toInt(), (x + radius).toInt(),
        (y + radius).toInt()
    )
    postInvalidate()
    if (event.action == MotionEvent.ACTION_DOWN) return true
    return super.onTouchEvent(event)
}
```