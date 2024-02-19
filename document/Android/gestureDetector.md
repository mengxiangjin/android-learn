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