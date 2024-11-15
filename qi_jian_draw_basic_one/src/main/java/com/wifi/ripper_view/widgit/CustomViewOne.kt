package com.wifi.ripper_view.widgit

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.Path
import android.graphics.Path.FillType
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.graphics.RegionIterator
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.MotionEvent
import android.view.View

class CustomViewOne @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    var pointX = 0f
    var pointY = 0f
    val paint = Paint().apply {
        color = Color.RED
        style = Style.STROKE
        strokeWidth = TypedValue.applyDimension(COMPLEX_UNIT_DIP,2f,resources.displayMetrics)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        testOne(canvas)
//        testTwo(canvas)
//        testThree(canvas)
//        testFour(canvas)
//        testFive(canvas)
//        testSix(canvas)
//        testSeven(canvas)
//        testEight(canvas)
//        testNine(canvas)
//        testTen(canvas)
        testEleven(canvas)
    }

    private fun testEleven(canvas: Canvas) {
        /*
     * Region 的相交处理 OP
         DIFFERENCE(0),  this与region不同的区域
         INTERSECT(1),   this与region相交的区域
         UNION(2),       this与region合并的区域
         XOR(3),         this与region相交之外的区域
         REVERSE_DIFFERENCE(4),  region与this不同的区域
         REPLACE(5);     region区域
     * */
        val rectOne = Rect(100,100,400,200)
        val rectTwo = Rect(200,0,300,300)

        val paint = Paint().apply {
            color = Color.RED
            style = Style.STROKE
            strokeWidth = 2f
        }

        canvas.drawRect(rectOne,paint)
        canvas.drawRect(rectTwo,paint)


        paint.style = Style.FILL
        paint.color = Color.BLUE
        val regionOne = Region(rectOne)
        val regionTwo = Region(rectTwo)

        //regionOne 与 regionTwo 不同的区域
//        regionOne.op(regionTwo,Region.Op.DIFFERENCE)
//        regionOne.op(regionTwo,Region.Op.INTERSECT)
//        regionOne.op(regionTwo,Region.Op.UNION)
//        regionOne.op(regionTwo,Region.Op.XOR)
//        regionOne.op(regionTwo,Region.Op.REVERSE_DIFFERENCE)
//        regionOne.op(regionTwo,Region.Op.REPLACE)

        val regionIterator = RegionIterator(regionOne)
        val rect = Rect()
        while (regionIterator.next(rect)) {
            canvas.drawRect(rect,paint)
        }
    }

    private fun testTen(canvas: Canvas) {
        val path = Path()
        val newPath = Path()
        val rectf = RectF(100f,100f,300f,300f)
        path.addRoundRect(rectf,10f,10f,Path.Direction.CCW)
        newPath.moveTo(180f,300f)
        newPath.lineTo(200f,320f)
        newPath.lineTo(220f,300f)
        path.op(newPath,Path.Op.UNION)
        canvas.drawPath(path,paint)
    }

    private fun testNine(canvas: Canvas) {
        //可通过rect来构造
        val region = Region()
        val path = Path()
        path.addOval(50f,50f,200f,500f,Path.Direction.CCW)

        val newRegion = Region(50,50,200,200)
        //path与newRegion 取交集
        region.setPath(path,newRegion)
        //canvas无直接绘制region的API，需通过RegionIterator类循环绘制
        val regionItar = RegionIterator(region)
        val rect = Rect()
        while (regionItar.next(rect)) {
            canvas.drawRect(rect,paint)
        }
    }

    private fun testEight(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.RED
            style = Style.FILL_AND_STROKE
            isAntiAlias = true

            textSize = TypedValue.applyDimension(COMPLEX_UNIT_SP,12f,resources.displayMetrics)

        }
        paint.typeface = Typeface.DEFAULT
        paint.typeface = Typeface.DEFAULT_BOLD
        //自定义字体样式 从asserts文件夹下读取ttf字体文件
        paint.typeface = Typeface.createFromAsset(context.assets,"font/font.ttf")
        canvas.drawText("床前明月光,疑是地上霜",100f,200f,paint)
    }

    private fun testSeven(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.BLUE
            style = Style.FILL
            strokeWidth = TypedValue.applyDimension(COMPLEX_UNIT_DIP,2f,resources.displayMetrics)
            isAntiAlias = true

            textSize = TypedValue.applyDimension(COMPLEX_UNIT_SP,13f,resources.displayMetrics)
            isFakeBoldText = true //加粗
            isUnderlineText = true //下划线
            isStrikeThruText = true //删除线
            textSkewX = -0.25f //字体倾斜度 负数时右边倾斜，正数左边
            textScaleX = 1f //拉伸倍数 1:不拉伸
        }

        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("床前明月光",100f,500f,paint)

        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("床前明月光",100f,600f,paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("床前明月光",100f,700f,paint)

        paint.color = Color.RED
        canvas.drawLine(100f,0f,100f,1000f,paint)

        val path = Path()
        paint.style = Style.STROKE
        path.addArc(400f,400f,800f,800f,30f,100f)
        canvas.drawPath(path,paint)

        /*
        * drawTextOnPath
        * hOffset:水平方向离path起始点的偏移量
        * vOffset:垂直方向里path中心点的偏移量
        * */
        paint.color = Color.GREEN
        canvas.drawTextOnPath("床前明月光,疑是地上霜",path,0f,-10f,paint)
    }

    private fun testSix(canvas: Canvas) {
        val path = Path()
        val paint = Paint().apply {
            color = Color.RED
            style = Style.FILL //重点(FILLANDSTROKE可能导致设置fillType有问题)
            strokeWidth = TypedValue.applyDimension(COMPLEX_UNIT_DIP,2f,resources.displayMetrics)
        }
        /*
        * fillType 自填充算法 需要设置为填充模式（否则失效）
        * FillType.EVEN_ODD:去除相交部分
        * FillType.WINDING
        * FillType.INVERSE_WINDING
        * FillType.INVERSE_EVEN_ODD
        * */
        path.addRect(100f,100f,300f,300f,Path.Direction.CW)
        path.addCircle(300f,300f,100f,Path.Direction.CW)
        path.fillType = FillType.EVEN_ODD   //去除相交

        canvas.drawPath(path,paint)
    }

    private fun testFive(canvas: Canvas) {
        val path = Path()
        path.moveTo(100f,100f)  //起始点
        path.lineTo(200f,200f)  //下一次点
        path.lineTo(300f,100f)
//        path.close()    //封闭路径
        val rectF = RectF(300f,300f,500f,500f)
        /*
        *  forceMoveTo
        *   false:画笔无缝连接过去 （默认）
        *   true:画笔中断，重新落点开始绘制
        * */
        path.arcTo(rectF,30f,90f,false)
        canvas.drawPath(path,paint)

        //addArc 实际上就是artTo(forceMoveTo = true)
        rectF.offset(300f,0f)
        path.addArc(rectF,30f,90f)  //弧
        path.addRect(400f,600f,600f,700f,Path.Direction.CW) //矩形
        path.addRoundRect(400f,800f,600f,1000f,20f,20f,Path.Direction.CCW)  //圆角矩形
        path.addCircle(600f,600f,100f,Path.Direction.CW)    //圆
        path.addOval(400f,1200f,600f,1250f,Path.Direction.CW)   //椭圆
        canvas.drawPath(path,paint)
    }

    private fun testFour(canvas: Canvas) {
        val srcRectF = RectF(100f,100f,200f,200f)
        val desRectF = RectF(150f,100f,400f,200f)
        srcRectF.contains(20f,20f) //是否包含某点
        var isIntersects = RectF.intersects(srcRectF,desRectF) //判断矩形是否相交(静态方法)
        var intersects = srcRectF.intersects(150f,100f,400f,200f)    //判断矩形是否相交(成员方法)

        var intersect = srcRectF.intersect(desRectF)    //判断矩形是否相交，若相交改变srcRectF 为相交后的值，不相交即不变

        srcRectF.union(desRectF)    //矩形合并，最小左上角与最大右下角，若为empty则不变
        srcRectF.union(20f,20f) //矩形与点合并，矩形为empty，则默认0,0, 与 20f,20f构成矩形 根据点在矩形位置，判断点是左上角还是右下角
    }

    private fun testThree(canvas: Canvas) {
        val rectF = RectF(200f,200f,600f,600f)


        if (rectF.contains(pointX,pointY)) {
            paint.color = Color.RED
        } else {
            paint.color = Color.GREEN
        }
        canvas.drawRect(rectF,paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                pointX = event.x
                pointY = event.y
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_UP -> {
            }
            else -> {
                //return false 即不打印
                //return true 此view需要消费此事件
                Log.d("lzy", "onTouchEvent: ")
            }
        }
        return super.onTouchEvent(event)
    }

    private fun  testOne(canvas: Canvas) {
        val paint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 3.0f  //PX
            color = 0xFFFFFF    //Color.RED  Color.parseColor("#ffffff")
            setARGB(255, 255, 255, 255) // 等同于setColor
            isAntiAlias = true //抗锯齿 边缘是否平滑
        }

        canvas.drawColor(Color.RED)
//        canvas.drawARGB(255, 255, 255, 255)
//        canvas.drawColor(0xFFFFFF)
//        canvas.drawRGB(255, 255, 255)


        canvas.drawLine(100f, 100f, 200f, 200f, paint)  //(100,100) -> (200,200)
        canvas.drawLines(
            floatArrayOf(
                100f,
                100f,
                200f,
                200f,
                300f,
                300f,
                400f,
                400f,
            ), paint
        ) //size必须是4的倍数 每二个点形成线 (100,100) -> (200,200) (300,300) -> (400,400)
        canvas.drawLines(floatArrayOf(
            100f,
            100f,
            200f,
            200f,
            300f,
            300f,
            400f,
            400f,
        ),2,4,paint) //跳过offset个数，绘后面count个 即 (200,200) -> (300,300)


        canvas.drawPoint(20f,20f,paint)
        canvas.drawPoints(floatArrayOf(20f,20f,30f,30f),paint)
        canvas.drawPoints(floatArrayOf(20f,20f,30f,30f),2,2,paint)  //(30,30)

        val rect = RectF(20f,20f,30f,40f)
        canvas.drawRect(rect,paint)
        canvas.drawRect(20f,20f,30f,40f,paint)
        canvas.drawRoundRect(rect,20f,10f,paint)    //圆角矩形 x,y方向的圆角弧度

        canvas.drawCircle(20f,20f,10f,paint)    //圆心，半径
        canvas.drawOval(rect,paint) //椭圆，依据矩形绘制椭圆

    }

    private fun testTwo(canvas: Canvas) {
        val rect = RectF(100f,100f,300f,300f)
        val paint = Paint().apply {
            color = Color.RED
            style = Style.STROKE
            strokeWidth = TypedValue.applyDimension(COMPLEX_UNIT_DIP,2f, resources.displayMetrics)
        }


        paint.style = Style.STROKE
        /*
        * 开始角度 30，扫 120度绘制圆弧
        * useCenter：是否连接圆弧圆心
        * */
        canvas.drawArc(rect,0f,120f,false,paint)

        rect.offset(300f,0f)    //右偏移500px
        canvas.drawArc(rect,0f,120f,true,paint)

        //当style设置为 Style.FILL_AND_STROKE
        paint.style = Style.FILL_AND_STROKE
        rect.offset(-300f,300f) //左下
        canvas.drawArc(rect,0f,120f,false,paint)

        rect.offset(300f,0f) //右下
        canvas.drawArc(rect,0f,120f,true,paint)
    }
}