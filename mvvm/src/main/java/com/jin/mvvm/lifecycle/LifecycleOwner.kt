package com.jin.mvvm.lifecycle

interface LifecycleOwner {

    fun getMyLifecycleProvider(): LifecycleProvider
}