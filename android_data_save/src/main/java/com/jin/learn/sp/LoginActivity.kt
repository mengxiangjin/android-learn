package com.jin.learn.sp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jin.learn.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    companion object {
        const val ISREMEMBER = "isRemember"
        const val USERNAME = "username"
        const val PASSWORD = "password"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        var sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        val isRemember = sharedPreferences.getBoolean(ISREMEMBER,false)
        if (isRemember) {
            var username = sharedPreferences.getString(USERNAME,"")
            var password = sharedPreferences.getString(PASSWORD,"")
            binding.account.setText(username)
            binding.password.setText(password)
        }

        binding.login.setOnClickListener {
            val account = binding.account.text.toString()
            val password = binding.password.text.toString()
            if (binding.remember.isChecked) {
                var edit = sharedPreferences.edit()
                edit.putBoolean(ISREMEMBER,true)
                edit.putString(USERNAME,account)
                edit.putString(PASSWORD,password)
                //提交本次操作 先提交到内存中，再异步写入到磁盘。而commit则是直接写入到磁盘
                edit.apply()
            }
            finish()
        }
    }
}