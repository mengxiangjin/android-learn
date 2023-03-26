package com.jin.lifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //1.LifecycleEventObserver对象
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                //监听此activity生命周期变化
                println(event.name)
            }
        })

        //为何不直接在activity直接监听lifecycle ，而是通过viewmodel
        //viewmodel会有异步获取数据的操作，需要实时感知界面生命周期变化
        //2.BaseViewModelLifecycleObserve ---> LifecycleObserver对象（注解方式监听已被废弃）
        //3.IBaseViewModelLifecycleObserver  ---> DefaultLifecycleObserver(推荐) FullLifecycleObserver子类
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        lifecycle.addObserver(viewModel)
    }
}