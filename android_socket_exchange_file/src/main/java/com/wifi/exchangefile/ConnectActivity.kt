package com.wifi.exchangefile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.wifi.exchangefile.databinding.ActivityConnectBinding
import com.wifi.exchangefile.utils.NetUtils

class ConnectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.tvIp.text = NetUtils.getIPAddress(this)

    }
}