package com.jin.rv

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.jin.rv.databinding.ActivityItemDecrationBinding

class ItemDecrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityItemDecrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemDecrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rv.adapter = RvAdapter(this)
        binding.rv.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        val paint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            color = Color.RED
            strokeWidth = 20f
        }
        binding.rv.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                if (parent.getChildAdapterPosition(view) != 0) {
                    outRect.top = 2
                }
                outRect.left = 200
            }

            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDraw(c, parent, state)
                for (i in 0 until parent.childCount) {
                    val childView = parent.getChildAt(i)

                    var divideTop = childView.top - 2f
                    val divideBottom = childView.bottom
                    val divideLeft = parent.paddingLeft
                    var divideRight = parent.width - parent.paddingRight

                    if (i == 0) {
                        divideTop = childView.top.toFloat()
                    }

                    val centerX = parent.paddingLeft + 200 / 2f
                    val centerY = divideTop + (divideBottom - divideTop) / 2f

                    val upStrartX = centerX
                    val upStartY = divideTop
                    val upEndX = centerX
                    val upEndY = centerY - 15

                    c.drawLine(upStrartX,upStartY,upEndX,upEndY,paint)

                    c.drawCircle(centerX,centerY,15f,paint)

                    val downStartX = centerX
                    val downStartY = centerY + 15f
                    val downEndX = centerX
                    val downEndY = divideBottom.toFloat()
                    c.drawLine(downStartX,downStartY,downEndX,downEndY,paint)

                }
            }
        })
    }
}