
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jin.movie.bean.VideoTask
import com.jin.movie.room.AppDatabase
import com.jin.movie.utils.M3u8Merger
import com.jin.movie.utils.ManualM3u8Downloader // 引用上一条回答里的手搓工具类
import com.jin.movie.utils.VideoStorageUtils
import kotlinx.coroutines.*

object MyDownloadManager {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val jobs = mutableMapOf<String, Job>() // 管理正在运行的任务

    // 用于通知 UI 更新 (观察者模式)
    private val _taskUpdateEvent = MutableLiveData<VideoTask>()
    val taskUpdateEvent: LiveData<VideoTask> = _taskUpdateEvent

    // 1. 添加任务并开始下载
    fun startDownload(context: Context, url: String, title: String, cover: String) {
        scope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.get(context).taskDao()
                var task = db.get(url)

                if (task == null) {
                    // 新任务
                    val dir = context.getExternalFilesDir(null)?.absolutePath + "/Download/${title}"
                    val path = "$dir/${title}.m3u8"
                    // 初始状态设为 WAIT
                    task = VideoTask(url, title, cover, path, state = VideoTask.STATE_WAIT)
                    db.insert(task)
                    Log.d("Manager", "新任务已插入数据库: $title")
                } else {
                    Log.w("Manager", "任务已存在，跳过插入: $title")
                    // 如果任务已存在但处于暂停或失败，自动恢复下载
                    if (task.state == VideoTask.STATE_PAUSE || task.state == VideoTask.STATE_FAIL) {
                        // 也可以选择在这里自动 resumeTask
                    }
                }

                // 启动下载
                startActualDownload(context, task)

            } catch (e: Exception) {
                Log.e("Manager", "插入任务失败: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // 2. 恢复/重试任务
    fun resumeTask(context: Context, task: VideoTask) {
        if (jobs.containsKey(task.url)) return // 已经在下载了
        startActualDownload(context, task)
    }

    // 3. 暂停任务
    fun pauseTask(context: Context, task: VideoTask) {
        // 取消协程
        jobs[task.url]?.cancel()
        jobs.remove(task.url)

        // 更新数据库状态
        task.state = VideoTask.STATE_PAUSE
        updateTaskInDb(context, task)
    }

    // 4. 删除任务
    fun deleteTask(context: Context, task: VideoTask) {
        pauseTask(context, task) // 先停止
        scope.launch(Dispatchers.IO) {
            // 删除数据库记录
            AppDatabase.get(context).taskDao().delete(task)
            // 删除本地文件
            try {
                val file = java.io.File(task.localPath)
                file.parentFile?.deleteRecursively()
            } catch (e: Exception) { e.printStackTrace() }

            // 通知 UI 移除（这里简单处理，让Fragment重新加载列表即可）
            // 实际项目可以用 LiveData 发送删除事件
        }
    }

    // 内部方法：执行下载
    private fun startActualDownload(context: Context, task: VideoTask) {
        // 标记为下载中
        task.state = VideoTask.STATE_DOWNLOADING
        updateTaskInDb(context, task)

        val job = scope.launch(Dispatchers.IO) {
            val headers = mapOf(
                "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36...",
                "Referer" to "https://zhuanma11.chineseteanet.com/", // 你的域名
                "Origin" to "https://zhuanma11.chineseteanet.com"
            )

            val saveDir = java.io.File(task.localPath).parent ?: ""

            // 调用我们之前写的工具类
            ManualM3u8Downloader.download(task.url, saveDir, headers, object : ManualM3u8Downloader.OnDownloadListener {
                override fun onProgress(current: Int, total: Int) {
                    val newProgress = (current * 100f / total).toInt()

                    // 只有进度真的变了才通知
                    if (newProgress != task.progress || current != task.currentTs) {
                        task.progress = newProgress
                        task.currentTs = current
                        task.totalTs = total

                        // 【核心优化】只发 LiveData 通知 UI 更新，不写数据库！
                        // 内存操作是纳秒级的，绝对不会卡顿
                        _taskUpdateEvent.postValue(task)
                    }
                }

                override fun onSuccess(localM3u8Path: String) {
                    scope.launch(Dispatchers.IO) {
                        // 1. 更新状态为 "合并中"
                        task.state = VideoTask.STATE_MERGING
                        task.progress = 100
                        updateTaskInDb(context, task)

                        val m3u8File = java.io.File(localM3u8Path) // 例如: .../Download/Title/index.m3u8
                        // 1. 先在内部目录合并成临时 MP4 (速度快，不涉及权限)
                        // m3u8File.parentFile 就是 .../Android/data/包名/files/Download/Title/
                        // 我们把 temp.mp4 放在这个文件夹里
                        val tempMp4File = java.io.File(m3u8File.parentFile, "temp_${System.currentTimeMillis()}.mp4")

                        Log.d("Manager", "正在内部合并: ${tempMp4File.absolutePath}")

                        // 执行合并
                        val mergeSuccess = M3u8Merger.merge(m3u8File, tempMp4File)

                        if (mergeSuccess) {
                            // ============================================
                            // 【核心修改】合并成功后，导出到公共目录
                            // ============================================
                            val finalFileName = "${task.title}.mp4"

                            // 调用工具类，复制到 /Download/JinMovie/
                            val publicPath = VideoStorageUtils.copyToPublicDownload(context, tempMp4File, finalFileName)

                            if (publicPath != null) {
                                Log.d("Manager", "导出成功，路径: $publicPath")

                                // 1. 更新数据库路径
                                task.state = VideoTask.STATE_SUCCESS
                                task.localPath = publicPath // 这里存的是 content://... 或者 /storage/...
                                updateTaskInDb(context, task)

                                // 2. 清理所有内部临时文件 (ts文件夹 + 临时mp4)
                                try {
                                    val sourceFolder = m3u8File.parentFile
                                    sourceFolder?.deleteRecursively() // 删掉 TS 和 Key
                                    // tempMp4File 如果在文件夹里已经被删了，如果在外面要单独删
                                    if (tempMp4File.exists()) tempMp4File.delete()
                                } catch (e: Exception) { e.printStackTrace() }

                            } else {
                                // 导出失败（比如磁盘满了）
                                task.state = VideoTask.STATE_FAIL
                                updateTaskInDb(context, task)
                            }

                        } else {
                            // 合并失败
                            task.state = VideoTask.STATE_FAIL
                            updateTaskInDb(context, task)
                        }

                        jobs.remove(task.url)
                    }
                }

                override fun onError(msg: String) {
                    task.state = VideoTask.STATE_FAIL
                    updateTaskInDb(context, task)
                    jobs.remove(task.url)
                }
            })
        }
        jobs[task.url] = job
    }

    // 更新数据库并通知 UI
    private fun updateTaskInDb(context: Context, task: VideoTask) {
        scope.launch(Dispatchers.IO) {
            AppDatabase.get(context).taskDao().update(task)
        }
        _taskUpdateEvent.postValue(task)
    }

    // 【新增】判断某个 URL 的任务是否正在内存中运行
    fun isDownloading(url: String): Boolean {
        val job = jobs[url]
        // Job 不为空 且 处于活跃状态(isActive) 才算正在下载
        return job != null && job.isActive
    }
}