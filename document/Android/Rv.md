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

##### 回收复用实现

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
