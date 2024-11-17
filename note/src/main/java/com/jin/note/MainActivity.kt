package com.jin.note

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.jin.note.databinding.ActivityMainBinding
import com.jin.note.surface.BitmapCropView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var imgResourseId = mutableListOf<Int>()

//    private var selectPictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(),object : ActivityResultCallback<ActivityResult> {
//        override fun onActivityResult(result: ActivityResult) {
//            result.data?.let { it ->
//                it.data?.let {uri->
//                    val openInputStream = contentResolver.openInputStream(uri)
//                    val insertableImg = InsertableImg(BitmapFactory.decodeStream(openInputStream))
//                    binding.surfaceView.addInsertableObject(insertableImg)
//                }
//            }
//            Log.d("TAG", "onActivityResult: " + result.data?.data)
//        }
//    })

    private var rootView = mutableListOf<View>()
    private var currentShowIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imgResourseId.add(R.drawable.img)
        imgResourseId.add(R.drawable.img_1)
        imgResourseId.add(R.drawable.img_2)

        imgResourseId.forEach {
            val view = LayoutInflater.from(this).inflate(R.layout.item_test, binding.clContent, false)
            val imgView = view.findViewById<ImageView>(R.id.img)
            var cropView = view.findViewById<BitmapCropView>(R.id.crop_view)
            rootView.add(view)
            imgView.setImageResource(it)
            binding.clContent.addView(view)
        }

        rootView.forEachIndexed { index, view ->
            view.isInvisible = index != currentShowIndex
        }

        binding.btnNext.setOnClickListener {
            currentShowIndex += 1
            if (currentShowIndex >= rootView.size) {
                //结束
                currentShowIndex = rootView.size - 1
            }
            rootView.forEachIndexed { index, view ->
                view.isInvisible = index != currentShowIndex
            }
        }

        binding.btnBefore.setOnClickListener {
            currentShowIndex -=1
            if (currentShowIndex < 0) {
                currentShowIndex = 0
            }
            rootView.forEachIndexed { index, view ->
                view.isInvisible = index != currentShowIndex
            }
        }

        binding.btnFinish.setOnClickListener {
            rootView.forEach {
                val cropView = it.findViewById<BitmapCropView>(R.id.crop_view)
                val imgView = it.findViewById<ImageView>(R.id.img)

                val cropRegion = cropView.getCropRegion()

                val bitmap = (imgView.drawable as BitmapDrawable).bitmap
                val createBitmap = Bitmap.createBitmap(
                    bitmap,
                    cropRegion!!.left,
                    cropRegion.top,
                    cropRegion.width(),
                    cropRegion.height()
                )
                Log.d("zyz", "onCreate: ")
            }
        }


//        binding.btnCrop.setOnClickListener {
//            if (binding.img.drawable is BitmapDrawable) {
//                val cropRegion = binding.cropView.getCropRegion()
//                val bitmap = (binding.img.drawable as BitmapDrawable).bitmap
//
//                Log.d("zyz", "onCreate:left " + cropRegion!!.left)
//                Log.d("zyz", "onCreate:top " + cropRegion.top)
//                Log.d("zyz", "onCreate:right " + cropRegion.right)
//                Log.d("zyz", "onCreate:bottom " + cropRegion.bottom)
//                Log.d("zyz", "onCreate:width " + bitmap.width)
//                Log.d("zyz", "onCreate:height " + bitmap.height)
//
//
//                val createBitmap = Bitmap.createBitmap(
//                    bitmap,
//                    cropRegion.left,
//                    cropRegion.top,
//                    cropRegion.width(),
//                    cropRegion.height()
//                )
//                binding.img2.setImageBitmap(createBitmap)
//
//            }
    }

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
//        binding.llTools.setOnTouchListener(object : OnTouchListener {
//            override fun onTouch(v: View?, event: MotionEvent): Boolean {
//                Log.d("zyz", "onTouch: " + event.action)
//                when(event.action) {
//                    MotionEvent.ACTION_DOWN -> {
//                        lastX = event.x
//                    }
//                    MotionEvent.ACTION_MOVE -> {
//                        var currentX = event.x - lastX
//                        if (currentX > 0) {
//                            //右滑
//                            if (binding.llTools.scrollX <= 0) {
//                                currentX = 0f
//                                binding.llTools.scrollTo(0,0)
//                                lastX = event.x
//                                return true
//                            }
//                        } else {
//                            //左滑
//                            if (binding.llTools.scrollX >= binding.view.width) {
//                                currentX = 0f
//                            }
//                        }
//                        if (binding.llTools.scrollX - currentX > binding.view.width) {
//                            currentX = (binding.llTools.scrollX - binding.view.width).toFloat()
//                        }
//                        binding.llTools.scrollBy(-currentX.toInt(),0)
//                        lastX = event.x
//                    }
//                    MotionEvent.ACTION_UP -> {
//                        var currentX = event.x - lastX
//                    }
//                }
//
//                return true
//            }
//
//        })
}

