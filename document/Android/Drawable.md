### 事件分发

#### 概览

##### 函数

- dispatchTouchEvent（MotionEvent）：View、ViewGroup、Activity
- onInterceptEvent（MotionEvent）：ViewGroup独有
- ouchEvent（MotionEvent）：View、ViewGroup、Activity

#### dispathTouchEvent

- 默认返回值：super.dispatchTouchEvent()

##### 自定义View调试

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

##### 事件分发初探

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

- 当点击FirstView时，事件分发（全部View不拦截消息）
  - FirstViewGroup（disPathchTouchEvent） ---> FirstViewGroup（onInterceptTouchEvent ） --->SecondViewGroup（disPathchTouchEvent） ---> SecondViewGroup（onInterceptTouchEvent ）--->FirstView（disPathchTouchEvent） ---> FirstView（onTouchEvent） ---> SecondViewGroup（onTouchEvent） --->FirstViewGroup（onTouchEvent）