package com.jin.movie

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.jin.movie.bean.PlayBackItem
import com.jin.movie.bean.RespPlayBack
import com.jin.movie.bean.TSBean
import com.jin.movie.databinding.ActivityMainBinding
import com.jin.movie.utils.TsDownload
import com.jin.movie.utils.TsDownload.DownloadCallback
import com.jin.movie.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.net.URL
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var okHttpClient: OkHttpClient
    private lateinit var movieRootFile: File

    private var msgStringBuilder = StringBuilder()

    private var currentPages = 1
    private var anchorUserId = ""
    private var currentRespPlayBack: RespPlayBack? = null

    val m3u8Url = "http://video.playback.tencent.taolu2.cn/live/20250611/11749573351717210449/1825693369320470570-56_eof.m3u8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setActivityBarStyle(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initDatas()

        binding.btnDownload.setOnClickListener {
            //下载m3u8文件到本地
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE),100)
                return@setOnClickListener
            }
            if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
                return@setOnClickListener
            }
            if (!movieRootFile.exists()) {
                movieRootFile.mkdir()
            }
            anchorUserId = binding.etId.text.toString()

            GlobalScope.launch {
                startGetDatasFromServer()
            }
        }


    }

    private fun initDatas() {
        okHttpClient = OkHttpClient()
        movieRootFile = Utils.getRootFile()
        TsDownload.setDownloadCallback(object : DownloadCallback {
            override fun onError(msg: String) {
                showToast(msg)
            }

            override fun onSuccess(msg: String) {
                showToast(msg)
            }

        })
    }

    private fun startGetDatasFromServer() {
        val url =
            "http://live.taolu.black/live/live/video/anchor/$currentPages/10?anchorUserId=$anchorUserId&sign=1745578191-c17548280b8d489fa64da236eff4d53c-0-e6558ed709eee7b8e0976c5edef652db&uid=218904&systemModel=Pixel 2 XL&appType=1&appVer=3.7.6&phoneBrand=google&version=3.7.6&deviceId=63bd2e866c6ef324&systemVersion=11&versionCode=20250105"
        val request = Request.Builder()
            .url(url)
            .addHeader("token", "aiya_1a3c0e62-37c7-4ebe-ae68-1ec0e18f056bgq")
            .addHeader(
                "appVersion",
                "{\"uid\":\"218904\",\"systemModel\":\"Pixel 2 XL\",\"appType\":\"1\",\"appVer\":\"3.8.4\",\"phoneBrand\":\"google\",\"version\":\"3.8.4\",\"deviceId\":\"63bd2e866c6ef324\",\"systemVersion\":\"11\",\"versionCode\":\"20250528\"}"
            )
            .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            val responseBodyStr = response.body!!.string()
            currentRespPlayBack = Gson().fromJson(responseBodyStr,RespPlayBack::class.java)
            if (currentRespPlayBack!!.isSuccess) {
                startDownloadDatas()

                if (currentRespPlayBack!!.data
                        .total > currentPages * currentRespPlayBack!!.data.size
                ) {
                    currentPages++
                    startGetDatasFromServer()
                }
            } else {
                showToast(currentRespPlayBack!!.message)
            }
        } catch (e: Exception) {
            showToast(e.toString())
        }
    }

    private fun startDownloadDatas() {
        val startTime = System.currentTimeMillis()
        //创建每个视频的小文件夹
        createParentDirs()

        currentRespPlayBack!!.data.records.forEach {playBackItem ->

            //判断是否已经下载过
            if (hasDownload(playBackItem)) {
                showToast(playBackItem.videoTitle + " 已下载过，跳过 ")
                //删除已创建的文件夹
                Files.walkFileTree(File(Utils.getPathForMovieName(playBackItem)).toPath(), object : SimpleFileVisitor<Path>() {
                    override fun visitFile(
                        file: Path?,
                        attrs: BasicFileAttributes?
                    ): FileVisitResult {
                        Files.delete(file)
                        return FileVisitResult.CONTINUE
                    }

                    override fun postVisitDirectory(
                        dir: Path?,
                        exc: IOException?
                    ): FileVisitResult {
                        Files.delete(dir)
                        return FileVisitResult.CONTINUE
                    }
                })
                return@forEach
            } else {
                showToast(playBackItem.videoTitle + " 未下载")
            }

            val executorService = Executors.newFixedThreadPool(1);

            try {
                if (playBackItem.videoUrl.endsWith("mp4")) {
                    showToast(playBackItem.videoTitle + " mp4文件 ")
//                    String path = getDownloadTsPath(playBackItem).replaceAll("\\\\", "/");
//                    downVideo(playBackItem.getVideoUrl(),path + playBackItem.getVideoTitle() + ".mp4");
//                    moveAndDelDirFromTask(playBackItem);
                    return@forEach
                }

                downloadM3U8File(playBackItem)
                val tsBeans = generateTSBeans(playBackItem)
                if (tsBeans.isEmpty()) return@forEach
                playBackItem.tsFileTotalCounts = tsBeans.size
                playBackItem.tsBeanList = tsBeans
                Utils.TASK_COUNTS_ITEM.set(tsBeans.size)

                generateFFMpegArgumentFile(playBackItem)

                //多线程下载tsBeans任务
                tsBeans.forEach {
                    executorService.execute(TsDownload(it.serialNumber,it.downloadURl,playBackItem))
                }
                // 关闭线程池，等待任务结束。
                executorService.shutdown()
                while (!executorService.isTerminated && Utils.TASK_COUNTS_ITEM.get() > 0) {
                    showToast("任务下载中: " + Utils.TASK_COUNTS_ITEM.get())
                    TimeUnit.SECONDS.sleep(2)
                }

            } catch (e: Exception) {
                showToast(e.toString())
            }
        }
        val endTime = System.currentTimeMillis()
        showToast("当前已下载完成，耗时：${endTime - startTime}")
    }

    private fun createParentDirs() {
        val records = currentRespPlayBack!!.data.records
        records.forEach { playBackItem->
            //文件名+id号
            playBackItem.videoTitle = playBackItem.videoTitle + "_" + playBackItem.id

            //去除空格
            playBackItem.videoTitle =
                playBackItem.videoTitle
                    .replace(" ", "")
                    .replace("?", "")
                    .replace(":", "")
                    .replace("/", "")
                    .replace("|","")

            val regex = "[\uD83C-\uDBFF\uDC00-\uDFFF\u2B50\u2600-\u26FF\u2700-\u27BF]"
            // 创建正则表达式模式
            val pattern = Pattern.compile(regex)
            // 使用正则表达式替换掉所有匹配的表情符号
            val matcher = pattern.matcher(playBackItem.videoTitle)
            playBackItem.videoTitle = matcher.replaceAll("")

            val itemFile = File(Utils.getPathForMovieName(playBackItem))
            itemFile.mkdirs()
        }
    }

    private fun downloadM3U8File(playBackItem: PlayBackItem):Boolean {
        try {
            val url = URL(playBackItem.videoUrl)
            val conn = url.openConnection()
            conn.setRequestProperty("Referer","http://MAfAIOo0E8EMOWPA.black")


            val m3u8File = File(Utils.getPathForM3U8File(playBackItem))
            m3u8File.deleteOnExit()
            val randomAccessFile = RandomAccessFile(m3u8File,"rw")
            val inputStream = conn.getInputStream()
            val buffer = ByteArray(1024)
            var hasRead = inputStream.read(buffer)
            while (hasRead != -1) {
                randomAccessFile.write(buffer,0,hasRead)
                hasRead = inputStream.read(buffer)
            }
            randomAccessFile.close()
            inputStream.close()
            return true
        }catch (e: Exception) {
            showToast("downloadM3U8FileError: $e")
            return false
        }

    }

    private fun generateTSBeans(playBackItem: PlayBackItem): List<TSBean> {
        val m3u8File = File(Utils.getPathForM3U8File(playBackItem))
        if (!m3u8File.exists()) return emptyList()
        var serialNumber = 0;
        val randomAccessFile = RandomAccessFile(m3u8File,"r")
        val result = mutableListOf<TSBean>()
        var content = randomAccessFile.readLine()
        while (content != null) {
            if (!content.startsWith("#")) {
                serialNumber++
                val tsBean = TSBean()
                tsBean.serialNumber = serialNumber
                tsBean.downloadURl = getTSBeanDownloadPrefix() + content
                result.add(tsBean)
            }
            content = randomAccessFile.readLine()
        }
        randomAccessFile.close()
        return result
    }

    private fun generateFFMpegArgumentFile(playBackItem: PlayBackItem) {
        val mergeFile = File(Utils.getPathForFFMpegFile(playBackItem))
        mergeFile.deleteOnExit()
        val randomAccessFile = RandomAccessFile(mergeFile,"rw")
        playBackItem.tsBeanList.forEach {
            val writeBytes = "file '" + it.serialNumber + ".ts" + "'\n"
            randomAccessFile.write(writeBytes.toByteArray())
        }
        randomAccessFile.close()
    }

    private fun getTSBeanDownloadPrefix(): String {
        val targetIndex = m3u8Url.lastIndexOf("/") + 1
        return m3u8Url.substring(0,targetIndex)
    }



    private fun setActivityBarStyle(activity: AppCompatActivity) {
        val decorView = activity.window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //注释掉这行代码
                //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        decorView.systemUiVisibility = option
        //设置导航栏（顶部和底部）颜色为透明，注释掉这行代码
        //getWindow().setNavigationBarColor(Color.TRANSPARENT);
        //设置通知栏颜色为透明
        activity.window.statusBarColor = Color.TRANSPARENT
        val actionBar = activity.supportActionBar
        actionBar?.hide()
    }

    private fun showToast(msg: String) {
        runOnUiThread {
            msgStringBuilder.append(msg)
            msgStringBuilder.append("\n")
            binding.tvContent.text = msgStringBuilder.toString()
            binding.scrollView.post {
                binding.scrollView.fullScroll(View.FOCUS_DOWN)
            }
//            Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasDownload(playBackItem: PlayBackItem): Boolean {
        val file = File(Utils.getPathForUserRoot(playBackItem))
        if (!file.exists() || !file.isDirectory || file.listFiles() == null) {
            return false
        }
        file.listFiles()!!.forEach {item->
            if (item.isFile && item.nameWithoutExtension == playBackItem.videoTitle) {
                return true
            }
        }
        return false
    }
}