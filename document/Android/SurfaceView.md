### SurfaceView

#### SurfaceView基本用法

- 子线程更新视图（自定义view更新都在主线程，当绘制过多时容易导致交互阻塞）
- 双缓冲技术
- 所有能用view实现的，也都可以用SurfaceView去实现（但一般不用）

##### setWillNotDraw(false)

- **是否不需要重绘**

- **自定义view默认false，自定义viewGroup默认true**
- **当surfaceView去写入自定义view时调用postInvalidate（）发现并不会回调onDraw函数。setWillNotDraw（false）即可**

##### SurfaceView绘制手指路径

- 同自定义view实现只需将invalidate（）改成surfaceView绘制即可，并在子线程更新

```kotlin
override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.reset()
                path.moveTo(event.x,event.y)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x,event.y)
            }
            MotionEvent.ACTION_UP -> {

            }
        }
//        invalidate()
        drawPathOnCanvas()
        return super.onTouchEvent(event)
    }

    private fun drawPathOnCanvas() {
        Thread(Runnable {
            val lockCanvas = holder.lockCanvas()
            lockCanvas.drawPath(path,mPaint)
            holder.unlockCanvasAndPost(lockCanvas)
        }).start()
    }
```

##### holder.lockCanvas(): Canvas

- **获取Canvas并锁住，防止多线程canvas同时绘制安全问题**

##### holder.unlockCanvasAndPost(lockCanvas)

- **解锁相应的canvas**

##### 背景图广告循环左右移动

- 循环更新利用surfaceview在子线程操作，防止主线程阻塞
- 将DstBitmap缩放至屏幕宽度的3/2
- 利用canvas绘制drawBitmap(bitmap,left,top,paint):更改left值以达到移动的效果

```kotlin
private var dstBitmap: Bitmap? = null
private var mPaint = Paint()
private var mCanvas: Canvas? = null

private var isDestroy = false
private var moveX = 0f
private var moveDirection = MoveDirection.LEFT //画布移动

init {
    holder.addCallback(this)
}

override fun surfaceCreated(holder: SurfaceHolder) {
    isDestroy = false
    drawAnim()
}

override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
}

override fun surfaceDestroyed(holder: SurfaceHolder) {
    isDestroy = true
}

private fun drawAnim() {
    val srcBitmap = BitmapFactory.decodeResource(resources, R.drawable.bird)
    val totalWidth = width * 3 / 2
    dstBitmap =
        Bitmap.createScaledBitmap(srcBitmap, totalWidth, srcBitmap.height, false)
    Thread {
        while (!isDestroy) {
            mCanvas = holder.lockCanvas()
            drawView()
            holder.unlockCanvasAndPost(mCanvas)
            Thread.sleep(50)
        }
    }.start()
}

private fun drawView() {
    mCanvas!!.drawColor(Color.TRANSPARENT,PorterDuff.Mode.CLEAR)
    mCanvas!!.drawBitmap(dstBitmap!!,moveX,0f,mPaint)

    when (moveDirection) {
        MoveDirection.LEFT -> {
            moveX -=1
        }
        MoveDirection.RIGHT -> {
            moveX +=1
        }
    }
    if (moveX <= -width / 2f) {
        moveDirection = MoveDirection.RIGHT
    }

    if (moveX >= 0) {
        moveDirection = MoveDirection.LEFT
    }
}

enum class MoveDirection {
    LEFT,RIGHT
}
```

![tutieshi_576x1280_31s _1_ _1_.gif](https://s2.loli.net/2024/01/31/sY54qgSifQrkdEX.gif)

#### SurfaceView双缓冲机制

- **SurfaceView存在着二个缓冲区，前景缓冲区与后置缓冲区**

- **前景缓冲区即当前屏幕显示的内容，后置缓冲区为绘制的内容，下一次将要更新到屏幕的内容（一次性）**

- **当调用holder.lockCanvas()获取的并不是前景缓冲区即屏幕显示内容，而是后置缓冲区画布，当unlockCanvas时，表面此时后置缓冲区画布已经绘制完成，此时将后置缓冲区推送到前景缓存区，将前景缓冲区推送到后置缓冲区（存在屏幕图像差）**

- **循环绘制文字(只显示了1，5，9)。结果依据se**

- ```kotlin
  private fun drawText() {
      Thread {
          for (i in 0 until 10) {
              val lockCanvas = holder.lockCanvas()
              lockCanvas.drawText("$i", width / 2f, (i + 1) * 50f, paint)
              holder.unlockCanvasAndPost(lockCanvas)
          }
      }.start()
  }
  ```

![微信图片_20240201111620.jpg](https://s2.loli.net/2024/02/01/WXLsJMh2ek54lpv.jpg)

##### 绘制现象解释

- **屏幕上只显示1，5，9，证明此设备surfaceView存在4个缓冲区**
- **倘若默认2个缓冲区（前置front，后置rear），则会显示1，3，5，7，9**
  1. 初始状态：前景缓冲区与后置缓冲区画布都为空内容
  2. 第一次循环：
     1. **text = 0：lockCanvas拿到rear（空），绘制text过后，rear（0）。unlock将front与rear交换，此时font（0），rear（空）**
     2. **text =1：lockCanvas拿到rear（空），绘制text过后，rear（1）。unlock将front与rear交换，此时font（1），rear（0）**
     3. **text =2：lockCanvas拿到rear（0），绘制text过后，rear（0，2）。unlock将front与rear交换，此时font（0，2），rear（1）**
     4. **text =3：lockCanvas拿到rear（1），绘制text过后，rear（1，3）。unlock将front与rear交换，此时font（1，3），rear（0，2）**
     5. **text =9：lockCanvas拿到rear（1，3，5，7），绘制text过后，rear（1，3，5，7，9）。unlock将front与rear交换，此时font（1，3，5，7，9），rear（0，2，4，6，8）**
     6. **故展示在屏幕上的即为front（1，3，5，7，9）**
- 当在绘制完成后，将线程休眠一段时间即可看见过程变化**Thread.sleep(800)**
- ![tutieshi_640x1422_9s.gif](https://s2.loli.net/2024/02/01/aHXDPilC3pobnAz.gif)

#### lockCanvas（Rect）

- **获取指定区域Rect的画布进行绘制，Rect以为的内容保留当前屏幕的内容**

- ```kotlin
  private fun draw() {
      Thread {
          
          //清屏循环
          while (true) {
              val lockCanvas = holder.lockCanvas(Rect(0, 0, 1, 1))
              val clipBoundsRect = lockCanvas.clipBounds
              if (clipBoundsRect.width() == width && clipBoundsRect.height() == height) {
                  lockCanvas.drawColor(Color.BLACK)
                  holder.unlockCanvasAndPost(lockCanvas)
              } else {
                  holder.unlockCanvasAndPost(lockCanvas)
                  break
              }
          }
  
          for (i in 0 until 10) {
              //大红圆
              if (i == 0) {
                  val lockCanvas = holder.lockCanvas(Rect(10,10,600,600))
                  lockCanvas.drawColor(Color.RED)
                  holder.unlockCanvasAndPost(lockCanvas)
              }
  
              //中绿圆
              if (i == 1) {
                  val lockCanvas = holder.lockCanvas(Rect(30,30,570,570))
                  lockCanvas.drawColor(Color.GREEN)
                  holder.unlockCanvasAndPost(lockCanvas)
              }
  
              //小蓝圆
              if (i == 2) {
                  val lockCanvas = holder.lockCanvas(Rect(60,60,540,540))
                  lockCanvas.drawColor(Color.BLUE)
                  holder.unlockCanvasAndPost(lockCanvas)
              }
  
              //小小白色圆
              if (i == 3) {
                  val lockCanvas = holder.lockCanvas(Rect(200,200,400,400))
                  paint.color = Color.WHITE
                  lockCanvas.drawCircle(300f,300f,100f,paint)
                  holder.unlockCanvasAndPost(lockCanvas)
              }
  
              //小小小文字
              if (i == 4) {
                  val lockCanvas = holder.lockCanvas(Rect(250,250,350,350))
                  paint.color = Color.RED
                  paint.style = Paint.Style.FILL_AND_STROKE
                  paint.textSize = 30f
                  paint.strokeWidth = 0f
                  lockCanvas.drawText("$i",300f,300f,paint)
                  holder.unlockCanvasAndPost(lockCanvas)
              }
              
              Thread.sleep(800)
          }
      }.start()
  }
  ```

- ![tutieshi_640x1422_6s.gif](https://s2.loli.net/2024/02/01/tXUvE6qWLl2kmu3.gif)

##### 绘制现象解释

- **倘若存在三个缓冲区A（屏幕），B（后置缓冲区1），C（后置缓冲区2）**

- **i == 0**

- **lockCanvas：拿出B后置缓冲区1，在B上所在的Rect区域内填充红色，Rect区域外保留当前屏幕绘制内容即（A），此时A为空画布，unlockCanvasAndPost将缓冲区B与A交换，此时屏幕显示红色矩形。队列，B（红）为屏幕，C（空）后置缓冲区1，A（空）后置缓冲区2**

- ```kotlin
  val lockCanvas = holder.lockCanvas(Rect(10,10,600,600))
  lockCanvas.drawColor(Color.RED)
  holder.unlockCanvasAndPost(lockCanvas)
  ```

- **i == 1**

- **lockCanvas:拿出C（空）后置缓冲区1，在C上所在的Rect区域内填充绿色，Rect区域外保留当前屏幕绘制内容即B（红），unlockCanvasAndPost将C（红，绿）后置缓冲区1与B(红)交换，此时屏幕上显示C（红，绿）。队列：C（红，绿），A（空）后置缓冲区1，B（红）后置缓冲区2**

  ```kotlin
  val lockCanvas = holder.lockCanvas(Rect(30,30,570,570))
  lockCanvas.drawColor(Color.GREEN)
  holder.unlockCanvasAndPost(lockCanvas)
  ```

- **i == 2**

- **lockCanvas:拿出A（空）后置缓冲区1，在A上所在的Rect区域内画蓝色矩形，Rect区域外保留当前屏幕绘制内容即C（红，绿），unlockCanvasAndPost将A（红，绿，蓝）与C（红，绿）屏幕交换，此时屏幕显示A（红，绿，蓝）。队列：A（红，绿，蓝），B（红）后置缓冲区1，C（红，绿）后置缓冲区2**

- ```kotlin
  val lockCanvas = holder.lockCanvas(Rect(60,60,540,540))
  lockCanvas.drawColor(Color.BLUE)
  holder.unlockCanvasAndPost(lockCanvas)
  ```

- **i == 3**

- **lockCanvas:拿出B（红）后置缓冲区1，在其Rect区域内叠加绘制白色圆形，Rect区域外保留当前屏幕绘制内容即A（红，绿，蓝），unlockCanvasAndPost将B（红，白色圆）后置缓冲区1与A（红，绿，蓝）屏幕交换，此时屏幕显示B（红，绿，蓝，红，白色圆）。队列：B（红，绿，蓝，红，白色圆）、C（红，绿）后置缓冲区1，A（红，绿，蓝）后置缓冲区2**

- ```kotlin
  val lockCanvas = holder.lockCanvas(Rect(200,200,400,400))
  paint.color = Color.WHITE
  lockCanvas.drawCircle(300f,300f,100f,paint)
  holder.unlockCanvasAndPost(lockCanvas)
  ```

- **i == 4**

- **lockCanvas:拿出C（红，绿）后置缓冲区1，在其Rect区域内绘制文本，Rect区域外保留当前屏幕绘制内容即B（红，绿，蓝，红，白色圆），unlockCanvasAndPost将C（红，绿）后置缓冲区1与B（红，绿，蓝，红，白色圆）屏幕交换，此时屏幕显示C（红，绿，蓝，红，白色圆，文字）。队列：C（红，绿，蓝，红，白色圆，文字）、A（红，绿，蓝）缓冲区1、B（红，绿，蓝，红，白色圆）缓冲区2**

- ```kotlin
  val lockCanvas = holder.lockCanvas(Rect(250,250,350,350))
  paint.color = Color.RED
  paint.style = Paint.Style.FILL_AND_STROKE
  paint.textSize = 30f
  paint.strokeWidth = 0f
  lockCanvas.drawText("$i",300f,300f,paint)
  holder.unlockCanvasAndPost(lockCanvas)
  ```

##### 绘制之前为何要清屏

```kotlin
while (true) {
    val lockCanvas = holder.lockCanvas(Rect(0, 0, 1, 1))
    val clipBoundsRect = lockCanvas.clipBounds
    if (clipBoundsRect.width() == width && clipBoundsRect.height() == height) {
        lockCanvas.drawColor(Color.BLACK)
        holder.unlockCanvasAndPost(lockCanvas)
    } else {
        holder.unlockCanvasAndPost(lockCanvas)
        break
    }
}
```

- **设备三个缓冲区，A，B，C。当lockCanvas指定Rect区域，由于初始状态，系统默认认为整个画布即（0,0，width，height）都为脏区域，所以此时指定lockCanvas（Rect）是无效的，只有当在画布上绘制的时候，才会生效。故需要在一开始就将画布绘制染成黑色**