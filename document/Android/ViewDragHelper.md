### ViewDragHelper

#### 作用

- 自定义ViewGroup时，对子View进行拖拽的工具类

#### 用法

##### 构造函数

-  **ViewDragHelper.create（View parent，float sensitivity，Callback callback）**

  - **静态函数构造对象**

  - **parent：要拖动子View的父控件、一般为自定义的ViewGroup**

  - **sensitivity：敏感参数。数值越大越敏感。源码中**

    - ```java
      helper.mTouchSlop = (int) (helper.mTouchSlop * (1 / sensitivity));
      ```

    - **mTouchSlop为View前后二次拖动的阈值。当前后二次拖动大于这个阈值时才会被认为是有效拖动**

    - **可见sensitivity越大。阈值越小即越灵敏**

  - **callback：触摸事件的反馈回调**

##### Callback回调

- **tryCaptureView(child: View, pointerId: Int)：Boolean 必须实现**

  - **child：触摸事件的View**
  - **pointerId：触摸事件MotionEvent中的pointerId**
  - **返回值：是否对此View进行各种事件的捕捉**
    - **true：对此View进行触摸事件**
    - **false：触摸事件对此View无效**
- **clampViewPositionHorizontal(child: View, left: Int, dx: Int)：Int**

  - **空实现：return 0**
  - **child：当前跟随手指水平方向移动的View**
  - **left：当前跟随手指水平方向移动到的left（总left）**
  - **dx：当前移动的水平距离（距离上一次移动）**
  - **返回值：返回子View新的坐标的left值**
    - **一般都是return left 即让view跟随手指移动**
    - **默认return 0 即让View水平方向永远贴最左边界**
- **clampViewPositionVertical(child: View, top: Int, dy: Int)：Int**

  - **空实现：return 0**
  - **child：当前跟随手指水平方向移动的View**
  - **top：当前跟随手指垂直方向移动到的top（总top）**
  - **dy：当前移动的垂直距离（距离上一次移动）**
  - **返回值：返回子View新的坐标的Top值**
    - **一般都是return top 即让view跟随手指移动**
    - **默认return 0 即让View垂直方向永远贴最上边界**
- **getViewHorizontalDragRange(child: View)：Int水平方向**
  - **空实现：return 0**
  - **child：当前跟随手指拖动的view**
  - **返回值：是否交由父parent进行拦截事件、dragHelper.shouldInterceptTouchEvent(ev)**
    - **大于0：dragHelper.shouldInterceptTouchEvent(ev) 返回true**

    - **小于等于0：dragHelper.shouldInterceptTouchEvent(ev)返回false**

    - **取决于onInterceptTouchEvent中是否拦截消息（子View可能会存在点击事件，导致消费了Move事件，Move事件传递不到父parent的onTouchEvent中导致viewDragHelper失效，可通过此函数使自定义ViewGroup中拦截器return true，将消息分发给自己的onTouchEvent中）**

- **getViewVerticalDragRange(child: View)：Int 垂直方向**
  - **空实现：return 0**
  - **child：当前跟随手指拖动的view**
  - **返回值：是否交由父parent进行拦截事件、dragHelper.shouldInterceptTouchEvent(ev)**
    - **大于0：dragHelper.shouldInterceptTouchEvent(ev) 返回true**

    - **小于等于0：dragHelper.shouldInterceptTouchEvent(ev)返回false**

    - **取决于onInterceptTouchEvent中是否拦截消息（子View可能会存在点击事件，导致消费了Move事件，Move事件传递不到父parent的onTouchEvent中导致viewDragHelper失效，可通过此函数使自定义ViewGroup中拦截器return true，将消息分发给自己的onTouchEvent中）**
- **onEdgeTouched(edgeFlags: Int, pointerId: Int)：Void**
  - **在ViewGroup的边缘进行点击、触摸时的回调（会被调用多次）**
  - **edgeFlags：触摸位置**
    - **EDGE_LEFT：左边缘**
    - **EDGE_TOP：上边缘**
    - **EDGE_BOTTOM：下边缘**
    - **EDGE_RIGHT：右边缘**

  - **pointerId：触摸事件对应的手指PointerId**

- **onEdgeDragStarted(edgeFlags: Int, pointerId: Int)：Void**
  - **在ViewGroup的边缘进行拖动时的回调（会被调用一次）**
  - **edgeFlags：触摸位置**
    - **EDGE_LEFT：左边缘**
    - **EDGE_TOP：上边缘**
    - **EDGE_BOTTOM：下边缘**
    - **EDGE_RIGHT：右边缘**
  - **pointerId：触摸事件对应的手指PointerId**
- **onEdgeLock(edgeFlags: Int)：Boolean**
  - **在ViewGroup的边缘锁定、多拖动不进行触摸事件处理**
  - **edgeFlags：触摸位置**
    - **EDGE_LEFT：左边缘**
    - **EDGE_TOP：上边缘**
    - **EDGE_BOTTOM：下边缘**
    - **EDGE_RIGHT：右边缘**
  - **返回值：是否对该边缘进行拖动锁定**

##### 消息分发

- 自定义的ViewGroup进行消息拦截，以便于**viewDragHelper**能响应事件

  - ```kotlin
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }
    ```

- 此时在布局中编写，即可随意拖动子View了 继承LinearLayout

  - ```xml
    <com.jin.drag.helper.widgit.DragLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical">
    
        <View
            android:layout_width="150dp"
            android:layout_height="150dp"
            android:layout_gravity="center"
            android:layout_marginTop="30dp"
            android:background="#BF3030" />
    
        <View
            android:layout_width="150dp"
            android:layout_height="150dp"
            android:layout_gravity="center"
            android:layout_marginTop="30dp"
            android:background="#5A1818" />
    
        <View
            android:layout_width="150dp"
            android:layout_height="150dp"
            android:layout_gravity="center"
            android:layout_marginTop="30dp"
            android:background="#D58D8D" />
    
    
    </com.jin.drag.helper.widgit.DragLayout>
    ```

  - ```kotlin
    private lateinit var dragHelper: ViewDragHelper
    
    init {
        dragHelper = ViewDragHelper.create(this, 1f, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return true
            }
    
            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                return left
            }
    
            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                return top
            }
        })
    }
    
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }
    ```

- ![tutieshi_640x1422_7s _1_.gif](https://s2.loli.net/2024/04/11/J5vHbsc1NGoLVyn.gif)

##### onTouchEvent

- 需要在其返回true，倘若返回false，Down事件流完向上传递，后续的Move也不会在传递到此

- ```kotlin
  override fun onTouchEvent(event: MotionEvent): Boolean {
      dragHelper.processTouchEvent(event)
      return true
  }
  ```

- ViewGroup中返回true消费事件，子View添加点击事件，测试效果

  - 第二个方块子view添加点击事件

  - ```kotlin
    binding.viewTwo.setOnClickListener {
        Toast.makeText(this,"click me",Toast.LENGTH_SHORT).show()
    }
    ```

  - ![tutieshi_640x1422_6s _3_.gif](https://s2.loli.net/2024/04/11/mPHzTySBJt4vYb7.gif)

  - 对于第二个View，down事件被此View消费，不管onTouchEvent中retun true、false都不会传递到此，故move事件就不会传递到ViewGroup中的onTouchEvent中，所以下面代码不会被执行，故拖动无效果

    - ```
      dragHelper.processTouchEvent(event)
      ```

  - 如何解决？

    - **Move事件会通过ViewGroup的onInterceptTouchEvent，在此回调中return ture将move事件交由ViewGroup的onTouchEvent处理即可**

    - ```kotlin
      override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
          return dragHelper.shouldInterceptTouchEvent(ev)
      }
      
      override fun onTouchEvent(event: MotionEvent): Boolean {
          dragHelper.processTouchEvent(event)
          return true
      }
      ```

    - **dragHelper.shouldInterceptTouchEvent(ev)的返回值**

      - **CallBack中的回调函数 重写**

      - **当return 值 > 0 时，shouldInterceptTouchEvent(event) return true、否则retur false**

      - **getViewHorizontalDragRange、getViewVerticalDragRange**

      - ```kotlin
        override fun getViewHorizontalDragRange(child: View): Int {
            return 1
        }
        
        override fun getViewVerticalDragRange(child: View): Int {
            return 1
        }
        ```

      - ```kotlin
        init {
            dragHelper = ViewDragHelper.create(this, 1f, object : ViewDragHelper.Callback() {
                override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                    return true
                }
        
                override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                    return left
                }
        
                override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                    return top
                }
        
                override fun getViewHorizontalDragRange(child: View): Int {
                    return 1
                }
        
                override fun getViewVerticalDragRange(child: View): Int {
                    return 1
                }
            })
        }
        ```

      - 添加回调并return 1; 此时shouldInterceptTouchEvent(event) return true即可接收到Move事件

      - ![tutieshi_640x1422_3s _1_.gif](https://s2.loli.net/2024/04/11/bqLSwX5sNf3x4Wo.gif)

##### 边界触摸通知

- Callback中的边界回调函数：

  - **onEdgeTouched(edgeFlags: Int, pointerId: Int)：Void**
    - **在ViewGroup的边缘进行点击、触摸时的回调（会被调用多次）**
    - **edgeFlags：触摸位置**
      - **EDGE_LEFT：左边缘**
      - **EDGE_TOP：上边缘**
      - **EDGE_BOTTOM：下边缘**
      - **EDGE_RIGHT：右边缘**

    - **pointerId：触摸事件对应的手指PointerId**
  - **onEdgeDragStarted(edgeFlags: Int, pointerId: Int)：Void**
    - **在ViewGroup的边缘进行拖动时的回调（会被调用一次）**
    - **edgeFlags：触摸位置**
      - **EDGE_LEFT：左边缘**
      - **EDGE_TOP：上边缘**
      - **EDGE_BOTTOM：下边缘**
      - **EDGE_RIGHT：右边缘**
    - **pointerId：触摸事件对应的手指PointerId**
  - **onEdgeLock(edgeFlags: Int)：Boolean**
    - **在ViewGroup的边缘锁定、多拖动不进行触摸事件处理**
    - **edgeFlags：触摸位置**
      - **EDGE_LEFT：左边缘**
      - **EDGE_TOP：上边缘**
      - **EDGE_BOTTOM：下边缘**
      - **EDGE_RIGHT：右边缘**
    - **返回值：是否对该边缘进行拖动锁定**

- 边界回调函数默认不会触发、需手动调用对边界触摸进行监听

  - ```
    dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT or ViewDragHelper.EDGE_TOP)
    ```

- 边界回调函数针对的是ViewGroup的边缘而非屏幕的边缘，若viewGroup充满整个屏幕，即为屏幕边缘

- 边界回调函数的触发是触发事件开始在边缘触发、而非将View拖动到边缘触发（不会触发回调）。即Down事件开始于边缘

- ```kotlin
  override fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {
      Log.d("TAG", "onEdgeTouched: " + edgeFlags)
      super.onEdgeTouched(edgeFlags, pointerId)
  }
  
  override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
      Log.d("TAG", "onEdgeDragStarted: " + edgeFlags)
      super.onEdgeDragStarted(edgeFlags, pointerId)
  }
  
  override fun onEdgeLock(edgeFlags: Int): Boolean {
      return super.onEdgeLock(edgeFlags)
  }
  ```

- ```kotlin
  dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT or ViewDragHelper.EDGE_TOP)
  ```

- 当触摸左边界、上边界时onEdgeTouched会被触发多次，而onEdgeDragStarted会被触发一次

##### 边界触摸通知结合captureChildView函数使用

- captureChildView（view：View,pointerId： Int）

  - viewDragHelper对象的方法
  - 手动捕捉view对触摸事件的处理
  - view：要捕捉的View
  - PointerId：捕捉view的触摸手指ID

- **captureChildView与Callback中的tryCaptureView**

  - tryCaptureView属于Callback中的回调、而captureChildView是viewDragHelper类对象的方法

  - tryCaptureView回调返回值：Boolean 用来表明对哪些View进行捕捉触摸事件

    - 仅仅对view_one、view_two进行触摸分发

    - ```kotlin
      override fun tryCaptureView(child: View, pointerId: Int): Boolean {
          return child.id == R.id.view_one || child.id == R.id.view_two
      }
      ```

  - captureChildView手动开启触摸分发

- 示例

  - 仅仅对view_one、view_two进行触摸分发

  - ```kotlin
    override fun tryCaptureView(child: View, pointerId: Int): Boolean {
        return child.id == R.id.view_one || child.id == R.id.view_two
    }
    override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                    return left
                }
    
                override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                    return top
                }
    ```

  - 边界拖动时，手动对view_three进行触摸分发

  - ```kotlin
    override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
        Log.d("TAG", "onEdgeDragStarted: " + edgeFlags)
        dragHelper.captureChildView(findViewById(R.id.view_three),pointerId)
        super.onEdgeDragStarted(edgeFlags, pointerId)
    }
    ```

  - 设置触摸边缘（上边缘、下边缘）

  - ```kotlin
    dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT or ViewDragHelper.EDGE_TOP)
    ```

  - 拦截事件

  - ```kotlin
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }
    ```

  - 效果如图：

    - 当触摸ViewThree时，此时并没有效果，因为tryCaptureView中并没有对ViewThree进行触摸反馈
    - 当拖动上边缘或者左边缘时，ViewThree会根据拖动位置进行触摸反馈（onEdgeDragStarted回调中手动调用了captureChildView方法导致）
    - 可以发现：ViewThree始终在与他的相对位置进行拖动，尽管此时触摸拖动在最左方或者最上方

  - ![tutieshi_640x1422_10s.gif](https://s2.loli.net/2024/04/12/9iVXt5xqZlbOTzk.gif)