package com.jin.matrix

import android.graphics.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout

class DrawerLayoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawerlayout)


        findViewById<View>(R.id.btn_menu).setOnClickListener {
            val view = findViewById<LinearLayout>(R.id.left)
            findViewById<DrawerLayout>(R.id.cl_drawerLayout).openDrawer(view)
        }

        findViewById<DrawerLayout>(R.id.cl_drawerLayout).addDrawerListener(object :
            DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {
                Toast.makeText(this@DrawerLayoutActivity,"open",Toast.LENGTH_LONG).show()
            }

            override fun onDrawerClosed(drawerView: View) {
                Toast.makeText(this@DrawerLayoutActivity,"close",Toast.LENGTH_LONG).show()
            }

            override fun onDrawerStateChanged(newState: Int) {
            }

        })

    }



}