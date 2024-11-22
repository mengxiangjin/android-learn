package com.svkj.pdf

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.svkj.pdf.activity.PDFOverviewActivity
import com.svkj.pdf.activity.PhotoOverviewActivity
import com.svkj.pdf.databinding.ActivityMainBinding
import com.svkj.pdf.dialog.TipStyleTwoDialog
import com.svkj.pdf.utils.PDFUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
    }

    private fun initViews() {
        binding.rlPdfMerge.setOnClickListener {
            allowManagerPermission {
                val intent = Intent(this,PDFOverviewActivity::class.java)
                intent.putExtra(PDFUtils.PDF_OPERATION_KEY,PDFUtils.PDF_MERGE_OPERATION)
                startActivity(intent)
            }
        }
        binding.rlPdfSplit.setOnClickListener {
            allowManagerPermission {
                val intent = Intent(this,PDFOverviewActivity::class.java)
                intent.putExtra(PDFUtils.PDF_OPERATION_KEY,PDFUtils.PDF_SPLIT_OPERATION)
                startActivity(intent)
            }
        }
        binding.rlPdfToPicture.setOnClickListener {
            allowManagerPermission {
                val intent = Intent(this,PDFOverviewActivity::class.java)
                intent.putExtra(PDFUtils.PDF_OPERATION_KEY,PDFUtils.PDF_TO_PICTURE_OPERATION)
                startActivity(intent)

            }
        }
        binding.rlPictureToPdf.setOnClickListener {
            allowManagerPermission {
                allowWritePermission {
                    startActivity(Intent(this,PhotoOverviewActivity::class.java))
                }
            }
        }
    }

    private fun allowManagerPermission(allowCallback: (() -> Unit)) {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                //申请管理文件权限
                TipStyleTwoDialog(this).show("管理所有文件权限请求","我们需要访问所有文件权限，才能正常使用文件读写等功能",object : TipStyleTwoDialog.OnEventListener{
                    override fun onConfirm() {
                        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                        startActivityForResult(intent, 200)
                    }
                })
                return
            }
        }
        allowCallback.invoke()
    }

    private fun allowWritePermission(allowCallback: (() -> Unit)) {
        val permission = mutableListOf<String>()
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            && applicationInfo.targetSdkVersion >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permission.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }
        if (permission.isNotEmpty()) {
            //申请管理文件权限
            TipStyleTwoDialog(this).show("存储权限请求","我们需要存储权限，才能正常使用相册导入、图片存储等功能",object : TipStyleTwoDialog.OnEventListener{
                override fun onConfirm() {
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        permission.toTypedArray(),
                        200
                    )
                }
            })
            return
        }
        allowCallback.invoke()
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivity(Intent(this,PhotoOverviewActivity::class.java))
        }
    }


}