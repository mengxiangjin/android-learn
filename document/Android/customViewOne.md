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

