package com.jin.mvvm.lifecycle

import com.jin.mvvm.lifecycle.IlifeCycle


/*
* 管理lifecycle
* */
class LifecycleProvider {

    private var lifeCycleListener = ArrayList<IlifeCycle>()

    private var currentLifeState: LifecycleState? = null


    fun addLifeCycleListener(listener: IlifeCycle) {
        if (!lifeCycleListener.contains(listener)) {
            lifeCycleListener.add(listener)
        }
    }

    fun removeLifeCycleListener(listener: IlifeCycle) {
        if (lifeCycleListener.contains(listener)) {
            lifeCycleListener.remove(listener)
        }
    }

    fun makeLifeState(state: LifecycleState) {
        currentLifeState = state
        when(state) {
            LifecycleState.CREATE -> {
                dispatchCreateState()
            }
            LifecycleState.START -> {
                dispatchStartState()
            }
            LifecycleState.RESUME -> {
                dispatchResumeState()
            }
            LifecycleState.PAUSE -> {
                dispatchPauseState()
            }
            LifecycleState.STOP -> {
                dispatchStopState()
            }
            LifecycleState.DESTROY -> {
                dispatchDestroyState()
            }

            else -> {}
        }
    }

    private fun dispatchCreateState() {
        lifeCycleListener.forEach {
            it.onCreate()
        }
    }

    private fun dispatchStartState() {
        lifeCycleListener.forEach {
            it.onStart()
        }
    }

    private fun dispatchResumeState() {
        lifeCycleListener.forEach {
            it.onResume()
        }
    }

    private fun dispatchPauseState() {
        lifeCycleListener.forEach {
            it.onStop()
        }
    }

    private fun dispatchStopState() {
        lifeCycleListener.forEach {
            it.onPause()
        }
    }

    private fun dispatchDestroyState() {
        lifeCycleListener.forEach {
            it.onDestroy()
        }
    }
}