package com.jin.basic_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import com.jin.basic_view.custom.BrightnessDrawable

class BrightnessSeekBar@JvmOverloads constructor
    (context: Context, attrs: AttributeSet? = null, def : Int = 0)
    : androidx.appcompat.widget.AppCompatSeekBar(context,attrs,def) {


    private var thumbBackPaint = Paint()
    private var thumbProgPaint = Paint()

    var brightnessDrawable: BrightnessDrawable? = null

    init {
        progressDrawable = AppCompatResources.getDrawable(context,R.drawable.shape_brightness_default_bg)
        brightnessDrawable = BrightnessDrawable(context)
        progressDrawable
        thumb = brightnessDrawable
        splitTrack = false

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}