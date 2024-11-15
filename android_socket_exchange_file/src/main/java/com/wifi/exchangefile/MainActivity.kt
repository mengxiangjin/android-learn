package com.wifi.exchangefile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wifi.exchangefile.databinding.ActivityMainBinding
import com.wifi.exchangefile.tools.ClientService
import com.wifi.exchangefile.tools.ServerService
import com.wifi.exchangefile.utils.NetUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvIp.text = NetUtils.getIPAddress(this)

        binding.btnListener.setOnClickListener {
            val serverService = ServerService.getImstance()
            serverService.startServer()
        }

        binding.btnConnect.setOnClickListener {
            if (binding.editIp.text.toString().isBlank()) {
                Toast.makeText(this, "服务端IP地址为空", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val clickService = ClientService.getImstance()
            clickService.startConnect(binding.editIp.text.toString())
        }

        binding.btnChoose.setOnClickListener {
            startActivity(Intent(this, ChooseFileActivity::class.java))
        }
    }
}