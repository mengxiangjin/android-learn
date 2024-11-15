package com.jin.matrix.widgit.viewGroup

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.widget.LinearLayout

/*
* 为其子view添加圆角效果
* */
class CustomConcernViewGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) : LinearLayout(context, attributeSet, defInt) {

    private val path = Path()


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




}