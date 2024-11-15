package com.jin.note

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.jin.note.bean.InsertableImg
import com.jin.note.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var selectPictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),object : ActivityResultCallback<ActivityResult> {
        override fun onActivityResult(result: ActivityResult) {
            result.data?.let { it ->
                it.data?.let {uri->
                    val openInputStream = contentResolver.openInputStream(uri)
                    val insertableImg = InsertableImg(BitmapFactory.decodeStream(openInputStream))
                    binding.surfaceView.addInsertableObject(insertableImg)
                }
            }
            Log.d("TAG", "onActivityResult: " + result.data?.data)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.llImg.setOnClickListener {
//            if (PermissionUtils.hasPermission(this, listOf(Manifest.permission.READ_MEDIA_IMAGES))) {
//                Log.d("zyz", "onCreate: " + 1)
//                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                selectPictureLauncher.launch(intent)
//            } else {
//                Log.d("zyz", "onCreate: " + 2)
//                requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES),PermissionUtils.PERMISSION_CODE_READ_WRITE)
//            }
//        }
//
        var lastX = 0f
        binding.llTools.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                Log.d("zyz", "onTouch: " + event.action)
                when(event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastX = event.x
                    }
                    MotionEvent.ACTION_MOVE -> {
                        var currentX = event.x - lastX
                        if (currentX > 0) {
                            //右滑
                            if (binding.llTools.scrollX <= 0) {
                                currentX = 0f
                                binding.llTools.scrollTo(0,0)
                                lastX = event.x
                                return true
                            }
                        } else {
                            //左滑
                            if (binding.llTools.scrollX >= binding.view.width) {
                                currentX = 0f
                            }
                        }
                        if (binding.llTools.scrollX - currentX > binding.view.width) {
                            currentX = (binding.llTools.scrollX - binding.view.width).toFloat()
                        }
                        binding.llTools.scrollBy(-currentX.toInt(),0)
                        lastX = event.x
                    }
                    MotionEvent.ACTION_UP -> {
                        var currentX = event.x - lastX
                    }
                }

                return true
            }

        })
    }


}