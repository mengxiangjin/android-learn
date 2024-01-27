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