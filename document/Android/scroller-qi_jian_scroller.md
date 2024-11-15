### Scroller --qi_jian_scroller

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

##### 使用

- 创建Scroller类对象

  - ```kotlin
    scrollerView = Scroller(context, LinearInterpolator(context, null))
    ```

- 调用startScroll函数

  - 注意：当调用scrollerView!!.startScroll函数时，需要手动调用invalidate()让View进行重新绘制

  - 这也说明了Scroller类需要在自定义View或者自定义ViewGroup中使用

  - ```kotlin
    fun scroll(startX: Int, dx: Int) {
        scrollerView!!.startScroll(startX, 0, dx, 0, 3000)
        invalidate()
    }
    ```

- 当startScroll后调用invalidate函数后，view会回调computeScroll（）方法

  - View本身的computeScroll（）是空实现

  - 判断view是否滚动结束：scrollerView!!.computeScrollOffset()

  - 获取当前的滚动X以及滚动Y，将view.scrollTo即可

  - 之后不断进行invalidate()即可

  - ```kotlin
    override fun computeScroll() {
        if (scrollerView!!.computeScrollOffset()) {
            scrollTo(scrollerView!!.currX, scrollerView!!.currY)
        }
        invalidate()
    }
    ```

- 案例

  - ScrollerView为自定义的ViewGroup extends LinearLayout

  - ```kotlin
    private var scrollerView: Scroller? = null
    
    init {
        scrollerView = Scroller(context, LinearInterpolator(context, null))
    }
    
    
    override fun computeScroll() {
        if (scrollerView!!.computeScrollOffset()) {
            scrollTo(scrollerView!!.currX, scrollerView!!.currY)
        }
        invalidate()
    }
    
    fun scroll(startX: Int, dx: Int) {
        scrollerView!!.startScroll(startX, 0, dx, 0, 3000)
        invalidate()
    }
    ```

  - ```xml
    <com.jin.scroller.widgit.ScrollerView
        android:id="@+id/scroll_view"
        android:layout_width="match_parent"
        android:layout_height="50dp"
        android:layout_marginTop="30dp"
        android:orientation="horizontal">
    
        <View
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="@color/black"/>
    
        <View
            android:layout_width="50dp"
            android:layout_height="50dp"
            android:background="#EC2D2D"/>
    
    </com.jin.scroller.widgit.ScrollerView>
    ```

- 当外部调用

  - ```kotlin
    binding.scrollView.scroll(0,100)	//0，0 scrollTo 100，0 即向左滚动
    binding.scrollView.scroll(100,-100)	//100，0 scrollTo -100，0 即向右滚动、归位
    ```

  - ![a63c970853cd179c03fbc7f7553ed8b5.gif](https://s2.loli.net/2024/04/10/cOWdxlky9NZrV3D.gif)

#### 自定义滑动开关

##### 示例效果

![20240410_224926.gif](https://s2.loli.net/2024/04/10/zBWhVmUS5Aku8oO.gif)

##### 分析

- 整个View分为二部分：滑块以及底部的背景图片
- 点击事件：点击滑块，滑块根据当前开关状态自动滑向另一侧
- 拖动事件：拖动滑块，滑块根据手指拖动
- 当拖动完成，Action_UP时，滑块根据当前位置，自动滑动到左边界或者右边界
- 滑块根据手指拖动时，需要考虑边界判断否则滑块会滑出屏幕外

##### 实现

- 自定义ViewGroup --- 》SlideViewGroup

- ```xml
  <com.jin.scroller.widgit.SlideViewGroup
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:layout_marginTop="30dp"/>
  ```

- 初始化成员变量

- ```kotlin
  //滑块宽度
  private var mSlideViewWidth = 0
  //滑块滚动到边界所需的宽度为正值
  private var mScrollWidth = 0
  //当前开关状态
  private var isOpen = false
  
  private var mScroller: Scroller
  //上次触摸滑块的x位置
  private var lastTouchX = 0f
  ```

- SlideViewGroup绘制背景以及添加滑块View、初始化Scroller对象

- ```kotlin
  init {
      mScroller = Scroller(context)
      background =  ResourcesCompat.getDrawable(resources,R.drawable.bg,null)
  
      val slideView = ImageView(context)
      slideView.scaleType = ImageView.ScaleType.CENTER_CROP
      slideView.setImageResource(R.drawable.slide_two)
      slideView.setOnClickListener {
          isOpen = !isOpen
          if (isOpen) {
              mScroller.startScroll(0,0,-mScrollWidth,0,500)
          } else {
              mScroller.startScroll(-mScrollWidth,0,mScrollWidth,0,500)
          }
          invalidate()
      }
  	//设置滑块宽度为背景图宽度的一半、高度为背景图的高度
      addView(slideView,background.intrinsicWidth / 2,background.intrinsicHeight)
  }
  ```

- 测量SlideViewGroup的宽度高度 --->即背景图的宽度、高度即可

- ```kotlin
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec)
      val widthMode = MeasureSpec.getMode(widthMeasureSpec)
      val widthSize = MeasureSpec.getSize(widthMeasureSpec)
      val heightMode = MeasureSpec.getMode(heightMeasureSpec)
      val heightSize = MeasureSpec.getSize(heightMeasureSpec)
  
      val drawable = ResourcesCompat.getDrawable(resources,R.drawable.bg,null)
  
      if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
          setMeasuredDimension(drawable!!.intrinsicWidth, drawable.intrinsicHeight)
      } else if (widthMode == MeasureSpec.AT_MOST) {
          setMeasuredDimension(drawable!!.intrinsicWidth,heightSize)
      } else if (heightMode == MeasureSpec.AT_MOST) {
          setMeasuredDimension(widthSize,drawable!!.intrinsicHeight)
      } else {
          setMeasuredDimension(widthSize,heightSize)
      }
  }
  ```

- 初始化摆放好滑块在置最左边

- ```kotlin
  override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
      val childView = getChildAt(0)
      //滑块宽度
      mSlideViewWidth = measuredWidth / 2
      //滑块滚动到最右边所需要的距离 > 0
      mScrollWidth = measuredWidth - mSlideViewWidth
      //初始布局左边铺满
      childView.layout(0,0,mSlideViewWidth,measuredHeight)
  }
  ```

- 对滑块点击事件监听

- ```kotlin
  slideView.setOnClickListener {
      isOpen = !isOpen
      if (isOpen) {
          mScroller.startScroll(0,0,-mScrollWidth,0,500)
      } else {
          mScroller.startScroll(-mScrollWidth,0,mScrollWidth,0,500)
      }
      invalidate()
  }
  ```

- mScroller.startScroll后invalidate()触发computeScroll方法

- ```kotlin
  override fun computeScroll() {
      super.computeScroll()
      if (mScroller.computeScrollOffset()) {
          scrollTo(mScroller.currX,mScroller.currY)
          invalidate()
      }
  }
  ```

- 此时滑块点击事件已经实现

- 滑块跟随手指拖动事件

  - 这里其实是调用了ViewGroup的scrollTo与ScrollBy函数，scrollTo与ScrollBy函数对背景是无效的，所以对整个ViewGroup进行scrollTo与ScrollBy，其内部的子View会发生滚动、效果上相当于滑块在拖动

  - 在ViewGroup进行事件分发与拦截

  - 子滑块需要消费点击事件Action_Down、故对于Avtion_Move事件ViewGroup进行拦截、处理自己的scroll即可：注意对边界的处理

  - ```kotlin
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.d("TAG", "onInterceptTouchEvent: " + ev.action)
        lastTouchX = ev.x
        when(ev.action) {
            MotionEvent.ACTION_DOWN -> {
                return false
            }
            MotionEvent.ACTION_MOVE -> {
                return true
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
    ```

    ```kotlin
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_MOVE -> {
                val dx = lastTouchX - event.x
                //边界进行处理
                if (scrollX + dx < -mScrollWidth) {
                    //右边界处理
                    scrollTo(-mScrollWidth,0)
                } else if (scrollX + dx > 0) {
                    //左边界
                    scrollTo(0,0)
                } else {
                    scrollBy(dx.toInt(),0)
                }
            }
            MotionEvent.ACTION_UP -> {
                smoothScroll()
            }
        }
        lastTouchX = event.x
        return true
    }
    ```

- 手指抬起、滑块根据当前位置自动滑到另一边的效果

  - ```kotlin
    MotionEvent.ACTION_UP -> {
        smoothScroll()
    }
    ```

  - ```kotlin
    private fun smoothScroll() {
        val halfWidth = measuredWidth / 2 - mSlideViewWidth / 2
        isOpen = if (abs(scrollX) > halfWidth) {
            Log.d("TAG", "smoothScroll:右 ")
            Log.d("TAG", "scrollX: ${scrollX}")
            Log.d("TAG", "mScrollWidth: ${mScrollWidth}")
            //滑到右边界
            mScroller.startScroll(scrollX,0,-(mScrollWidth + scrollX),0,500)
            true
        } else {
            //滑到左边界
            Log.d("TAG", "smoothScroll:左 ")
            mScroller.startScroll(scrollX,0,-scrollX,0,500)
            false
        }
        invalidate()
    }
    ```

