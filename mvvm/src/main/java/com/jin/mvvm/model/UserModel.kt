package com.jin.mvvm.model

import java.util.Random


class UserModel {

    private val Api by lazy {
        API()
    }

    companion object {
        const val STATE_LOGIN_LOADING = 0
        const val STATE_LOGIN_SUCCESS = 1
        const val STATE_LOGIN_FAIL = 2
    }

    private val random = Random()

    fun doLogin(account: String, password: String,block: (Int) -> Unit) {
        block.invoke(STATE_LOGIN_LOADING)
        //调用api登录（耗时间）
        //异步操作（线程） 更新UI需要切换到主线程
        val result = random.nextInt(2)
        if (result == 0) {
            block.invoke(STATE_LOGIN_SUCCESS)
        } else {
            block.invoke(STATE_LOGIN_FAIL)
        }
    }

    //登录之前检查用户是否注册
    fun checkUserState(account: String,block: ((Int) -> Unit)) {
        //0 未注册 1 已注册
        block.invoke(random.nextInt(2))
    }


}