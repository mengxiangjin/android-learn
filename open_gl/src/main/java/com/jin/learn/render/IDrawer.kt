package com.jin.learn.render

interface IDrawer {
    fun draw()
    fun setTextureID(id: Int)
    fun release()
}