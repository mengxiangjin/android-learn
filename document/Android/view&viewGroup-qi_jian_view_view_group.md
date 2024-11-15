### View与ViewGroup

#### View

##### onMeasure（宽度为例）

- **思考：自定义view时，宽度为wrap_content,为何需要重写onMeasure方法给其测量布局设置setMeasureDimen()设置真正宽度**

- view默认的onMeasure方法

  - widthMeasureSpec、heightMeasureSpec：父容器传递过来的值

  - ```java
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
       
     	setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }
    ```

  - 调用getDefaultSize（）获取宽度、高度设置进去

  - 当父容器传递的measureSpec的mode不为MeasureSpec.UNSPECIFIED时，子view测量宽度返回MeasureSpec.getSize(measureSpec)，即父容器传递的size

  - ```java
    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
    
        switch (specMode) {
        case MeasureSpec.UNSPECIFIED:
            result = size;
            break;
        case MeasureSpec.AT_MOST:
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
        }
        return result;
    }
    ```

- **父容器测量子view传递过来的measureSpec**

  - 拿到子view的getLayoutParams()，通过getChildMeasureSpec函数获取宽度（parentWidthMeasureSpec，totalPadding，lp.width）

  - lp.width可能取值：

    - MATCH_PARENT：-1
    - WRAP_CONTENT：-2
    - 确切值：>0

  - ```java
    protected void measureChild(View child, int parentWidthMeasureSpec,
            int parentHeightMeasureSpec) {
        final LayoutParams lp = child.getLayoutParams();
    
        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                mPaddingLeft + mPaddingRight, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                mPaddingTop + mPaddingBottom, lp.height);
    
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }
    ```

  - 通过getChildMeasureSpec获取子view的值

  - ```java
    public static int getChildMeasureSpec(int spec, int padding, int childDimension) {
        int specMode = MeasureSpec.getMode(spec);
        int specSize = MeasureSpec.getSize(spec);
    
        int size = Math.max(0, specSize - padding);
    
        int resultSize = 0;
        int resultMode = 0;
    
        switch (specMode) {
        // Parent has imposed an exact size on us
        case MeasureSpec.EXACTLY:
            if (childDimension >= 0) {
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size. So be it.
                resultSize = size;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size. It can't be
                // bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;
    
        // Parent has imposed a maximum size on us
        case MeasureSpec.AT_MOST:
            if (childDimension >= 0) {
                // Child wants a specific size... so be it
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size, but our size is not fixed.
                // Constrain child to not be bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size. It can't be
                // bigger than us.
                resultSize = size;
                resultMode = MeasureSpec.AT_MOST;
            }
            break;
    
        // Parent asked to see how big we want to be
        case MeasureSpec.UNSPECIFIED:
            if (childDimension >= 0) {
                // Child wants a specific size... let them have it
                resultSize = childDimension;
                resultMode = MeasureSpec.EXACTLY;
            } else if (childDimension == LayoutParams.MATCH_PARENT) {
                // Child wants to be our size... find out how big it should
                // be
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            } else if (childDimension == LayoutParams.WRAP_CONTENT) {
                // Child wants to determine its own size.... find out how
                // big it should be
                resultSize = View.sUseZeroUnspecifiedMeasureSpec ? 0 : size;
                resultMode = MeasureSpec.UNSPECIFIED;
            }
            break;
        }
        //noinspection ResourceType
        return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
    }
    ```

  - **总结起来：子View的size与Mode**

    | 子View/父View | EXACTLY            | AT_MOST            | UNSPECIFIED            |
    | ------------- | ------------------ | ------------------ | ---------------------- |
    | 固定数值      | childSize/EXACTLY  | childSize/EXACTLY  | childSize/EXACTLY      |
    | match_parent  | parentSize/EXACTLY | parentSize/AT_MOST | parentSize/UNSPECIFIED |
    | wrap_content  | parentSize/AT_MOST | parentSize/AT_MOST | parentSize/UNSPECIFIED |

  - 当子View为固定数值时，onMeasure默认即设置固定数值、模式EXACTLY。此时无需重写onMeasure即可

  - 当子View为match_parent时，onMeasure默认即设置为与parentSize同样大小

  - 当子View为wrap_parent时，onMeasure默认即设置为与parentSize同样大小，此时需要重写onMeasure设置自定义尺寸

##### onLayout

- **view无需重写onLayout函数**

##### onDraw

- drawBackground(canvas)：绘制背景
- onDraw(canvas)：绘制自身（View类是空实现，其子类具体实现）
- dispatchDraw(canvas)：绘制子view（View是空实现，其子类ViewGroup具体实现）
- onDrawForeground(canvas)：绘制滚动条（实际每个View都有其滚动条，只是隐藏罢了）

##### 总结

- **当重写View时，若view宽度或高度为wrap_content、需重写onMeasure，设置宽度高度，否则会match_parent**
- **重写onDraw函数绘制自身逻辑**

##### 示例

- 自定义View绘制Bitmap，宽度高度为wrap_content,背景色为黑色

- ```kotlin
  override fun onDraw(canvas: Canvas) {
      val bitmap = BitmapFactory.decodeResource(resources, R.drawable.img)
      canvas.drawBitmap(bitmap,0f,0f,paint)
  }
  ```

- ```xml
  <com.jin.matrix.widgit.CustomView
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:background="@color/black"/>
  ```

- ![image-20240330102724010](https://s2.loli.net/2024/03/30/P24IvUJxtdeQqGm.png)

- **当宽度高度为wrap_content,又未重写onMeasure设置具体宽度高度时，其view大小为match_parent**

- **重写onMeasure函数，依据绘制的Bitmap设置宽高度（如果为wrap_content）**

- ```kotlin
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec)
      val widthMode = MeasureSpec.getMode(widthMeasureSpec)
      val widthSize = MeasureSpec.getSize(widthMeasureSpec)
  
      val heightMode = MeasureSpec.getMode(heightMeasureSpec)
      val heightSize = MeasureSpec.getMode(heightMeasureSpec)
  
      if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
          setMeasuredDimension(bitmap.width,bitmap.height)
      } else if (widthMode == MeasureSpec.AT_MOST) {
          setMeasuredDimension(bitmap.width,heightSize)
      } else {
          setMeasuredDimension(widthSize,bitmap.height)
      }
  }
  ```

- ![image-20240330103611119](https://s2.loli.net/2024/03/30/bgKoZWzPuJNdwFl.png)

#### ViewGroup

##### 自定义ViewGroup

- 自定义ViewGroup

- 发现未正常展示子控件ImageView，发现无视图显示

- ```xml
  <com.jin.matrix.widgit.CustomViewGroup
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"                                		      android:background="#D51E1E"
      app:layout_constraintStart_toStartOf="parent"
      app:layout_constraintTop_toTopOf="parent">
  
      <ImageView
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:src="@drawable/img"/>
  
  
  </com.jin.matrix.widgit.CustomViewGroup>
  ```

- **需对子视图进行摆放逻辑处理onLayout**

- 模拟Linearayout垂直布局，对child进行layout摆放

- ```kotlin
   override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
          var top = 0
          for (i in 0 until childCount) {
              val child = getChildAt(i)
              child.layout(0, top, child.measuredWidth, top + child.measuredHeight)
              top += child.measuredHeight
          }
      }
  ```

- **摆放完成，发现ImageView仍然没有显示，因为onLayout中对child.getMeasureWidth、getMeasureHeight值为0，需要重写onMeasure对子View进行测量**

- **measureChild(child, widthMeasureSpec, heightMeasureSpec)：对子view进行测量，计算出viewGroup的宽度、高度。设置进去即可**

- ```kotlin
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
          super.onMeasure(widthMeasureSpec, heightMeasureSpec)
  
          var maxWidth = 0
          var totalHeight = 0
  
          Log.d("zyz", "onMeasure:childCount " + childCount)
  
          for (i in 0 until childCount) {
              val child = getChildAt(i)
              measureChild(child,widthMeasureSpec, heightMeasureSpec)
  
              Log.d("zyz", "onMeasure:measuredHeight " + child.measuredHeight)
              Log.d("zyz", "onMeasure:measuredWidth " + child.measuredWidth)
  
              maxWidth = max(child.measuredWidth, maxWidth)
              totalHeight += child.measuredHeight
          }
          val widthMode = MeasureSpec.getMode(widthMeasureSpec)
          val widthSize = MeasureSpec.getSize(widthMeasureSpec)
  
          val heightMode = MeasureSpec.getMode(heightMeasureSpec)
          val heightSize = MeasureSpec.getMode(heightMeasureSpec)
  
          if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
              setMeasuredDimension(maxWidth,totalHeight)
          } else if (widthMode == MeasureSpec.AT_MOST) {
              setMeasuredDimension(maxWidth,heightSize)
          } else {
              setMeasuredDimension(widthSize,totalHeight)
          }
      }
  ```

- 此时在xml给自定义viewGoroup设置背景色红色，会发现被ImageView遮挡，因为此时ViewGroup的大小就是ImageView的大小

![image-20240330113431880](https://s2.loli.net/2024/03/30/P92137UdVHrqzyX.png)

- 当给次ViewGroup再添加一个TextView时

- ```xml
  <com.jin.matrix.widgit.CustomViewGroup
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:background="#D51E1E"
      app:layout_constraintStart_toStartOf="parent"
      app:layout_constraintTop_toTopOf="parent">
  
  
      <ImageView
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:src="@drawable/img"/>
  
  
      <TextView
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:textColor="@color/white"
          android:textSize="20sp"
          android:text="text"/>
  
  </com.jin.matrix.widgit.CustomViewGroup>
  ```

- ![image-20240330114924870](https://s2.loli.net/2024/03/30/9EZ6L5FkTPdJhOq.png)

- textView宽度不够的部分会被ViewGroup的背景色渲染

##### 自定义EditText

- ![tutieshi_640x1422_8s.gif](https://s2.loli.net/2024/03/30/EPBdh4YvUtjiRcD.gif)

- 自定义EditText需要focusable、focusableInTouchMode否则可能无法弹出键盘

- ```xml
  <com.jin.matrix.widgit.CustomEditText
      android:layout_width="200dp"
      android:layout_height="wrap_content"
      android:hint="请输入"
      android:clickable="true"
      android:focusable="true"
      android:textSize="22sp"
      android:focusableInTouchMode="true"
      android:layout_marginStart="10dp"
      app:layout_constraintStart_toStartOf="parent"
      app:layout_constraintTop_toTopOf="parent"/>
  ```

- ```kotlin
  init {
      addTextChangedListener(object : TextWatcher {
          override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
  
          }
  
          override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
              isNeedShowCloseIcon(s.isNotEmpty())
          }
  
          override fun afterTextChanged(s: Editable?) {
          }
  
      })
  
      closeDrawable =
          BitmapFactory.decodeResource(resources, R.drawable.ic_close).toDrawable(resources)
      closeDrawable!!.setBounds(0, 0, 80, 80)
  
  }
  ```

- setCompoundDrawables为EditText自带Api绘制drawable

- ```kotlin
  override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)
      if (isNeedShowClose) {
          setCompoundDrawables(null, null, closeDrawable, null)
      } else {
          setCompoundDrawables(null, null, null, null)
      }
  }
  
  fun isNeedShowCloseIcon(show: Boolean) {
      this.isNeedShowClose = show
      invalidate()
  }
  ```

- 点击事件添加

- ```kotlin
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
      super.onSizeChanged(w, h, oldw, oldh)
      rect.set(w - closeDrawable!!.minimumWidth.toFloat(), 0f, w.toFloat(), height.toFloat())
  }
  
  override fun onTouchEvent(event: MotionEvent): Boolean {
      if (!isNeedShowClose) {
          return super.onTouchEvent(event)
      }
      when (event.action) {
          MotionEvent.ACTION_DOWN -> {
              if (rect.contains(event.x, event.y)) {
                  setText("")
              }
          }
      }
      return super.onTouchEvent(event)
  }
  ```

##### 自定义圆角ViewGroup

- 省略那些onMeasuer、onLayout。可以选择去继承已经有的子类ViewGroup，例如LinearLayout，而只关注内部细节即可

- **dispatchDraw()：ViewGroup绘制子View的回调，ViewGroup一般不会触发onDraw()函数，除非给该ViewGroup设置了背景**

- **将canvas.clipPath截取后的canvas传递给子View进行绘制，子View绘制canvas即为截取后的canvas，自然而然显示了圆角效果**

- ```kotlin
  private val path = Path()
  
  override fun dispatchDraw(canvas: Canvas) {
      path.addRoundRect(
          0f,
          0f,
          measuredWidth.toFloat(),
          measuredHeight.toFloat(),
          20f,
          20f,
          Path.Direction.CW
      )
      canvas.save()
      canvas.clipPath(path)
      super.dispatchDraw(canvas)
      canvas.restore()
  }
  ```

- ![image-20240330161749933](https://s2.loli.net/2024/03/30/IzwJyGmUaxOBAPg.png)

- ```xml
  <com.jin.matrix.widgit.CustomConcernViewGroup
      android:layout_width="wrap_content"
      android:layout_height="wrap_content"
      android:orientation="vertical"
      app:layout_constraintStart_toStartOf="parent"
      app:layout_constraintEnd_toEndOf="parent"
      android:layout_marginTop="200dp"
      android:gravity="center"
      app:layout_constraintTop_toTopOf="parent">
  
      <ImageView
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:src="@drawable/ic_scene"/>
  
      <TextView
          android:layout_width="match_parent"
          android:layout_height="wrap_content"
          android:textColor="@color/white"
          android:textSize="20sp"
          android:background="@color/black"
          android:text="text"/>
  
  
  </com.jin.matrix.widgit.CustomConcernViewGroup>
  ```

- 当给自定义ViewGroup设置背景的时候，会出现如下效果：四个边边角还会有背景色**android:background="#D81313"**
- ![image-20240330162010033](https://s2.loli.net/2024/03/30/LHqWyFucXR7zIo5.png)

- 思考：已经对canvas进行clipPath，为何还会存在背景角角

  - 对自定义ViewGroup设置背景，会优先触发onDraw()方法，此时，已经将背景色绘制出来

  - 解决办法：onDraw里面清空背景色，然后在dispathchDraw中clipPath后绘制背景即可

  - **TextView：宽度高度为wrap_content,而裁剪的canvas是整个ViewGroup的大小，故TextView的右边并不是圆角效果**

  - ```xml
    <com.jin.matrix.widgit.CustomConcernViewGroup
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        android:layout_marginTop="200dp"
        android:background="#D81313"
        app:layout_constraintTop_toTopOf="parent">
    
        <ImageView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:src="@drawable/ic_scene"/>
    
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textColor="@color/white"
            android:textSize="20sp"
            android:background="@color/black"
            android:text="text"/>
    
    
    </com.jin.matrix.widgit.CustomConcernViewGroup>
    ```

- bgColor = "#" + String.format("%08x",bg.color)：十进制转换为十六进制

- ```kotlin
  private var bgColor = ""
  
  override fun onDraw(canvas: Canvas) {
      super.onDraw(canvas)
  
      if (bgColor.isBlank()) {
          if (background is ColorDrawable) {
              val bg = background as ColorDrawable
              bgColor = "#" + String.format("%08x",bg.color)
          }
      }
      setBackgroundColor(Color.parseColor("#00FFFFFF"))
  }
  ```

- **clipPath后绘制背景色**

- ```kotlin
  override fun dispatchDraw(canvas: Canvas) {
      path.addRoundRect(
          0f,
          0f,
          measuredWidth.toFloat(),
          measuredHeight.toFloat(),
          20f,
          20f,
          Path.Direction.CW
      )
      canvas.save()
      canvas.clipPath(path)
      
      if (bgColor.isNotBlank()) {
          canvas.drawColor(Color.parseColor(bgColor))
      }
      
      super.dispatchDraw(canvas)
      canvas.restore()
  }
  ```

- ![7c36a567060a54acdc6ada5749d3dd7](https://s2.loli.net/2024/03/30/jmLofbOta6dzqS3.jpg)

#### ViewGroup

#### 绘制流程

- **测量	onMeasure()**
- **布局    onLayout()**
- **绘制    onDraw()**

##### onMeasure()

- **测量当前控件的大小，为正式布局提供建议（只是建议，最后是否确定要看Layout）**
- **onMeasure(int widthMeasureSpec,int heightMeasureSpec)**
- **widthMeasureSpec与heightMeasureSpec：int类型，4个字节即32位，前2未代表模式Mode，后30为代表size**
- **Mode：**
  - **UNSPECIFIED（未指定）：父元素不对子元素进行限制，子元素可以任意大小	0**
  - **EXACTLY（完全）：子元素限定  1**  ----  > match_parent,100dp
  - **AT_MOST（至多）：最大的大小  2**  ------> wrap_content
- **MeasureSpec**
  - MeasureSpec.getMode(int)	//获取模式
  - MeasureSpec.getSize(int)       //获取大小

##### onLayout()

- 对所有的子控件进行布局

##### onDraw()

- 根据布局的位置绘图

#### 自定义垂直布局的LinearLayout

- **宽度match_parent(EXACTLY),高度wrap_content(AT_MOST)**

```kotlin
<com.jin.layout.widgit.MyLinearLayout 
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
```

- **自定义MyLinearLayout继承ViewGroup，重新onMeasure、onLayout函数**
- **获取宽、高原mode、size**
- **依据子控件计算出本身ViewGroup的宽度、高度**
- **逻辑：垂直布局。故该ViewGroup的宽度为子控件宽度的最大值、高度为所有子控件的高度之和**
- **measureChild（view，parentWidthMeasureSpec,parentHeightMeasureSpec）**：**对子控件进行测量**
- **判断模式，只有当模式为EXACTLY值（确切值，无须我们手动计算）时，设置进去的为MeasureSpec.getSize（）的值，否则即为我们计算出来的值**

```kotlin
override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    val widthMode = MeasureSpec.getMode(widthMeasureSpec)
    val heightMode = MeasureSpec.getMode(heightMeasureSpec)
    val widthSize = MeasureSpec.getSize(widthMeasureSpec)
    val heightSize = MeasureSpec.getSize(heightMeasureSpec)

    //测量子控件
    var height = 0
    var width = 0
    for (i in 0 until childCount) {
        val childView = getChildAt(i)
        measureChild(childView,widthMeasureSpec,heightMeasureSpec)
        width = max(childView.measuredWidth,width)
        height += childView.measuredHeight
    }
    val realWidth = if (widthMode == MeasureSpec.EXACTLY) {
        widthSize
    } else {
        width
    }
    val realHeight = if (heightMode == MeasureSpec.EXACTLY) {
        heightSize
    } else {
        height
    }
    setMeasuredDimension(realWidth,realHeight)
}
```

- **onLayout对子控件进行布局**
- **逻辑：垂直布局的子view（不考虑margin相关），改变每个子view的top值即可**

```kotlin
override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    var top = 0
    for (i in 0 until childCount) {
        val childView = getChildAt(i)
        childView.layout(0,top,childView.measuredWidth,top + childView.measuredHeight)
        top+=childView.measuredHeight
    }
}
```

![微信图片_20240203161224.jpg](https://s2.loli.net/2024/02/03/ZajwehYtI6EBRyp.jpg)

##### getMeasureWidth与getWidth

- **getMeasureWidth（）：onMeasure结束。通过setMeasuredDimension()设置的**
- **getWidth（）：onLayout结束，通过layout（left,top,right,bottom） right - left**
- **上述案例：**
  - **childView.layout(0,top,childView.measuredWidth,top + childView.measuredHeight)** 
  - **getWidth = childView.measuredWidth - 0 =  childView.measuredWidth**
  - **所以上述案例中getWidth = getMeasureWidth的**

#### 间距调整

- ##### 直接对MyLinearLayout下的子view加上marginLeft是没有任何效果的

- **为何LinearLayout下的子view可以？**

- **自定义属性然后去获取？任何自定义view也可以margin，显然不是**

  ```xml
  android:layout_marginTop="20dp"
  ```

- **onMeasure，onLayout实现margin**

##### Margin效果

- **重写generateLayoutParams、generateDefaultLayoutParams方法返回MarginLayoutParams**

- ```kotlin
  override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
      return MarginLayoutParams(context, attrs)
  }
  
  override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
      return MarginLayoutParams(p)
  }
  
  override fun generateDefaultLayoutParams(): LayoutParams {
      return MarginLayoutParams(
          LayoutParams.MATCH_PARENT,
          LayoutParams.MATCH_PARENT
      )
  }
  ```

- **计算ViewGroup的宽高时，遍历子view得到measureWidth、measureHeight同时带上margin值**

- **marginLayoutParams: childView.getLayoutParams as MarginLayoutParams**(**onMeasure**)

```kotlin
width = max(
    childView.measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin,
    width
)
height += childView.measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin
```

- 布局时，根据margin，摆放子控件位置

```kotlin
val marginLayoutParams = childView.layoutParams as MarginLayoutParams

val childWidth = childView.measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin
val childHeight = childView.measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin
childView.layout(
    0,
    top,
    childWidth,
    top +childHeight
)
top += childHeight
```

![微信图片_20240204153727.jpg](https://s2.loli.net/2024/02/04/iKtvMkmd47RXwf5.jpg)

#### 自定义FlowLayout

- **子view是否能容纳下、否则即换行摆放**
- **FlowLayout的宽度即为最大的宽度行，高度为每行最高的子View高度之和**

```kotlin
override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    val widthMode = MeasureSpec.getMode(widthMeasureSpec)
    val heightMode = MeasureSpec.getMode(heightMeasureSpec)
    val widthSize = MeasureSpec.getSize(widthMeasureSpec)
    val heightSize = MeasureSpec.getSize(heightMeasureSpec)

    var width = 0
    var height = 0
    var lineWidth = 0
    var lineHeight = 0

    for (i in 0 until childCount) {
        val childView = getChildAt(i)
        measureChild(childView, widthMeasureSpec, heightMeasureSpec)
        val marginLayoutParams = childView.layoutParams as MarginLayoutParams
        val childWidth =
            childView.measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin
        val childHeight =
            childView.measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin

        //当前view是否需要换行
        if (lineWidth + childWidth > widthSize) {
            width = max(width, lineWidth)
            height += lineHeight

            lineWidth = childWidth
            lineHeight = childHeight
        } else {
            lineWidth += childWidth
            lineHeight = max(lineHeight, childHeight)
        }

        if (i == childCount - 1) {
            width = max(width, lineWidth)
            height += lineHeight
        }
    }

    val realWidth = if (widthMode == MeasureSpec.EXACTLY) {
        widthSize
    } else {
        width
    }
    val realHeight = if (heightMode == MeasureSpec.EXACTLY) {
        heightSize
    } else {
        height
    }
    setMeasuredDimension(realWidth, realHeight)
}
```

```kotlin
override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
    var lineWidth = 0
    var lineHeight = 0
    var left = 0
    var top = 0
    for (i in 0 until childCount) {
        val childView = getChildAt(i)
        val marginLayoutParams = childView.layoutParams as MarginLayoutParams
        val childViewWidth =
            childView.measuredWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin
        val childViewHeight =
            childView.measuredHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin

        if (lineWidth + childViewWidth > measuredWidth) {
            //换行
            left = 0
            top += lineHeight

            lineWidth = childViewWidth
            lineHeight = childViewHeight
        } else {
            lineWidth += childViewWidth
            lineHeight = max(lineHeight, childViewHeight)
        }
        childView.layout(left + marginLayoutParams.leftMargin, top + marginLayoutParams.topMargin, left + childViewWidth, top + childViewHeight)
        left += childViewWidth
    }
}
```

```kotlin
override fun generateDefaultLayoutParams(): LayoutParams {
    return MarginLayoutParams(
        LayoutParams.MATCH_PARENT,
        LayoutParams.MATCH_PARENT
    )
}

override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
    return MarginLayoutParams(context, attrs)
}

override fun generateLayoutParams(p: LayoutParams?): LayoutParams {
    return MarginLayoutParams(p)
}
```

```xml
<com.jin.layout.widgit.FlowLayout
    android:layout_width="200dp"
    android:layout_height="wrap_content"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toBottomOf="@id/my_linearLayout"
    android:layout_marginTop="20dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Java"
        android:gravity="center"
        android:textColor="@color/white"
        android:layout_margin="5dp"
        android:background="@color/black"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Kotlin"
        android:gravity="center"
        android:textColor="@color/white"
        android:layout_margin="5dp"
        android:background="@color/black"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="PHP"
        android:gravity="center"
        android:textColor="@color/white"
        android:layout_margin="5dp"
        android:background="@color/black"/>


    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="C++"
        android:gravity="center"
        android:textColor="@color/white"
        android:layout_margin="5dp"
        android:background="@color/black"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="GO"
        android:gravity="center"
        android:textColor="@color/white"
        android:layout_margin="5dp"
        android:background="@color/black"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="C#"
        android:gravity="center"
        android:textColor="@color/white"
        android:layout_margin="5dp"
        android:background="@color/black"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="JavaScript"
        android:gravity="center"
        android:textColor="@color/white"
        android:layout_margin="5dp"
        android:background="@color/black"/>

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="SQL"
        android:gravity="center"
        android:textColor="@color/white"
        android:layout_margin="5dp"
        android:background="@color/black"/>


</com.jin.layout.widgit.FlowLayout>
```

![微信图片_20240219115044.jpg](https://s2.loli.net/2024/02/19/dBm4ASuaT1YobLG.jpg)