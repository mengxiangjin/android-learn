package com.svkj.pdf.activity

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.svkj.pdf.R
import com.svkj.pdf.adapter.PhotoOperationAdapter
import com.svkj.pdf.bean.PhotoBean
import com.svkj.pdf.databinding.ActivityPhotoOperationBinding
import com.svkj.pdf.utils.PDFUtils
import kotlin.math.max
import kotlin.math.min


class PhotoOperationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoOperationBinding
    private lateinit var photoOperationAdapter: PhotoOperationAdapter

    private var photoContainerViews = mutableListOf<View>()


    private val MAX_SELECT_COUNTS = 100
    private var currentSelectPageCounts = 0
        set(value) {
            field = value
            binding.tvImport.text = "导入${field}/${MAX_SELECT_COUNTS}"
            binding.tvImport.alpha = if (field == 0) {
                0.2f
            } else {
                1f
            }
            binding.llBottom.isVisible = field != 0
        }

    private var currentShowIndex = 0
        set(value) {
            field = value
            binding.tvTitle.text = "${currentShowIndex + 1}/${PhotoOverviewActivity.currentBucketPhotoList.size}"
            binding.tvOperation.isSelected = hasSelected()
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoOperationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
    }

    private fun initViews() {
        //修改
        currentSelectPageCounts = PhotoOverviewActivity.selectedPhotoList.size

        photoOperationAdapter = PhotoOperationAdapter(this, PhotoOverviewActivity.currentBucketPhotoList)
        binding.rvPhoto.adapter = photoOperationAdapter
        binding.rvPhoto.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)

        val snapHelper: LinearSnapHelper = object : LinearSnapHelper() {
            override fun findTargetSnapPosition(
                layoutManager: RecyclerView.LayoutManager,
                velocityX: Int,
                velocityY: Int
            ): Int {
                val centerView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION

                val position = layoutManager.getPosition(centerView)
                var targetPosition = -1
                if (layoutManager.canScrollHorizontally()) {
                    targetPosition = if (velocityX < 0) {
                        position - 1
                    } else {
                        position + 1
                    }
                }

                if (layoutManager.canScrollVertically()) {
                    targetPosition = if (velocityY < 0) {
                        position - 1
                    } else {
                        position + 1
                    }
                }

                val firstItem = 0
                val lastItem = layoutManager.itemCount - 1
                targetPosition =
                    min(lastItem.toDouble(), max(targetPosition.toDouble(), firstItem.toDouble()))
                        .toInt()
                Log.d("zyz", "findTargetSnapPosition: " + targetPosition)
                currentShowIndex = targetPosition
                return targetPosition
            }
        }
        snapHelper.attachToRecyclerView(binding.rvPhoto)
        currentShowIndex = intent.getIntExtra(PDFUtils.PHOTO_BEANS_KEY, 0)
        binding.rvPhoto.post {
            binding.rvPhoto.scrollToPosition(currentShowIndex)
        }

        binding.tvImport.setOnClickListener {
            if (currentSelectPageCounts > MAX_SELECT_COUNTS || currentSelectPageCounts <= 0) {
                return@setOnClickListener
            }
            PhotoTransformActivity.launchPhotoOperationActivity(this,PhotoOverviewActivity.selectedPhotoList)
            finish()
        }
        binding.backView.setOnClickListener {
            onBackPressed()
        }
        binding.tvOperation.setOnClickListener {
            var photoBean = PhotoOverviewActivity.currentBucketPhotoList[currentShowIndex]

            if (binding.tvOperation.isSelected) {
                //取消选择
                val removePhotoBean = PhotoOverviewActivity.selectedPhotoList.find {
                    it.id == photoBean.id
                }
                if (removePhotoBean != null) {
                    removeView(PhotoOverviewActivity.selectedPhotoList.indexOf(removePhotoBean))
                }
            } else {
                //选中
                PhotoOverviewActivity.selectedPhotoList.add(photoBean)
                addBottomView(photoBean)
            }
            binding.tvOperation.isSelected = !binding.tvOperation.isSelected
        }

        initBottomView()

    }

    private fun initBottomView() {
        photoContainerViews.clear()
        val selectedPhotoList = PhotoOverviewActivity.selectedPhotoList
        selectedPhotoList.forEach {
            val rootView = LayoutInflater.from(this).inflate(R.layout.item_small_photo,binding.main,false)
            val imgPhoto = rootView.findViewById<ImageView>(R.id.img_photo)
            loadRoundImageCorner(it.filePath,TypedValue.applyDimension(COMPLEX_UNIT_DIP,10f,resources.displayMetrics).toInt(),imgPhoto)
            rootView.setOnClickListener {view ->
                startScrollByBean(it)
            }
            binding.llBottom.addView(rootView)
            photoContainerViews.add(rootView)
        }
    }

    private fun addBottomView(photoBean: PhotoBean) {
        val rootView = LayoutInflater.from(this).inflate(R.layout.item_small_photo,binding.main,false)
        val imgPhoto = rootView.findViewById<ImageView>(R.id.img_photo)
        loadRoundImageCorner(photoBean.filePath,TypedValue.applyDimension(COMPLEX_UNIT_DIP,10f,resources.displayMetrics).toInt(),imgPhoto)
        binding.llBottom.addView(rootView)
        photoContainerViews.add(rootView)
        currentSelectPageCounts = PhotoOverviewActivity.selectedPhotoList.size
        rootView.setOnClickListener {
            startScrollByBean(photoBean)
        }
        Log.d("zyz", "addBottomView: ")
        scrollToRight()
    }

    private fun removeView(photoBeanIndex: Int) {
        binding.llBottom.removeViewAt(photoBeanIndex)
        PhotoOverviewActivity.selectedPhotoList.removeAt(photoBeanIndex)
        currentSelectPageCounts = PhotoOverviewActivity.selectedPhotoList.size
        scrollToRight()

    }

    private fun startScrollByBean(bean: PhotoBean) {
        var photoBean = PhotoOverviewActivity.currentBucketPhotoList.find {
            it.id == bean.id
        }
        photoBean?.let {
            val targetIndex = PhotoOverviewActivity.currentBucketPhotoList.indexOf(it)
            binding.rvPhoto.scrollToPosition(targetIndex)
            currentShowIndex = targetIndex
        }
    }

    fun scrollToRight() {
        binding.scrollView.post {
            binding.scrollView.fullScroll(ScrollView.FOCUS_RIGHT)
        }
    }

    fun loadRoundImageCorner(
        filePath: String,
        corner: Int,
        imageView: ImageView
    ) {
        val options = RequestOptions.bitmapTransform(RoundedCorners(corner))

        Glide.with(this)
            .load(filePath)
            .apply(options)
            .into(imageView)
    }


    private fun hasSelected(): Boolean {
        val photoBean = PhotoOverviewActivity.currentBucketPhotoList[currentShowIndex]
        val bean = PhotoOverviewActivity.selectedPhotoList.find {
            photoBean.id == it.id
        }
        return bean != null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(200)
        finish()

    }

}