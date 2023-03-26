package com.jin.lifecycle.base

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner


/*
* 2.实现DefaultLifecycleObserver，重写生命周期回调
*   将其对象直接addObserver即可
* */
interface IBaseViewModelLifecycleObserver: DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner)
    override fun onStart(owner: LifecycleOwner)
    override fun onResume(owner: LifecycleOwner)
    override fun onPause(owner: LifecycleOwner)
    override fun onStop(owner: LifecycleOwner)
    override fun onDestroy(owner: LifecycleOwner)

}