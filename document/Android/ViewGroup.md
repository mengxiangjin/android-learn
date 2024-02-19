### ViewGroup

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