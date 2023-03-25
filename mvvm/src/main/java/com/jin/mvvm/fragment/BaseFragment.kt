package com.jin.mvvm.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.jin.mvvm.lifecycle.LifecycleProvider
import com.jin.mvvm.lifecycle.LifecycleState

open class BaseFragment: Fragment() {

    val lifecycleProvider by lazy {
        LifecycleProvider()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}