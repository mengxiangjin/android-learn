package com.wifi.exchangefile

import android.Manifest
import android.content.ContentResolver
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Video
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.wifi.exchangefile.adapter.FileTypeAdapter
import com.wifi.exchangefile.adapter.ImageVideoAdapter
import com.wifi.exchangefile.bean.FileTypeBean
import com.wifi.exchangefile.databinding.ActivityChooseBinding
import com.wifi.exchangefile.tools.ClientService
import com.wifi.exchangefile.tools.ServerService
import java.io.File


class ChooseFileActivity: AppCompatActivity() {


    private lateinit var binding: ActivityChooseBinding

    private var imgPaths = mutableListOf<String>()
    private var videoPaths = mutableListOf<String>()

    private var imageAdapter: ImageVideoAdapter? = null
    private var videoAdapter: ImageVideoAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }


    private fun initView() {
        val fileTypeBeans = listOf(
            FileTypeBean(0,"图片"),
            FileTypeBean(1,"视频"),
//            FileTypeBean(2,"文档"),
//            FileTypeBean(3,"应用"),
//            FileTypeBean(4,"音乐"),
//            FileTypeBean(5,"联系人"),
            )
        binding.rvFileType.adapter = FileTypeAdapter(this,fileTypeBeans).apply {
            onItemClickAction = {
                binding.rvVideo.isVisible = it.id == 1
                binding.rvImage.isVisible = it.id == 0
            }
        }
        binding.rvFileType.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)


        imageAdapter = ImageVideoAdapter(this, imgPaths)
        videoAdapter = ImageVideoAdapter(this, videoPaths)

        binding.rvImage.adapter = imageAdapter
        binding.rvImage.layoutManager = GridLayoutManager(this,3)


        binding.rvVideo.adapter = videoAdapter
        binding.rvVideo.layoutManager = GridLayoutManager(this,3)


        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO

        )
        requestPermissions(permissions,0)
        getImages()
        getVideos()

        binding.rvVideo.isVisible = false
        binding.rvImage.isVisible = true

        binding.tvSend.setOnClickListener {
            val tempList = mutableListOf<String>()
            val result = mutableListOf<File>()

            imageAdapter?.chooseSet?.let { it1 ->
                tempList.addAll(it1)
            }
            videoAdapter?.chooseSet?.let { it1 -> tempList.addAll(it1) }

            tempList.forEach {
                result.add(File(it))
            }

            val clientService = ClientService.getImstance()
            clientService.sendFiles(result)
        }
    }


    private fun getImages() {
        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val contentResolver: ContentResolver = contentResolver
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME,
            MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.DATE_ADDED
        )
        val cursor = contentResolver.query(
            imageUri, null, null, null,
            MediaStore.Images.Media.DATE_ADDED + " desc"
        )
        if (cursor != null) {
            if (cursor.count != 0) {
                while (cursor.moveToNext()) {
                    val path =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                    val fileName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                    val size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
                    val date =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))
                    imgPaths.add(path)
                }
                imageAdapter?.setNewDatas(imgPaths)
            }
            cursor.close()
        }
    }

    private fun getVideos() {
        val contentResolver = contentResolver
        val projection = arrayOf(
            Video.Media._ID,
            Video.Media.TITLE,
            Video.Media.DATA,
            Video.Media.DURATION
        )
        val cursor = contentResolver.query(
            Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val path = cursor.getString(cursor.getColumnIndex(Video.Media.DATA))
                videoPaths.add(path)
            }
            cursor.close()
        }
        videoAdapter?.setNewDatas(videoPaths)

    }
}