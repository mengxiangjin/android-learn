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
- **getViewHorizontalDragRange（child： View）： Int 水平方向**
  - **返回值大于0：对捕捉事件进行拦截**
    - **onInterceptTouchEvent中 dragHelper.shouldInterceptTouchEvent(ev) 返回true**

  - **返回值小于等于0：对捕捉事件不进行拦截**
    - **onInterceptTouchEvent中 dragHelper.shouldInterceptTouchEvent(ev) 返回false**

  - **当ViewGroup中的子View有点击事件消费时，触摸事件将不会分发到viewGroup的onTouchEvent上、此时仍想要获取触摸事件，则需要在onInterceptTouchEvent中return true即可**

- **getViewVerticalDragRange（child： View）： Int 垂直方向**
  - **返回值大于0：对捕捉事件进行拦截**
    - **onInterceptTouchEvent中 dragHelper.shouldInterceptTouchEvent(ev) 返回true**
  - **返回值小于等于0：对捕捉事件不进行拦截**
    - **onInterceptTouchEvent中 dragHelper.shouldInterceptTouchEvent(ev) 返回false**
  - **当ViewGroup中的子View有点击事件消费时，触摸事件将不会分发到viewGroup的onTouchEvent上、此时仍想要获取触摸事件，则需要在onInterceptTouchEvent中return true即可**
- **onEdgeTouched（edgeFlags: Int, pointerId: Int）：Void**
  - **边界触摸的回调（一次触摸事件可能会回调多次）**
  - **edgeFlags：**
    - **EDGE_LEFT：左边缘**
    - **EDGE_RIGHT：右边缘**
    - **EDGE_TOP：上边缘**
    - **EDGE_BOTTOM：下边缘**

  - **pointerId：触摸事件所对应的手指pointerId**

- **onEdgeDragStarted（edgeFlags: Int, pointerId: Int）：Void**
  - **边界拖动的回调（一次触摸事件只会回调一次）**
  - **edgeFlags：**
    - **EDGE_LEFT：左边缘**
    - **EDGE_RIGHT：右边缘**
    - **EDGE_TOP：上边缘**
    - **EDGE_BOTTOM：下边缘**
  - **pointerId：触摸事件所对应的手指pointerId**
- **onEdgeLock（edgeFlags: Int）：Boolean**
  - **边界锁住**
  - **edgeFlags：**
    - **EDGE_LEFT：左边缘**
    - **EDGE_RIGHT：右边缘**
    - **EDGE_TOP：上边缘**
    - **EDGE_BOTTOM：下边缘**
  - **返回值：是否锁住边界拖动事件的捕捉（onEdgeDragStarted）**
- **onViewReleased（childView：View，speedX：Float，speedY：Float）: Void**
  - **子View手指离开了屏幕（即UP事件触发）**
  - **childView：子View**
  - **speedX：离开屏幕时的X方向的速度**
  - **speedY：离开屏幕时的Y方向的速度**
- **onViewPositionChanged（changedView：View，left：Int，top：Int，dx：Int，dy：Int）**
  - **触摸反馈的view视图变化回调**
  - **changedView：变化的view**
  - **left：新的left**
  - **top：新的top**
  - **dx：变化的x距离**
  - **dy：变化的y距离**



##### 使用

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

      - getViewHorizontalDragRange、getViewVerticalDragRange

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

##### 边界触摸判断

- CallBack回调函数中提供了一系列边界触摸的回调

- **onEdgeTouched（edgeFlags: Int, pointerId: Int）：Void**

  - **边界触摸的回调（一次触摸事件可能会回调多次）**
  - **edgeFlags：**
    - **EDGE_LEFT：左边缘**
    - **EDGE_RIGHT：右边缘**
    - **EDGE_TOP：上边缘**
    - **EDGE_BOTTOM：下边缘**
  - **pointerId：触摸事件所对应的手指pointerId**

- **onEdgeDragStarted（edgeFlags: Int, pointerId: Int）：Void**

  - **边界拖动的回调（一次触摸事件只会回调一次）**
  - **edgeFlags：**
    - **EDGE_LEFT：左边缘**
    - **EDGE_RIGHT：右边缘**
    - **EDGE_TOP：上边缘**
    - **EDGE_BOTTOM：下边缘**
  - **pointerId：触摸事件所对应的手指pointerId**

- **onEdgeLock（edgeFlags: Int）：Boolean**

  - **边界锁住**
  - **edgeFlags：**
    - **EDGE_LEFT：左边缘**
    - **EDGE_RIGHT：右边缘**
    - **EDGE_TOP：上边缘**
    - **EDGE_BOTTOM：下边缘**
  - **返回值：是否锁住边界拖动事件的捕捉（onEdgeDragStarted）**

- 默认三个函数都不会被触发回调，需手动设置监听

  - 上边界、右边界触摸捕捉

  - ```kotlin
    dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT or ViewDragHelper.EDGE_TOP)
    ```

- 当对边界进行触摸不住时、onEdgeTouched可能会被调用多次，而onEdgeDragStarted只会被调用一次
- 常常配合CaptureChildView进行事件捕捉

##### CaptureChildView（view： View，pointerId：Int）

- ViewDragHelper类对象方法

- 手动开启对view进行事件捕捉

- view：开启触摸捕捉的view

- pointerId：捕捉对应手指的pointerId

- Callback当中存在 tryCaptureView(child: View, pointerId: Int)：Boolean回调返回对哪些View进行触摸捕捉、而CaptureChildView函数可临时开启一次（up结束即取消）对某子View的触摸捕捉（尽管tryCaptureView中没有对其进行触摸捕捉）

- ```xml
  <com.jin.drag.helper.widgit.DragLayout
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:orientation="vertical">
  
      <View
          android:id="@+id/view_one"
          android:layout_width="150dp"
          android:layout_height="150dp"
          android:layout_gravity="center"
          android:layout_marginTop="30dp"
          android:background="#BF3030" />
  
      <View
          android:id="@+id/view_two"
          android:layout_width="150dp"
          android:layout_height="150dp"
          android:layout_gravity="center"
          android:layout_marginTop="30dp"
          android:background="#5A1818" />
  
      <View
          android:id="@+id/view_three"
          android:layout_width="150dp"
          android:layout_height="150dp"
          android:layout_gravity="center"
          android:layout_marginTop="30dp"
          android:background="#D58D8D" />
  
  
  </com.jin.drag.helper.widgit.DragLayout>
  ```

- tryCaptureView中只对第一个、第二个view进行事件捕捉

- setEdgeTrackingEnabled监听左边缘与上边缘触摸

- 当拖动回调onEdgeDragStarted触发手动调用captureChildView对第三个view进行触摸捕捉

- ```kotlin
  dragHelper = ViewDragHelper.create(this, 1f, object : ViewDragHelper.Callback() {
      override fun tryCaptureView(child: View, pointerId: Int): Boolean {
          return child.id == R.id.view_one || child.id == R.id.view_two
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
  
      override fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {
          Log.d("TAG", "onEdgeTouched: " + edgeFlags)
          super.onEdgeTouched(edgeFlags, pointerId)
      }
  
      override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
          Log.d("TAG", "onEdgeDragStarted: " + edgeFlags)
          dragHelper.captureChildView(findViewById(R.id.view_three),pointerId)
          super.onEdgeDragStarted(edgeFlags, pointerId)
      }
  
      override fun onEdgeLock(edgeFlags: Int): Boolean {
          return super.onEdgeLock(edgeFlags)
      }
  })
  
  dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT or ViewDragHelper.EDGE_TOP)
  ```

- 事件拦截

- ```kotlin
  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
      return dragHelper.shouldInterceptTouchEvent(ev)
  }
  
  override fun onTouchEvent(event: MotionEvent): Boolean {
      dragHelper.processTouchEvent(event)
      return true
  }
  ```

- ![thc6n-bw5of.gif](https://s2.loli.net/2024/04/12/yB7bVj6Hd1n2aQm.gif)

- 注意：当一次边缘触摸结束时，第三个view触摸捕捉结束，直到再次出现边缘捕捉。边缘捕捉只会移动子View的相对位置，而不会实时跟随手指移动

##### onEdgeLocked（edgeFlags: Int）： Boolean

- Callback中的回调函数

- 是否锁住某边缘，依据条件禁止拖动（即不会触发onEdgeDragStarted函数）

- 返回值：true --上锁 false-解锁

- ```xaml
  <com.jin.drag.helper.widgit.DragLayout
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:orientation="vertical">
  
      <View
          android:id="@+id/view_one"
          android:layout_width="150dp"
          android:layout_height="150dp"
          android:layout_gravity="center"
          android:layout_marginTop="30dp"
          android:background="#BF3030" />
  
      <View
          android:id="@+id/view_two"
          android:layout_width="150dp"
          android:layout_height="150dp"
          android:layout_gravity="center"
          android:layout_marginTop="30dp"
          android:background="#5A1818" />
  
      <View
          android:id="@+id/view_three"
          android:layout_width="150dp"
          android:layout_height="150dp"
          android:layout_gravity="center"
          android:layout_marginTop="30dp"
          android:background="#D58D8D" />
  
  
  </com.jin.drag.helper.widgit.DragLayout>
  ```

  - onEdgeLock将左边缘锁住（return true）同时监听左边缘与上边缘

  - ```kotlin
    dragHelper = ViewDragHelper.create(this, 1f, object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child.id == R.id.view_one || child.id == R.id.view_two
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
    
        override fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {
            Log.d("TAG", "onEdgeTouched: " + edgeFlags)
            super.onEdgeTouched(edgeFlags, pointerId)
        }
    
        override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
            Log.d("TAG", "onEdgeDragStarted: " + edgeFlags)
            dragHelper.captureChildView(findViewById(R.id.view_three),pointerId)
            super.onEdgeDragStarted(edgeFlags, pointerId)
        }
    
        override fun onEdgeLock(edgeFlags: Int): Boolean {
            Log.d("TAG", "onEdgeLock: " + edgeFlags)
            if (edgeFlags == ViewDragHelper.EDGE_LEFT) {
                return true
            }
            return false
        }
    })
    
    dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT or ViewDragHelper.EDGE_TOP)
    ```

  - ViewGroup中拦截事件

  - ```kotlin
    override fun onInterceptHoverEvent(event: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(event)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }
    ```

  - 效果

    - 当在左边缘触摸拖动（**上下拖动时**）时：
      - onEdgeTouched （可能多次触发）
      - onEdgeLock（触发一次）：左边缘return true、
      - onEdgeDragStarted：并不会触发（被上锁）
    - 当在左边缘触摸拖动（**左右拖动时**）时：
      - onEdgeTouched （可能多次触发）
      - onEdgeLock（触发一次）：左边缘return true、
      - onEdgeDragStarted：会触发一次（即使被上锁）
    - 为何锁定左边缘，当左右拖动时还会响应到拖动事件onEdgeDragStarted、而上下拖动时不会响应
      - onEdgeLock触发前提：
        - 针对左边缘拖动（上边缘、下边缘、右边缘同理）
          - 当拖动的横向距离 < 拖动的纵向距离 * 0.5 此时会被系统认为左边缘的上下拖动，此时会触发 onEdgeLock函数，判断是否锁住了左边缘，进而不会触发onEdgeDragStarted
          - 当拖动的横向距离 < 拖动的纵向距离 * 0.5 此时会被系统认为左边缘的左右拖动，此时不会触发 onEdgeLock函数，故会触发onEdgeDragStarted
        - 为什么要这样设计？
          - 一般对于左边缘来说，偏向用户是可以左右拖动的，当在左边缘出现上下拖动这下反常行为，系统会回调onEdgeLock判断是否该回调onEdgeDragStarted
          - 同理对于上边缘来说，偏向用户是可以上下拖动的，当在上边缘出现左右拖动这一反常行为，系统会回调onEdgeLock判断是否该回调onEdgeDragStarted
          - 但是对于左边缘来说，用户进行左右拖动，此时是不会触发onEdgeLock的，被认为是正常触摸行为，所以仍然会回调onEdgeDragStarted

##### onViewReleased（childView：View，speedX：Float，speedY：Float）: Void

- **CallBack中的回调函数**

- **子View手指离开了屏幕（即UP事件触发）**
- **childView：子View**
- **speedX：离开屏幕时的X方向的速度**
- **speedY：离开屏幕时的Y方向的速度**

##### smoothSlideViewTo（view：View，left：Int，top：Int）：Boolean

- **ViewDragHelper类对象的smoothSlideViewTo方法**

- **将view滑动到left、top位置**

- **view：将要滑动的view**

- **left：拖动的目标位置left**

- **top：拖动的目标位置top**

- **返回值：当前是否需要继续移动，如果移动未结束需要继续则返回true，否则返回false**

- **smoothSlideViewTo同Scroller相同，调用后，需要重绘界面让移动生效 invalidate()**

- **重绘后会回调到自定义ViewGroup的computeScroll 函数回调上，在其中做循环重绘的步骤（移动）**

- **onViewReleased中对view_three调用smoothSlideViewTo 滚动到与view_one相同位置**

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
  
          override fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {
              super.onEdgeTouched(edgeFlags, pointerId)
          }
  
          override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
              dragHelper.captureChildView(findViewById(R.id.view_three),pointerId)
              super.onEdgeDragStarted(edgeFlags, pointerId)
          }
  
          override fun onEdgeLock(edgeFlags: Int): Boolean {
              if (edgeFlags == ViewDragHelper.EDGE_LEFT) {
                  return true
              }
              return false
          }
  
          override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
              if (releasedChild.id == R.id.view_three) {
                  val viewOne = findViewById<View>(R.id.view_one)
                  dragHelper.smoothSlideViewTo(releasedChild,viewOne.left,viewOne.top)
                  invalidate()
              }
              super.onViewReleased(releasedChild, xvel, yvel)
          }
      })
  ```

  - 重写computeScroll函数，循环重绘（移动）dragHelper.continueSettling(true)

  - ```kotlin
    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            invalidate()
        }
        super.computeScroll()
    }
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

  - 效果：当拖动view_three后放开的时候view_three会移动到与view_one相同的位置![zxv93-egu7a.gif](https://s2.loli.net/2024/04/13/7RGfYTkpOQ8FsB5.gif)



##### settleCapturedViewAt（left：Int，top：Int）

-  	`**ViewDragHelper类对象的settleCapturedViewAt方法**
- **left：拖动的目标位置left**
- **top：拖动的目标位置top**
- **同smoothSlideViewTo作用类似，将view移动到目标位置**
- **没有子View参数，该函数只能在onViewReleased被调用，只能将当前release的view对象进行移动**
- **不同于smoothSlideViewTo，settleCapturedViewAt移动开始的起始速度为手指脱离屏幕时的速度，而smoothSlideViewTo移动开始的起始速度为0**

#### 仿照QQ侧滑

- ![tutieshi_640x1422_8s _2_.gif](https://s2.loli.net/2024/04/15/b8oS9TcpzuGAvHa.gif)

- 利用ViewDragHelper对子view进行触摸反馈

  - 主页MainView（捕捉触摸事件）

  - 侧边栏SlideView（存在点击事件），点击菜单item，MainView中text发生改变

  - 对mainView触摸事件进行捕捉

  - ```kotlin
    override fun tryCaptureView(child: View, pointerId: Int): Boolean {
        return child == mainView
    }
    ```

  - 捕捉事件，对应left，top进行限制。top始终在最上边缘，left根据当前slideWidth进行判断

  - ```kotlin
    override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
        if (left < 0) {
            return 0
        }
        return min(slideWidth,left)
    }
    
    override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
        return 0
    }
    ```

  - mainView触摸结束后，根据当前left，决定展示或是收回

  - ```kotlin
    override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
        if (releasedChild == mainView) {
            if (mainView!!.left <= slideWidth / 2) {
                dragHelper.smoothSlideViewTo(releasedChild,0,0)
            } else {
                dragHelper.smoothSlideViewTo(releasedChild,slideWidth,0)
            }
            invalidate()
        }
        super.onViewReleased(releasedChild, xvel, yvel)
    }
    ```

  - ```kotlin
    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            invalidate()
        }
        super.computeScroll()
    }
    ```

  - 当view位置发生改变时的回调，设置mainView的缩放相关以及SlideView的缩放与平移相关

  - ```kotlin
    override fun onViewPositionChanged(
        changedView: View,
        left: Int,
        top: Int,
        dx: Int,
        dy: Int
    ) {
        super.onViewPositionChanged(changedView, left, top, dx, dy)
        val scale = mainView!!.left / 1f / slideWidth
        setScale(scale)
    }
    ```

  - ```kotlin
    fun setScale(showPercent: Float) {
        mainView!!.scaleX = (1 - 0.2 * showPercent).toFloat()
        mainView!!.scaleY = (1 - 0.2 * showPercent).toFloat()
    
        slideView!!.scaleX = (0.5 + 0.5 * showPercent).toFloat()
        slideView!!.scaleY = (0.5 + 0.5 * showPercent).toFloat()
        slideView!!.translationX = -slideWidth / 2 + slideWidth / 2 * showPercent
    }
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

  - 提供接口向ViewGroup中添加MainView以及SlideView

  - ```kotlin
    fun addCustomView(mainView: View,mainViewParams: ViewGroup.LayoutParams,slideView: View,slideViewParams: ViewGroup.LayoutParams) {
        this.slideView = slideView
        this.mainView = mainView
        this.slideWidth = slideViewParams.width
        addView(slideView,slideViewParams)
        addView(mainView,mainViewParams)
    }
    ```

  - SlideFrameLayout

  - ```kotlin
    class SlideFrameLayout @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defInt: Int = 0
    ) :
        FrameLayout(context, attributeSet, defInt) {
    
        private var mainView: View? = null
        private var slideView: View? = null
    
        private var slideWidth = 0
        private lateinit var dragHelper: ViewDragHelper
    
    
        init {
            dragHelper = ViewDragHelper.create(this,object : ViewDragHelper.Callback() {
                override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                    return child == mainView
                }
    
                override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                    if (left < 0) {
                        return 0
                    }
                    return min(slideWidth,left)
                }
    
                override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                    return 0
                }
    
                override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                    if (releasedChild == mainView) {
                        if (mainView!!.left <= slideWidth / 2) {
                            dragHelper.smoothSlideViewTo(releasedChild,0,0)
                        } else {
                            dragHelper.smoothSlideViewTo(releasedChild,slideWidth,0)
                        }
                        invalidate()
                    }
                    super.onViewReleased(releasedChild, xvel, yvel)
                }
    
                override fun onViewPositionChanged(
                    changedView: View,
                    left: Int,
                    top: Int,
                    dx: Int,
                    dy: Int
                ) {
                    super.onViewPositionChanged(changedView, left, top, dx, dy)
                    val scale = mainView!!.left / 1f / slideWidth
                    setScale(scale)
                }
            })
        }
    
        fun setScale(showPercent: Float) {
            mainView!!.scaleX = (1 - 0.2 * showPercent).toFloat()
            mainView!!.scaleY = (1 - 0.2 * showPercent).toFloat()
    
            slideView!!.scaleX = (0.5 + 0.5 * showPercent).toFloat()
            slideView!!.scaleY = (0.5 + 0.5 * showPercent).toFloat()
            slideView!!.translationX = -slideWidth / 2 + slideWidth / 2 * showPercent
        }
    
    
        fun addCustomView(mainView: View,mainViewParams: ViewGroup.LayoutParams,slideView: View,slideViewParams: ViewGroup.LayoutParams) {
            this.slideView = slideView
            this.mainView = mainView
            this.slideWidth = slideViewParams.width
            addView(slideView,slideViewParams)
            addView(mainView,mainViewParams)
        }
    
        override fun computeScroll() {
            if (dragHelper.continueSettling(true)) {
                invalidate()
            }
            super.computeScroll()
        }
    
        override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
            return dragHelper.shouldInterceptTouchEvent(ev)
        }
    
        override fun onTouchEvent(event: MotionEvent): Boolean {
            dragHelper.processTouchEvent(event)
            return true
        }
    
        fun resetMainView() {
            if (mainView == null) return
            dragHelper.smoothSlideViewTo(mainView!!,0,0)
            invalidate()
        }
    ```

  - SlideFrameLayoutActivity

  - ```kotlin
    class SlideFrameLayoutActivity : AppCompatActivity() {
    
        private lateinit var binding: ActivitySlideBinding
    
        private lateinit var tvMainContent: TextView
    
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivitySlideBinding.inflate(layoutInflater)
            setContentView(binding.root)
    
            val mainView = layoutInflater.inflate(R.layout.main_view, null, false)
            val slideView = layoutInflater.inflate(R.layout.slide_view, null, false)
    
            tvMainContent = mainView.findViewById(R.id.tv_content)
    
            slideView.findViewById<TextView>(R.id.tv_one).setOnClickListener {
                tvMainContent.text = (it as TextView).text
                binding.slideLayout.resetMainView()
            }
    
            slideView.findViewById<TextView>(R.id.tv_two).setOnClickListener {
                tvMainContent.text = (it as TextView).text
                binding.slideLayout.resetMainView()
            }
    
            slideView.findViewById<TextView>(R.id.tv_three).setOnClickListener {
                tvMainContent.text = (it as TextView).text
                binding.slideLayout.resetMainView()
            }
    
            val mainViewLayoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
    
            val slideLayoutParams = ViewGroup.LayoutParams(
                resources.getDimension(R.dimen.dp_160).toInt(),
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            binding.slideLayout.addCustomView(
                mainView,
                mainViewLayoutParams,
                slideView,
                slideLayoutParams
            )
        }
    ```

  - activity_slide.xml

  - ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@drawable/bg"
        tools:context=".MainActivity">
    
        <com.jin.drag.helper.widgit.SlideFrameLayout
            android:id="@+id/slide_layout"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
    
    
    </androidx.constraintlayout.widget.ConstraintLayout>
    ```

  - slide_view

  - ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="200dp"
        android:layout_height="match_parent"
        android:background="#00ffffff"
        android:gravity="center_horizontal"
        android:orientation="vertical">
    
        <TextView
            android:id="@+id/tv_one"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="20dp"
            android:text="One"
            android:textColor="@color/black"
            android:textSize="22sp"
            android:textStyle="bold" />
    
    
        <TextView
            android:id="@+id/tv_two"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="20dp"
            android:text="two"
            android:textColor="@color/black"
            android:textSize="22sp"
            android:textStyle="bold" />
    
        <TextView
            android:id="@+id/tv_three"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="20dp"
            android:text="three"
            android:textColor="@color/black"
            android:textSize="22sp"
            android:textStyle="bold" />
    
    </LinearLayout>
    ```

  

