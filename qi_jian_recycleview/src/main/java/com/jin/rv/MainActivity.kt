package com.jin.rv

import android.graphics.BitmapFactory
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
import com.jin.rv.main.adpater.RvAdapter
import com.jin.rv.databinding.ActivityMainBinding
import com.jin.rv.main.adpater.GalleryAdapter
import com.jin.rv.main.manager.RepeatUseLayoutManagerForHorizontally
import com.jin.rv.main.manager.RepeatUseLayoutManagerTwo


/*
* recycleView回收复用
* */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rv.layoutManager = RepeatUseLayoutManagerTwo()
        binding.rv.adapter = RvAdapter(this)


        binding.rvGallery.layoutManager = RepeatUseLayoutManagerForHorizontally()
        binding.rvGallery.adapter = GalleryAdapter(this)

        val paint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            color = Color.BLACK
        }

        val bitmap = BitmapFactory.decodeResource(resources,R.drawable.medal)
        binding.rv.addItemDecoration(object : ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
//                super.getItemOffsets(outRect, view, parent, state)
//                outRect.bottom = 5
//                outRect.left = 160
//                outRect.right = 80
            }

            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDraw(c, parent, state)
//                for (i in 0  until parent.childCount) {
//                    val childView = parent.getChildAt(i)
//                    val x = 80f
//                    val y = childView.top + childView.height / 2f
//                    c.drawBitmap(bitmap,0f,childView.top.toFloat(),paint)
//                }
            }

            override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDrawOver(c, parent, state)
//                for (i in 0  until parent.childCount) {
//                    val childView = parent.getChildAt(i)
//                    if (parent.layoutManager == null) continue
//                    val x = parent.layoutManager!!.getLeftDecorationWidth(childView)
//                    val left = x.minus(bitmap.width / 2f)
//                    c.drawBitmap(bitmap,left,childView.top + (childView.height / 2f - bitmap.height / 2),paint)
//                }
            }
        })
    }
}