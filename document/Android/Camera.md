### Camera

- **实现View 3D变化媒介**
- **设备三维坐标系**
  - **x轴屏幕水平方向向右**
  - **y轴屏幕垂直方向向上**
  - **z轴屏幕里面**

![image-20240220141514106](https://s2.loli.net/2024/02/20/Vj1T6tiA4hcLvdf.png)

- **Camera的位置：z轴的负反方向上，即屏幕外**（0,0，-576）

![tutieshi_640x1422_5s.gif](https://s2.loli.net/2024/02/20/NdzvbKhc24mW3r5.gif)

#### 函数

- ##### 构造函数：private val camera = Camera()

- **状态保存恢复：camera.save()、camera.restore()**

- **更改camera的位置，应用到canvas上，最后应用的view上**

- **利用Matrix更改应用到canvas上**

- ```kotlin
  canvas.save()
  camera.save()
  
  val matrix = Matrix()
  camera.translate(0f,0f,10f)
  camera.getMatrix(matrix)
  canvas.setMatrix(matrix)
  
  camera.restore()
  canvas.restore()
  ```

- **camera.applyToCanvas(canvas)：直接将camera变换应用到canvas，不需要经过Matrix**

#### 案例：图片3D应用

- **拖动条SeekBar（1~360）**
- **TextView显示当前拖动值**
- **自定义CameraView显示图片**

```xml
<SeekBar
    android:id="@+id/seek_bar"
    android:layout_width="200dp"
    android:layout_height="wrap_content"
    android:min="1"
    android:max="360"
     />

<TextView
    android:id="@+id/tv_progress"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="1"/>

<com.jin.camera.widgit.CameraImageView
    android:id="@+id/camera_view"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@drawable/img"/>
```

- **CameraImageView：继承自ImageView**

- ```kotlin
  private var bitmap: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.img)
  private var paint = Paint()
  
  private val camera = Camera()
  private var progress = 1f
  ```

- **注意：xml中 android:src="@drawable/img" 通过super.onDraw(canvas) 来实现**

- **Camera各函数操作的不是摄像而是view物体，Translate等**

- **camera进行Y轴旋转当前拖动条的进度，然后应用到Canvas上，Canvas绘制出半透明的bitmap，然后再调用super.onDraw(canvas)将底层的src显示出来**

  ```kotlin
  override fun onDraw(canvas: Canvas) {
      canvas.save()
      camera.save()
      paint.alpha = 100
      canvas.drawBitmap(bitmap, 0f, 0f, paint)
      camera.rotateY(progress)
      camera.applyToCanvas(canvas)
      camera.restore()
      super.onDraw(canvas)
      canvas.restore()
  }
  ```

![tutieshi_432x960_9s.gif](https://s2.loli.net/2024/02/20/Ss6OAUhrxH7i2uF.gif)

#### 平移（translate）

- **camera.rotateY(progress) ---> camera.translate(progress,0f,0f)：x轴偏移progress**

![tutieshi_576x1280_7s.gif](https://s2.loli.net/2024/02/20/EKvn2GsXPF8pOBL.gif)

- **camera.rotateY(progress) ---> camera.translate(0f，progress,0f)：y轴偏移progress**

![tutieshi_576x1280_5s.gif](https://s2.loli.net/2024/02/20/FZy4I75vJqkxWNw.gif)

- **camera.rotateY(progress) ---> camera.translate(0f,0f，progress)：z轴偏移progress**
- **沿着z轴偏移正向，图像会越来越小，z轴正方向屏幕里面，图像离屏幕越来越远，导致越来越小**，**若为负方向偏移，则会越来越大**

![tutieshi_432x960_5s.gif](https://s2.loli.net/2024/02/20/6CSnl9sdObkrUDZ.gif)

- **camera.rotateY(progress) ---> camera.translate(0f，0f，-progress * 2)：沿z轴负方向*2倍进行偏移**
  - **图像越来越大：Camera本身位于z轴-576处，随着物体像z轴负方向偏移，图像会越来越大**
  - **图像最后消失不见：当图像偏移到Camera处时，相当于大水杯由远到近到眼前，到眼前即看不见了**

![tutieshi_432x960_6s.gif](https://s2.loli.net/2024/02/20/9JA4V1hDNTWcUsy.gif)

#### 旋转（Rotate）

-  **camera.rotateX(value)、rotateY、rotateZ**
- ![Snipaste_2024-02-20_15-50-26](https://s2.loli.net/2024/02/20/PZd4WBM2Qjtz1mC.png)

- **camera.rotateX（）：90~270不可见**![tutieshi_576x1280_5s _1_.gif](https://s2.loli.net/2024/02/20/GNnLSJEmM4kC9Kd.gif)
- **camera.rotateY（）：90~270不可见**![tutieshi_576x1280_6s.gif](https://s2.loli.net/2024/02/20/BLvk5Kwjoi1SY6M.gif)
- **camera.rotateZ（）：90~270不可见**

![tutieshi_576x1280_4s.gif](https://s2.loli.net/2024/02/20/2HEVbJ8Yx35yKBa.gif)

- **更改旋转中心点**

- ```kotlin
  private fun exampleRotate(canvas: Canvas) {
      canvas.save()
      camera.save()
  
      paint.alpha = 100
      canvas.drawBitmap(bitmap, 0f, 0f, paint)
  
      val matrix = Matrix()
      camera.rotateX(progress)
      camera.getMatrix(matrix)
      
      val centerX = width / 2f
      val centerY = height / 2f
      matrix.preTranslate(-centerX,-centerY)
      matrix.postTranslate(centerX,centerX)
      canvas.setMatrix(matrix)
  
      camera.restore()
      super.onDraw(canvas)
      canvas.restore()
  }
  ```

![tutieshi_576x1280_5s _1_.gif](https://s2.loli.net/2024/02/20/kPilJLDwbgexUha.gif)

- **绕y轴，图片中心点旋转**![tutieshi_576x1280_5s _2_.gif](https://s2.loli.net/2024/02/20/DuhsoF4gpMZP1bw.gif)

- **绕z轴，图片中心点旋转**![tutieshi_576x1280_5s _2_.gif](https://s2.loli.net/2024/02/20/QA1tY2wWxadejmJ.gif)

#### 案例：图片3D旋转

- 自定义动画Animation
- 利用Camera.Rotation()绕图片中心进行翻转

```xml
<Button
    android:id="@+id/btn_reverse"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="reverse"
    android:layout_marginTop="20dp"/>

<LinearLayout
    android:id="@+id/ll_content"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content">

    <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/img"/>

</LinearLayout>
```

- **传入角度0~180,180~0**

```kotlin
private var isOpen = false
private fun initReverse() {
    val openAnim = CameraRotateAnim(0, 180)
    openAnim.duration = 3000
    openAnim.fillAfter = true

    val closeAnim = CameraRotateAnim(180, 0)
    closeAnim.duration = 3000
    closeAnim.fillAfter = true

    binding.btnReverse.setOnClickListener {
        isOpen = !isOpen
        if (isOpen) {
            binding.llContent.startAnimation(openAnim)
        } else {
            binding.llContent.startAnimation(closeAnim)
        }

    }
}
```

- **CameraRotateAnim继承Animation（）类，重写initialize（）、applyTransformation（）方法**

- **initialize（）：动画开始之前执行，准备工作**

- **applyTransformation（）：动画开始执行**

- **Transformation参数：动画实际执行者，改变其中的matrix，实现动画效果**

- ```kotlin
  private var viewCenterX = 0f
  private var viewCenterY = 0f
  private var camera = Camera()
  
  override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
      super.initialize(width, height, parentWidth, parentHeight)
      viewCenterX = width / 2f
      viewCenterY = height / 2f
  }
  
  
  override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
      val currentDegress = fromDegress + (toDegress - fromDegress) * interpolatedTime
      camera.save()
      camera.rotateY(currentDegress)
      camera.getMatrix(t.matrix)
  	//绕着中心点翻转
      t.matrix.preTranslate(-viewCenterX, -viewCenterY)
      t.matrix.postTranslate(viewCenterX, viewCenterY)
  
      camera.restore()
      super.applyTransformation(interpolatedTime, t)
  }
  ```

![tutieshi_576x1280_7s _1_.gif](https://s2.loli.net/2024/02/20/gwjNzRLvs6rcKmI.gif)

- **问题：随着旋转的角度动画，发现图像变大了。0~90度慢慢增大，90~180慢慢减小**
  - **角度旋转，图像离camera的距离也更近导致，达到90度垂直时距离最近，故图像最大**
  - **如何解决:0~90,距离变近，故我们需要手动平移z轴的距离。即translate(0f,0f,z)**
  - **以一方反转为例（0~180），可分割为0~90度（视图变大，距离变近，移动z轴变大），90度~180度（移动z轴变小）**
  - **CameraRotateAnim构造函数（fromdegress,toDegress,boolean isReverse）:isReverse是否翻转超过90度**
  - **camera.translate(0f,0f,translateZ)**
  - ```kotlin
    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val currentDegress = fromDegress + (toDegress - fromDegress) * interpolatedTime
        camera.save()
    
        val translateZ = if (isReverse) {
            400 * (1 - interpolatedTime)
        } else {
            400 * interpolatedTime
        }
    
        camera.translate(0f,0f,translateZ)
        camera.rotateY(currentDegress)
        camera.getMatrix(t.matrix)
    
        t.matrix.preTranslate(-viewCenterX, -viewCenterY)
        t.matrix.postTranslate(viewCenterX, viewCenterY)
    
        camera.restore()
        super.applyTransformation(interpolatedTime, t)
    }
    ```
  
  - **0~90，90~180二段动画**
  
  - ```kotlin
    val openAnim = CameraRotateAnim(0, 90)
    openAnim.duration = 2000
    openAnim.fillAfter = true
    openAnim.interpolator = AccelerateInterpolator()
    
    openAnim.setAnimationListener(object : AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
    
        }
    
        override fun onAnimationEnd(animation: Animation?) {
            binding.imgOne.isVisible = false
            binding.imgTwo.isVisible = true
            val anim = CameraRotateAnim(90,180,true)
            anim.duration = 2000
            anim.fillAfter = true
            anim.interpolator = DecelerateInterpolator()
    
    
            binding.llContent.startAnimation(anim)
        }
    
        override fun onAnimationRepeat(animation: Animation?) {
        }
    
    })
    
    val closeAnim = CameraRotateAnim(180, 90)
    closeAnim.duration = 2000
    closeAnim.fillAfter = true
    closeAnim.interpolator = AccelerateInterpolator()
    closeAnim.setAnimationListener(object : AnimationListener {
        override fun onAnimationStart(animation: Animation?) {
    
        }
    
        override fun onAnimationEnd(animation: Animation?) {
            binding.imgOne.isVisible = true
            binding.imgTwo.isVisible = false
            val anim = CameraRotateAnim(90,0,true)
            anim.duration = 2000
            anim.fillAfter = true
            anim.interpolator = DecelerateInterpolator()
    
            binding.llContent.startAnimation(anim)
        }
    
        override fun onAnimationRepeat(animation: Animation?) {
        }
    
    })
    ```
  
  - **第一段动画结束的时候，更改视图的显示隐藏，即可实现二张图片的翻转效果ll_content做动画**
  
  - ```xml
    <LinearLayout
        android:id="@+id/ll_content"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content">
    
        <ImageView
            android:id="@+id/img_one"
            android:layout_width="300dp"
            android:layout_height="400dp"
            android:scaleType="fitXY"
            android:src="@drawable/img"/>
    
        <ImageView
            android:id="@+id/img_two"
            android:layout_width="300dp"
            android:layout_height="400dp"
            android:scaleType="fitXY"
            android:visibility="gone"
            android:src="@drawable/img_1"/>
    
    
    </LinearLayout>
    ```
  
    ​		![tutieshi_576x1280_9s-min.gif](https://s2.loli.net/2024/02/21/JVxz3iHF1D4rn2u.gif)

#### 案例：旋转的时钟

![tutieshi_576x1280_8s.gif](https://s2.loli.net/2024/02/21/mBwIZdLGnxH8CF6.gif)

- **时钟随着手势而做旋转3D效果**

- **倘若非时钟，而是一组view，故此处选择自定义viewGroup**

- **对viewGroup中dispatchDraw(canvas),提前对canvas进行变化变换即可,此处继承LinearLayout**

- ```xml
  <com.jin.camera.widgit.CameraRotateGroup
      android:layout_width="match_parent"
      android:layout_height="match_parent"
      android:gravity="center">
  
      <ImageView
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:src="@drawable/img_2" />
  
  
  </com.jin.camera.widgit.CameraRotateGroup>
  ```

- ```kotlin
  private val MAX_ROTATE_DEGRESS = 20f	//偏移最大角度
  private val camera = Camera()
  private var centerX = 0f	//视图中心点，用于绕组件中心点进行便宜
  private var centerY = 0f	//视图中心
  private val matrix = Matrix()	//camera应用后的matrix
  private var rotateX = 0f	//x轴偏移角度
  private var rotateY = 0f	//y轴偏移角度
  ```

- **算出视图中心点**

- ```kotlin
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
      super.onSizeChanged(w, h, oldw, oldh)
      centerX = w / 2f
      centerY = h / 2f
  }
  ```

- **根据手指位置x，y计算偏移的角度，如图：**
  - **A（视图中心点）、B（触摸位置）**
  - **AC距离占width/2的比例即为翻转Y轴的比例**
  - **AD距离占height/2的比例即为翻转X轴的比例**
  - **rotateX = （AD） / (height/ 2f) * MAX_ROTATE_DEGRESS**
  - **rotateY = （AC） / (width / 2f) * MAX_ROTATE_DEGRESS**
- ![image-20240221142316071](https://s2.loli.net/2024/02/21/fzO3dYFvHItMB69.png)

- ```kotlin
  private fun calRotateDegress(x: Float, y: Float) {
      var precentX =  (x - centerX) / (width / 2f)
      var precentY = (y - centerY) / (height / 2f)
      if (precentX > 1f) {
          precentX = 1f
      }
      if (precentX < -1f) {
          precentX = -1f
      }
      if (precentY > 1f) {
          precentY = 1f
      }
      if (precentY < -1f) {
          precentY = -1f
      }
      rotateX = precentY * MAX_ROTATE_DEGRESS
      rotateY = precentX * MAX_ROTATE_DEGRESS
      postInvalidate()
  }
  ```

- ```kotlin
  override fun dispatchDraw(canvas: Canvas) {
      canvas.save()
      camera.save()
      matrix.reset()
      camera.rotateX(rotateX)
      camera.rotateY(rotateY)
      camera.getMatrix(matrix)
  
      matrix.preTranslate(-centerX,-centerY)
      matrix.postTranslate(centerX,centerY)
      canvas.setMatrix(matrix)
      super.dispatchDraw(canvas)
      canvas.restore()
      camera.restore()
  
  }
  ```

- **当手势UP时，需将view归位，即rotateX -> 0,rotateY -> 0即可**

- ```kotlin
  private fun resetView() {
      val rotateXPropertyHolder = PropertyValuesHolder.ofFloat("rotateX", rotateX, 0f)
      val rotateYPropertyHolder = PropertyValuesHolder.ofFloat("rotateY", rotateY, 0f)
      val valueAnimator =
          ValueAnimator.ofPropertyValuesHolder(rotateXPropertyHolder, rotateYPropertyHolder)
      valueAnimator.addUpdateListener {
          rotateX = it.getAnimatedValue("rotateX") as Float
          rotateY = it.getAnimatedValue("rotateY") as Float
          postInvalidate()
      }
      valueAnimator.duration = 3000
      valueAnimator.interpolator = BounceInterpolator()
      valueAnimator.start()
  }
  ```