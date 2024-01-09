### Paint基本使用

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