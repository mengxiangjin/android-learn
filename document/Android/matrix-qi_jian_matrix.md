### Matrix

#### ColorMatrix

##### 矩阵表示（4*5）：

- **第5列表示各颜色、alpha的偏移量**

```kotlin
1,0,0,0,0	Red  			R（原图片）		 R
0,1,0,0,0	Green    *  	G			    G
0,0,1,0,0	Blue			B		= 		B
0,0,0,1,0	Alpha			A				A
							1				1
```

- **构造一维数组colorMatrix即可**

```kotlin
paint.colorFilter = ColorMatrixColorFilter(ColorMatrix(colorMatrix))
```

- **绘制全蓝色图片**
  - 绘制原图srcBitmap
  - **canvas平移后，设置paint.colorFilter**
  - colorMatrix将Red、Green都置为0，只保留了Blue与Alpha

```kotlin
private fun exampleDrawBlue(canvas: Canvas) {
    val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
    val paint = Paint()
    canvas.drawBitmap(srcBitmap,0f,0f,paint)

    canvas.translate(0f,srcBitmap.height +200f)
    val colorMatrix = floatArrayOf(
        0f,0f,0f,0f,0f,
        0f,0f,0f,0f,0f,
        0f,0f,1f,0f,0f,
        0f,0f,0f,1f,0f)
    paint.colorFilter = ColorMatrixColorFilter(ColorMatrix(colorMatrix))
    canvas.drawBitmap(srcBitmap,0f,0f,paint)
}
```

![微信图片_20240202104656.jpg](https://s2.loli.net/2024/02/02/W31Xw5IFPmqldyB.jpg)

##### 动态更改ColorMatrix值

```xml
<ImageView
    android:id="@+id/img_bird"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:layout_constraintTop_toTopOf="parent"
    android:scaleType="fitXY"
    android:src="@drawable/bird"/>


<LinearLayout
    android:id="@+id/ll_content"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    app:layout_constraintTop_toBottomOf="@id/img_bird"
    android:layout_marginTop="10dp"/>


<Button
    android:id="@+id/btn_reset"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="重置"
    android:layout_marginEnd="10dp"
    app:layout_constraintHorizontal_chainStyle="packed"
    app:layout_constraintTop_toBottomOf="@id/ll_content"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toStartOf="@id/btn_apply"/>


<Button
    android:id="@+id/btn_apply"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="应用"
    android:layout_marginStart="10dp"
    app:layout_constraintTop_toBottomOf="@id/ll_content"
    app:layout_constraintStart_toEndOf="@id/btn_reset"
    app:layout_constraintEnd_toEndOf="parent"/>
```

```kotlin
private fun initView() {
    //添加editText
    editViews.clear()
    binding.llContent.removeAllViews()
    for (i in 0 until 4) {
        val linearLayout =  LinearLayout(this)
        linearLayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        linearLayout.orientation = LinearLayout.HORIZONTAL
        for (j in 0 until 5) {
            val editText = EditText(this)
            val layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            editText.setText(if (i == j) {
                "1"
            } else {
                "0"
            })
            editText.inputType = EditorInfo.TYPE_CLASS_NUMBER
            editText.gravity = Gravity.CENTER
            layoutParams.weight = 1f
            editText.layoutParams = layoutParams

            editViews.add(editText)
            linearLayout.addView(editText)
        }
        binding.llContent.addView(linearLayout)
    }
}
```

```kotlin
binding.btnApply.setOnClickListener {
    val floatArray = getColorMatrix()
    val srcBitmap = BitmapFactory.decodeResource(resources,R.drawable.bird)

    val bitmap = Bitmap.createBitmap(srcBitmap.width,srcBitmap.height,Bitmap.Config.ARGB_8888)
    val bitmapCanvas = Canvas(bitmap)
    val paint = Paint()
    paint.colorFilter = ColorMatrixColorFilter(ColorMatrix(floatArray))
    bitmapCanvas.drawBitmap(srcBitmap,0f,0f,paint)
    binding.imgBird.setImageBitmap(bitmap)
}
```

```kotlin
private fun getColorMatrix(): FloatArray {
    val result = FloatArray(20)
    editViews.forEachIndexed { index, editText ->
        result[index] = editText.text.toString().toFloat()
    }
    return result
}
```

![tutieshi_640x1422_16s.gif](https://s2.loli.net/2024/02/02/IJyU1xF5Hl6qVw3.gif)

##### 饱和度增加

- 增加最后一列的偏移量即可

![微信图片_20240202162920.jpg](https://s2.loli.net/2024/02/02/mNrC5hyUJ1HRQL4.jpg)

##### 亮度增加

- R,G,B,A都**同时**进行缩放，表现的即为亮度调整

#### Matrix

- **3 x 3矩阵[1, 0, 0, 0, 1, 0, 0, 0, 1]**
- [scaleX，skewX，translateX，skewY，scaleY，translateY，透视，透视，透视]

#### 函数

##### 构造函数

- **空构造、拷贝构造**

- ```kotlin
  val matrix = Matrix()
  val newMatrix = Matrix(matrix)
  ```

##### 重置Reset

- **matrix初始化**

- ```kotlin
  matrix.reset()
  ```

##### 平移Translate

- dx，dy：x，y方向上的平移量。2D坐标系

- ```kotlin
  matrix.setTranslate(20f,20f)
  ```

##### 旋转Rotate

- **旋转60度，默认view左上角，可指定旋转中心如下（50，50）**

- ```kotlin
  matrix.setRotate(60f)
  matrix.setRotate(60f,50f,50f)
  ```

##### 缩放Scale

- **缩放倍数，默认view左上角，可指定缩放中心点（200，200）**

- **缩放倍数 -1~1：缩小、其他放大**

- **缩放倍数负数为翻转**

- ```kotlin
  matrix.setScale(10f,10f)
  matrix.setScale(10f,10f,200f,200f)
  ```

- **matrix将坐标系平移到正中央绘制出原矩形Rect，而后缩放坐标系的像素密度为原来一半**

- ```kotlin
  override fun onDraw(canvas: Canvas) {
      canvas.save()
      val matrix = Matrix()
      matrix.preTranslate(width / 2f,height / 2f)
      canvas.setMatrix(matrix)
      canvas.drawRect(0f,0f,400f,400f,paint)
  
      matrix.preScale(0.5f,0.5f)
      canvas.setMatrix(matrix)
      paint.color = Color.RED
      canvas.drawRect(0f,0f,400f,400f,paint)
      canvas.restore()
      super.onDraw(canvas)
  }
  ```

- ![d323f897b76bb46d5afd14e42529d36](https://s2.loli.net/2024/02/21/3J2grkMlpFvxAow.jpg)

- **matrix.preScale(-0.5f,0.5f) x轴更改为负方向**
- ![2c19d9a211f094bcd5e54ef49d2f39b](https://s2.loli.net/2024/02/21/dUctVCmhlqXE6ak.jpg)

- **缩放依据缩放中心点canvas.setScale（源码）**

  - **平移到缩放中心点位置px，py**
  - **进行缩放（倘若0.5f）**
  - **平移回去 -px，-py**
  - **注意：当平移回去时，平移回去的原点并非一开始平移之前的点，因为中间进行了缩放操作。例如当第一步先平移到400，400时，然后进行缩放（0.5，0.5），然后再平移回去-400，-400。此时的-400是坐标系进行缩放后的即原坐标的一半，故实际平移-200，-200**

- ```java
  public final void scale(float sx, float sy, float px, float py) {
      if (sx == 1.0f && sy == 1.0f) return;
      translate(px, py);
      scale(sx, sy);
      translate(-px, -py);
  }
  ```

- **400,400 点处开始缩放为一半**

- ```kotlin
  override fun onDraw(canvas: Canvas) {
      canvas.save()
      val matrix = Matrix()
      matrix.preTranslate(width / 2f,height / 2f)
      canvas.setMatrix(matrix)
      canvas.drawRect(0f,0f,400f,400f,paint)
  
      matrix.preScale(0.5f,0.5f,400f,400f)
      canvas.setMatrix(matrix)
      paint.color = Color.RED
      canvas.drawRect(0f,0f,400f,400f,paint)
      canvas.restore()
      super.onDraw(canvas)
  }
  ```

- ![25598e5bc5b32062d44cd79299693f7](https://s2.loli.net/2024/02/21/LP9UlBgdyTNHueW.jpg)

##### 点映射MapPoints

- **matrix.mapPoints（float[] points）:更改自身Point点值**

  - ```kotlin
    private fun mapPoints(canvas: Canvas) {
        val matrix = Matrix()
        matrix.setScale(0.5f,1f)
        val srcPoints = floatArrayOf(100f,100f,200f,100f,100f,200f,200f,200f)
        Log.d("zyz", "mapPoints:before " + Arrays.toString(srcPoints))
        matrix.mapPoints(srcPoints)
        Log.d("zyz", "mapPoints:after " + Arrays.toString(srcPoints))
    }
    ```

- **matrix.mapPoints（float[] dstPoints,float srcPoints）:不更改自身Point点值，而是将结果保存到新数组中**

  - ```kotlin
    private fun mapPoints(canvas: Canvas) {
        val matrix = Matrix()
        matrix.setScale(0.5f,1f)
        val srcPoints = floatArrayOf(100f,100f,200f,100f,100f,200f,200f,200f)
        val dstPoints = floatArrayOf()
        matrix.mapPoints(dstPoints,srcPoints)
    }
    ```

##### 半径映射MapRadius()

- **matrix.mapRadius(srcRadius):将srcRadius进行matrix半径映射，返回新值，srcRadius不会改变**

  - ```kotlin
    private fun mapRadius(canvas: Canvas) {
        val matrix = Matrix()
        val srcRadius = 10f
        matrix.setScale(0.5f,1f)
        val result = matrix.mapRadius(srcRadius)
        Log.d("lzy", "mapRadius: " + result)
    }
    ```

##### 矩形映射MapRect

- **matrix.mapRect(Rectf):将RectF进行matrix映射，RectF会改变，返回值为Boolean是否仍然为矩形**

  - ```kotlin
    private fun mapRect(canvas: Canvas) {
        val rect = RectF(100f,500f,600f,1000f)
        val matrix = Matrix()
        matrix.setScale(0.5f,1f)
        matrix.mapRect(rect)
    }
    ```

- **matrix.mapRect(Rectf dstRectf,Rectf srcRectf):将srcRectf进行matrix映射，dstRectf不会改变，返回值为Boolean是否仍然为矩形**

##### 多边形映射SetPolyToPoly

- **matrix.setPolyToPoly(float[] srcPoints,int srcIndex,float[] dstPoints,int dstIndex,int pointCount)**

  - srcPoints:映射的原数组点
  - srcIndex:映射的原数组起始下标
  - dstPoints:映射参照物数组点
  - dstIndex:映射参照物数组起始下标点
  - pointCount:开始映射的点的个数
  - **注意：setPolyToPoly并不会更改srcPoints、dstPoints的值，而会将应用到的值赋给matrix**

- pointCount = 0：代表不进行映射

  - **matrix.mapPoints(srcPoints)：将matrix应用的srcPoints上**

  - 打印结果：srcPoints:[100.0, 100.0, 400.0, 100.0, 100.0, 400.0, 400.0, 400.0]	并未改变

  - ```kotlin
    private fun setPolyToPoly(canvas: Canvas) {
        val srcPoints = floatArrayOf(100f,100f,400f,100f,100f,400f,400f,400f)
        val dstPoints = floatArrayOf(100f,300f,400f,200f,100f,400f,400f,300f)
    
        val matrix = Matrix()
        		      matrix.setPolyToPoly(srcPoints,0,dstPoints,0,0)
        matrix.mapPoints(srcPoints)
        Log.d("zyz", "setPolyToPoly:srcPoints " + Arrays.toString(srcPoints))
        Log.d("zyz", "setPolyToPoly:dstPoints " + Arrays.toString(dstPoints))
    }
    ```

- pointCount = 1：代表前二个值进行dst映射，即一个Point进行映射
  - **映射规则：srcPoints中的前二个值100f,100f 经过变换转为dstPoints中的前二个值100f,300f。所需要的matrix**
  - **matrix.mapPoints(srcPoints)：将matrix应用的srcPoints上**
  - 打印结果：srcPoints:[100.0, 300.0, 400.0, 300.0, 100.0, 600.0, 400.0, 600.0] 前二值同dstPoint值，srcPoint剩下的值进行matrix变换得到的值
- pointCount = 2：代表前四个值进行dst映射，即二个Point进行映射
  - **映射规则：srcPoints中的前四个值100f,100f ，400f,100f,经过变换转为dstPoints中的前四个值100f,300f,400f,200f。所需要的matrix**
  - **matrix.mapPoints(srcPoints)：将matrix应用的srcPoints上**
  - 打印结果：srcPoints:[100.0, 300.0, 400.0, 200.0, 200.0, 600.0, 500.0,500.0] 前四值同dstPoint值，srcPoint剩下的值进行matrix变换得到的值
- pointCount = 3：代表前六个值进行dst映射，即三个Point进行映射
  - **映射规则：srcPoints中的前六个值100f,100f ，400f,100f,100f,400f经过变换转为dstPoints中的前四个值100f,300f,400f,200f,100f,400f所需要的matrix**
  - **matrix.mapPoints(srcPoints)：将matrix应用的srcPoints上**
  - 打印结果：srcPoints:[100.0, 300.0, 400.0, 200.0, 100.0,400.0, 400.0,300.0] 前四值同dstPoint值，srcPoint剩下的值进行matrix变换得到的值
- pointCount = 4：代表前八个值进行dst映射，即四个Point进行映射
  - **映射规则：srcPoints中的前八个值100f,100f ，400f,100f,100f,400f，400f,400f,经过变换转为dstPoints中的前四个值100f,300f,400f,200f,100f,400f,400f,300f所需要的matrix**
  - **matrix.mapPoints(srcPoints)：将matrix应用的srcPoints上**
  - 打印结果：srcPoints:[100.0, 300.0, 400.0, 200.0, 100.0,400.0, 400.0,300.0] 前四值同dstPoint值，srcPoint剩下的值进行matrix变换得到的值

##### 矩形映射SetRectToRect

- **matrix.setRectToRect(RectF srcRect,RectF dstRect,Matrix.ScaleToFit mode):将srcRect通过mode模式映射到dstRect上**，**matrix映射值返回**

  - srcRect：原矩形

  - dstRect：映射到的矩形

  - mode：

    - Matrix.ScaleToFit.FILL:将srcRect全拉伸与dstRect大小相同，返回变换的Matrix
    - Matrix.ScaleToFit.START:将srcRect等比例从dstRect的左上角开始放置，返回变换的Matrix
    - Matrix.ScaleToFit.CENTER:将srcRect等比例从dstRect的中心开始放置，返回变换的Matrix
    - Matrix.ScaleToFit.END:将srcRect等比例从dstRect的右下角开始放置，返回变换的Matrix

  - srcRectF为bitmap的方位，dstRectF为整个view的方位，将srcRectf通过Center放置的dstRectF中返回的matrix值，将matrix应用的bitmap中绘制出来

  - ```kotlin
    private fun setRectToRect(canvas: Canvas) {
        val matrix = Matrix()
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img)
    
        val bitmapRect = RectF(0f,0f,bitmap.width.toFloat(),bitmap.height.toFloat())
        val viewRect = RectF(0f,0f,width.toFloat(),height.toFloat())
        matrix.setRectToRect(bitmapRect,viewRect,Matrix.ScaleToFit.CENTER)
        
        canvas.drawBitmap(bitmap,matrix,paint)
    }
    ```

![40bd700917a1356929340adb3eafb8b](https://s2.loli.net/2024/03/26/R8AdcDFiqJZv1wz.jpg)

##### 单位矩阵isIdentity

- **matrix.isIdentity():Boolean:是否为单位矩阵**

##### 获取矩阵值getValues

- matrix.getValues:返回matrix当中的值

##### 设置矩阵值setValues

- matrix.setValues:设置matrix当中的值

##### 设置矩阵set

- matrix.set(Matrix):设置matrix到当前matrix，如果为null，则重置当前matrix

#### 折叠Bitmap

- ![10757fdd30df8fdcb040dbbd5308689](https://s2.loli.net/2024/03/28/hn7p3zG6XNQEgi9.jpg)

- **setPolyToPoly将点对点进行映射，返回的映射matrix用于绘制bitmap，并利用clipRect截取整个bitmap绘制每个部分**

- **分为8部分、第1,2部分绘制出，后面重复即可**

- 第1部分的绘制：
  - 原始总宽度： bitmap.width
  - 原始每部分宽度itemWidth：bitmap.wdith / 8
  - 折叠后的总宽度itemFoldWidth：bitmap.width * factor(折叠因子)  / 8 (默认折叠因子为0.8)
  - ![6be187593d1c3867c753af4d094c089](https://s2.loli.net/2024/03/29/GbEyiOZYoJPjF9I.jpg)
  
  - 如图：b = sqrt（c * c - a *a） c = itemWidth, a = itemFoldWidth。故b可求，记作H
  
  - 图中第一部分4个顶点坐标dstPoints可得出：（0,0）（itemFoldWidth,H）(0,bitmap.height)（itemFoldWidth,bitmap.height + H）	
  
  - 原始第一部分4个顶点坐标srcPoints可得出：（0,0）（itemWidth，0）（0，bitmap.height）(itemWidth,bitmap.height)
  
  - 故可通过srcPoints与dstPoints通过setPolyToPoly进行matrix映射得到相应的matrix，再将matrix应用到画布，画出bitmap即可。思考：此时绘制的bitmap是斜方向的全图，只想绘制第一部分？利用clipRect进行裁剪，此时裁剪区域（0,0，itemWidth，bitmap.height）
  
  - ```kotlin
    srcMatrix.reset()
    val srcPoints = floatArrayOf(
        0f, 0f,
        itemWidth, 0f,
        itemWidth, bitmap.height.toFloat(),
        0f, bitmap.height.toFloat()
    )
    val dstPoints =  floatArrayOf(
        0f, 0f,
        itemFolderWidth, h,
        itemFolderWidth, bitmap.height + h,
        0f, bitmap.height.toFloat()
    )
    srcMatrix.setPolyToPoly(srcPoints,0,dstPoints,0,srcPoints.size / 2)
    
    canvas.save()
    canvas.setMatrix(srcMatrix)
    canvas.clipRect(0f,0f,itemWidth,bitmap.height.toFloat())
    canvas.drawBitmap(bitmap,0f,0f,paint)
    
    canvas.restore()
    ```
  
  - ​	![309e5301d5b13cb27fa704376f4fd5d](https://s2.loli.net/2024/03/29/DvFEfPk3GoHiZtd.jpg)

- 第二部分绘制：

  - ![7b36241a68655c831e4d4cc7d77ac09](https://s2.loli.net/2024/03/29/MEKefpWh3U6YPDu.jpg)

  - 此时dstPoints四个顶点坐标：a（itemFoldWidth,H）、b(itemFoldWidth * 2,0) 、 c（itemFoldWidth，bitmap.height+H）、d（itemFoldWidth,bitmap.height)
  - 原始srcPoints四个顶点坐标：（itemWidth，0）（itemWidth*2，0）（itemWidth,bitmap.height）(itemWidth * 2,bitmap.heigth)
  - 通过setPolyToPoly进行映射Matrix即可，此时clipRect（itemWidth,0,itemWidth * 2,bitmap.height）

  - ```kotlin
    srcMatrix.reset()
    val srcPoints = floatArrayOf(
        itemWidth, 0f,
        itemWidth * 2, 0f,
        itemWidth, bitmap.height.toFloat(),
        itemWidth * 2, bitmap.height.toFloat()
    )
    val dstPoints =  floatArrayOf(
        itemFolderWidth, h,
        itemFolderWidth * 2, 0f,
        itemFolderWidth, bitmap.height + h,
        itemFolderWidth * 2, bitmap.height.toFloat(),
    )
    srcMatrix.setPolyToPoly(srcPoints,0,dstPoints,0,srcPoints.size / 2)
    
    canvas.save()
    canvas.setMatrix(srcMatrix)
    canvas.clipRect(itemWidth,0f,itemWidth * 2,bitmap.height.toFloat())
    canvas.drawBitmap(bitmap,0f,0f,paint)
    
    canvas.restore()
    ```

  - ![003674471c6ebd6edb812717749d4ea](https://s2.loli.net/2024/03/29/WoeHM5XpAnCrYDZ.jpg)

- 循环绘制，找出规律即可

  - ```kotlin
    for (i in 0 until counts) {
                srcMatrix.reset()
                val srcPoints = floatArrayOf(
                    itemWidth * i, 0f,
                    itemWidth * (i + 1), 0f,
                    itemWidth * (i + 1), bitmap.height.toFloat(),
                    itemWidth * i, bitmap.height.toFloat()
                )
                val dstPoints = if (i % 2 == 0) {
                    //偶
                    floatArrayOf(
                        itemFolderWidth * i, 0f,
                        itemFolderWidth * (i + 1), h,
                        itemFolderWidth * (i + 1), bitmap.height + h,
                        itemFolderWidth * i, bitmap.height.toFloat()
                    )
    
                } else {
                    floatArrayOf(
                        itemFolderWidth * i, h,
                        itemFolderWidth * (i + 1), 0f,
                        itemFolderWidth * (i + 1), bitmap.height.toFloat(),
                        itemFolderWidth * i, bitmap.height + h
                    )
                }
                srcMatrix.setPolyToPoly(srcPoints, 0, dstPoints, 0, srcPoints.size / 2)
    
                canvas.save()
                canvas.setMatrix(srcMatrix)
                canvas.clipRect(itemWidth * i, 0f, itemWidth * (i + 1), bitmap.height.toFloat())
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
    
                if (i % 2 == 0) {
                    //绘制阴影相关
                    lineaPaint.shader = LinearGradient(
                        itemWidth * i,
                        0f,
                        itemWidth * (i + 1),
                        0f,
                        Color.BLACK,
                        Color.TRANSPARENT,
                        Shader.TileMode.CLAMP
                    )
                    canvas.drawRect(
                        itemWidth * i,
                        0f,
                        itemWidth * (i + 1),
                        bitmap.height.toFloat(),
                        lineaPaint
                    )
                } else {
    //                canvas.drawRect(itemWidth * i, 0f, itemWidth * (i + 1), bitmap.height.toFloat(),solidPaint)
                }
                canvas.restore()
            }
    ```