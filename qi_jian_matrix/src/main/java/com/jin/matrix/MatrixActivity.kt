package com.jin.matrix

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jin.matrix.databinding.ActivityMatrixBinding

class MatrixActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMatrixBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMatrixBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}