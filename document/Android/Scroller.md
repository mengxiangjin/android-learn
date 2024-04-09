### Scroller

#### 函数

##### scrollTo(offsetX,offsetY)

- View中的函数，调用此函数可以使view移动到偏移处
- offsetX，offsetY：负数为右偏移、下偏移
- 偏移视图改变，但原位置不会发生改变
- 相当于原位置的偏移量

##### scrollBy(offsetX,offsetY)

- View中的函数，调用此函数可以使view移动到偏移处
- offsetX，offsetY：负数为右偏移、下偏移
- 偏移视图改变，但原位置不会发生改变
- 相当于新偏移的偏移量

##### 区别

- ```xml
  <LinearLayout
      android:id="@+id/ll_content"
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:layout_marginTop="20dp"
      android:orientation="vertical">
  
      <Button
          android:id="@+id/btn_scroll_to"
          android:layout_width="200dp"
          android:layout_height="wrap_content"
          android:text="scroll to" />
  
      <Button
          android:id="@+id/btn_scroll_by"
          android:layout_width="200dp"
          android:layout_height="wrap_content"
          android:text="scroll by" />
  
      <Button
          android:id="@+id/btn_reset"
          android:layout_width="200dp"
          android:layout_height="wrap_content"
          android:text="reset" />
  
  </LinearLayout>
  ```

- 点击事件：

- ```kotlin
  binding.btnScrollTo.setOnClickListener {
      binding.llContent.scrollTo(-50,-50)
  }
  binding.btnScrollBy.setOnClickListener {
      binding.llContent.scrollBy(-50,-50)
  }
  binding.btnReset.setOnClickListener {
      binding.llContent.scrollTo(0,0)
  }
  ```

- 对于scrollTo函数：![tutieshi_640x1422_3s.gif](https://s2.loli.net/2024/04/09/ICEUKaFMfkrx8lX.gif)

- 对于scrollBy函数![tutieshi_640x1422_6s _2_.gif](https://s2.loli.net/2024/04/09/lt4EjpbFGgPk3C5.gif)

#### 仿QQ侧滑删除

- ```xml
  <LinearLayout
      android:id="@+id/ll_action"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      android:orientation="horizontal">
  
      <View
          android:layout_width="match_parent"
          android:layout_height="50dp"
          android:background="@color/black"/>
  
      <View
          android:layout_width="50dp"
          android:layout_height="50dp"
          android:background="#EC2D2D"/>
  
  </LinearLayout>
  ```

- ```kotlin
  binding.btnScrollBy.setOnClickListener {
      binding.llAction.scrollBy(10,0)
  }
  ```

- ![tutieshi_640x1422_5s _1_.gif](https://s2.loli.net/2024/04/09/AsH3Rjb9KZw6B8f.gif)

#### Scorller类

##### 构造函数

- Scroller(context)
- Scroller(context,Interpolator)：插值器

##### 滚动

- startScroll(int startX,int startY,int dx,int dy,int duration)
  - startX：开始滚动起点X
  - startY：开始滚动起点Y
  - dx：滚动偏移X
  - dy：滚动偏移Y
  - duration：滚动总耗时