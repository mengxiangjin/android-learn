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