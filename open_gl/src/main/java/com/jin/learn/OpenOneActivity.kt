package com.jin.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.open_gl.databinding.ActivityOpenOneBinding

class OpenOneActivity : AppCompatActivity() {


    /*
    * 顶点坐标 -- 世界坐标 【-1,1】
    *   ：显示的坐标，即像素显示的位置     GPU上顶点着色器
    * 纹理坐标 -- 左下角 【0,1】     GPU上片元着色器
    *   ：顶点坐标指定的位置点想要显示的颜色
    *
    * 画面上每一个点都会执行一次顶点着色器与片元着色器上片段，渲染到屏幕
    * */

    private lateinit var bind: ActivityOpenOneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityOpenOneBinding.inflate(layoutInflater)
        setContentView(bind.root)

//        initRender(drawer)
    }

    private fun initRender() {
        bind.surfaceView.setEGLContextClientVersion(2)
//        bind.surfaceView.setRender
    }
}