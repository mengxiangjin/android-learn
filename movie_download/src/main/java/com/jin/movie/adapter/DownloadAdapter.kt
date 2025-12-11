package com.jin.movie.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.jin.movie.R
import com.jin.movie.bean.VideoTask

class DownloadAdapter(
    private val dataList: MutableList<VideoTask>,
    private val onActionClick: (VideoTask) -> Unit, // 点击暂停/继续
    private val onItemClick: (VideoTask) -> Unit,   // 点击条目(播放)
    private val onLongClick: (VideoTask) -> Unit    // 长按(删除)
) : RecyclerView.Adapter<DownloadAdapter.ViewHolder>() {

    companion object {
        private const val PAYLOAD_PROGRESS = "PAYLOAD_PROGRESS"
    }

    /**
     * 刷新整个列表（例如进入页面时）
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<VideoTask>) {
        dataList.clear()
        dataList.addAll(newList)
        notifyDataSetChanged()
    }

    /**
     * 高效局部刷新：只更新进度，不重新加载图片
     */
    fun updateItemProgress(task: VideoTask) {
        val index = dataList.indexOfFirst { it.url == task.url }
        if (index != -1) {
            // 替换旧数据
            dataList[index] = task
            // 发送 payload 通知，触发带参数的 onBindViewHolder
            notifyItemChanged(index, PAYLOAD_PROGRESS)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_download_task, parent, false)
        return ViewHolder(view)
    }

    // 全量绑定（初始化或滑动时调用）
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        bindView(holder, dataList[position])
    }

    // 局部绑定（进度更新时调用）
    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty() && payloads[0] == PAYLOAD_PROGRESS) {
            // 只更新进度和状态文字，不重新加载 Glide 图片
            updateStatusView(holder, dataList[position])
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int = dataList.size

    private fun bindView(holder: ViewHolder, task: VideoTask) {
        // 1. 设置基础信息
        holder.tvTitle.text = task.title

        // 2. 加载封面
        Glide.with(holder.itemView.context)
            .load(task.coverUrl)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(16))) // 圆角
            .placeholder(android.R.color.darker_gray)
            .error(android.R.color.holo_red_light)
            .into(holder.ivCover)

        // 3. 设置点击事件
        holder.btnAction.setOnClickListener { onActionClick(task) }
        holder.itemView.setOnClickListener { onItemClick(task) }
        holder.itemView.setOnLongClickListener {
            onLongClick(task)
            true
        }

        // 4. 更新状态 UI
        updateStatusView(holder, task)
    }

    @SuppressLint("SetTextI18n")
    private fun updateStatusView(holder: ViewHolder, task: VideoTask) {
        // 设置进度条
        holder.progressBar.progress = task.progress

        // 根据状态设置文字颜色和图标
        when (task.state) {
            VideoTask.STATE_DOWNLOADING -> {
                // 下载中：蓝色文字，显示暂停图标
                holder.tvStatus.text = "下载中 ${task.progress}% (${task.currentTs}/${task.totalTs})"
                holder.tvStatus.setTextColor(Color.parseColor("#2196F3"))
                holder.btnAction.setImageResource(R.drawable.ic_pause_circle) // 确保你有这个图标
                holder.btnAction.visibility = View.VISIBLE
            }
            VideoTask.STATE_PAUSE -> {
                // 暂停：灰色文字，显示播放图标
                holder.tvStatus.text = "已暂停 • ${task.progress}%"
                holder.tvStatus.setTextColor(Color.parseColor("#9E9E9E"))
                holder.btnAction.setImageResource(R.drawable.ic_play_circle)
                holder.btnAction.visibility = View.VISIBLE
            }
            VideoTask.STATE_WAIT -> {
                // 等待中
                holder.tvStatus.text = "等待下载..."
                holder.tvStatus.setTextColor(Color.parseColor("#FF9800"))
                holder.btnAction.setImageResource(R.drawable.ic_pause_circle)
                holder.btnAction.visibility = View.VISIBLE
            }
            VideoTask.STATE_SUCCESS -> {
                holder.progressBar.isIndeterminate = false // 恢复正常
                // 成功：绿色文字，显示打钩或隐藏按钮
                holder.tvStatus.text = "下载完成"
                holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"))
                holder.btnAction.setImageResource(R.drawable.ic_check_circle)
                holder.progressBar.progress = 100
                // 完成后通常不需要操作按钮了，或者保留做其他用途
                holder.btnAction.visibility = View.VISIBLE
            }
            VideoTask.STATE_FAIL -> {
                // 失败：红色文字，显示重试图标
                holder.tvStatus.text = "下载失败，点击重试"
                holder.tvStatus.setTextColor(Color.parseColor("#F44336"))
                holder.btnAction.setImageResource(R.drawable.ic_refresh) // 需要一个刷新图标
                holder.btnAction.visibility = View.VISIBLE
            }
            VideoTask.STATE_MERGING -> {
                holder.tvStatus.text = "正在转码合并中，请稍候..."
                holder.tvStatus.setTextColor(Color.parseColor("#9C27B0")) // 紫色
                holder.progressBar.progress = 100
                holder.progressBar.isIndeterminate = true // 变成循环转圈的进度条
                holder.btnAction.visibility = View.INVISIBLE // 合并时不让操作
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCover: ImageView = view.findViewById(R.id.iv_cover)
        val tvTitle: TextView = view.findViewById(R.id.tv_task_title)
        val progressBar: ProgressBar = view.findViewById(R.id.pb_progress)
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
        val btnAction: ImageView = view.findViewById(R.id.btn_action)
    }
}