package com.jin.camera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jin.camera.databinding.ActivityCameraRotateBinding

class CameraRotateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraRotateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraRotateBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


}