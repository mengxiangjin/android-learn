### 事件分发

#### 概览

##### 函数

- dispatchTouchEvent（MotionEvent）：View、ViewGroup、Activity
- onInterceptEvent（MotionEvent）：ViewGroup独有
- ouchEvent（MotionEvent）：View、ViewGroup、Activity

#### 自定义View调试

- 自定义ViewGroup1：FirstViewGroup

- 当点击ViewGroup的时候：**dispatchTouchEvent ->onInterceptTouchEvent ->onTouchEvent**

- ```kotlin
  override fun onTouchEvent(event: MotionEvent): Boolean {
      Log.d("TAG---FirstViewGroup", "onTouchEvent: ")
      return super.onTouchEvent(event)
  }
  
  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
      Log.d("TAG---FirstViewGroup", "dispatchTouchEvent: ")
      return super.dispatchTouchEvent(ev)
  }
  
  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
      Log.d("TAG---FirstViewGroup", "onInterceptTouchEvent: ")
      return super.onInterceptTouchEvent(ev)
  }
  ```

- 自定义View时

- 当点击View时：**dispatchTouchEvent -> onTouchEvent**

- ```kotlin
  override fun onTouchEvent(event: MotionEvent): Boolean {
      Log.d("TAG---FirstView", "onTouchEvent: ")
      return super.onTouchEvent(event)
  }
  
  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
      Log.d("TAG---FirstView", "dispatchTouchEvent: ")
      return super.dispatchTouchEvent(ev)
  }
  ```

#### 事件分发（只是针对Action_Down事件）

##### dispatchTouchEvent：默认返回值super.dispatchTouchEvent  不拦截

- 如下：FirstViewGroup（最外层）、SecondViewGroup（中间）、FirstView（最上层）

- ```xml
  <com.jin.touch.widgit.FirstViewGroup
      android:layout_width="200dp"
      android:layout_height="200dp"
      android:background="#D10E0E"
      android:gravity="center"
      app:layout_constraintStart_toStartOf="parent"
      app:layout_constraintEnd_toEndOf="parent"
      app:layout_constraintTop_toTopOf="parent"
      app:layout_constraintBottom_toBottomOf="parent">
  
      <com.jin.touch.widgit.SecondViewGroup
          android:layout_width="100dp"
          android:layout_height="100dp"
          android:background="#4C2929"
          android:gravity="center">
  
  
          <com.jin.touch.widgit.FirstView
              android:layout_width="wrap_content"
              android:layout_height="wrap_content"
              android:text="我在最上层"
              android:gravity="center"
              android:textColor="@color/white"
              android:layout_gravity="center"
             />
  
      </com.jin.touch.widgit.SecondViewGroup>
  
  </com.jin.touch.widgit.FirstViewGroup>
  ```

- **当点击FirstView时，返回supr.xxxxx，事件分发：**
  
  - FirstViewGroup（disPathchTouchEvent） ---> FirstViewGroup（onInterceptTouchEvent ） --->SecondViewGroup（disPathchTouchEvent） ---> SecondViewGroup（onInterceptTouchEvent ）--->FirstView（disPathchTouchEvent） ---> FirstView（onTouchEvent） ---> SecondViewGroup（onTouchEvent） --->FirstViewGroup（onTouchEvent）![image.png](https://s2.loli.net/2024/04/02/nGUk9LAIPm6S2Hf.png)

- **当在dispatchEvent中return true时，点击FirstView，事件分发：**
  - FirstViewGroup（disPathchTouchEvent）
  - 在Activity中dispathTouchEvent返回ture：消息停止传递，传递到该Activity终止
  - 在ViewGroup中dispatchTouchEvent返回ture：消息通过activity--->viewGroup终止
  - 在View中dispatchTouchEvent返回ture：消息通过activity--->viewGroup--->view终止
  - **无论在哪个控件的dispatchTouchEvent返回true：该事件都不会向下传递，即消息传递到此截止**
  - ![image.png](https://s2.loli.net/2024/04/02/vsiQexlXMYzJhgj.png)

- 当在dispatchEvent中return false时，点击FirstView，事件分发：

  - Activity（dispatchTouchEvent） --->ViewGroup（dispatchTouchEvent） --->ViewGroup（onInterceptTouchEvent）--->View(dispatchTouchEvent)--->ViewGroup(onTouchEvent) --->Activity(onTouchEvent)
  - ![image.png](https://s2.loli.net/2024/04/02/Au9fMpOcNyUQV1Z.png)

  - **在控件的dispatchTouchEvent返回false时，事件会在dispatchTouchEvent函数中停止传递，转而分发给控件的父View的onTouchEvent中，层层向上传递。onTouchEvent与dispatchTouchEvent传递正好相反，onTouchEvent子View向上传递给父view，dispatchTouchEvent是从父view向下传递给子View**

##### onTouchEvent：默认返回值false  不拦截

- 当在View的TouchEvent中返回true，点击FirstView，事件分发：
- Activity（dispatchTouchEvent） --->ViewGroup（dispatchTouchEvent） --->ViewGroup（onInterceptTouchEvent）--->View(dispatchTouchEvent)--->View(onTouchEvent)![image.png](https://s2.loli.net/2024/04/02/gzBsaUXMG8fYDm1.png)
- **任何控件的dispatchTouchEvent返回true或者onTouchEvent返回true都会使事件传递截止**

##### onInterceptTouchEvent：ViewGroup独有

- 是否需要拦截该消息交由自己来处理
- 当在ViewGroup的onInterceptTouchEvent返回true时，点击FirstView，事件分发：
- Activity（dispatchTouchEvent） --->ViewGroup（dispatchTouchEvent） --->ViewGroup（onInterceptTouchEvent） --->ViewGroup（onTouchEvent）--->Activity（onTouchEvent）![image.png](https://s2.loli.net/2024/04/02/Tr5zdHpu8Gfs3E2.png)

##### 总结

- **当在dispatchTouchEvent与onTouchEvent返回true时，事件会被截断，后续节点不会再收到Action_Down消息**
- **当在dispathTouchEvent返回false时，事件会改变传递方向，改为向父view的onTouchEvent开始进行传递**
- **当在onInterceptTouchEvent返回true时，代表事件交由自己处理，改变为自己view的onTouchEvent开始进行传递**
- **一般在拦截消息的时候，我们会在onInterceptTouchEvent中返回true交由自己处理，然后在自己的onTouchEvent中返回true，截断消息**

#### 事件分发（只是针对Action_Move、Action_UP）

##### dispatchTouchEvent

- 在diszpatchTouchEvent中返回true，Action_Move与Action_Down完全一样，都会被截断
-  在ViewGroup中的onTouchEvent对逻辑进行处理，对down事件进行拦截，对其他事件放行
- ```kotlin
  override fun onTouchEvent(event: MotionEvent): Boolean {
      Log.d("TAG---SecondViewGroup", "onTouchEvent: " + event.action)
      when(event.action) {
          MotionEvent.ACTION_DOWN -> {
              return true
          }
      }
      return super.dispatchTouchEvent(event)
  }
  ```

- **当点击FirstView时，ActionDown事件传递：**
  
  - Activity(DispatchTouchEvent) --->ViewGroup(DispatchTouchEvent) ---> ViewGroup(onIntercepterTouchEvent)--->View(dispatchTouchevent) --->View(onTouchEvent) --->ViewGroup(onTouchEvent(返回true，down事件被拦截))
- ActionMove事件传递：
  - Activity(DispatchTouchEvent) --->ViewGroup(DispatchTouchEvent) ---> ViewGroup(onTouchEvent（返回super.xxxx）)--->Activity(onTouchEvent) ![image.png](https://s2.loli.net/2024/04/03/tsL2j1f8owvCuUJ.png)

- **在View中的dispatchTouchEvent对Down事件返回false，在ViewGroup中的pnTouchEvent对Down返回true进行拦截，ActionDown事件传递**

  - Activity(DispatchTouchEvent) --->ViewGroup(DispatchTouchEvent) ---> ViewGroup(onIntercepterTouchEvent)--->View(dispatchTouchevent return false) --->ViewGroup(onTouchEvent return true拦截) 

- ActionMove事件传递：

  - Activity(DispatchTouchEvent) --->ViewGroup(DispatchTouchEvent) ---> ViewGroup(onTouchEvent)![image.png](https://s2.loli.net/2024/04/04/DjBogSqnWzy5Fbr.png)


- 在ViewGroup中的onInterceptTouchEvent中多Down事件返回true，其余默认，ViewGroup中的onTouchEvent对所有事件返回true,ActionDown事件传递
  - Activity(DispatchTouchEvent) --->ViewGroup(DispatchTouchEvent) ---> ViewGroup(onIntercepterTouchEvent return true) --->ViewGroup(onTouchEvent return true拦截) 

- Action_MOVE事件传递
  - Activity(DispatchTouchEvent) --->ViewGroup(DispatchTouchEvent) ---> ViewGroup(onTouchEvent) 结束

- ```kotlin
  override fun onTouchEvent(event: MotionEvent): Boolean {
      Log.d("TAG---SecondViewGroup", "onTouchEvent: " + event.action)
      when(event.action) {
          MotionEvent.ACTION_DOWN -> {
              return true
          }
      }
      return super.onTouchEvent(event)
  }
  
  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
      Log.d("TAG---SecondViewGroup", "onInterceptTouchEvent: " + ev.action)
      when(ev.action) {
          MotionEvent.ACTION_DOWN -> {
              return true
          }
      }
      return super.onInterceptTouchEvent(ev)
  }
  ```

- ![image.png](https://s2.loli.net/2024/04/04/cYsxW1aMpIJ4lCw.png)

##### 总结

- **在DispathTouchEvent中返回ture，Down事件会在其停止传递，MOVE与UP事件同Down事件相同，传递到该控件后不再向下传递**
- **无论Down的传递是怎样的，只要最后在控件A的onTouchEvent上返回ture，即MOVE与UP事件都会流向该控件的dispatchTouchEvent后直接传递到该控件的onTouchEvent事件上**

##### Action_Move事件到来时进行拦截

- View中的onTouchEvent事件中return true，即对所以事件进行拦截
- ViewGroup中的onInterceptTouchEvent对Action_Move事件进行return true，对ViewGroup中的onTouchEvent对move事件return true
- 当点击View时候，Action_Down事件分发：
  - Activity(DispatchTouchEvent) --->ViewGroup(DispatchTouchEvent) ---> ViewGroup(onIntercepterTouchEvent )  --->View(dispatchTouchEvent) --->View(onTouchEvent)
- Action_Move事件分发：
  - Activity(DispatchTouchEvent) --->ViewGroup(DispatchTouchEvent) ---> ViewGroup(onIntercepterTouchEvent )  --->ViewGroup(onTouchEvent) 
  - 此时Action_Move事件被拦截，转换为Action_Cance继续向下分发（按照未被拦截逻辑）![image.png](https://s2.loli.net/2024/04/06/OKZLzJvHs8oxUEk.png)

- ViewGroup中DispatchTouchEvent对Move事件进行return true拦截![image.png](https://s2.loli.net/2024/04/06/GFySzDTPoIvrVsY.png)

- ViewGroup中DispatchTouchEvent对Move事件进行return false拦截![image.png](https://s2.loli.net/2024/04/06/UQwrM2obYtgjfZa.png)
  - 事件会直接走向Activity的onTouchEvent中，无论哪个控件的DispathTouchEvent中对Move事件返回false，事件都会直接走向Activity的onTouchEvent中

#### requestDisallowInterceptTouchEvent(Boolean)

- ViewGroup自带拦截失效函数：true：对事件不进行拦截，默认false

- View可以调用getParent().requestDisallowInterceptTouchEvent(true)请求ViewGroup不拦截事件

- ViewGroup中对事件进行拦截（return true），View中的onTouchEvent与dispatchTouchEvent请求ViewGroup对事件进行放行

  - ```kotlin
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---SecondViewGroup", "dispatchTouchEvent: " + ev.action)
        return true
    }
    ```

  - ```kotlin
    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("TAG---FirstView", "onTouchEvent: " + event.action)
        parent.requestDisallowInterceptTouchEvent(true)
        return super.onTouchEvent(event)
    }
    
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---FirstView", "dispatchTouchEvent: " + ev.action)
        parent.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(ev)
    }
    ```

  - 对View进行触摸：Activity(dispatchTouchEvent)--->ViewGroup(dispatchTouchEvent) --->ViewGroup(onInterceptTouchEvent) --->View(dispathchTouchEvent)--->View(onTouchEvent return true)结束

  - 发现事件仍然被拦截了，原因？因为View中的onTouchEvent与dispatchTouchEvent根本没有被执行到，代码根本没有生效。即getParent().requestDisallowInterceptTouchEvent(true)根本未被执行到

  - 改写ViewGroup的onInterceptTouchEvent，对down事件进行放行，以便于能执行到View中的parent.requestDisallowInterceptTouchEvent(true)此行代码，对Move事件进行拦截

    - ```kotlin
      override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
          Log.d("TAG---SecondViewGroup", "onInterceptTouchEvent: " + ev.action)
          when(ev.action) {
              MotionEvent.ACTION_MOVE -> {
                  return true
              }
          }
          return super.onInterceptTouchEvent(ev)
      }
      ```

    - Action_Down事件：
      - Activity（dispatchTouchEvent）--->ViewGroup(dispathchTouchEvent)--->ViewGroup(onInterceptTouchEvent)--->View(dispathchTouchEvent)--->View(onTouchEvent return true)
    - Action_Move事件：
      - Activity（dispatchTouchEvent）--->ViewGroup(dispathchTouchEvent)--->View(dispathchTouchEvent)--->View(onTouchEvent return true)
    - **总结：当对Move事件进行请求不拦截时，所有父控件的onInterceptTouchEvent将不会执行**

##### 总结

- requestDisallowInterceptTouchEvent（true）生效：即需要执行到该行代码，要求父控件不能对Down事件进行拦截
- requestDisallowInterceptTouchEvent只针对父控件的onInterceptTouchEvent，若在父控件的dispatchTouchEvent对事件进行拦截，子控件的requestDisallowInterceptTouchEvent将不会生效
- 通过requestDisallowInterceptTouchEvent请求放行后，所有父控件的onInterceptTouchEvent将不会被回调

#### 滑动冲突解决思路（可滑动的ViewGroup内部嵌套可滑动的子View）

##### 外部拦截法

- ViewGroup中的onInterceptTouchEvent选择对Move事件进行拦截、放行

- 需要自己处理的事件，再onTouchEvent中进行逻辑即可

- ```kotlin
  override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
      Log.d("TAG---SecondViewGroup", "onInterceptTouchEvent: " + ev.action)
      when(ev.action) {
          MotionEvent.ACTION_DOWN -> {
              return false
          }
          MotionEvent.ACTION_MOVE -> {
              if (需要自己处理) {
                  return true
              } else {
                  return false
              }
          }
          else -> {
              return false
          }
      }
  }
  ```

##### 内部拦截法

- 在ViewGroup中对所有的事件都进行不拦截，全都分发的子View

- 在View中对事件进行请求父控件操作

- ```kotlin
  override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
      Log.d("TAG---FirstView", "dispatchTouchEvent: " + ev.action)
      when(ev.action) {
          MotionEvent.ACTION_MOVE -> {
              if (需要自己处理) {
                  parent.requestDisallowInterceptTouchEvent(true)
              } else {
                  parent.requestDisallowInterceptTouchEvent(false)
              }
          }
      }
      return super.dispatchTouchEvent(ev)
  }
  ```

##### 滑动冲突案例

- ScrollView包裹固定高度的TextView

- ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:background="#BC1A1A">
  
      <LinearLayout
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:orientation="vertical">
  
          <com.jin.touch.widgit.CustomTextView
              android:layout_width="match_parent"
              android:layout_height="50dp"
              android:text="这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例这是一段测试用例"/>
  
          <TextView
              android:layout_width="match_parent"
              android:layout_height="5000dp"
              android:background="#F19494"/>
  
      </LinearLayout>
  
  </ScrollView>
  ```

- ![tutieshi_576x1280_5s.gif](https://s2.loli.net/2024/04/07/JoWMz7LTiabrO3x.gif)

- 滑动固定高度的TextView但是确触发了ScrollView的滑动
- 如何解决？
  - 外部拦截法：通过ScrollView判断手指触摸位置，如果为TextView的区域，放行，交由TextView进行处理，否则进行拦截
  - 内部拦截法：TextView通过监听Move事件，请求ScrollView是否进行拦截
  
- 外部拦截法：CustomScrollView

  - ```kotlin
    private var downY = 0f
    
    private var textViewHeight = TypedValue.applyDimension(COMPLEX_UNIT_DIP,50f,context.resources.displayMetrics)
    
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---CustomScrollView", "onInterceptTouchEvent: " + ev.action)
        when(ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downY = ev.y
                Log.d("TAG", "onInterceptTouchEvent:downY " + downY)
                Log.d("TAG", "onInterceptTouchEvent:textViewHeight " + textViewHeight)
            }
            MotionEvent.ACTION_MOVE -> {
                return downY >= textViewHeight
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
    
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---CustomScrollView", "dispatchTouchEvent: " + ev.action)
        return super.dispatchTouchEvent(ev)
    }
    
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---CustomScrollView", "onTouchEvent: " + ev.action)
        return super.onTouchEvent(ev)
    }
    ```

  - CustomTextView

  - ```kotlin
    init {
        movementMethod = ScrollingMovementMethod.getInstance()
    }
    
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        Log.d("TAG---CustomTextView", "dispatchTouchEvent: " + event.action)
        return super.dispatchTouchEvent(event)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("TAG---CustomTextView", "onTouchEvent: " + event.action)
        return super.onTouchEvent(event)
    }
    ```

  - Action_Down、Action_Move事件相同

    - CustomScrollView（dispatchTouchEvent）--->CustomScrollView（onInterceptTouchEvent）--->CustomTextView（dispatchTouchEvent）--->CustomTextView（onTouchEvent super返回true）

- 内部拦截法：CustomScrollView

  - ```kotlin
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return super.onInterceptTouchEvent(ev)
    }
    
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---CustomScrollView", "dispatchTouchEvent: " + ev.action)
        return super.dispatchTouchEvent(ev)
    }
    
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG---CustomScrollView", "onTouchEvent: " + ev.action)
        return super.onTouchEvent(ev)
    }
    ```

  - CustomTextView

  - ```kotlin
    init {
        movementMethod = ScrollingMovementMethod.getInstance()
    }
    
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        Log.d("TAG---CustomTextView", "dispatchTouchEvent: " + event.action)
        when(event.action) {
            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(true)
            }
        }
        return super.dispatchTouchEvent(event)
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        Log.d("TAG---CustomTextView", "onTouchEvent: " + event.action)
        return super.onTouchEvent(event)
    }
    ```

  - Action_Down事件：

    - CustomScrollView（dispatchTouchEvent）--->CustomScrollView（onInterceptTouchEvent）--->CustomTextView（dispatchTouchEvent）--->CustomTextView（onTouchEvent super返回true）

  - 第一次Action_Move事件：

    - CustomScrollView（dispatchTouchEvent）--->CustomScrollView（onInterceptTouchEvent）--->CustomTextView（dispatchTouchEvent 通知父控件不拦截Move事件）--->CustomTextView（onTouchEvent super返回true）

  - 后续Action_Move事件：

    - CustomScrollView（dispatchTouchEvent--->CustomTextView（dispatchTouchEvent 通知父控件不拦截Move事件）--->CustomTextView（onTouchEvent super返回true）

![tutieshi_640x1422_6s.gif](https://s2.loli.net/2024/04/07/LrxhRK8XsygQSuk.gif)