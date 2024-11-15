### 多点触控

#### 概览

##### 区分

- 单指触控与多指都会触发onTouchEvent函数
- 单指通过event.action获取手势
- 多指通过event.actionMasked获取手势
- 多指的actionMasked：
  - MotionEvent.ACTION_DOWN：第一个手指按下
  - MotionEvent.ACTION_POINTER_DOWN：又一个手指按下
  - MotionEvent.ACTION_UP：最后一个手指抬起
  - MotionEvent.ACTION_POINTER_UP：又一个手指抬起
- 单指多指对ACTION_MOVE是相同的，多指对MOVE事件没有自动区分，但是我们可以通过代码去判断MOVE是哪个手指（后续）

##### event.actionMasked

- 如图，依次按下1,2,3 然后抬起1,3,2![tutieshi_576x1280_6s.gif](https://s2.loli.net/2024/04/08/eTGdMgIma2X8RCr.gif)

- ```kotlin
  override fun onTouchEvent(event: MotionEvent): Boolean {
      when(event.actionMasked) {
          MotionEvent.ACTION_POINTER_DOWN -> {
              Log.d("TAG---", "ACTION_POINTER_DOWN ")
          }
          MotionEvent.ACTION_DOWN -> {
              Log.d("TAG---", "ACTION_DOWN ")
          }
          MotionEvent.ACTION_UP -> {
              Log.d("TAG---", "ACTION_UP ")
          }
          MotionEvent.ACTION_POINTER_UP -> {
              Log.d("TAG---", "ACTION_POINTER_UP ")
          }
      }
      return true
  }
  ```

- ACTION_DOWN -> ACTION_POINTER_DOWN -> ACTION_POINTER_DOWN -> ACTION_POINTER_UP -> ACTION_POINTER_UP -> ACTION_UP

##### PointerId，PointerIndex

- 一个触摸点对应一个Pointer，一个MotionEvent可能包含多个Pointer
- 一个Pointer包含PointerId、PointerIndex
- **PointerId**：一个手指按下，分配的唯一ID，此ID是不会被改变的
- **PointerIndex**：一个手指按下，当前手指的Index，此ID是会发生变化的
- ![image.png](https://s2.loli.net/2024/04/08/HZJt3BuK6CelmdI.png)

- **getActionIndex：获取当前手指的pointerIndex**
- **getPointerId（pointerIndex）：根据pointerIndex获取PointerId**
- **getPointCount：当前屏幕上手指个数，通过循环遍历,结合getPointId（）可获取屏幕上的手指PointerId**

- **findPointerIndex(PointerId)：根据PointerId获取PointerIndex**

##### 第二个手指手势绘制轨迹

- ![tutieshi_640x1422_8s _1_.gif](https://s2.loli.net/2024/04/08/FyLYMNEc83xsVQd.gif)

- 第一个手指不绘制、监听第二个手指移动按下进行绘制

- ```kotlin
  //当前屏幕是否存在第二根手指
  private var hasSecondPointer = false
  //第二个手指触摸点
  private var secondPoint = PointF()
  ```

- 存在第二个手指，根据secondPoint绘制圆

- ```kotlin
  override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)
      if (hasSecondPointer) {
          canvas.drawCircle(secondPoint.x, secondPoint.y, 20f, paint)
      }
  }
  ```

- 在onTouchEvent进行监听

  - 对Down事件：不需要处理逻辑，第一个手指按下

  - 对ACTION_POINTER_DOWN事件：监听是否是第二个手指

    - **无论后续多少手指按下抬起，第二个手指的pointId永远等于1，可以通过此判断**

    - action.getPointerId(pointerIndex)获取pointerId

    - event.actionIndex获取pointerIndex

    - ```kotlin
      MotionEvent.ACTION_POINTER_DOWN -> {
          Log.d("TAG---", "ACTION_POINTER_DOWN ")
          if (event.getPointerId(actionIndex) == 1) {
              //第二个手指down
              hasSecondPointer = true
              secondPoint.set(event.getX(actionIndex),event.getY(actionIndex))
          }
      }
      ```

  - 对ACTION_UP事件：将hasSecondPointer置为false即可
  - 对ACTION_POINTER_UP事件：判断是不是第二根手指抬起，若是，将hasSecondPointer置为false即可

- ```kotlin
  override fun onTouchEvent(event: MotionEvent): Boolean {
      val actionIndex = event.actionIndex
      when (event.actionMasked) {
          MotionEvent.ACTION_DOWN -> {
              Log.d("TAG---", "ACTION_DOWN ")
          }
  
          MotionEvent.ACTION_POINTER_DOWN -> {
              Log.d("TAG---", "ACTION_POINTER_DOWN ")
              if (event.getPointerId(actionIndex) == 1) {
                  //第二个手指down
                  hasSecondPointer = true
                  secondPoint.set(event.getX(actionIndex),event.getY(actionIndex))
              }
          }
  
          MotionEvent.ACTION_MOVE -> {
              if (hasSecondPointer) {
                  try {
                      val pointerIndex = event.findPointerIndex(1)
                      secondPoint.set(event.getX(pointerIndex),event.getY(pointerIndex))
                  }catch (e: Exception) {
                      hasSecondPointer = false
                  }
              }
          }
  
          MotionEvent.ACTION_UP -> {
              Log.d("TAG---", "ACTION_UP ")
              hasSecondPointer = false
          }
  
          MotionEvent.ACTION_POINTER_UP -> {
              Log.d("TAG---", "ACTION_POINTER_UP ")
              if (event.getPointerId(actionIndex) == 1) {
                  //
                  hasSecondPointer = false
              }
          }
      }
      invalidate()
      return true
  }
  ```

##### getAcionIndex

- 在onTouchEvent中event.getActionIndex()可以获取到当前手势的pointerIndex
- 此方法只对down、up、pointer_down、pointer_up事件有效，而对Move事件无效，即返回值永远为0

##### getX与getX（pointerIndex）

- getX():获取当前活动手指的手势信息
- getX(pointerIndex):根据pointerIndex获取手指的手势信息

##### 可双指缩放的TextView

- ![tutieshi_432x960_7s.gif](https://s2.loli.net/2024/04/08/uNgw7XchqJzlWB6.gif)

- 缩放比例：通过move时计算双指距离与双指down下（缩放前）距离的比值 * textSize(当前的文本大小即可)

- ```kotlin
  //当前屏幕手指的数量
  private var pointerCounts = 0
  //双指缩放前的距离
  private var moveBeforeDistance = 0f
  //当前的文本大小
  private var textSize = 0f
  ```

- ACTION_DOWN时：将变量置为0，增加手指数量

- ACTION_POINTER_DOWN时：计算二个手指缩放前的距离

- ACTION_MOVE时：计算二个手指缩放后的距离，计算比值，改变textSize即可

- ```kotlin
  override fun onTouchEvent(event: MotionEvent): Boolean {
      if (textSize == 0f) {
          textSize = getTextSize()
      }
      when (event.actionMasked) {
          MotionEvent.ACTION_DOWN -> {
              pointerCounts++
              moveBeforeDistance = 0f
          }
          MotionEvent.ACTION_POINTER_DOWN -> {
              pointerCounts++
              //计算二个手指伸缩之前的距离
              moveBeforeDistance = calDistance(event)
          }
  
          MotionEvent.ACTION_POINTER_UP -> {
  
          }
  
          MotionEvent.ACTION_UP -> {
              moveBeforeDistance = 0f
          }
  
          MotionEvent.ACTION_MOVE -> {
              if (pointerCounts >= 2) {
                  val newDistance = calDistance(event)
                  if (newDistance != -1f && abs(newDistance - moveBeforeDistance) > 50) {
                      zoom(newDistance / moveBeforeDistance)
                      moveBeforeDistance = newDistance
                  }
              }
          }
      }
      return true
  }
  
  private fun zoom(zoom: Float) {
      textSize *= zoom
      setTextSize(TypedValue.COMPLEX_UNIT_SP,textSize)
  }
  
  private fun calDistance(event: MotionEvent): Float {
      try {
          val x = event.getX(0) - event.getX(1)
          val y = event.getY(0) - event.getY(1)
          return sqrt(x * x + y * y).toFloat()
      }catch (e: Exception) {
  
      }
      return -1f
  }
  ```

### GestureDetector

- **手势检测监听类**

#### GestureDetector.OnGestureListener

- **onDown()：按下屏幕**
- **onShowPress()：按下超过某个瞬间值，且此期间内未进行拖动或者松开**
- **onLongPress()：长按超过某个时间段**
- **onSingleTabUp()：单一的点击抬起操作**
- **onScroll(MotionEvent,MotionEvent,distanceX,distanceY)：滚动，拖动。参数一为按下的事件，参数二为拖动的事件**
- **onFiling(MotionEvent,MotionEvent,velocityX,velocityY)：滑屏结束，最后触发**
- **滑动屏幕：onDown -> onScroll -> onScroll -> onScroll -> onFiling**

#### 监听手势

```kotlin
private var gestureListener = object : GestureDetector.OnGestureListener {
    override fun onDown(e: MotionEvent): Boolean {
        Log.d("TAG", "onDown: ")
        return true
    }

    override fun onShowPress(e: MotionEvent) {
        Log.d("TAG", "onShowPress: ")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        Log.d("TAG", "onSingleTapUp: ")
        return true
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        Log.d("TAG", "onScroll: ")
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        Log.d("TAG", "onLongPress: ")
    }

    override fun onFling(
        e1: MotionEvent,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        Log.d("TAG", "onFling: ")
        return true
    }
}
```

```kotlin
val gestureDetector = GestureDetector(this, gestureListener)

binding.tvExample.setOnTouchListener { v, event ->
    gestureDetector.onTouchEvent(event)
}
```

#### OnDoubleTapListener

- **onSingleTapConfirmed()：单击确认，判断是单击事件而不是双击事件。如果用户连续单击二次，即双击事件，如果用户单击一次，在一顿事件内未进行第二次单击，则onSingleTapConfirmed触发**

- **onDoubleTab()：双击事件**

- **onDoubleTabEvent()：双击事件之间触发的事件**

- ```kotlin
  val gestureDetector = GestureDetector(this, gestureListener)
  
  gestureDetector.setOnDoubleTapListener(object : OnDoubleTapListener {
      override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
          Log.d("TAG", "onSingleTapConfirmed: ")
          return true
      }
  
      override fun onDoubleTap(e: MotionEvent): Boolean {
          Log.d("TAG", "onDoubleTap: ")
          return true
      }
  
      override fun onDoubleTapEvent(e: MotionEvent): Boolean {
          Log.d("TAG", "onDoubleTapEvent: ")
          return true
      }
  
  })
  ```

#### SimpleOnGestureListener

- **GestureDetector.OnGestureListener与OnDoubleTapListener结合体**
- **SimpleOnGestureListener可根据需要重写方法即可，而OnGestureListener、OnDoubleTapListener则必须全部重写**
- **SimpleOnGestureListener存在OnGestureListener类的方法与OnDoubleTapListener的方法，用法一致**