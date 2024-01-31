package com.jin.draw

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.jin.draw.widgit.CustomDrawable

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.img_test)
        val bitmap = BitmapFactory.decodeResource(resources,R.drawable.ic_alipay)
        Bitmap.createBitmap(10,10,Bitmap.Config.ALPHA_8)
        val customDrawable = CustomDrawable(bitmap)
        imageView.setImageDrawable(customDrawable)
    }
}