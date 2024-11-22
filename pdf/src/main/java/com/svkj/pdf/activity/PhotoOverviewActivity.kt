package com.svkj.pdf.activity

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.svkj.pdf.adapter.PhotoOverviewAdapter
import com.svkj.pdf.bean.PhotoBean
import com.svkj.pdf.databinding.ActivityPhotoOverviewBinding
import com.svkj.pdf.utils.PDFUtils


class PhotoOverviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoOverviewBinding

    private lateinit var photoOverviewAdapter: PhotoOverviewAdapter

    val launcherOperationActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
        photoOverviewAdapter.selectedPhotoSet.clear()
        photoOverviewAdapter.selectedPhotoSet.addAll(selectedPhotoList)
        photoOverviewAdapter.notifyItemRangeChanged(0,currentBucketPhotoList.size,PhotoOverviewAdapter.ONLY_UPDATE_SELECT_STATUS)
        currentSelectPageCounts = selectedPhotoList.size
    }


    companion object {
        var currentBucketPhotoList = mutableListOf<PhotoBean>()
        var selectedPhotoList = mutableListOf<PhotoBean>()
    }



    private var photoGroupMap = mutableMapOf<String,MutableList<PhotoBean>>()


    private val MAX_SELECT_COUNTS = 100
    private var currentSelectPageCounts = 0
        set(value) {
            field = value
            binding.tvConfirm.text = "导入${field}/${MAX_SELECT_COUNTS}"
            binding.tvConfirm.isVisible = field != 0
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedPhotoList.clear()
        currentBucketPhotoList.clear()
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        getAllPhotoFiles()
    }

    private fun initViews() {
        currentSelectPageCounts = 0
        photoOverviewAdapter = PhotoOverviewAdapter(this, mutableListOf()).apply {
            onSelectStatusChangeAction = {selectedPdfs ->
                selectedPhotoList.clear()
                selectedPhotoList.addAll(selectedPdfs)
                Log.d("zyz", "initViews: " + selectedPdfs.size)
                currentSelectPageCounts = selectedPdfs.size
            }
            onItemClickAction = {clickBean ->
                val intent = Intent(this@PhotoOverviewActivity,PhotoOperationActivity::class.java)
                var currentPosition = 0
                currentBucketPhotoList.forEachIndexed { index, photoBean ->
                    if (photoBean.id == clickBean.id) {
                        currentPosition = index
                    }
                }
                intent.putExtra(PDFUtils.PHOTO_BEANS_KEY,currentPosition)
                launcherOperationActivity.launch(intent)
//                startActivity(intent)
            }
        }
        binding.rvPhoto.adapter = photoOverviewAdapter
        binding.rvPhoto.layoutManager = GridLayoutManager(this,3)

        binding.tvConfirm.setOnClickListener {
            if (currentSelectPageCounts > MAX_SELECT_COUNTS || currentSelectPageCounts <= 0) {
                return@setOnClickListener
            }
            PhotoTransformActivity.launchPhotoOperationActivity(this,selectedPhotoList)
            finish()
        }
        binding.backView.setOnClickListener {
            finish()
        }
    }

    private fun getAllPhotoFiles() {
        val imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        photoGroupMap.clear()
        var numId = 0
        Thread {
            val cursor = contentResolver.query(
                imageUri, null, null, null,
                MediaStore.Images.Media.DATE_ADDED + " desc"
            )
            cursor?.let {
                while (it.moveToNext()) {
                    val filePath =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA))
                    val fileName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                    val size = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.SIZE))
                    val dateStr =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED))
                    val bucketName =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    Log.d("zyz", "getPDFFiles:filePath " + filePath)
                    Log.d("zyz", "getPDFFiles:fileName " + fileName)
                    Log.d("zyz", "getPDFFiles:size " + size)
                    Log.d("zyz", "getPDFFiles:date " + dateStr)
                    Log.d("zyz", "getPDFFiles:bucketName " + bucketName)

                    val photoBean = PhotoBean(numId,fileName,filePath,dateStr,size,bucketName)
                    numId++
                    if (photoGroupMap[bucketName] == null) {
                        val photoBeans = mutableListOf<PhotoBean>()
                        photoBeans.add(photoBean)
                        photoGroupMap[bucketName] = photoBeans
                    } else {
                        val photoBeans = photoGroupMap[bucketName]
                        photoBeans!!.add(photoBean)
                    }
                }
            }
            runOnUiThread {
                getPhotosByBucketName()
                currentBucketPhotoList.clear()
                currentBucketPhotoList.addAll(getPhotosByBucketName())
                photoOverviewAdapter.setNewDatas(currentBucketPhotoList)
            }

        }.start()
    }


    private fun getPhotosByBucketName(bucketName: String? = null): MutableList<PhotoBean> {
        if (bucketName == null) {
            //获取全部
            val resultList = mutableListOf<PhotoBean>()
            photoGroupMap.keys.forEach {
                resultList.addAll(photoGroupMap[it]!!)
            }
            return resultList
        }else {
            return photoGroupMap[bucketName]?: mutableListOf()
        }
    }



}