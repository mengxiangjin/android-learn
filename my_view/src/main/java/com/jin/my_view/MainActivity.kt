package com.jin.my_view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jin.my_view.view.InputNumberView

class  MainActivity : AppCompatActivity() {

    private var numberView: InputNumberView? = null

    companion object {
        private const val TAG = "MainActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        numberView = findViewById(R.id.input_view)
        numberView?.setNumberChangeListener {value->
            Log.d(TAG, "onCreate: " + value)
        }
    }
}