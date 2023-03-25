package com.jin.mvvm.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.jin.mvvm.lifecycle.LifecycleOwner
import com.jin.mvvm.lifecycle.LifecycleProvider
import com.jin.mvvm.lifecycle.LifecycleState

open class BaseActivity: AppCompatActivity(),LifecycleOwner {


    val lifecycleProvider by lazy {
        LifecycleProvider()
    }


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        lifecycleProvider.makeLifeState(LifecycleState.CREATE)
    }

    override fun onStart() {
        super.onStart()
        lifecycleProvider.makeLifeState(LifecycleState.START)
    }

    override fun onResume() {
        super.onResume()
        lifecycleProvider.makeLifeState(LifecycleState.RESUME)

    }

    override fun onPause() {
        super.onPause()
        lifecycleProvider.makeLifeState(LifecycleState.PAUSE)

    }

    override fun onStop() {
        super.onStop()
        lifecycleProvider.makeLifeState(LifecycleState.STOP)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleProvider.makeLifeState(LifecycleState.DESTROY)
    }

    override fun getMyLifecycleProvider(): LifecycleProvider {
        return lifecycleProvider
    }

}