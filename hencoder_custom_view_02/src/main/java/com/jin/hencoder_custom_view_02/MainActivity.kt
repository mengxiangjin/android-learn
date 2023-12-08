package com.jin.hencoder_custom_view_02

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import com.jin.hencoder_custom_view_02.mask.ShadowView
import com.jin.hencoder_custom_view_02.mask.UIUtils
import org.libpag.PAGFile
import org.libpag.PAGImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var decorView = window.decorView as FrameLayout
        var shadowView = ShadowView(this)
        shadowView.setRect(Rect(0,0,UIUtils.getScreenWidth(this),UIUtils.getScreenHeight(this)))
        decorView.addView(shadowView)

//        val pagImg = findViewById<PAGImageView>(R.id.pag_view)
//        val pagFile = PAGFile.Load(assets,"wifi_racking_animations.png")
//        pagImg.composition = pagFile
//        pagImg.setRepeatCount(0)
//        pagImg.play()

    }
}