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