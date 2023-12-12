### 绘图基础

#### 画布操作

```kotlin
private fun testOne(canvas: Canvas) {
    val generatePaint = generatePaint(Paint.Style.FILL_AND_STROKE, Color.GREEN, 5f)
    canvas.drawRect(0f,0f,100f,100f,generatePaint)

    /*
    * canvas画布操作不可逆
    * canvas操作对之前绘制内容无影响，只会对之后绘制产生影响
    * canvas.translate(200f,200f) dx,dy
    * canvas.rotate(90,0f,0f) 旋转角度，旋转中心点（画布旋转，画布上的内容也会旋转）
    * canvas.scale(0.8f,0.8f) 缩放 1为原大小，不进行缩放
    * */
    canvas.translate(200f,200f)
    canvas.rotate(90f,0f,0f)
    canvas.scale(0.8f,0.8f)
    canvas.drawRect(0f,0f,100f,100f,generatePaint)

}
```

#### ClipPath

```kotlin
private fun testTwo(canvas: Canvas) {
    canvas.drawColor(Color.RED)
    val path = Path()
    path.addRect(RectF(100f,100f,400f,400f),Path.Direction.CCW)
    
    /*
    *  canvas.clipPath(path) 操作不可逆，对画布进行裁剪（默认与path取交集）
    * */
    canvas.clipPath(path)
    canvas.drawColor(Color.GREEN)
}
```

#### save与restore

```kotlin
private fun testThree(canvas: Canvas) {
    canvas.drawColor(Color.RED)

    val save1 = canvas.save()

    canvas.clipRect(100f,100f,800f,800f)
    canvas.drawColor(Color.YELLOW)

    val save2 = canvas.save()

    canvas.clipRect(200f,200f,700f,700f)
    canvas.drawColor(Color.BLUE)

    val save3 = canvas.save()

    canvas.clipRect(300f,300f,600f,600f)
    canvas.drawColor(Color.GREEN)

    canvas.restore() //恢复画布为栈顶的并弹出
    canvas.restoreToCount(save1) //恢复画布为save1并弹出，在save1之后的（save2,save3）的全部弹出
}
```

#### 绘制圆形头像clipPath示例

```kotlin
private fun testFour(canvas: Canvas) {
    canvas.drawColor(Color.RED)

    //clipPath绘制圆形头像
    val circlePath = Path()

    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.avatar)

    val radius = min(bitmap.width / 2,bitmap.height / 2)
    circlePath.addCircle((bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
        radius.toFloat(),Path.Direction.CCW
    )
    canvas.clipPath(circlePath)
    val rect = Rect(0,0,bitmap.width,bitmap.height)
    canvas.drawBitmap(bitmap,rect,rect,generatePaint(Paint.Style.FILL_AND_STROKE,Color.GREEN,2f))
}
```

#### 图片登场效果示例

```kotlin
private var clipWidth = 0

private fun testFive(canvas: Canvas) {
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.avatar)
    val path = Path()
    val clipHeight = 30
    var i = 0
    while (i * clipHeight < bitmap.height) {
        if (i % 2 == 0) {
            path.addRect(RectF(0f, (i * clipHeight).toFloat(), clipWidth.toFloat(),
                ((i + 1) * clipHeight).toFloat()
            ),Path.Direction.CCW)
        } else {
            path.addRect(RectF((bitmap.width - clipWidth).toFloat(),
                (i * clipHeight).toFloat(), bitmap.width.toFloat(), ((i + 1) * clipHeight).toFloat()
            ),Path.Direction.CCW)
        }
        i++
    }
    clipWidth += 5
    canvas.clipPath(path)
    canvas.drawBitmap(bitmap,0f,0f,generatePaint(Paint.Style.FILL_AND_STROKE,Color.GREEN,2f))
    invalidate()
}
```

