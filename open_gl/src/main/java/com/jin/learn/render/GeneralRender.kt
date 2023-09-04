package com.jin.learn.render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GeneralRender(val drawer: IDrawer): GLSurfaceView.Renderer {


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        //清除屏幕，为黑色
        GLES20.glClearColor(0f,0f,0f,0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        drawer.setTextureID(createTextureIds(1)[0])
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        //设置OpenGL绘制区域
        GLES20.glViewport(0,0,width,height)
    }

    override fun onDrawFrame(gl: GL10?) {
        //不停回调，刷新
        drawer.draw()
    }

    //创建纹理
    private fun createTextureIds(count: Int): IntArray {
        val texture = IntArray(count)
        //生成纹理
        GLES20.glGenTextures(count,texture,0)
        return texture
    }
}