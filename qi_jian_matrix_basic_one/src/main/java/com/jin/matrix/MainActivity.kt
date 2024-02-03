package com.jin.matrix

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import com.jin.matrix.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var editViews = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

        binding.btnReset.setOnClickListener {
            initView()
            binding.imgBird.setImageResource(R.drawable.bird)
        }

        binding.btnApply.setOnClickListener {
            val floatArray = getColorMatrix()
            val srcBitmap = BitmapFactory.decodeResource(resources,R.drawable.bird)

            val bitmap = Bitmap.createBitmap(srcBitmap.width,srcBitmap.height,Bitmap.Config.ARGB_8888)
            val bitmapCanvas = Canvas(bitmap)
            val paint = Paint()
            paint.colorFilter = ColorMatrixColorFilter(ColorMatrix(floatArray))
            bitmapCanvas.drawBitmap(srcBitmap,0f,0f,paint)
            binding.imgBird.setImageBitmap(bitmap)
        }
    }

    private fun initView() {
        binding.llContent.removeAllViews()
        editViews.clear()
        for (i in 0 until 4) {
            val linearLayout =  LinearLayout(this)
            linearLayout.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            linearLayout.orientation = LinearLayout.HORIZONTAL
            for (j in 0 until 5) {
                val editText = EditText(this)
                val layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                editText.setText(if (i == j) {
                    "1"
                } else {
                    "0"
                })
                editText.inputType = EditorInfo.TYPE_NUMBER_FLAG_SIGNED
                editText.gravity = Gravity.CENTER
                layoutParams.weight = 1f
                editText.layoutParams = layoutParams

                editViews.add(editText)
                linearLayout.addView(editText)
            }
            binding.llContent.addView(linearLayout)
        }
    }

    private fun getColorMatrix(): FloatArray {
        val result = FloatArray(20)
        editViews.forEachIndexed { index, editText ->
            result[index] = editText.text.toString().toFloat()
        }
        return result
    }
}