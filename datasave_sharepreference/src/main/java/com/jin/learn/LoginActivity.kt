package com.jin.learn

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.jin.learn.base.BaseActivity
import com.jin.learn.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

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
            if (account == "admin" && password == "123456") {
                if (binding.remember.isChecked) {
                    var edit = sharedPreferences.edit()
                    edit.putBoolean(ISREMEMBER,true)
                    edit.putString(USERNAME,"admin")
                    edit.putString(PASSWORD,"123456")
                    //提交本次操作
                    edit.apply()
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "invalid message", Toast.LENGTH_LONG).show()
            }
        }
    }
}