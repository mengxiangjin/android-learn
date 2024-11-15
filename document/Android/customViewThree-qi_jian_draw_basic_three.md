### 混合模式

#### PorterDuffXfermode

#####  paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)

目标图像：红色圆

源图像：蓝色矩形

```kotlin
private fun initSrcBitmap() {
    srcBitmap = Bitmap.createBitmap(bitmapWidth.toInt(), bitmapHeight.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(srcBitmap!!)
    paint.color = Color.BLUE
    canvas.drawRect(0f,0f,bitmapWidth,bitmapHeight,paint)
}

private fun initDstBitmap() {
    dstBitmap = Bitmap.createBitmap(bitmapWidth.toInt(), bitmapHeight.toInt(), Bitmap.Config.ARGB_8888)
    val canvas = Canvas(dstBitmap!!)
    paint.color = Color.RED
    canvas.drawOval(0f,0f,bitmapWidth,bitmapHeight,paint)
}

override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val saveLayerID = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
    canvas.drawBitmap(dstBitmap!!,0f,0f,paint)

    //xfermode设置前为目标对象，即给谁应用xfermode
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.ADD)

    //xfermode设置后为源对象，拿什么应用xfermode
    canvas.drawBitmap(srcBitmap!!,bitmapWidth / 2f,bitmapHeight / 2f,paint)
    paint.xfermode = null
    canvas.restoreToCount(saveLayerID)
}
```

##### PorterDuff.Mode.ADD

- 目标图像（红）与源图像每个像素饱和度相加
- 相交区域饱和度为增加后的值
- 相交处饱和度颜色值相加，未相交处即保持原样（一方饱和度为0）

![微信图片_20240125163615](https://s2.loli.net/2024/01/25/X4o9VRpn1yrOB8W.jpg)

##### PorterDuff.Mode.LIGHTEN

- 相交区域变亮

![微信图片_20240125163615](https://s2.loli.net/2024/01/25/X4o9VRpn1yrOB8W.jpg)

##### PorterDuff.Mode.DARKEN

- 相交区域变暗

![2edd879d1714b1cbac06734e8e408ff](https://s2.loli.net/2024/01/25/NqkoHmbLdYVIXgE.jpg)

##### PorterDuff.Mode.MULTIPLY

- **[Sa * Da,Sc * Dc]**
- 颜色相乘，透明度相乘
- 源图非相交区域透明度 *（目标图像在此区域透明度为0） = 0，源图像非相交区域透明度=0

![be98ba206160e83d19346d3d343632a](https://s2.loli.net/2024/01/25/KeSCt2IPGiOfjaQ.jpg)

##### PorterDuff.Mode.OVERLAY

![a90442e344b7ec4452eb5397c1159c2](https://s2.loli.net/2024/01/25/VAH7IFdJn3LwTU4.jpg)

##### PorterDuff.Mode.SCREEN

- **[Sa + Da - Sa * Da , Sc + Dc - Sc * Dc]**

![ad5110444edda5e7668f1e72a3f64da](https://s2.loli.net/2024/01/25/GCRrepUANEkDoJI.jpg)

##### 以源图像显示为主的模式

##### PorterDuff.Mode.SRC

- **[Sa,Sc]**
- 源图像显示为主

![a70196cec7039bd3bfc85fa6c12b6db](https://s2.loli.net/2024/01/25/UexAi4vREnoOKVp.jpg)

##### PorterDuff.Mode.SRC_IN

- **[Sa * Da,Sc * Da]**

- [源图像透明度 * 目标图像透明度，源图像颜色* 目标图像透明度]
- 非相交区域，目标图像在源图像位置处透明度0，故源图像非相交区域透明

![3bb6b7355af83aefd0acfdf24c2c09e](https://s2.loli.net/2024/01/25/dhJM87osfSYHl3v.jpg)

##### 绘制圆角图片

- 利用**PorterDuff.Mode.SRC_IN** 相交特性
- 图片为源图像，圆角框为目标头像
- 圆角框大小与图片相同
- 设置xfermode属性后绘制，即图片非相交区域变透明实现圆角效果

```kotlin
srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.girl)
dstBitmap = Bitmap.createBitmap(srcBitmap!!.width, srcBitmap!!.height, Bitmap.Config.ARGB_8888)
val canvas = Canvas(dstBitmap!!)
paint.color = Color.WHITE
canvas.drawRoundRect(0f,0f,srcBitmap!!.width.toFloat(),srcBitmap!!.height.toFloat(),20f,20f,paint)
canvas.drawBitmap(dstBitmap!!,0f,0f,paint)
paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
canvas.drawBitmap(srcBitmap!!,0f,0f,paint)
```

![9c42ccd88829cbb036bb8f9c1f6c5ad](https://s2.loli.net/2024/01/25/OoHptqPBS5G9IN3.jpg)

##### 图像的倒影效果

- 利用**PorterDuff.Mode.SRC_IN** 特性 透明度相互乘积特性
- 源图像，复制一个倒立的图像，复制图像绘制在原图像下方，白色遮罩作为目标图像同复制图像位置一样
- 将复制图像作为源图像，白色遮罩层（从上到下为透明度50%->0）作为目标图像，设置**PorterDuff.Mode.SRC_IN**，相交区域透明度会相交，实现倒影
- 为何下方为偏移srcBitmap.height * 2f ？setScale(1f,-1f) 会

```kotlin
private fun invertedExample(canvas: Canvas) {
    //绘制原图像
    val srcBitmap = BitmapFactory.decodeResource(resources,R.drawable.girl)
    canvas.drawBitmap(srcBitmap,0f,0f,paint)

    val saveLayerID = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

    //源图像下方绘制遮罩作为目标图像
    val dstBitmap = BitmapFactory.decodeResource(resources,R.drawable.shader)
    canvas.drawBitmap(dstBitmap,0f,srcBitmap!!.height.toFloat(),paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

    //绘制倒过来的原图像（作为源图像） PorterDuff.Mode.SRC_IN应用此源图像
    val newMatrix = Matrix()
    newMatrix.setScale(1f,-1f)
    newMatrix.postTranslate(0f,srcBitmap.height * 2f)
    canvas.drawBitmap(srcBitmap,newMatrix,paint)

    paint.xfermode = null
    canvas.restoreToCount(saveLayerID)
}
```

![0dd87b9e63559307cf76f277111a9aa](https://s2.loli.net/2024/01/26/1QTcYGsHPCox492.jpg)

##### PorterDuff.Mode.SRC_OUT

- **[Sa * (1 - Da),Sc * (1 - Da)]**
- 目标图像的补集作为源图像的透明度、颜色
- 目标图像在该像素点有图像时显示目标图像，没有图像时显示源图像
- 相交区域，目标图像透明度100，取补集即为0，故源图像相交区域为空白像素。源图像未相交区域，目标图像透明度为0，补集即为100，故源图像未相交区域完全展示

![3c61971e8c2e69e72d244a8be493c07](https://s2.loli.net/2024/01/26/ntIlrGuJ67isNUY.jpg)

##### 橡皮擦效果

- **SRC_OUT**属性源图像与目标图像相交区域会变空白像素
- 绘制源图像，创建新的空白目标图像同源图像相同大小
- 根据手指移动的**Path**绘制在目标图像上
- 设置**PorterDuff.Mode.SRC_OUT**绘制源图像即可
- 相交区域变空白像素达到效果

```kotlin
private fun eraserExample(canvas: Canvas) {
    paint.strokeWidth = 30f
    val saveLayerID = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), paint)

    val bitmapCanvas = Canvas(eraserDstBitmap!!)
	//path绘制到目标图像上，导致目标图像与源图像相交，源图像相交处空白
    bitmapCanvas.drawPath(path,paint)
    canvas.drawBitmap(eraserDstBitmap!!,0f,0f,paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
    canvas.drawBitmap(eraserSrcBitmap!!,0f,0f,paint)
    paint.xfermode = null
    canvas.restoreToCount(saveLayerID)
}
```

![tutieshi_640x1422_12s (1)](https://s2.loli.net/2024/01/26/31FgODqov9rpJQw.gif)

##### 刮刮乐效果

- **SRC_OUT**属性源图像与目标图像相交区域会变空白像素
- 绘制出中奖图片（最底层）
- 绘制手势路径
- 设置**PorterDuff.Mode.SRC_OUT**绘制源图像即蒙层
- 手势路径（目标图像）与蒙层（源图像）相交会导致区域变空白，露出最底层中奖图片

```kotlin
eraserSrcBitmap = BitmapFactory.decodeResource(resources, R.drawable.girl)
eraserDstBitmap =
    Bitmap.createBitmap(eraserSrcBitmap!!.width, eraserSrcBitmap!!.height, Bitmap.Config.ARGB_8888)
bonusBitmap =
    BitmapFactory.decodeResource(resources, R.drawable.avatar)

//将中奖图片缩放到与遮罩图相同大小
bouncedMatrix.setScale(eraserSrcBitmap!!.width / 1f / bonusBitmap!!.width,eraserSrcBitmap!!.height / 1f / bonusBitmap!!.height)
```

```kotlin
private fun eraserExample(canvas: Canvas) {
    paint.strokeWidth = 30f
    
    //绘制奖励图片（最底层）
    canvas.drawBitmap(bonusBitmap!!,bouncedMatrix,paint)
    val saveLayerID = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), paint)

    val bitmapCanvas = Canvas(eraserDstBitmap!!)
    paint.style = Paint.Style.STROKE

    bitmapCanvas.drawPath(path,paint)
    canvas.drawBitmap(eraserDstBitmap!!,0f,0f,paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
    canvas.drawBitmap(eraserSrcBitmap!!,0f,0f,paint)

    paint.xfermode = null
    canvas.restoreToCount(saveLayerID)
}
```

![tutieshi_576x1280_13s](https://s2.loli.net/2024/01/26/cpJQ6XHmvNqgAZ8.gif)

##### PorterDuff.Mode.SRC_OVER

- **[Sa + (1 - Sa)*Da,Sc + (1 - Sc) *Dc]**
- 源图像透明度 +（1-源图像透明度）*目标图像透明度，颜色值同理
- 源图像透明度100%时，原样显示原图像

![1b58a98fbdb1a064507e09f5f68c067](https://s2.loli.net/2024/01/27/ZsArvfC5qehDyHM.jpg)

##### PorterDuff.Mode.SRC_ATOP

- **[Da,Sc * Da + (1 - Sa) * Dc]**
- 当透明度为100%与0%时，效果同**SEC_IN ** **[Sa * Da,Sc * Dc]**

![93dede931869fe1efd545c0606beefb](https://s2.loli.net/2024/01/27/El2wXDboIM7VgP8.jpg)

##### 以目标图像显示为主的模式

##### PorterDuff.Mode.DST

- **[Da,Dc]**
- 全部以目标图像显示为主

![caf823f6c7e43e60e5efbaf9b09065b](https://s2.loli.net/2024/01/27/RbChNnF5cZEDil2.jpg)

##### PorterDuff.Mode.DST_IN

- **[Sa * Da,Sa * Dc]**

- 与**SRC_IN[Sa*Da,Sc*Da]**相反

  ![caf823f6c7e43e60e5efbaf9b09065b](https://s2.loli.net/2024/01/27/RbChNnF5cZEDil2.jpg)

##### PorterDuff.Mode.DST_OUT

- **[ Da * ( 1 - Sa) , Dc * ( 1 “ Sa)]** 
- 非相交区域完全显示目标图像
- 相交区域显示目标图像，当相交区域，源图像透明度100时，目标图像透明度为0，当源图像透明度0时，目标图像完全显示

![515ae02b9707ff041d2cce2cd66fec0](https://s2.loli.net/2024/01/27/wPAWbB4qryNCIx7.jpg)

##### PorterDuff.Mode.DST_OVER

- **[Sa+ (1 - Sa) *Da ,Dc+ (1 - Da)*Sc]** 
- 显示目标图像为主

![2401d417b4d0e41c9a6fa0a7b43a96d](https://s2.loli.net/2024/01/27/IrPjsDvumQRtXVK.jpg)

##### PorterDuff.Mode.DST_ATOP

- **[Sa , Sa *Dc  + Sc * (1 - Da)]** 
- 当源图像透明度为0或者100时，效果同**DST_IN**相同
- 当源图像透明度不为0或者100时，效果比**DST_IN**更亮一点

![2401d417b4d0e41c9a6fa0a7b43a96d](https://s2.loli.net/2024/01/27/IrPjsDvumQRtXVK.jpg)

##### PorterDuff.Mode.CLEAR

- **[0,0]**
- 源图像所在区域变为空白像素

![515ae02b9707ff041d2cce2cd66fec0](https://s2.loli.net/2024/01/27/wPAWbB4qryNCIx7.jpg)

### Canvas与图层

#### Canvas的获取

##### 重写方法ondraw()、dispatchDraw（）方法

- 继承View重写**ondraw**(**canvas**)、**dispatchDraw**（**canvas**）方法
- 调用顺序**ondraw** -> **dispatchDraw**
- **View**二者都会调用，一般写在重写**ondraw**（）即可
- **ViewGroup**当存在背景时会调用**ondraw**，否则**dispatchDraw**

##### Canvas(Bitmap)创建

- 通过Bitmap创建一个Canvas
- 在此Canvas上（Bitmap上）绘制文本
- 绘制此Bitmap（若不绘制此Bitmap，画布将不会有任何内容）

```kotlin
override fun onDraw(canvas: Canvas) {
    val bitmap = Bitmap.createBitmap(200,200,Bitmap.Config.ARGB_8888)
    val bitmapCanvas = Canvas(bitmap)
    bitmapCanvas.drawText("Bitmap",0f,100f,mPaint)
    
    //绘制此bitmap
    canvas.drawBitmap(bitmap,0f,0f,mPaint)
    super.onDraw(canvas)
}
```

##### SurfaceHolder.lockCanvas()

#### canvas.saveLayer

##### canvas.saveLayer（Rect,paint）

- 通过**Rect**的大小生成全新的透明画布
- **saveLayer**之前将画布**canvas.drawColor(Color.GREEN)**
- 利用**PorterDuff.Mode.SRC_IN**模式绘制**[Sa * Da,Sc * Da]**

```kotlin
private fun introduceSaveLayer(canvas: Canvas) {
    val srcBitmap = Bitmap.createBitmap(400,400,Bitmap.Config.ARGB_8888)
    val dstBitmap = Bitmap.createBitmap(400,400,Bitmap.Config.ARGB_8888)

    mPaint.color = Color.RED
    val srcCanvas = Canvas(srcBitmap)
    srcCanvas.drawRect(0f,0f,400f,400f,mPaint)

    mPaint.color = Color.BLUE
    val dstCanvas = Canvas(dstBitmap)
    dstCanvas.drawOval(0f,0f,400f,400f,mPaint)

    canvas.drawColor(Color.GREEN)

    val saveLayerID = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), mPaint)
    canvas.drawBitmap(dstBitmap,0f,0f,mPaint)

    mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(srcBitmap,200f,200f,mPaint)

    canvas.restoreToCount(saveLayerID)
}
```

![97a74c8a2b39898e843e244cb6a5824.jpg](https://s2.loli.net/2024/01/27/FD7yV4dCnKzqPMR.jpg)

- 把**val saveLayerID = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), mPaint)**注释掉
- **PorterDuff.Mode.SRC_IN**模式失效，源图像直接全部显示

![222da372c43c9e088411f0ef4ee5c0e.jpg](https://s2.loli.net/2024/01/27/459NI8PcMjSsXJl.jpg)

- **canvas.saveLayer**作用：

  **生成一个全新的透明画布，之后所绘制的内容都在此透明画布上**

  **透明画布中，源图像与目标图像[Sa * Da,Sc * Da],非相交区域，目标图像透明度为0，故源图像非相交区域不显示，相交区域目标图像透明度Da = 100%，故相交区域源图像原样显示**

  **当注释掉Canvas.saveLayer时。真正的目标图像为目标图像+绿色画布，导致源图像非相交区域，目标图像透明度100%，故源图像正常展示**

- canvas.saveLayer之后对画布的操作不会影响之前的画布效果

##### saveLayerAlpha（Rect，alpha）

- 同**canvas.saveLayer**，新建一个指定透明度的画布【0,255】

##### 图层、画布、Canvas

- 图层：canvas.drawXXX(),即新建一个透明图层绘制内容覆盖到画布中
- 画布（Bitmap）：每块画布其实都是一个Bitmap。原始画布：即view原始的draw，人造画布：通过Canvas构造函数
- Canvas：对画布的操作工具、比如clip等会改变画布大小

#### 画布的恢复

##### Canvas.restore与restoreToCount(int ID)

- 当调用canvas.save、saveLayer都会返回一个ID
- 此ID为当前画布在栈的标记ID
- 原始画布ID = 0，递增
- saveID== 1
- **restoreToCount(ID)即在此之前的画布ID全部弹出，此ID也弹出，恢复此ID之前的画布状态。restoreToCount（2），即恢复成ID = 1的画布状态**
- **restore（）即弹出栈顶即可**

```kotlin
var saveID = canvas.save()
```
