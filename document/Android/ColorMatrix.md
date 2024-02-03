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