package com.jin.matrix.widgit.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.graphics.drawable.toDrawable
import com.jin.matrix.R

class CustomEditText @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defInt: Int = 0
) :
    androidx.appcompat.widget.AppCompatEditText(context, attributeSet, defInt) {


    private var isNeedShowClose = false
    private var closeDrawable: Drawable? = null
    private var rect = RectF()


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
}