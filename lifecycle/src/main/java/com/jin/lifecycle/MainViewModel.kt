package com.jin.lifecycle

import androidx.lifecycle.LifecycleOwner
import com.jin.lifecycle.base.BaseViewModel

class MainViewModel : BaseViewModel() {

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        println("MainViewModel - onCreate")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        println("MainViewModel - onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        println("MainViewModel - onPause")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        println("MainViewModel - onStop")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        println("MainViewModel - onDestroy")
    }
}