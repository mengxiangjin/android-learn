package com.jin.movie.fragment

import MyDownloadManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.jin.movie.activity.PlayerActivity
import com.jin.movie.adapter.DownloadAdapter
import com.jin.movie.bean.VideoTask
import com.jin.movie.databinding.FragmentDownloadBinding
import com.jin.movie.room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DownloadFragment : Fragment() {

    private var _binding: FragmentDownloadBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DownloadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        // 1. 【核心】监听数据库变化 (解决添加延迟问题)
        // 只要 startDownload 插入了数据，这里立刻就会回调，无需手动刷新
        AppDatabase.get(requireContext()).taskDao().getAllLive().observe(viewLifecycleOwner) { list ->
            // 更新 UI
            if (list.isNullOrEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvDownloadList.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvDownloadList.visibility = View.VISIBLE
                adapter.updateData(list)
            }
        }

        // 2. 监听实时进度 (保持不变，用于更新进度条)
        MyDownloadManager.taskUpdateEvent.observe(viewLifecycleOwner) { updatedTask ->
            adapter.updateItemProgress(updatedTask)
        }
    }

    override fun onResume() {
        super.onResume()
        // 【修改】onResume 里只需要处理“状态修正”，不需要再加载列表了
        checkAndFixTaskStatus()
    }

    private fun initRecyclerView() {
        adapter = DownloadAdapter(
            dataList = mutableListOf(),
            onActionClick = { entity -> handleAction(entity) },
            onItemClick = { entity -> handleItemClick(entity) },
            onLongClick = { entity -> showDeleteDialog(entity) }
        )

        binding.rvDownloadList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDownloadList.adapter = adapter
    }

    // 从原来的 refreshData 改名而来，只负责修正假死状态，不负责加载列表
    private fun checkAndFixTaskStatus() {
        lifecycleScope.launch(Dispatchers.IO) {
            val taskDao = AppDatabase.get(requireContext()).taskDao()
            val list = taskDao.getAll() // 这里用同步方法查一下用于检查逻辑
            var needUpdateDb = false

            for (task in list) {
                // 1. 修正假死
                if (task.state == VideoTask.STATE_DOWNLOADING && !MyDownloadManager.isDownloading(task.url)) {
                    task.state = VideoTask.STATE_PAUSE
                    taskDao.update(task)
                    needUpdateDb = false // Room 的 LiveData 会自动处理刷新，这里不需要标记了
                }
                // 2. 修正已完成
                if (task.progress == 100 && task.state != VideoTask.STATE_SUCCESS) {
                    val file = java.io.File(task.localPath)
                    if (file.exists() && file.length() > 0) {
                        task.state = VideoTask.STATE_SUCCESS
                        taskDao.update(task)
                    }
                }
            }
        }
    }

    // 3. 处理点击事件
    private fun handleAction(task: VideoTask) {
        when (task.state) {
            VideoTask.STATE_DOWNLOADING -> {
                MyDownloadManager.pauseTask(requireContext(), task)
            }
            VideoTask.STATE_PAUSE, VideoTask.STATE_FAIL -> {
                MyDownloadManager.resumeTask(requireContext(), task)
            }
        }
        // 此时 Adapter 会通过 observe 收到状态更新
    }

    private fun handleItemClick(task: VideoTask) {
        if (task.state == VideoTask.STATE_SUCCESS) {
            val path = task.localPath

            // 判断是 Uri 还是 路径
            val finalUrl = if (path.startsWith("content://") || path.startsWith("file://")) {
                path
            } else {
                // 如果是绝对路径，加上 file:// 协议头更稳妥
                "file://$path"
            }

            PlayerActivity.start(
                requireContext(),
                url = finalUrl,
                title = task.title,
                coverUrl = task.coverUrl
            )
        }
    }




    /**
     * 长按删除任务
     */
    private fun showDeleteDialog(task: VideoTask) {
        AlertDialog.Builder(requireContext())
            .setTitle("删除任务")
            .setMessage("确定要删除 ${task.title} 吗？\n同时删除本地文件。")
            .setPositiveButton("删除") { _, _ ->
                // 删除任务并删除文件
                MyDownloadManager.deleteTask(requireContext(), task)
                Toast.makeText(requireContext(), "已删除", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("取消", null)
            .show()
    }

    // ===============================================
    //      Aria 注解回调区域 (用于实时更新进度)
    // ===============================================




    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}