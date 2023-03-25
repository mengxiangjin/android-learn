package com.jin.mvvm.listener

import android.os.Looper
import com.jin.mvvm.App

class DataListener<T> {


    private var blocks = arrayListOf<(T?) -> Unit>()

    var value: T? = null
    set(value) {
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            blocks.forEach {
                it.invoke(value)
            }
        } else {
            App.handler.post {
                blocks.forEach {
                    it.invoke(value)
                }
            }
        }

    }

    fun addListener(block: (T?) -> Unit) {
        if (!blocks.contains(block)) {
            blocks.add(block)
        }
    }


}