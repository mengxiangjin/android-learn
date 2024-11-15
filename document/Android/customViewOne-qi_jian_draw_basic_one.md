### 绘图基础

#### Paint设置

```kotlin
val paint = Paint().apply {
    style = Paint.Style.FILL_AND_STROKE
    strokeWidth = 3.0f  //PX
    color = 0xFFFFFF    //Color.RED  Color.parseColor("#ffffff") 
    setARGB(255,255,255,255) // 等同于setColor
    isAntiAlias = true //抗锯齿 边缘是否平滑
}
```

#### Canvas绘制

##### 画布背景设置

```kotlin
canvas.drawColor(Color.RED)
canvas.drawARGB(255,255,255,255)
canvas.drawColor(0xFFFFFF)
canvas.drawRGB(255,255,255)
```

##### 直线

```kotlin
canvas.drawLine(100f, 100f, 200f, 200f, paint)  //(100,100) -> (200,200)
canvas.drawLines(
    floatArrayOf(
        100f,
        100f,
        200f,
        200f,
        300f,
        300f,
        400f,
        400f,
    ), paint
) //size必须是4的倍数 每二个点形成线 (100,100) -> (200,200) (300,300) -> (400,400)
canvas.drawLines(floatArrayOf(
    100f,
    100f,
    200f,
    200f,
    300f,
    300f,
    400f,
    400f,
),2,4,paint) //跳过offset个数，绘后面count个 即 (200,200) -> (300,300)
```

##### 点

```kotlin
canvas.drawPoint(20f,20f,paint)
canvas.drawPoints(floatArrayOf(20f,20f,30f,30f),paint)
canvas.drawPoints(floatArrayOf(20f,20f,30f,30f),2,2,paint)  //(30,30)
```

##### 矩形、圆、椭圆

```kotlin
val rect = RectF(20f,20f,30f,40f)
canvas.drawRect(rect,paint)
canvas.drawRect(20f,20f,30f,40f,paint)
canvas.drawRoundRect(rect,20f,10f,paint)    //圆角矩形 x,y方向的圆角弧度

canvas.drawCircle(20f,20f,10f,paint)    //圆心，半径
canvas.drawOval(rect,paint) //椭圆，依据矩形绘制椭圆
```

##### 圆弧

```kotlin
paint.style = Style.STROKE
/*
* 开始角度 30，扫 120度绘制圆弧
* useCenter：是否连接圆弧圆心
* */
canvas.drawArc(rect,0f,120f,false,paint)//左上

rect.offset(300f,0f)    //右偏移500px	右上
canvas.drawArc(rect,0f,120f,true,paint)

//当style设置为 Style.FILL_AND_STROKE
paint.style = Style.FILL_AND_STROKE
rect.offset(-300f,300f) //左下
canvas.drawArc(rect,0f,120f,false,paint)

rect.offset(300f,0f) //右下
canvas.drawArc(rect,0f,120f,true,paint)
```



<img src="https://s2.loli.net/2023/11/23/EV9Bu8IgJiaqrhW.jpg" alt="20231123-103148" style="zoom: 50%;" />

##### 矩形操作

```kotlin
val srcRectF = RectF(100f,100f,200f,200f)
val desRectF = RectF(150f,100f,400f,200f)
srcRectF.contains(20f,20f) //是否包含某点
var isIntersects = RectF.intersects(srcRectF,desRectF) //判断矩形是否相交(静态方法)
var intersects = srcRectF.intersects(150f,100f,400f,200f)    //判断矩形是否相交(成员方法)

var intersect = srcRectF.intersect(desRectF)    //判断矩形是否相交，若相交改变srcRectF 为相交后的值，不相交即不变
srcRectF.union(desRectF)    //矩形合并，最小左上角与最大右下角，若为empty则不变
srcRectF.union(20f,20f) //矩形与点合并，矩形为empty，则默认0,0, 与 20f,20f构成矩形 根据点在矩形位置，判断点是左上角还是右下角
```

##### Path

```kotlin
val path = Path()
path.moveTo(100f,100f)  //起始点
path.lineTo(200f,200f)  //下一次点
path.lineTo(300f,100f)
// path.close()    //封闭路径
val rectF = RectF(300f,300f,500f,500f)
/*
 *  forceMoveTo
 *   false:画笔无缝连接过去 （默认）
 *   true:画笔中断，重新落点开始绘制
 * */
path.arcTo(rectF,30f,90f,false)
canvas.drawPath(path,paint)
 //addArc 实际上就是artTo(forceMoveTo = true)
rectF.offset(300f,0f)
path.addArc(rectF,30f,90f)  //弧
path.addRect(400f,600f,600f,700f,Path.Direction.CW) //矩形
path.addRoundRect(400f,800f,600f,1000f,20f,20f,Path.Direction.CCW)  //圆角矩形
path.addCircle(600f,600f,100f,Path.Direction.CW)    //圆
path.addOval(400f,1200f,600f,1250f,Path.Direction.CW)   //椭圆
canvas.drawPath(path,paint)                        
```

##### Path中的FillType

```kotlin
val path = Path()
val paint = Paint().apply {
    color = Color.RED
    style = Style.FILL //重点(FILLANDSTROKE可能导致设置fillType有问题)
    strokeWidth = TypedValue.applyDimension(COMPLEX_UNIT_DIP,2f,resources.displayMetrics)
}
/*
* fillType 自填充算法 需要设置为填充模式（否则失效）
* FillType.EVEN_ODD:去除相交部分
* FillType.WINDING 
* FillType.INVERSE_EVEN_ODD
* FillType.INVERSE_WINDING
* */
path.addRect(100f,100f,300f,300f,Path.Direction.CW)
path.addCircle(300f,300f,100f,Path.Direction.CW)
path.fillType = FillType.EVEN_ODD   //去除相交

canvas.drawPath(path,paint)
```

##### 文本绘制

```kotlin
val paint = Paint().apply {
    color = Color.BLUE
    style = Style.FILL
    strokeWidth = TypedValue.applyDimension(COMPLEX_UNIT_DIP,2f,resources.displayMetrics)
    isAntiAlias = true

    textSize = TypedValue.applyDimension(COMPLEX_UNIT_SP,13f,resources.displayMetrics)
    isFakeBoldText = true //加粗
    isUnderlineText = true //下划线
    isStrikeThruText = true //删除线
    textSkewX = -0.25f //字体倾斜度 负数时右边倾斜，正数左边
    textScaleX = 1f //拉伸倍数 1:不拉伸
}

paint.textAlign = Paint.Align.LEFT
canvas.drawText("床前明月光",100f,500f,paint)

paint.textAlign = Paint.Align.CENTER
canvas.drawText("床前明月光",100f,600f,paint)

paint.textAlign = Paint.Align.RIGHT
canvas.drawText("床前明月光",100f,700f,paint)

paint.color = Color.RED
canvas.drawLine(100f,0f,100f,1000f,paint)

val path = Path()
paint.style = Style.STROKE
path.addArc(400f,400f,800f,800f,30f,100f)
canvas.drawPath(path,paint)

/*
* drawTextOnPath
* hOffset:水平方向离path起始点的偏移量
* vOffset:垂直方向里path中心点的偏移量
* */
paint.color = Color.GREEN
canvas.drawTextOnPath("床前明月光,疑是地上霜",path,0f,-10f,paint)
```

![](https://s2.loli.net/2023/11/24/qoPdmlO4trJcsau.jpg)

##### 字体样式

```kotlin
val paint = Paint().apply {
    color = Color.RED
    style = Style.FILL_AND_STROKE
    isAntiAlias = true

    textSize = TypedValue.applyDimension(COMPLEX_UNIT_SP,12f,resources.displayMetrics)

}
paint.typeface = Typeface.DEFAULT
paint.typeface = Typeface.DEFAULT_BOLD
//自定义字体样式 从asserts文件夹下读取ttf字体文件
paint.typeface = Typeface.createFromAsset(context.assets,"font/font.ttf")
canvas.drawText("床前明月光,疑是地上霜",100f,200f,paint)
```

##### Region绘制

```kotlin
//可通过rect来构造
val region = Region()
val path = Path()
path.addOval(50f,50f,200f,500f,Path.Direction.CCW)

val newRegion = Region(50,50,200,200)
//path与newRegion 取交集
region.setPath(path,newRegion)
//canvas无直接绘制region的API，需通过RegionIterator类循环绘制
val regionItar = RegionIterator(region)
val rect = Rect()
while (regionItar.next(rect)) {
    canvas.drawRect(rect,paint)
}
```

##### Region Op

```kotlin
/*
* Region 的相交处理 OP
    DIFFERENCE(0),  this与region不同的区域
    INTERSECT(1),   this与region相交的区域
    UNION(2),       this与region合并的区域
    XOR(3),         this与region相交之外的区域
    REVERSE_DIFFERENCE(4),  region与this不同的区域
    REPLACE(5);     region区域
* */
val rectOne = Rect(100,100,400,200)
val rectTwo = Rect(200,0,300,300)

val paint = Paint().apply {
    color = Color.RED
    style = Style.STROKE
    strokeWidth = 2f
}

canvas.drawRect(rectOne,paint)
canvas.drawRect(rectTwo,paint)


paint.style = Style.FILL
paint.color = Color.BLUE
val regionOne = Region(rectOne)
val regionTwo = Region(rectTwo)

//regionOne 与 regionTwo 不同的区域
regionOne.op(regionTwo,Region.Op.DIFFERENCE)
regionOne.op(regionTwo,Region.Op.INTERSECT)
regionOne.op(regionTwo,Region.Op.UNION)
regionOne.op(regionTwo,Region.Op.XOR)
regionOne.op(regionTwo,Region.Op.REVERSE_DIFFERENCE)
regionOne.op(regionTwo,Region.Op.REPLACE)

val regionIterator = RegionIterator(regionOne)
val rect = Rect()
while (regionIterator.next(rect)) {
    canvas.drawRect(rect,paint)
}
```

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

##### ClipPath

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

##### save与restore

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

##### 绘制圆形头像clipPath示例

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

#### Canvas.drawText

##### canvas.drawText(text,x,y,paint)

- text:绘制的文本
- x:绘制文本的位置（起始）
- y:绘制文本的位置y（基线）

```kotlin
private fun introduceBaseLine(canvas: Canvas) {
    val x = 0f
    val y = 300f
    paint.color = Color.RED
    canvas.drawLine(x,y,x + 1000,y,paint)
    paint.color = Color.BLACK
    canvas.drawText("hello worldg",x,y,paint)
}
```

![微信图片_20240108095508](https://s2.loli.net/2024/01/08/Gt1YoPIFDOCcVW5.jpg)

##### Paint.textAlign

drawText中x的坐标与textAlign设置有关。

- Align.Left：文字所在的矩形框左边与x坐标对齐
- Align.Center：文字所在的矩形框中间与x坐标对齐
- Align.Right：文字所在的矩形框右边与x坐标对齐

##### Paint.fontMetrics(系统已固定)

- Paint.fontMetrics.ascent：系统推荐的文字绘制的上限top（y轴）
- Paint.fontMetrics.descent：系统推荐的文字绘制的下限bottom（y轴）
- Paint.fontMetrics.baseLine：基线所在位置，drawText中Y参数
- Paint.fontMetrics.top：实际文字绘制的上限top（y轴）
- Paint.fontMetrics.bottom：实际文字绘制的下限top（y轴）

```kotlin
//Paint.fontMetrics.ascent = this文字绘制的ascent（y轴）  - this文字绘制的基线baseLine（y轴） 负数
//Paint.fontMetrics.descent = this文字绘制的descent（y轴）  - this文字绘制的基线baseLine（y轴） 正数
//Paint.fontMetrics.top = this文字绘制的top（y轴）  - this文字绘制的基线baseLine（y轴） 负数
//Paint.fontMetrics.bottom = this文字绘制的bottom（y轴）  - this文字绘制的基线baseLine（y轴） 正数
```

```kotlin
private fun introduceFontMetrics(canvas: Canvas) {
    val x = 0f
    val baseLineY = 300f
    paint.color = Color.RED
    canvas.drawLine(x, baseLineY, x + 1000, baseLineY, paint)
    val ascentY = paint.fontMetrics.ascent + baseLineY
    val descentY = paint.fontMetrics.descent + baseLineY
    val topY = paint.fontMetrics.top + baseLineY
    val bottomY = paint.fontMetrics.bottom + baseLineY
    canvas.drawLine(x, ascentY, x + 1000, ascentY, paint)
    canvas.drawLine(x, descentY, x + 1000, descentY, paint)
    canvas.drawLine(x, topY, x + 1000, topY, paint)
    canvas.drawLine(x, bottomY, x + 1000, bottomY, paint)

    paint.color = Color.BLACK
    paint.textAlign = Paint.Align.LEFT

    canvas.drawText("hello worldg", x, baseLineY, paint)
}
```

##### Paint.getTextBounds(text,start,end,rect)

##### Paint.measureText(text)：测量该文本的宽度

- 此函数为获取文本的最小矩形位置

- text：需要测量的文本
- start：在文本中的Index
- end：文本中的Index
- rect：返回过来文本所在矩形位置
- 绘制文本所占的最大矩形位置

```kotlin
val maxRectY = paint.fontMetrics.top + baseLineY
val measureTextWidth = paint.measureText("hello worldg")
canvas.drawRect(0f,maxRectY,measureTextWidth,paint.fontMetrics.bottom + baseLineY,paint)
```

- 绘制文本所占的最小矩形位置

```kotlin
val minRect = Rect()
paint.getTextBounds("hello worldg",0,"hello worldg".length,minRect)
minRect.offset(0, baseLineY.toInt())
canvas.drawRect(minRect,paint)
```

​	为何需要offset个baseLineY的高度

​	getTextBounds默认是基线为0时获取的矩形，故需要下移基线即可

![微信图片_20240108112252](https://s2.loli.net/2024/01/08/syf28qJv9Q4MHWX.jpg)

##### 中心线求基线

```kotlin
(paint.fontMetrics.descent - paint.fontMetrics.ascent) / 2 - paint.fontMetrics.descent + centerY
```
