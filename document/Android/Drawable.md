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

- 在dispatchTouchEvent中返回true，Action_Move与Action_Down完全一样，都会被截断
- 