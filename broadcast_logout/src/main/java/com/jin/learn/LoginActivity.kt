package com.jin.learn

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : BaseActivity() {

    private lateinit var login: Button
    private lateinit var account: EditText
    private lateinit var password: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initView()
        initListener()
    }

    private fun initView() {
        login = findViewById(R.id.login)
        account = findViewById(R.id.account)
        password = findViewById(R.id.password)
    }

    private fun initListener() {
        login.setOnClickListener {
            val account = account.text.toString()
            val password = password.text.toString()
            if (account == "admin" && password == "123456") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "invalid message", Toast.LENGTH_LONG).show()
            }
        }
    }
}