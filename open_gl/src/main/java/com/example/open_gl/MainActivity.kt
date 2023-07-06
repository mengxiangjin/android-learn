package com.example.open_gl

import android.graphics.BitmapFactory
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {


    private lateinit var mEffectView: GLSurfaceView

    private lateinit var renderer: TextureRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        renderer = TextureRenderer()
        renderer.setImageBitmap(BitmapFactory.decodeResource(resources, R.mipmap.ic_splash_bg))
        renderer.setCurrentEffect(R.id.none)

        mEffectView = findViewById<View>(R.id.effectsview) as GLSurfaceView
        //mEffectView = new GLSurfaceView(this);
        //mEffectView = new GLSurfaceView(this);
        mEffectView.setEGLContextClientVersion(2)
        //mEffectView.setRenderer(this);
        //mEffectView.setRenderer(this);
        mEffectView.setRenderer(renderer)
        mEffectView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        Log.i("info", "menu create")
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        renderer.setCurrentEffect(item.itemId)
        mEffectView.requestRender()
        return true
    }
}