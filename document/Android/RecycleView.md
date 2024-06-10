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

##### 回收复用判断

- onCreateViewHolder：当创建一个新的View时被回调
- onBindViewHolder：view绑定数据时回调
- **当使用上述自定义LayoutManager时，会发现当itemCount为N时，会一次性回调N次onCreateViewHolder以及N次onBindViewHolder，消耗性能资源**
- **当使用自带的LayoutManager时，例如LinearLayoutManager，当itemCount为N时，会一次性回调很少次onCreateViewHolder，以及onBindViewHolder（当前屏幕可见的item数量），当来回滑动Recyclerview此时并不会回调onCreateViewHolder，而是直接回调onBindViewHolder进行数据绑定，此时即View进行了复用**
- 按需加载创建View，当滑动时，将不可见的View回收，可见的item可选择复用之前的View

##### 回收复用原理

- RecycleView中的Recycler类
  - **AttachedScrap：不参与滑动时的回收复用，只保存重新布局时从RecycleView中分离出的无效的、未移除的、未更新的holderView。RecycleView在onLayout时，会事先将children全移除，再重新添加加入，这些移除的会保存在mAttachedScrap中**
  - **chanedScrap：不参与滑动时的回收复用，只保存重新布局时发生变化的item的无效、未移除的holderView**
  - **CachedViews：用于保存最新的被移除的holderView。作用是滑动时如果需要新的ViewHolder时，会从其中精确匹配是否存在，如果存在返回此ViewHolder直接使用，无须创建ViewHolder与绑定数据。如果不存在，则去从RecyclerPool中匹配查找返回（需要重新绑定数据），这一级最大缓存数量为2**
  - **RecyclerPool：最终回收站，存储着真正标记着被废弃的（其他缓存池都不回收的）ViewHolder，如果上述都找不到ViewHolder时，会从其中匹配返回一个废弃的ViewHolder并重新绑定数据**
  - **ViewCacheExtension：开发者自定义缓存逻辑（一般不会用到）**
  - 总结：**Scrap（AttachedScrap、chanedScrap） -> CachedViews -> ViewCacheExtension -> RecyclerPool**
- **recycler.getViewForPosition(Int)**
  - RecycleView.Recycler对象中函数
  - **根据Position获取对应holderView**
  - **首先从Scrap查找holderView是否是刚刚从屏幕剥离下来的holderView，如果是直接返回，无需重新创建、绑定数据（精确匹配）。如果没有，则会在去CachedViews中精确匹配，如果没有再去RecyclerPool去获取，只有当RecyclerPool为空时，才会重新创建holderView并绑定数据，否则只需重新绑定数据即可**
- **RecycleView.detachAndScrapAttachedViews（RecycleView.Recycler）**
  - **用于自定义LayoutManager中的onLayoutChildren函数中，将当前正在显示的HolderView从屏幕中剥离，放入的Scrap缓存中，以便重新布局时使用**
- **RecycleView.removeAndRecycleView（View,RecycleView.Recycler）**
  - **滑动时调用，将滚动出屏幕外的标记为Remove。将View标记为Remove即将其放入到CacheViews中，若CacheView已满，将CacheView老的View放入到RecyclePool中，再将此View放入到CacheView中，便于下次精准匹配复用**
- **RecycleView.getItemCount()：Int**
  - **获取RecycleView的总共item数量**
- **RecycleView.getChildCount()：Int**
  - **获取当前屏幕显示的item数量**
- **RecycleView.getChildAt(postion: Int)：View**
  - **根据position获取View**
  - **注意：position是相当于屏幕的而不是整个RecycleView，例如，获取当前屏幕展示的第一个View与最后一个View**
    - **getChildAt（0）**
    - **getChildAt（getChildCount - 1）**
- **RecycleView.getPostion（View）：Int**
  - **根据View获取position**
  - **注意：获取到的position是相对于整个RecycleView的位置而不是屏幕，常常与getChildAt搭配使用。例如，获取屏幕显示的最后一个View在RecycleView中的位置**
    - **val view = getChildAt（getChildCount - 1）**
    - **val position = getPostion（view ）**

##### 回收复用实现一

- **onLayoutChildren中只添加当前屏幕所能容纳的View即可，不需要一次性全部添加布局中**
- **scrollVerticallyBy中当用户滚动时，将滚动不可见的View回收，将可见的View通过复用拿到添加到布局**

###### onLayoutChildren

- detachAndScrapAttachedViews(recycler)：将当前屏幕中的View进行剥离

- **获取当前屏幕所能展示item的个数visiableCount，遍历此visiableCount将View添加到布局中摆放即可**

- **遍历所有的itemCount，将初始item位置信息存放到rectMap中**

- ```kotlin
  private var rectMap = mutableMapOf<Int,Rect>()
  private var totalHeight = 0
  
  override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
      detachAndScrapAttachedViews(recycler)
      if (itemCount == 0) {
          return
      }
      val view = recycler.getViewForPosition(0)
      measureChildWithMargins(view,0,0)
  
      val itemWidth = getDecoratedMeasuredWidth(view)
      val itemHeight = getDecoratedMeasuredHeight(view)
  
      var offsetY = 0
      for (i in 0 until itemCount) {
          val rect = Rect(0,offsetY,itemWidth,offsetY + itemHeight)
          offsetY += itemHeight
          rectMap[i] = rect
      }
  
      val visiableCount = getVerticalHeight() / itemHeight
      for (i in 0 until visiableCount) {
          val childView = recycler.getViewForPosition(i)
          addView(childView)
          measureChildWithMargins(childView,0,0)
          layoutDecorated(childView,rectMap[i]!!.left,rectMap[i]!!.top,rectMap[i]!!.right,rectMap[i]!!.bottom)
      }
      totalHeight = max(offsetY,getVerticalHeight())
  }
  
  
  private fun getVerticalHeight(): Int {
          return height - paddingBottom - paddingTop
  }
  ```

###### scrollVerticallyBy

- 滑动到顶、到底部限制

  ```kotlin
  var travel = dy
  if (mSumDy + dy < 0) {
      //到顶
      travel = -mSumDy
  } else if (mSumDy + dy >= totalHeight - getVerticalHeight()) {
      //到底
      travel = totalHeight - getVerticalHeight() - mSumDy
  }
  ```

- **从下往上滑动时，回收顶部的View，复用并布局底部显示的View**

  - **travel > 0**

  - **遍历当前屏幕上的item，判断假设滑动后，哪些View是不可见的需要回收**

  - **回收顶部：getDecoratedBottom(childView) -  travel <= 0 即代表此View滑动后是不可见的状态，需要回收（代码看从上往下滑动）**

  - **复用底部：判断到滑动到哪一个View停止**

    - **循环开始条件：当前未滑动状态下，屏幕上显示的最后一个View的Position + 1开始遍历，position + 1< itemCount**

    - **循环终止条件：当前屏幕所在位置，从下往上滚动后，偏移后的矩形框，与View所在记录的原始位置信息rectMap当中是否相交，相交即代表此View滑动后是可见状态，需要复用并布局，否则代表此View滑动后仍然是不可见状态，后续的所有View即也不可能是可见的了，循环跳出**

    - **获取滑动travel后，屏幕所在矩形框的位置**

    - ```kotlin
      //屏幕滑动后所在矩形框位置
      val legalScreenRect = getLegalScreenRect(travel)
      
       private fun getLegalScreenRect(travel: Int): Rect {
              return Rect(paddingLeft,mSumDy + travel + paddingTop,width + paddingRight,mSumDy + travel + getVerticalHeight())
          }
      ```

    - **遍历判断是否存在交集，决定是否布局**

    - ```kotlin
      //复用底部
      val lastShowView = getChildAt(childCount - 1)
      if (lastShowView != null) {
          var position = getPosition(lastShowView) + 1
          while (position < itemCount) {
              if (rectMap[position] == null) break
              if (legalScreenRect.intersect(rectMap[position]!!)) {
                  //存在交集，说明需要复用展示
                  val childView = recycler.getViewForPosition(position)
                  addView(childView)
                  measureChildWithMargins(childView,0,0)
                  layoutDecorated(childView,rectMap[position]!!.left,rectMap[position]!!.top - mSumDy,rectMap[position]!!.right,rectMap[position]!!.bottom - mSumDy)
              } else {
                  break
              }
              position++
          }
      }
      ```

- **从上往下滑动时，回收底部的View，复用并布局顶部显示的View**

  - **travel < 0**

  - **遍历当前屏幕上的item，判断假设滑动后，哪些View是不可见的需要回收**

  - **回收底部：getDecoratedTop(childView) - travel >= height - paddingBottom 即代表此View滑动后是不可见的状态，需要回收**

  - ```kotlin
    for (i in 0 until childCount) {
        val childView = getChildAt(i) ?: break
        if (dy > 0) {
            //从下往上滑动
            //回收顶部的item
            if (getDecoratedBottom(childView) - travel <= 0) {
                removeAndRecycleView(childView,recycler)
                continue
            }
        }else if (dy < 0){
            //从上往下滑动
            //回收底部的item
            if (getDecoratedTop(childView) - travel >= height - paddingBottom) {
                Log.d("TAG", "scrollVerticallyBy:回收底部的 " + i)
                removeAndRecycleView(childView,recycler)
                continue
            }
        }
    }
    ```

  - 复用顶部：判断到滑动到哪一个View停止

    - **循环开始条件：当前未滑动状态下，屏幕上显示的第一个View的Position - 1开始遍历，position - 1 >= 0**

    - **循环终止条件：当前屏幕所在位置，从上往下滚动后，偏移后的矩形框，与View所在记录的原始位置信息rectMap当中是否相交，相交即代表此View滑动后是可见状态，需要复用并布局，否则代表此View滑动后仍然是不可见状态，后续的所有View即也不可能是可见的了，循环跳出**

    - **获取滑动travel后，屏幕所在矩形框的位置**

    - ```kotlin
      //屏幕滑动后所在矩形框位置
      val legalScreenRect = getLegalScreenRect(travel)
      
      private fun getLegalScreenRect(offset: Int): Rect {
              return Rect(paddingLeft,mSumDy + offset + paddingTop,width + paddingRight,mSumDy + offset + getVerticalHeight())
          }
      ```

    - **判断是否存在交集，决定是否布局**

    - ```kotlin
      //复用顶部
      val firstShowView = getChildAt(0)
      if (firstShowView != null) {
          var position = getPosition(firstShowView) - 1
          while (position >= 0) {
              if (rectMap[position] == null) break
              if (legalScreenRect.intersect(rectMap[position]!!)) {
                  //存在交集，说明需要复用展示
                  Log.d("TAG", "scrollVerticallyBy:复用顶部 " + position)
                  val childView = recycler.getViewForPosition(position)
                  addView(childView,0)
                  measureChildWithMargins(childView,0,0)
                  layoutDecorated(childView,rectMap[position]!!.left,rectMap[position]!!.top - mSumDy,rectMap[position]!!.right,rectMap[position]!!.bottom - mSumDy)
              } else {
                  break
              }
              position--
          }
      }
      ```

##### 完整代码

- ```kotlin
  package com.jin.rv.manager
  
  import android.graphics.Rect
  import android.util.Log
  import androidx.recyclerview.widget.RecyclerView
  import androidx.recyclerview.widget.RecyclerView.LayoutManager
  import kotlin.math.max
  
  class RepeatUseLayoutManager : LayoutManager() {
  
      override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
          return RecyclerView.LayoutParams(
              RecyclerView.LayoutParams.WRAP_CONTENT,
              RecyclerView.LayoutParams.WRAP_CONTENT
          )
      }
  
      private var rectMap = mutableMapOf<Int,Rect>()
      var totalHeight = 0
  
      override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
          detachAndScrapAttachedViews(recycler)
          if (itemCount == 0) {
              return
          }
          val view = recycler.getViewForPosition(0)
          measureChildWithMargins(view,0,0)
  
          val itemWidth = getDecoratedMeasuredWidth(view)
          val itemHeight = getDecoratedMeasuredHeight(view)
  
          var offsetY = 0
          for (i in 0 until itemCount) {
              val rect = Rect(0,offsetY,itemWidth,offsetY + itemHeight)
              offsetY += itemHeight
              rectMap[i] = rect
          }
  
          val visiableCount = getVerticalHeight() / itemHeight
          for (i in 0 until visiableCount) {
              val childView = recycler.getViewForPosition(i)
              addView(childView)
              measureChildWithMargins(childView,0,0)
              layoutDecorated(childView,rectMap[i]!!.left,rectMap[i]!!.top,rectMap[i]!!.right,rectMap[i]!!.bottom)
          }
          totalHeight = max(offsetY,getVerticalHeight())
      }
  
      override fun isAutoMeasureEnabled(): Boolean {
          return true
      }
      override fun canScrollVertically(): Boolean {
          return true
      }
  
      var mSumDy = 0
      override fun scrollVerticallyBy(
          dy: Int,
          recycler: RecyclerView.Recycler,
          state: RecyclerView.State
      ): Int {
          if (childCount <= 0) return dy
  
          var travel = dy
          if (mSumDy + dy < 0) {
              //到顶
              travel = -mSumDy
          } else if (mSumDy + dy >= totalHeight - getVerticalHeight()) {
              //到底
              travel = totalHeight - getVerticalHeight() - mSumDy
          }
  
          for (i in 0 until childCount) {
              val childView = getChildAt(i) ?: break
              if (dy > 0) {
                  //从下往上滑动
                  //回收顶部的item
                  if (getDecoratedBottom(childView) - travel <= 0) {
                      removeAndRecycleView(childView,recycler)
                      continue
                  }
              }else if (dy < 0){
                  //从上往下滑动
                  //回收底部的item
                  if (getDecoratedTop(childView) - travel >= height - paddingBottom) {
                      Log.d("TAG", "scrollVerticallyBy:回收底部的 " + i)
                      removeAndRecycleView(childView,recycler)
                      continue
                  }
              }
          }
  
          //底部view复用
          val legalScreenRect = getLegalScreenRect(travel)
  
          if (travel > 0) {
              //复用底部
              val lastShowView = getChildAt(childCount - 1)
              if (lastShowView != null) {
                  var position = getPosition(lastShowView) + 1
                  while (position < itemCount) {
                      if (rectMap[position] == null) break
                      if (legalScreenRect.intersect(rectMap[position]!!)) {
                          //存在交集，说明需要复用展示
                          val childView = recycler.getViewForPosition(position)
                          addView(childView)
                          measureChildWithMargins(childView,0,0)
                          layoutDecorated(childView,rectMap[position]!!.left,rectMap[position]!!.top - mSumDy,rectMap[position]!!.right,rectMap[position]!!.bottom - mSumDy)
                      } else {
                          break
                      }
                      position++
                  }
              }
          } else {
              //复用顶部
              val firstShowView = getChildAt(0)
              if (firstShowView != null) {
                  var position = getPosition(firstShowView) - 1
                  while (position >= 0) {
                      if (rectMap[position] == null) break
                      if (legalScreenRect.intersect(rectMap[position]!!)) {
                          //存在交集，说明需要复用展示
                          Log.d("TAG", "scrollVerticallyBy:复用顶部 " + position)
                          val childView = recycler.getViewForPosition(position)
                          addView(childView,0)
                          measureChildWithMargins(childView,0,0)
                          layoutDecorated(childView,rectMap[position]!!.left,rectMap[position]!!.top - mSumDy,rectMap[position]!!.right,rectMap[position]!!.bottom - mSumDy)
                      } else {
                          break
                      }
                      position--
                  }
              }
          }
  
          offsetChildrenVertical(-travel)
          mSumDy += travel
          return dy
      }
  
      private fun getLegalScreenRect(offset: Int): Rect {
          return Rect(paddingLeft,mSumDy + offset + paddingTop,width + paddingRight,mSumDy + offset + getVerticalHeight())
      }
  
      private fun getVerticalHeight(): Int {
          return height - paddingBottom - paddingTop
      }
  }
  ```

##### 回收复用二

- 回顾回收复用一的实现方式，利用offsetChildrenVertical（int dy）直接让RecycleView进行滚动dy距离，倘若需要对单个item在滚动的过程中产生一些动画效果逻辑，此时回收复用一显然不行
- 回收复用方法二：
  - 不使用offsetChildrenVertical对recycleview进行滚动处理操作
  - RecycleView中的scrollVerticallyBy重写函数中，当发生滚动行为时，先离屏清空所有正在展示的Item，然后滚动dy后，将滚动后应该出现的item在布局上去，即可实现不使用offsetChildrenVertical从而实现滚动效果
  - 回收复用一的方式是：先假设滚动后计算出应该展示的item（实际还未滚动），将他们布局出来，然后利用offsetChildrenVertical实现布局后的item滚动效果
  - 回收复用二的方式是：首先将屏幕上展示的item全部离屏清空掉，实际滚动了dy后，将滚动后应该展示的item布局到屏幕上即可，此时可对布局的item进行动画效果操作

###### onLayoutChildren

```kotlin
override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
    detachAndScrapAttachedViews(recycler)
    if (itemCount == 0) {
        return
    }
    val view = recycler.getViewForPosition(0)
    measureChildWithMargins(view,0,0)

    val itemWidth = getDecoratedMeasuredWidth(view)
    val itemHeight = getDecoratedMeasuredHeight(view)

    var offsetY = 0
    for (i in 0 until itemCount) {
        val rect = Rect(0,offsetY,itemWidth,offsetY + itemHeight)
        offsetY += itemHeight
        rectMap[i] = rect
    }

    val visiableCount = getVerticalHeight() / itemHeight
    for (i in 0 until visiableCount) {
        val childView = recycler.getViewForPosition(i)
        addView(childView)
        measureChildWithMargins(childView,0,0)
        layoutDecorated(childView,rectMap[i]!!.left,rectMap[i]!!.top,rectMap[i]!!.right,rectMap[i]!!.bottom)
    }
    totalHeight = max(offsetY,getVerticalHeight())
}
```

###### scrollVerticallyBy

- 离屏清空时，先获取当前屏幕显示的第一个与最后一个itemView

- 调用离屏清空detachAndScrapAttachedViews

- 当此次滚动travel，滚动的总距离mSumDy += travel

- 当前滚动后屏幕所在矩形位置：

  - ```kotlin
    return Rect(paddingLeft,mSumDy  + paddingTop,width + paddingRight,mSumDy  + getVerticalHeight())
    ```

- dy > 0 时：从下往上滚动，从先前获取到第一个View所在的position遍历，直到整个itemCount，判断交集与否，决定是否布局

- dy < 0 时：从上往下滚动，从先前获取到最后一个View所在的position遍历，直到position == 0， 判断交集与否，决定是否布局

- 布局时，设置Y轴方向的旋转角度                    **childView.rotationY = childView.rotationY + 1**

```kotlin
override fun scrollVerticallyBy(
    dy: Int,
    recycler: RecyclerView.Recycler,
    state: RecyclerView.State
): Int {
    if (childCount <= 0) return dy
    var travel = dy
    if (mSumDy + dy < 0) {
        //到顶
        travel = -mSumDy
    } else if (mSumDy + dy >= totalHeight - getVerticalHeight()) {
        //到底
        travel = totalHeight - getVerticalHeight() - mSumDy
    }
    mSumDy += travel
    val legalScreenRect = getLegalScreenRect()

    for (i in 0 until childCount) {
        val childView = getChildAt(i) ?: break
        if (dy > 0) {
            //从下往上滑动
            //回收顶部的item
            if (getDecoratedBottom(childView) - travel <= 0) {
                removeAndRecycleView(childView,recycler)
                continue
            }
        }else if (dy < 0){
            //从上往下滑动
            //回收底部的item
            if (getDecoratedTop(childView) - travel >= height - paddingBottom) {
                removeAndRecycleView(childView,recycler)
                continue
            }
        }
    }

    val firstView = getChildAt(0)
    val lastView = getChildAt(childCount - 1)

    detachAndScrapAttachedViews(recycler)

    //底部view复用
    if (travel > 0) {
        //复用底部
        if (firstView != null) {
            var position = getPosition(firstView)

            while (position < itemCount) {
                if (Rect.intersects(rectMap[position]!!,legalScreenRect)) {
                    //存在交集，说明需要复用展示
                    val childView = recycler.getViewForPosition(position)
                    addView(childView)
                    measureChildWithMargins(childView,0,0)
                    layoutDecorated(childView,rectMap[position]!!.left,rectMap[position]!!.top - mSumDy,rectMap[position]!!.right,rectMap[position]!!.bottom - mSumDy)
                    childView.rotationY = childView.rotationY + 1
                }
                position++
            }
        }
    }
    else {
        //复用顶部
        if (lastView != null) {
            var position = getPosition(lastView)
            while (position >= 0) {
                if (rectMap[position] == null) break
                if (Rect.intersects(rectMap[position]!!,legalScreenRect)) {
                    //存在交集，说明需要复用展示
                    val childView = recycler.getViewForPosition(position)
                    addView(childView,0)
                    measureChildWithMargins(childView,0,0)
                    layoutDecorated(childView,rectMap[position]!!.left,rectMap[position]!!.top - mSumDy,rectMap[position]!!.right,rectMap[position]!!.bottom - mSumDy)
                    childView.rotationY = childView.rotationY + 1
                }
                position--
            }
        }
    }
    return dy
}
```

##### 完整代码

```kotlin
class RepeatUseLayoutManagerTwo : LayoutManager() {

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    private var rectMap = mutableMapOf<Int,Rect>()
    var totalHeight = 0

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)
        if (itemCount == 0) {
            return
        }
        val view = recycler.getViewForPosition(0)
        measureChildWithMargins(view,0,0)

        val itemWidth = getDecoratedMeasuredWidth(view)
        val itemHeight = getDecoratedMeasuredHeight(view)

        var offsetY = 0
        for (i in 0 until itemCount) {
            val rect = Rect(0,offsetY,itemWidth,offsetY + itemHeight)
            offsetY += itemHeight
            rectMap[i] = rect
        }

        val visiableCount = getVerticalHeight() / itemHeight
        for (i in 0 until visiableCount) {
            val childView = recycler.getViewForPosition(i)
            addView(childView)
            measureChildWithMargins(childView,0,0)
            layoutDecorated(childView,rectMap[i]!!.left,rectMap[i]!!.top,rectMap[i]!!.right,rectMap[i]!!.bottom)
        }
        totalHeight = max(offsetY,getVerticalHeight())
    }

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }
    override fun canScrollVertically(): Boolean {
        return true
    }

    var mSumDy = 0
    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if (childCount <= 0) return dy
        var travel = dy
        if (mSumDy + dy < 0) {
            //到顶
            travel = -mSumDy
        } else if (mSumDy + dy >= totalHeight - getVerticalHeight()) {
            //到底
            travel = totalHeight - getVerticalHeight() - mSumDy
        }
        mSumDy += travel
        val legalScreenRect = getLegalScreenRect()

        for (i in 0 until childCount) {
            val childView = getChildAt(i) ?: break
            if (dy > 0) {
                //从下往上滑动
                //回收顶部的item
                if (getDecoratedBottom(childView) - travel <= 0) {
                    removeAndRecycleView(childView,recycler)
                    continue
                }
            }else if (dy < 0){
                //从上往下滑动
                //回收底部的item
                if (getDecoratedTop(childView) - travel >= height - paddingBottom) {
                    removeAndRecycleView(childView,recycler)
                    continue
                }
            }
        }

        val firstView = getChildAt(0)
        val lastView = getChildAt(childCount - 1)

        detachAndScrapAttachedViews(recycler)

        //底部view复用
        if (travel > 0) {
            //复用底部
            if (firstView != null) {
                var position = getPosition(firstView)

                while (position < itemCount) {
                    if (Rect.intersects(rectMap[position]!!,legalScreenRect)) {
                        //存在交集，说明需要复用展示
                        val childView = recycler.getViewForPosition(position)
                        addView(childView)
                        measureChildWithMargins(childView,0,0)
                        layoutDecorated(childView,rectMap[position]!!.left,rectMap[position]!!.top - mSumDy,rectMap[position]!!.right,rectMap[position]!!.bottom - mSumDy)
                        childView.rotationY = childView.rotationY + 1
                    }
                    position++
                }
            }
        }
        else {
            //复用顶部
            if (lastView != null) {
                var position = getPosition(lastView)
                while (position >= 0) {
                    if (rectMap[position] == null) break
                    if (Rect.intersects(rectMap[position]!!,legalScreenRect)) {
                        //存在交集，说明需要复用展示
                        val childView = recycler.getViewForPosition(position)
                        addView(childView,0)
                        measureChildWithMargins(childView,0,0)
                        layoutDecorated(childView,rectMap[position]!!.left,rectMap[position]!!.top - mSumDy,rectMap[position]!!.right,rectMap[position]!!.bottom - mSumDy)
                        childView.rotationY = childView.rotationY + 1
                    }
                    position--
                }
            }
        }
        return dy
    }

    private fun getLegalScreenRect(): Rect {
        return Rect(paddingLeft,mSumDy  + paddingTop,width + paddingRight,mSumDy  + getVerticalHeight())
    }

    private fun getVerticalHeight(): Int {
        return height - paddingBottom - paddingTop
    }
}
```

###### 效果展示![tutieshi_592x1142_11s.gif](https://s2.loli.net/2024/06/08/4CdasUBMYg8ZniR.gif)

#### ItemTouchHelper（RecycleView内部item拖拽帮助类）

- 拖拽是长按触发的操作Drag
- 滑动不是Swipe

##### 基本使用

- 创建类**ItemTouchHelperCallback**继承**ItemTouchHelperCallback**类

- **ItemTouchHelperCallback中必须重写的方法**

  - getMovementFlags（RecycleView，RecycleView.ViewHolder）：Int :设置Item可滑动、拖拽的标志位
  - onMove（RecycleView，RecycleView.ViewHolder，RecycleView.ViewHolder）：Boolean：拖拽View时触发的回调
  - onSwiped（RecycleView，RecycleView.ViewHolder）：Void：滑动View时触发的回调

- 创建ItemTouchHelper类，传递ItemTouchHelperCallback对象

- 将创建好的ItemTouchHelper类attachToRecyclerView（RecycleView）中

  - ```kotlin
    val touchHelper = ItemTouchHelper(ItemTouchHelperCallback(datas,apapter))
    touchHelper.attachToRecyclerView(binding.rv)
    ```

##### ItemTouchHelperCallback类

- getMovementFlags（RecycleView，RecycleView.ViewHolder）：Int :设置Item可滑动、拖拽的标志位

  - RecycleView：当前关联上的RecycleView。

  - RecycleView.ViewHolder：当前触发的RecycleView.ViewHolder

  - 返回可滑动、拖拽的标志位。一般通过makeMovementFlags（int dragFlags，int swipeFlags）

  - ItemTouchHelper.UP、ItemTouchHelper.DOWN、ItemTouchHelper.LEFT、ItemTouchHelper.RIGHT、ItemTouchHelper.START、ItemTouchHelper.END

  - 垂直方向进行拖拽，水平方向进行滑动：

  - ```kotlin
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
        val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags,swipeFlags)
    }
    ```

- onMove（RecycleView，RecycleView.ViewHolder srcHolder，RecycleView.ViewHolder targetHolder）：Boolean：拖拽View时触发的回调
  - RecycleView：当前关联上的RecycleView。
  - srcHolder：当前正在Drag触发的ItemView的RecycleView.ViewHolder
  - targetHolder：当前正在Drag停止的目标位置的Holder
  - return值代表是否触发onMoved回调
- onMoved（RecycleView，RecycleView.ViewHolder srcHolder，int fromPos，RecycleView.ViewHolder targetHolder，int toPos，int x，int y）：Void
  - RecycleView：当前关联上的RecycleView。
  - srcHolder：当前正在Drag触发的ItemView的RecycleView.ViewHolder
  - fromPos：拖拽起始位置
  - targetHolder：当前正在Drag停止的目标位置的Holder
  - toPos：拖拽终止位置
  - x：拖拽过后最新的位置X坐标（非相对于屏幕）
  - y：拖拽过后最新的位置Y坐标（非相对于屏幕）
- onSwiped（RecycleView.ViewHolder，int direction）：Void
  - 滑动时触发的回调
  - RecycleView.ViewHolder：当前滑动的ItemView
  - direction：滑动的方向
- onSelectedChanged（RecycleView.ViewHolder，int actionState）：Void
  - 滑动或者拖动状态改变的回调
  - RecycleView.ViewHolder：当前状态值改变的ItemView
  - actionState：状态值
    -  ItemTouchHelper.ACTION_STATE_SWIPE：滑动状态
    -  ItemTouchHelper.ACTION_STATE_DRAG：拖动状态
    -  ItemTouchHelper.ACTION_STATE_IDLE：闲置状态
  - 当滑动时，会回调二次
    - 滑动时： ItemTouchHelper.ACTION_STATE_SWIPE
    - 滑动结束： ItemTouchHelper.ACTION_STATE_IDLE
  - 当拖动时，会回调二次
    - 滑动时： ItemTouchHelper.ACTION_STATE_DRAG
    - 滑动结束： ItemTouchHelper.ACTION_STATE_IDLE
- clearView（RecycleView，RecycleView.ViewHolder）：Void
  - 拖拽结束后触发的回调
  - 通常在拖拽过程中可能会改变itemView的样式，当拖拽结束后需要恢复样式，则在此回调中写逻辑即可
- isLongPressDragEnabled（）：Boolean
  - 是否关闭长按拖拽功能
  - **注意：拖拽能力真正总开关取决于getMovementFlags中的返回值，isLongPressDragEnabled相对于分开关，只有当总开关开启时，分开关才有意义，否则分开关取值无意义**
  - **常常搭配ItemTouchHelper类对象.startDrag(ViewHolder)去对某个特定的区域触发才进行拖拽效果**
- isItemViewSwipeEnabled（）：Boolean
  - 是否关闭滑动功能
  - **注意：拖拽能力真正总开关取决于getMovementFlags中的返回值，isItemViewSwipeEnabled相对于分开关，只有当总开关开启时，分开关才有意义，否则分开关取值无意义**
  - **常常搭配ItemTouchHelper类对象.startSwipe(ViewHolder)去对某个特定的区域触发才进行拖拽效果**
- **onChildDraw（Canvas，RecycleView，RecycleView.ViewHolder，float dx，float dy，int actionState，Boolean isCurrentActively）**
  - **ItemView在滑动、拖拽的过程中触发的回调函数（对ItemView进行操作）**
  - Canvas：可利用此对象在滑动、拖拽操作中对itemView进行绘制操作
  - RecycleView：当前关联的RecycleView
  - RecycleView.ViewHolder：当前操作的ItemView
  - dx：水平方向相对于原始的移动距离，右正左负
  - dy：垂直方向相对于原始的移动距离，上负下正
  - actionState：itemView的操作状态（拖拽、滑动、闲置）
  - isCurrentActively：是否是用户触发的动画操作（用户取消操作的归位动画为false）
- onChildDrawOver（Canvas，RecycleView，RecycleView.ViewHolder，float dx，float dy，int actionState，Boolean isCurrentActively）
  - **ItemView在滑动、拖拽的过程中触发的回调函数（对RecycleView进行操作）**
  - Canvas：可利用此对象在滑动、拖拽操作中对itemView进行绘制操作
  - RecycleView：当前关联的RecycleView
  - RecycleView.ViewHolder：当前操作的ItemView
  - dx：水平方向相对于原始的移动距离，右正左负
  - dy：垂直方向相对于原始的移动距离，上负下正
  - actionState：itemView的操作状态（拖拽、滑动、闲置）
  - isCurrentActively：是否是用户触发的动画操作（用户取消操作的归位动画为false）
- getSwipeThreshold（RecycleView.ViewHolder）：Float
  - RecycleView.ViewHolder：当前操作的ItemView
  - 返回值是swipe滑动超过的阈值，超过阈值即触发onSwiped回调
- getSwipeEscapeVelocity（defaultValue: Float）：Float
  - defaultValue：滑动操作的逃逸速度
  - 返回值是swipe操作的逃逸速度，超过逃逸速度即使没用超过上述设置的阈值也会触发onSwiped回调
- getSwipeVelocityThreshold（defaultValue: Float）：Float
  - defaultValue：滑动操作的阻尼系数
  - 返回值是swipe操作的最大滑动速度
- canDropOver（RecycleView，RecycleView.ViewHolder currentViewHolder,RecycleView.ViewHolder targetViewHolder）：Boolean
  - **拖拽操作的限制条件，返回值判断某个itemView是否可以被拖拽（当前targetViewHolder是否可被拖拽交换）**
  - RecycleView：当前关联的RecycleView
  - currentViewHolder：当前操作的ItemView
  - targetViewHolder：拖拽之后的ItemView
- getBoundingBoxMargin（）：Int
  - 针对拖拽操作，被拖拽itemView的设置额外的margin，增大拖拽区域
- getMoveThreshold（RecycleView.ViewHolder）：Float
  - 拖拽阈值
  - RecycleView.ViewHolder：当前操作的ItemView
  - 返回值是swipe拖拽超过的阈值，超过阈值即触发onMove回调

##### 需求实现

- 效果展示
- ![tutieshi_528x1076_11s-min.gif](https://s2.loli.net/2024/06/09/JdLoAkOx4XP8mw7.gif)

- 需求分析：

  - 网格布局的LayouManager，其中ItemView可向四个方向进行拖拽，并且可滑动删除的效果
  - 拖拽时选择itemView变成蓝色，拖拽结束变为原色
  - 滑动时选择ItemView变为红色，滑动后变为原色

- 代码实现

  - datas：数据源、adapter：RecycleView的adapter  构造函数传递过来

  - getMovementFlags返回可拖拽、滑动的标志位

    - ```kotlin
      override fun getMovementFlags(
          recyclerView: RecyclerView,
          viewHolder: RecyclerView.ViewHolder
      ): Int {
          val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
          val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
          return makeMovementFlags(dragFlags,swipeFlags)
      }
      ```

  - onSwiped中对数据源删除，更新adapter

    - ```kotlin
      override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
          datas.removeAt(viewHolder.adapterPosition)
          adapter.notifyItemRemoved(viewHolder.adapterPosition)
      }
      ```

  - onMove中对数据源进行交换操作，更新adapter

    - ```kotlin
      override fun onMove(
          recyclerView: RecyclerView,
          viewHolder: RecyclerView.ViewHolder,
          target: RecyclerView.ViewHolder
      ): Boolean {
          Collections.swap(datas,viewHolder.adapterPosition,target.adapterPosition)
          adapter.notifyItemMoved(viewHolder.adapterPosition,target.adapterPosition)
          //决定是否回调onMoved函数
          return true
      }
      ```

  - onSelectedChanged中对拖拽的View改变样式变为蓝色，滑动的变为红色

    - ```kotlin
      override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
          super.onSelectedChanged(viewHolder, actionState)
          if (viewHolder == null || viewHolder !is RvAdapter.MyHolder) return
          if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
              Log.d("TAG", "onSelectedChanged: ACTION_STATE_SWIPE")
              viewHolder.bgView.setBackgroundColor(Color.RED)
          } else if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
              Log.d("TAG", "onSelectedChanged: ACTION_STATE_DRAG")
              viewHolder.bgView.setBackgroundColor(Color.BLUE)
          } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
              Log.d("TAG", "onSelectedChanged: ACTION_STATE_IDLE")
          }
      }
      ```

  - clearView中对View的释放时将其恢复成原色

    - ```kotlin
      override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
          super.clearView(recyclerView, viewHolder)
          if (viewHolder !is RvAdapter.MyHolder) return
          viewHolder.bgView.setBackgroundColor(Color.parseColor("#4CAF50"))
      }
      ```

  - 将上述ItemTouchHelperCallback类关联到RecycleView即可

    - ```kotlin
      val touchHelper = ItemTouchHelper(ItemTouchHelperCallback(datas,apapter))
      touchHelper.attachToRecyclerView(binding.rv)
      ```

##### 需求实现

- 效果展示
- ![tutieshi_528x1076_9s.gif](https://s2.loli.net/2024/06/09/ZEknrAPMT13I5ua.gif)

- 需求分析

  - 拖拽时对拖拽的区域有了判断，只有当拖拽右侧图片时，才能进行拖拽操作，否则无拖拽效果

- 代码实现

  - getMovementFlags返回可拖拽、滑动的标志位（未变，可拖动可滑动）

    - ```kotlin
      override fun getMovementFlags(
          recyclerView: RecyclerView,
          viewHolder: RecyclerView.ViewHolder
      ): Int {
          val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
          val swipeFlags = ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
          return makeMovementFlags(dragFlags,swipeFlags)
      }
      ```

  - isLongPressDragEnabled中临时关闭拖动开关

    - ```kotlin
      override fun isLongPressDragEnabled(): Boolean {
          return false
      }
      ```

  - Adapter中对右侧图片监听touch事件

    - ```kotlin
      var onOperationTouchAction: ((MyHolder) -> Unit)? = null
      
      holder.imgOperation.setOnTouchListener { v, event ->
          if (event.action == MotionEvent.ACTION_DOWN) {
              onOperationTouchAction?.invoke(holder)
          }
          false
      }
      ```

  - startDrag手动开启拖动即可

    - ```kotlin
      val touchHelper = ItemTouchHelper(ItemTouchHelperCallback(datas,adapter))
      adapter.onOperationTouchAction = {
          touchHelper.startDrag(it)
      }
      touchHelper.attachToRecyclerView(binding.rv)
      ```

  - 同理，滑动类似效果也是相同，改变isItemViewSwipeEnabled即可

    - ```
      override fun isItemViewSwipeEnabled(): Boolean {
          return false
      }
      touchHelper.startSwipe(it) //即可（it ==> ViewHolder）
      
      ```

##### 需求实现

- 效果展示
- ![tutieshi_528x1076_9s _1_-min.gif](https://s2.loli.net/2024/06/09/A5beEWoIPcXVQFO.gif)

- 需求分析

  - itemView在滑动删除的过程中自定义了一些动画效果（旋转，缩放，透明度）

- 代码实现

  - onChildDraw回调中

    - 水平滑动过程中，dx绝对值是慢慢变化到与viewHolder.itemView.width相同的值

    - **super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)代码的作用是使Item向左或者向右移动的动画效果，注释掉改行的话，itemView会在原地进行缩放、透明度、旋转动画效果删除。并不会向左或者向右进行移动效果**

    - ```ko
      override fun onChildDraw(
          c: Canvas,
          recyclerView: RecyclerView,
          viewHolder: RecyclerView.ViewHolder,
          dX: Float,
          dY: Float,
          actionState: Int,
          isCurrentlyActive: Boolean
      ) {
          super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
          val alpha = 1 - abs(dX) / viewHolder.itemView.width
          viewHolder.itemView.alpha = alpha
          viewHolder.itemView.scaleX = alpha
          viewHolder.itemView.rotation = viewHolder.itemView.rotation + 1
      }
      ```

##### 需求实现

- 效果展示
- ![tutieshi_640x285_9s.gif](https://s2.loli.net/2024/06/10/JNrl3b5WFESazsU.gif)

- 需求分析

  - 横向滚动的画廊效果LayoutManager
  - 初始状态：
    - 每张图片都展示一半相互折叠在一块
    - 第一张照片位于屏幕正中央
  - 滚动时
    - 对itemView进行缩放动画
    - 对itemView绘制顺序保证最中间显示的最后绘制（达到叠加的效果）

- 代码实现

  - 实现基本叠加效果如图所示![tutieshi_640x285_4s.gif](https://s2.loli.net/2024/06/10/Qwt5JhNqe4sHr8C.gif)

    - 自定义LayoutManager中onLayoutChildren回调中将其布局成横向滚动

    - rectMap：保存每个ItemView所在的Rect位置

    - totalWidth：Recycle展示全部的总宽度

    - halfItemWidth：itemView宽度的一半

    - startOffset：刚开始首个itemView的起始偏移位置（居中展示）

    - ```kotlin
      private val rectMap = mutableMapOf<Int,Rect>()	
      private var totalWidth = 0
      private var halfItemWidth = 0
      override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
          detachAndScrapAttachedViews(recycler)
          if (itemCount == 0) {
              return
          }
          val firstView = recycler.getViewForPosition(0)
          measureChildWithMargins(firstView,0,0)
      
          val itemWidth = getDecoratedMeasuredWidth(firstView)
          val itemHeight = getDecoratedMeasuredHeight(firstView)
      
          halfItemWidth = itemWidth / 2
      
          val startOffset = width / 2 - halfItemWidth
          Log.d("TAG", "onLayoutChildren:startOffset " + startOffset)
      
          var offsetX = 0
          for (i in 0 until itemCount) {
              // Rect(offsetX + startOffset,0,offsetX + startOffset + itemWidth,itemHeight)
              val rect = Rect(offsetX + startOffset,0,offsetX + startOffset + itemWidth,itemHeight)
              rectMap[i] = rect
              offsetX += halfItemWidth
          }
      
          val visiableCount = getHorizontallyWidth() / halfItemWidth
      
          for (i in 0 until visiableCount) {
              val childView = recycler.getViewForPosition(i)
              addView(childView)
              measureChildWithMargins(childView,0,0)
              layoutDecorated(childView,rectMap[i]!!.left,rectMap[i]!!.top,rectMap[i]!!.right,rectMap[i]!!.bottom)
          }
          totalWidth = max(offsetX + startOffset + halfItemWidth * (visiableCount + 1) / 2 ,getHorizontallyWidth())
      }
      ```

    - scrollHorizontallyBy函数中

    - ```kotlin
      override fun scrollHorizontallyBy(
              dx: Int,
              recycler: RecyclerView.Recycler,
              state: RecyclerView.State?
          ): Int {
              if (childCount <= 0) return dx
              var travel = dx
              if (mSumDx + dx < 0) {
                  //到左边界
                  travel = -mSumDx
              } else if (mSumDx + dx >= totalWidth - getHorizontallyWidth()) {
                  //到右边界
                  travel = totalWidth - getHorizontallyWidth() - mSumDx
              }
              mSumDx += travel
              val legalScreenRect = getLegalScreenRect()
      
      
              for (i in 0 until childCount) {
                  val childView = getChildAt(i) ?: break
                  if (dx > 0) {
                      //从右往左滑动
                      //回收左部的item
                      if (getDecoratedRight(childView) - travel <= 0) {
                          removeAndRecycleView(childView,recycler)
                          continue
                      }
                  }else if (dx < 0){
                      //从左往右滑动
                      //回收底部的item
                      if (getDecoratedLeft(childView) - travel >= width - paddingRight) {
                          removeAndRecycleView(childView,recycler)
                          continue
                      }
                  }
              }
      
              val firstView = getChildAt(0)
              val lastView = getChildAt(childCount - 1)
      
              detachAndScrapAttachedViews(recycler)
      
              //底部view复用
              if (travel > 0) {
                  //复用底部
                  if (firstView != null) {
                      var position = getPosition(firstView)
      
                      while (position < itemCount) {
                          if (Rect.intersects(rectMap[position]!!,legalScreenRect)) {
                              //存在交集，说明需要复用展示
                              val childView = recycler.getViewForPosition(position)
                              addView(childView)
                              measureChildWithMargins(childView,0,0)
                              layoutDecorated(childView,rectMap[position]!!.left  - mSumDx,rectMap[position]!!.top,rectMap[position]!!.right  - mSumDx,rectMap[position]!!.bottom)
      
                              val startOffset = width / 2 - halfItemWidth
                              handlerChildView(childView,rectMap[position]!!.left - mSumDx - startOffset)
                          }
                          position++
                      }
                  }
              }
              else {
                  //复用顶部
                  if (lastView != null) {
                      var position = getPosition(lastView)
                      while (position >= 0) {
                          if (rectMap[position] == null) break
                          if (Rect.intersects(rectMap[position]!!,legalScreenRect)) {
                              //存在交集，说明需要复用展示
                              val childView = recycler.getViewForPosition(position)
                              addView(childView,0)
                              measureChildWithMargins(childView,0,0)
                              layoutDecorated(childView,rectMap[position]!!.left - mSumDx,rectMap[position]!!.top,rectMap[position]!!.right - mSumDx,rectMap[position]!!.bottom)
      
                              val startOffset = width / 2 - halfItemWidth
                              handlerChildView(childView,rectMap[position]!!.left - mSumDx - startOffset)
                          }
                          position--
                      }
                  }
              }
              return dx
          }
      ```

    - 滑动时，itemView动画效果实现

    - ```kotlin
      private fun handlerChildView(childView: View,moveX: Int) {
          var scale = 1 - abs(moveX * 1.0f / (8f * halfItemWidth))
          if (scale < 0) {
              scale = 0F
          }
          if (scale > 1) {
              scale = 1F
          }
          childView.scaleX = scale
          childView.scaleY = scale
      }
      ```

    - 滑动时，绘制顺序更改。比如当滑动到第三个itemView（position == 2）时，各itemView下标：0，1，2，3，4，5，6

      - 绘制顺序：0，1，2，6，5，4，3
      - 即将中间的最后绘制，达到盖上的效果
      - **RecycleView中的getChildDrawingOrder回调函数**
      - **getChildDrawingOrder（childCount: Int, i: Int）：Int**
        - **childCount：当前屏幕展示的itemCount数量**
        - **i：当前item的下标**
        - **返回值越大，代表越后绘制，越小，越先绘制**
        - **绘制顺序：**
          - **i == 0 retunr 0**
          - **i == 1 return 1**
          - **i == 2 return 2**
          - **i ==3 return 6**
          - **i == 4 return 5**
          - **i == 5 return 4**
          - **i == 6 return 3**
        - **总结：找到中间itemView的下标centerIndex**
          - **i == centerIndex return childCount - 1 （最后被绘制）**
          - **i < centerIndex return i**
          - **i > centerIndex return centerIndex + childCount - 1 - i**
        - **注意：当自定义layoutmanager触发滚动回调时，也会触发recycleView的getChildDrawingOrder回调**
          - **前提：设置RecycleView  isChildrenDrawingOrderEnabled = true**

    - 自定义GalleryRecycleView继承RecycleView

      - 中间itemView的下标index获取 LayoutManager中

        - ```kotlin
          fun getCenterPosition(): Int {
              val centerPosition = mSumDx / halfItemWidth
              val remainder = mSumDx % halfItemWidth
              val result = if (remainder >= halfItemWidth / 2) {
                  centerPosition + 1
              } else {
                  centerPosition
              }
              return result
          }
          
          fun getFirstVisiabViewPosition(): Int {
              val firstView = getChildAt(0)
              return getPosition(firstView!!)
          }
          ```

      - ```kotlin
        fun getCustomLayoutManager(): RepeatUseLayoutManagerForHorizontally? {
            if (layoutManager != null) {
                return layoutManager as RepeatUseLayoutManagerForHorizontally
            }
            return layoutManager
        }
        
        override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
            val manager = getCustomLayoutManager()
            manager?.let {
                val centerIndex = it.getCenterPosition() - it.getFirstVisiabViewPosition()
        
                var order = 0
                if (centerIndex == i) {
                    order = childCount - 1
                } else if (centerIndex > i) {
                    order = i
                } else {
                    order = centerIndex + childCount - 1 - i
                }
                return order
            }
            return super.getChildDrawingOrder(childCount, i)
        }
        ```

      - ```kotlin
        init {
            isChildrenDrawingOrderEnabled = true
        }
        ```

    - 完整代码

      - ```xml
        <com.jin.rv.main.recycleView.GalleryRecycleView
            android:id="@+id/rv_gallery"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:layout_constraintBottom_toBottomOf="parent"/>
        ```

      - 自定义RecycleView

      - ```kotlin
        class GalleryRecycleView @JvmOverloads constructor(context: Context,attributeSet: AttributeSet? = null,defInt: Int = 0): RecyclerView(context,attributeSet,defInt) {
        
        
            init {
                isChildrenDrawingOrderEnabled = true
            }
        
        
            fun getCustomLayoutManager(): RepeatUseLayoutManagerForHorizontally? {
                if (layoutManager != null) {
                    return layoutManager as RepeatUseLayoutManagerForHorizontally
                }
                return layoutManager
            }
        
            override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
                val manager = getCustomLayoutManager()
                manager?.let {
                    val centerIndex = it.getCenterPosition() - it.getFirstVisiabViewPosition()
        
                    var order = 0
                    if (centerIndex == i) {
                        order = childCount - 1
                    } else if (centerIndex > i) {
                        order = i
                    } else {
                        order = centerIndex + childCount - 1 - i
                    }
                    return order
                }
                return super.getChildDrawingOrder(childCount, i)
            }
        }
        ```

      - 自定义LayoutManager

      - ```kotlin
        class RepeatUseLayoutManagerForHorizontally : LayoutManager() {
        
            override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
                return RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.WRAP_CONTENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
                )
            }
        
            private val rectMap = mutableMapOf<Int,Rect>()
            var totalWidth = 0
            var halfItemWidth = 0
        
            override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
                detachAndScrapAttachedViews(recycler)
                if (itemCount == 0) {
                    return
                }
                val firstView = recycler.getViewForPosition(0)
                measureChildWithMargins(firstView,0,0)
        
                val itemWidth = getDecoratedMeasuredWidth(firstView)
                val itemHeight = getDecoratedMeasuredHeight(firstView)
        
                halfItemWidth = itemWidth / 2
        
                val startOffset = width / 2 - halfItemWidth
                Log.d("TAG", "onLayoutChildren:startOffset " + startOffset)
        
                var offsetX = 0
                for (i in 0 until itemCount) {
                    val rect = Rect(offsetX + startOffset,0,offsetX + startOffset + itemWidth,itemHeight)
                    rectMap[i] = rect
                    offsetX += halfItemWidth
                }
        
                val visiableCount = getHorizontallyWidth() / halfItemWidth
        
                for (i in 0 until visiableCount) {
                    val childView = recycler.getViewForPosition(i)
                    addView(childView)
                    measureChildWithMargins(childView,0,0)
                    layoutDecorated(childView,rectMap[i]!!.left,rectMap[i]!!.top,rectMap[i]!!.right,rectMap[i]!!.bottom)
                }
                totalWidth = max(offsetX + startOffset + halfItemWidth * (visiableCount + 1) / 2 ,getHorizontallyWidth())
            }
        
            override fun isAutoMeasureEnabled(): Boolean {
                return true
            }
        
            override fun canScrollHorizontally(): Boolean {
                return true
            }
        
        
            var mSumDx = 0
        
        
            fun getCenterPosition(): Int {
                val centerPosition = mSumDx / halfItemWidth
                val remainder = mSumDx % halfItemWidth
                val result = if (remainder >= halfItemWidth / 2) {
                    centerPosition + 1
                } else {
                    centerPosition
                }
                return result
            }
        
            fun getFirstVisiabViewPosition(): Int {
                val firstView = getChildAt(0)
                return getPosition(firstView!!)
            }
        
        
            override fun scrollHorizontallyBy(
                dx: Int,
                recycler: RecyclerView.Recycler,
                state: RecyclerView.State?
            ): Int {
                if (childCount <= 0) return dx
                var travel = dx
                if (mSumDx + dx < 0) {
                    //到左边界
                    travel = -mSumDx
                } else if (mSumDx + dx >= totalWidth - getHorizontallyWidth()) {
                    //到右边界
                    travel = totalWidth - getHorizontallyWidth() - mSumDx
                }
                mSumDx += travel
                val legalScreenRect = getLegalScreenRect()
        
        
                for (i in 0 until childCount) {
                    val childView = getChildAt(i) ?: break
                    if (dx > 0) {
                        //从右往左滑动
                        //回收左部的item
                        if (getDecoratedRight(childView) - travel <= 0) {
                            removeAndRecycleView(childView,recycler)
                            continue
                        }
                    }else if (dx < 0){
                        //从左往右滑动
                        //回收底部的item
                        if (getDecoratedLeft(childView) - travel >= width - paddingRight) {
                            removeAndRecycleView(childView,recycler)
                            continue
                        }
                    }
                }
        
                val firstView = getChildAt(0)
                val lastView = getChildAt(childCount - 1)
        
                detachAndScrapAttachedViews(recycler)
        
                //底部view复用
                if (travel > 0) {
                    //复用底部
                    if (firstView != null) {
                        var position = getPosition(firstView)
        
                        while (position < itemCount) {
                            if (Rect.intersects(rectMap[position]!!,legalScreenRect)) {
                                //存在交集，说明需要复用展示
                                val childView = recycler.getViewForPosition(position)
                                addView(childView)
                                measureChildWithMargins(childView,0,0)
                                layoutDecorated(childView,rectMap[position]!!.left  - mSumDx,rectMap[position]!!.top,rectMap[position]!!.right  - mSumDx,rectMap[position]!!.bottom)
        
                                val startOffset = width / 2 - halfItemWidth
                                handlerChildView(childView,rectMap[position]!!.left - mSumDx - startOffset)
                            }
                            position++
                        }
                    }
                }
                else {
                    //复用顶部
                    if (lastView != null) {
                        var position = getPosition(lastView)
                        while (position >= 0) {
                            if (rectMap[position] == null) break
                            if (Rect.intersects(rectMap[position]!!,legalScreenRect)) {
                                //存在交集，说明需要复用展示
                                val childView = recycler.getViewForPosition(position)
                                addView(childView,0)
                                measureChildWithMargins(childView,0,0)
                                layoutDecorated(childView,rectMap[position]!!.left - mSumDx,rectMap[position]!!.top,rectMap[position]!!.right - mSumDx,rectMap[position]!!.bottom)
        
                                val startOffset = width / 2 - halfItemWidth
                                handlerChildView(childView,rectMap[position]!!.left - mSumDx - startOffset)
                            }
                            position--
                        }
                    }
                }
                return dx
            }
        
            private fun handlerChildView(childView: View,moveX: Int) {
                var scale = 1 - abs(moveX * 1.0f / (8f * halfItemWidth))
                if (scale < 0) {
                    scale = 0F
                }
                if (scale > 1) {
                    scale = 1F
                }
                childView.scaleX = scale
                childView.scaleY = scale
            }
        
        
        
            private fun getLegalScreenRect(): Rect {
                return Rect(paddingLeft + mSumDx,  paddingTop,width - paddingRight + mSumDx, height - paddingBottom)
            }
        
            private fun getHorizontallyWidth(): Int {
                return width - paddingLeft - paddingRight
            }
        }
        ```

      - MainActivity

      - ```kotlin
        binding.rvGallery.layoutManager = RepeatUseLayoutManagerForHorizontally()
        binding.rvGallery.adapter = GalleryAdapter(this)
        ```

      - GalleryAdapter

      - ```kotlin
        class GalleryAdapter(private val context: Context) : RecyclerView.Adapter<GalleryAdapter.MyHolder>() {
        
            var count = 0
        
            private var imgs = listOf(
                R.drawable.img_0,
                R.drawable.img_1,
                R.drawable.img_2,
                R.drawable.img_3,
                R.drawable.img_4,
                R.drawable.img_5,
                R.drawable.img_2,
                )
        
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
                count++
                Log.d("TAG", "onCreateViewHolder: ${count}")
                return MyHolder(ItemGalleryBinding.inflate(LayoutInflater.from(context),parent,false))
            }
        
            override fun getItemCount(): Int {
                return 7
            }
        
            override fun onBindViewHolder(holder: MyHolder, position: Int) {
                Log.d("TAG", "onBindViewHolder: ${count}")
                holder.bgView.text = "item ${position + 1}"
                holder.imgGallery.setImageResource(imgs[position % imgs.size])
            }
        
        
            class MyHolder(val binding: ItemGalleryBinding) : ViewHolder(binding.root) {
                val bgView = binding.viewBg
                val imgGallery = binding.imgGallery
            }
        }
        ```

      - itemView

      - ```xml
        <?xml version="1.0" encoding="utf-8"?>
        <androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            xmlns:app="http://schemas.android.com/apk/res-auto">
        
            <TextView
                android:id="@+id/view_bg"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="这是一段测试用例"
                android:gravity="center"
                android:textColor="@color/black"
                app:layout_constraintTop_toTopOf="parent"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"/>
        
        
            <ImageView
                android:id="@+id/img_gallery"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:scaleType="fitXY"
                android:layout_marginTop="10dp"
                app:layout_constraintTop_toBottomOf="@id/view_bg"
                app:layout_constraintStart_toStartOf="parent" />
        
        </androidx.constraintlayout.widget.ConstraintLayout>
        ```
