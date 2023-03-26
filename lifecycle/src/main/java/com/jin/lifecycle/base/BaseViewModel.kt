package com.jin.lifecycle.base

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel(), IBaseViewModelLifecycleObserver {
//    override fun onCreate() {
//        println("BaseViewModel - onCreate")
//    }
//
//    override fun onResume() {
//        println("BaseViewModel - onResume")
//    }
//
//    override fun onPause() {
//        println("BaseViewModel - onPause")
//    }
//
//    override fun onStop() {
//        println("BaseViewModel - onStop")
//    }
//
//    override fun onDestroy() {
//        println("BaseViewModel - onDestroy")
//    }

    override fun onCreate(owner: LifecycleOwner) {
        println("BaseViewModel - onCreate")
    }

    override fun onStart(owner: LifecycleOwner) {
        println("BaseViewModel - onStart")
    }

    override fun onResume(owner: LifecycleOwner) {
        println("BaseViewModel - onResume")
    }

    override fun onPause(owner: LifecycleOwner) {
        println("BaseViewModel - onPause")
    }

    override fun onStop(owner: LifecycleOwner) {
        println("BaseViewModel - onStop")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        println("BaseViewModel - onDestroy")
    }


}