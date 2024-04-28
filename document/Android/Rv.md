### Recycleview

#### 自定义LayoutManager

##### 步骤

- **自定义类继承LayoutManager类**

- **重写默认函数generateDefaultLayoutParams（）**

  - RecycleView中子Item的布局参数，一般即为WRAP_CONTENT、WRAP_CONTENT

  - ```kotlin
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }
    ```

- 此时，替换LayoutManager后，界面是没有任何View的，因为还没有对其进行布局

- **重写onLayoutChildren函数，对子item进行布局**

  - **模拟LinearLayoutManager的垂直布局效果**

  - **recycler.getViewForPosition(i)：根据Position获取对应的HolderView，添加到LayoutManager中**

  - **测量获取到的HolderView，对其进行布局**

  - **getDecoratedMeasuredWidth（view）：获取holderView的宽度（包含item + itemDecration）**

  - **view.getMeasureWidth()：获取view的测量宽度（不包含itemDecration）**

  - **totalHeight：Recycleview的总高度（包括item未展示出来的）**

    - **倘若计算出的offsetY没填满Recycleview设置的高度即item数量不足未填满，则实际总高度为Recycleview设置的总高度height - paddingBottom - paddingTop**

  - ```kotlin
    var totalHeight = 0
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        var offsetY = 0
        for (i in 0 until itemCount) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val viewWidth = getDecoratedMeasuredWidth(view)
            val viewHeight = getDecoratedMeasuredHeight(view)
            layoutDecorated(view, 0, offsetY, viewWidth, offsetY + viewHeight)
            offsetY += viewHeight
        }
        //Recycleview的总高度
        totalHeight = max(offsetY, getVerticalHeight())
    }
    
     private fun getVerticalHeight(): Int {
            return height - paddingBottom - paddingTop
     }
    ```

  - ```xml
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_constraintTop_toTopOf="parent"/>
    ```

  - 效果如下：![d09fcfc741c47b991b6f5fa584e4eed.jpg](https://s2.loli.net/2024/04/28/TxOXCsIM6hB32Un.jpg)

  - 注意：当在xml中将RecyclerView高度设置为wrap_content时，会发现界面什么都不会显示

  - isAutoMeasureEnabled：是否开启自动测量，默认返回false，需要重写该方法，返回true，让Recycleview自动取测量即可

    - ```kotlin
      override fun isAutoMeasureEnabled(): Boolean {
          return true
      }
      ```

- 布局好了Recycleview的子item，发现滚动并没有效果，需要自实现滚动效果

  - canScrollVertically、canScrollHorizontally：是否支持垂直滚动、水平滚动

  - ```kotlin
    override fun canScrollVertically(): Boolean {
        return true
    }
    ```

  - **scrollVerticallyBy(dy：Int,recycler: RecyclerView.Recycler,state：RecyclerView.State)**
    - **垂直滚动的回调**
    - **dy：垂直滚动的偏移量**
    - **recycler：Recycleview的回收器Recycler类**
    - **state：Recycleview状态**
    - **dy符合问题：**
      - **当手指从下往上滑动，即列表向上滚动，dy > 0**
      - **当手指从上往下滑动，即列表向下滚动，dy < 0**

  - offsetChildrenVertical（offset：Int）
    - 垂直方向对Recycleview滑动的偏移量
    - 由dy的取值可知，当dy > 0 时，列表向上滚动，需要对Recycleview向上进行偏移，故offset = -dy
    - 当dy < 0 时，列表向下滚动，需要对Recycleview向下进行偏移，故offset = -dy

  - **进行滚动偏移**

    - ```kotlin
      override fun scrollVerticallyBy(
          dy: Int,
          recycler: RecyclerView.Recycler,
          state: RecyclerView.State
      ): Int {
          offsetChildrenVertical(-dy)
          return dy
      }
      ```

  - 效果如下
    - ![tutieshi_432x960_5s.gif](https://s2.loli.net/2024/04/28/SmGxnTqavN1RcPE.gif)

- 问题：上边界下边界未判断，导致Recycleview滑动时顶部底部会存在空白

  - 上边界到顶判断（即滑动距离dy < 0 时，界判断）

    - **每次滑动记录下总滑动距离sumDy，倘若一直从下往上滑动，sumDy为正且会越来越大**

    - **当开始从上往下滑动，dy < 0，sumDy累积会逐渐变小，当总滑动距离 + 当前滑动的距离（dy< =0）时，代表滑动到上顶部了需将dy重置为-sumDy最大滑动即可**

    - ```kotlin
      var tempDy = dy
      if (sumDy + tempDy <= 0) {
          //到顶
          tempDy = -sumDy
      }
      sumDy += tempDy
      offsetChildrenVertical(-tempDy)
      ```

  - 下边界到底判断（即滑动距离dy > 0时，界判断）

    - **倘若当前Recycleview展示的高度为Y，而总item计算出的总高度为TotalY，则dy可滑动的最大值为TotalY - Y （即到底部）**

    - **getVerticalHeight即为Recycleview展示的高度（Y）**

    - ```kotlin
      var tempDy = dy
      if (sumDy + tempDy >= totalHeight - getVerticalHeight()) {
          //到底
          tempDy = totalHeight - sumDy - getVerticalHeight()
      }
      sumDy += tempDy
      offsetChildrenVertical(-tempDy)
      ```

      ```kotlin
      private fun getVerticalHeight(): Int {
          return height - paddingBottom - paddingTop
      }
      ```

  - 效果如下![tutieshi_576x1280_4s.gif](https://s2.loli.net/2024/04/28/WXITzm1KnMAiJgF.gif)

- 完整代码

  - ```kotlin
    binding.rv.layoutManager = CustomLayoutManager()
            binding.rv.adapter = RvAdapter(this)
    
            val paint = Paint().apply {
                style = Paint.Style.FILL_AND_STROKE
                color = Color.BLACK
            }
    
            val bitmap = BitmapFactory.decodeResource(resources,R.drawable.medal)
            binding.rv.addItemDecoration(object : ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    Log.d("TAG", "getItemOffsets:")
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.bottom = 10
                    outRect.left = 160
                    outRect.right = 80
                }
    
                override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    super.onDraw(c, parent, state)
    //                for (i in 0  until parent.childCount) {
    //                    val childView = parent.getChildAt(i)
    //                    val x = 80f
    //                    val y = childView.top + childView.height / 2f
    //                    c.drawBitmap(bitmap,0f,childView.top.toFloat(),paint)
    //                }
                }
    
                override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                    super.onDrawOver(c, parent, state)
                    for (i in 0  until parent.childCount) {
                        val childView = parent.getChildAt(i)
                        if (parent.layoutManager == null) continue
                        val x = parent.layoutManager!!.getLeftDecorationWidth(childView)
                        val left = x.minus(bitmap.width / 2f)
                        c.drawBitmap(bitmap,left,childView.top + (childView.height / 2f - bitmap.height / 2),paint)
                    }
                }
            })
    ```

  - ```xml
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintTop_toTopOf="parent"/>
    ```

  - ```kotlin
    class CustomLayoutManager : LayoutManager() {
    
        override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
            return RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }
    
        var sumDy = 0
        var totalHeight = 0
        override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
            var offsetY = 0
            for (i in 0 until itemCount) {
                val view = recycler.getViewForPosition(i)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                val viewWidth = getDecoratedMeasuredWidth(view)
                val viewHeight = getDecoratedMeasuredHeight(view)
                layoutDecorated(view, 0, offsetY, viewWidth, offsetY + viewHeight)
                offsetY += viewHeight
            }
            totalHeight = max(offsetY, getVerticalHeight())
        }
    
        override fun isAutoMeasureEnabled(): Boolean {
            return true
        }
        override fun canScrollVertically(): Boolean {
            return true
        }
    
    
    
        override fun scrollVerticallyBy(
            dy: Int,
            recycler: RecyclerView.Recycler,
            state: RecyclerView.State
        ): Int {
            var tempDy = dy
            if (sumDy + tempDy <= 0) {
                //到顶
                tempDy = -sumDy
            }
            if (sumDy + tempDy >= totalHeight - getVerticalHeight()) {
                //到底
                tempDy = totalHeight - sumDy - getVerticalHeight()
            }
            sumDy += tempDy
            offsetChildrenVertical(-tempDy)
            return dy
        }
    
        private fun getVerticalHeight(): Int {
            return height - paddingBottom - paddingTop
        }
    
    
    }
    ```

#### 回收复用

