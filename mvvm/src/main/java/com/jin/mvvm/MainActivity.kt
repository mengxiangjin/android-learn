package com.jin.mvvm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.jin.mvvm.controller.LoginPresenter
import com.jin.mvvm.databinding.ActivityMainBinding
import com.jin.mvvm.model.UserModel

class MainActivity : AppCompatActivity(),
    LoginPresenter.OnCheckUserNameStateCallBack, LoginPresenter.OnLoginStateChanged {


    private lateinit var binding: ActivityMainBinding

    private var isLegalName = false

    private val loginController by lazy {
        LoginPresenter()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()
    }

    private fun initListener() {
        binding.login.setOnClickListener {
            toLogin()
        }
        binding.accountEdit.addTextChangedListener {
            loginController.checkUserState(it.toString(),this@MainActivity)
        }
    }


    private fun toLogin() {
        val account = binding.accountEdit.text.toString()
        val password = binding.password.text.toString()

        //检查合法性
        //异步操作
        if (!isLegalName) return
        loginController.doLogin(account,password,this)
    }



    override fun onNoteExist() {
        binding.loginTip.text = "账号不可用"
        isLegalName = false
    }

    override fun onExist() {
        binding.loginTip.text = "账号可用"
        isLegalName = true
    }

    override fun onLoginLoading() {
        binding.loginTip.text = "登录中"
    }

    override fun onLoginSuccess() {
        binding.loginTip.text = "登录成功"
    }

    override fun onLoginFail() {
        binding.loginTip.text = "登录失败"
    }
}