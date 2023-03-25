package com.jin.mvvm.controller

import com.jin.mvvm.model.UserModel
import com.jin.mvvm.model.UserModel.Companion.STATE_LOGIN_FAIL
import com.jin.mvvm.model.UserModel.Companion.STATE_LOGIN_LOADING
import com.jin.mvvm.model.UserModel.Companion.STATE_LOGIN_SUCCESS

class LoginPresenter {


    private val userModel by lazy {
        UserModel()
    }


    fun checkUserState(account: String,callback: OnCheckUserNameStateCallBack) {
        userModel.checkUserState(account) {
            when(it) {
                0 -> {
                    //未注册 更新UI
                    callback.onNoteExist()
                }
                1 -> {
                    callback.onExist()
                }
            }
        }
    }

    fun doLogin(account: String, password: String,callback: OnLoginStateChanged) {
        userModel.doLogin(account,password) {
            when(it) {
                STATE_LOGIN_LOADING -> {
                    callback.onLoginLoading()
                }

                STATE_LOGIN_SUCCESS -> {
                    callback.onLoginSuccess()
                }

                STATE_LOGIN_FAIL -> {
                    callback.onLoginFail()
                }
            }
        }
    }

    interface OnCheckUserNameStateCallBack {
        fun onNoteExist()
        fun onExist()
    }


    interface OnLoginStateChanged {
        fun onLoginLoading()
        fun onLoginSuccess()
        fun onLoginFail()
    }
}