### Drawable&Bitmap

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

### 自定义Drawable

- ##### draw(canvas: Canvas)：drawable显示的内容绘制

- **setAlpha(alpha: Int)：将alpha参数设置给绘制的paint即可**

- **setColorFilter(colorFilter: ColorFilter?)：将colorFilter参数设置给绘制的paint即可**

- **getIntrinsicHeight(): Int：drawable的高度**

- **getIntrinsicWidth(): Int：drawable的宽度**

- **setBounds(left: Int, top: Int, right: Int, bottom: Int)：drawable显示区域设置**

- **getOpacity():Int：返回drawable的模式（一般TRANSLUCENT即可）**

  - **PixelFormat.TRANSLUCENT：具有alpha通道，即使绘制drawable，其底部的图像仍有可能被看到**
  -  **PixelFormat.TTRANSPARENT：完全透明的，相当于什么也没绘制**
  - **PixelFormat.OPAQUE：完全不透明的，底部的图像不可能被看到**
  - **PixelFormat.UNKNOWN：未知**

  

#### 自定义圆角图片Drawable

```kotlin
override fun setAlpha(alpha: Int) {
    mPaint.alpha = alpha
}

override fun setColorFilter(colorFilter: ColorFilter?) {
    mPaint.colorFilter = colorFilter
}

override fun getOpacity(): Int {
    return PixelFormat.TRANSLUCENT
}
```

```kotlin
override fun getIntrinsicHeight(): Int {
    return bitmap.height
}

override fun getIntrinsicWidth(): Int {
    return bitmap.width
}
```

```kotlin
override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
    super.setBounds(left, top, right, bottom)
    bitmapShader = BitmapShader(
        Bitmap.createScaledBitmap(bitmap, right - left, bottom - top, false),
        Shader.TileMode.CLAMP,
        Shader.TileMode.CLAMP
    )
    mPaint.shader = bitmapShader
    rect.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}
```

```kotlin
override fun draw(canvas: Canvas) {
    canvas.drawRoundRect(rect, 10f, 10f, mPaint)
}
```

```xml
<ImageView
    android:id="@+id/img_test"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    android:scaleType="center"
    android:background="@color/white"/>
```

```kotlin
val bitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_alipay)
val customDrawable = CustomDrawable(bitmap)
imageView.setImageDrawable(customDrawable)
```

- 当ImageView为wrap_content时，背景颜色为白色

![ad4a391fbe8c216d18cf69fd724c3fc.jpg](https://s2.loli.net/2024/01/29/Vjd7Ls5Y6DBzv1q.jpg)

- 当imageview宽高远远大于bitmap时，scaleType = center时

![2797ae766d452fcb7674b8d4e21da0b.jpg](https://s2.loli.net/2024/01/29/9g5vN7RJsWQyIhk.jpg)

- 只有当scaleType=fitXY或者centerCrop时才会撑满全view

### Bitmap

#### Bitmap使用

- BitmapDrawable（bitmap）：设置view的backGround或者imageDrawable
- 充当画布canvas（bitmap）

#### Bitmap内存所占大小

- Bitmap是位图，存储每个像素。totalSize = width（px） * height（px） * 1像素所占的字节数
- 每像素所占的字节数，可根据Bitmap.Config来指定
  - Bitmap.Config.ARGB_8888：argb通道各占8位，总共4字节
  - Bitmap.Config.ARGB_4444：argb通道各占4位，总共2字节
  - Bitmap.Config.ARGB_565：无alpha通道，默认255.rgb通道各占5，6，5位，总共2字节
  - Bitmap.Config.ALPHA_8：无颜色通道，8位alpha的通道，总共1字节

#### Bitmap创建

- BitmapFactory.decodeResource(Resource,ResourceID)
- BitmapFactory.decodeFile(String pathName):从文件中解析bitmap
- BitmapFactory.decodeByteArray(ByteArray,start,length) //一般从网络上加载
  - 从网络上下载图片需要新的线程
  - 更新UI需要在主线程中，故view.post

- ```kotlin
  private fun downloadImg() {
      Thread {
          val byteArray = getImg()
          binding.imgNet.post {
              val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray!!.size)
              binding.imgNet.setImageBitmap(bitmap)
          }
      }.start()
  }
  
  private fun getImg(): ByteArray? {
      val url = URL("https://b.bdstatic.com/searchbox/icms/searchbox/img/cheng_girl.png")
      val urlConnection = url.openConnection() as HttpURLConnection
      urlConnection.readTimeout = 3000
      urlConnection.requestMethod = "GET"
      if (urlConnection.responseCode == 200) {
          val inputStream = urlConnection.inputStream
          val out = ByteArrayOutputStream()
          val byteBuffer = ByteArray(1024)
          var length = inputStream.read(byteBuffer)
          while (length != -1) {
              out.write(byteBuffer,0,length)
              length = inputStream.read(byteBuffer)
          }
          inputStream.close()
          return out.toByteArray()
      }
      return null
  }
  ```

- BitmapFactory.decodeFileDescriptor(FileDescriptor fd)：通过文件描述符fd创建bitmap

  - FileDescriptor 对象：InputStream.getFD()

  - InputStream:根据文件路径或者根据路径File

  - ```kotlin
    val path = ""
    val input = FileInputStream(path)
    BitmapFactory.decodeFileDescriptor(input.fd)
    ```

  - BitmapFactory.decodeFile与decodeFileDescriptor相比较：

    - decodeFileDescriptor效率更快，更省内存

- BitmapFactory.decodeStream(InputStream)：根据输入流创建bitmap

#### BitmapFactory.Options类

- inBitmap：设置bitmap
- **inJustDecodeBounds：设置是否仅仅测量模式而不从内存加载bitmap**
- inSampleSize：设置采样率，3（每3个像素组合进行压缩）
- outWidth：获取bitmap的宽度
- outHeight：获取bitmap的高度
- inScaled：false，不进行缩放。默认为true（缩放依据设置的inDensity，inTargetDensity）
- inDensity：设置文件所在资源文件夹的分辨率
- inTargetDensity：设置真实的屏幕分辨率
- inPreferredConfig：像素的存储格式，bitmap内存情况变化，宽度、高度不变

##### inJustDecodeBounds

- true：仅仅测量bitmap的宽高type等信息，而不将此bitmap加载的内存中

- ```kotlin
  val options = BitmapFactory.Options()
  options.inJustDecodeBounds = true
  val bitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_alipay,options)
  Log.d("TAG", "onCreate: " + bitmap) //null
  Log.d("TAG", "onCreate: " + options.outWidth)   //38
  Log.d("TAG", "onCreate: " + options.outHeight)  //38
  Log.d("TAG", "onCreate: " + options.outMimeType)    //image/png
  ```

##### inSampleSize

- 采样率（最小值为1）：**bitmap缩小的值**（每n个像素合并），可防止Bitmap过大加载到内存导致OOM

- 计算目标宽高与bitmap宽高的比值，取最小设置为采样率

- ```kotlin
  private fun introduceSampleSize() {
      val options = BitmapFactory.Options()
      options.inJustDecodeBounds = true
      val bitmap = BitmapFactory.decodeResource(resources,R.drawable.girl,options)
  
      //根据目标宽高与bitmap宽高计算采样率
      val sampleWidth = options.outWidth / 200
      val sampleHeight = options.outHeight / 100
      options.inSampleSize = min(sampleWidth,sampleHeight)
  
      options.inJustDecodeBounds = false
      val realBitmap = BitmapFactory.decodeResource(resources,R.drawable.girl,options)
  
      binding.imgNet.setImageBitmap(realBitmap)
  }
  ```

#### Bitmap静态方法

- ##### Bitmap.createBitmap(width,height,Bitmap.Config):根据宽高创建空白图像

- **Bitmap.createBitmap(Bitmap src):根据src创建**

- **Bitmap.createBitmap(Bitmap,x,y,width,height):裁剪对应的Bitmap，返回一个矩形的Bitmap**

- **Bitmap.createBitmap(Bitmap,x,y,width,height，Matrix，Boolean)：裁剪对应的Bitmap，返回一个矩形的Bitmap，并对返回的bitmap做矩阵变换。Boolean是否设置滤波样式**

- **Bitmap.createBitmap(int[] colors,width,height,Bitmap.Config):设置每个像素点的颜色（基本不用）**

- **Bitmap.createScaleBitmap(Bitmap,width,height,Boolean):将bitmap缩放至目标宽高返回，Boolean是否设置滤波样式**

#### Bitmap实例方法

##### isMutable():Boolean

- 返回bitmap是否可变，即像素能否被更改

##### copy(Bitmap.Config,isMutable):Bitmap

- **this图像以Bitmap.Config配置拷贝副本，isMutable=true可变**
- **isMutable代表bitmap图像是否可变，false：不可变，true：可变**（**即能否改变bitmap的像素内容**）
- **当bitmap图像不可变时，利用bitmap当画布去绘制内容会出错，只有bitmap可变才可当成画布去绘制内容**
- 通过BitmapFactory创建的bitmap都为不可变的
  - Bitmap.createBitmap(Bitmap,x,y,width,height)
  - Bitmap.createBitmap(width,height,Bitmap.Config)
  - Bitmap.createScaleBitmap(Bitmap,width,height,Boolean)（当width，height等于原宽度高度时，直接返回的原图像，需要看原图像是否可变。否则，新图像即为不可变）

##### extractAlpha():Bitmap

- 获取源图像的透明度，返回其透明度图像Bitmap（仅具有Alpha通道）

- 除了中心实体鸟具有透明度，其余四周均透明度为0

  ![bird.png](https://s2.loli.net/2024/01/31/NFG7ZmvAn2XputT.png)

- canvas.drawBitmap参数中的paint对具有颜色的bitmap是无效，只有当bitmap仅有Alpha通道时，paint的颜色才会有效，故此时会将仅仅具有alpha通道的bitmap绘制成天蓝色Color.CYAN

- ```kotlin
  private fun introduceExtraAlpha() {
      val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
      val bitmap = Bitmap.createBitmap(srcBitmap.width,srcBitmap.height,Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)
      val extractAlphaBitmap = srcBitmap.extractAlpha()
      val paint = Paint()
      paint.color = Color.CYAN
      canvas.drawBitmap(extractAlphaBitmap,0f,0f,paint)
      binding.imgNet.setImageBitmap(bitmap)
  }
  ```

![6139e1e1ff3c144f1c89719289258bf.jpg](https://s2.loli.net/2024/01/31/cUFDO4hJ8Bq6VLo.jpg)

##### extractAlpha(Paint,int[2])

- **同extrackAlpha（）**

- **Paint:一般具有BlurMaskFilter的对象**

- **int[2]：返回bitmap绘制推荐的起始位置。当Paint设置BlurMaskFilter时，需设置模糊半径X，此时在（0,0）处绘制bitmap可能导致不完整，则需要在（-x,-x）处进行绘制**

- **模糊效果BlurMaskFilter（28f）**：此时返回的offset[0] = 28,offset[1] = 28

- ```kotlin
  private fun introduceExtraAlphaBlurMask() {
      val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
      val dstBitmap = Bitmap.createBitmap(srcBitmap.width,srcBitmap.height,Bitmap.Config.ARGB_8888)
      val paint = Paint().apply {
          maskFilter = BlurMaskFilter(28f,BlurMaskFilter.Blur.NORMAL)
          color = Color.CYAN
      }
      val offset = IntArray(2)
      val alphaBitmap = srcBitmap.extractAlpha(paint, offset)
  
      val canvas = Canvas(dstBitmap)
      canvas.drawBitmap(alphaBitmap,0f,0f,paint)
  
      binding.imgNet.setImageBitmap(dstBitmap)
  }
  ```

![微信图片_20240131140944.jpg](https://s2.loli.net/2024/01/31/9PT5J3iVFQL8ymM.jpg)

- 图片描边发光效果
  - **通过透明度图像设置BlurMaskFilter绘制透明度图像**
  - **绘制源图像覆盖上去，便宜offset**

```kotlin
private fun exampleExtraAlphaLight() {
    val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
    val dstBitmap = Bitmap.createBitmap(srcBitmap.width,srcBitmap.height,Bitmap.Config.ARGB_8888)

    val paint = Paint().apply {
        maskFilter = BlurMaskFilter(20f,BlurMaskFilter.Blur.NORMAL)
        color = Color.CYAN
    }
    val offset = IntArray(2)
    val alphaBitmap = srcBitmap.extractAlpha(paint, offset)

    val canvas = Canvas(dstBitmap)

    //绘制透明度图像
    canvas.drawBitmap(alphaBitmap,0f,0f,paint)

    //绘制源图像
    canvas.drawBitmap(srcBitmap,-offset[0].toFloat(),-offset[1].toFloat(),paint)
    binding.imgNet.setImageBitmap(dstBitmap)

}
```

![微信图片_20240131141657.jpg](https://s2.loli.net/2024/01/31/ukcMpHyOZ62dPh4.jpg)

##### byteCount()

##### allocationByteCount()

##### rowBytes()

- bitmap所占的空间大小。版本区分

- ```kotlin
  private fun introduceSizeOfBitmap(): Int {
      val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
      return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
          srcBitmap.allocationByteCount
      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
          srcBitmap.allocationByteCount
      } else {
          srcBitmap.rowBytes * srcBitmap.height
      }
  }
  ```

##### recycle()

- 强制回收bitmap所占用内存

##### isRecycled():Boolean

- 判断bitmap是否被回收

##### setPixel(x,y,int color)

##### getPixel(x,y)

- 设置、获取bitmap像素点颜色

**compress(CompressFormat,int quality,OutputStream): Boolean**

- Boolean:压缩结果的返回值

- quality：压缩比质量（0~100） 0：最低画质压缩

- OutputStream：压缩后的图像输出流

- CompressFormat：

  - PNG

  - WEBP

  - JPEG

    

  