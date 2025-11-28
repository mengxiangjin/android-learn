package com.jin.movie.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.jin.movie.R
import com.jin.movie.bean.Video

class VideoAdapter(
    private val videoList: MutableList<Video>,
    private val onVideoClick: (Video) -> Unit // 点击回调
) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    // 刷新数据（清空旧的，换新的）
    fun setNewData(newData: List<Video>) {
        videoList.clear()
        videoList.addAll(newData)
        notifyDataSetChanged()
    }

    // 追加数据（上拉加载用）
    fun addData(newData: List<Video>) {
        val startPos = videoList.size
        videoList.addAll(newData)
        notifyItemRangeInserted(startPos, newData.size)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCover: ImageView = view.findViewById(R.id.iv_cover)
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvDuration: TextView = view.findViewById(R.id.tv_duration)
        val tvPlayCount: TextView = view.findViewById(R.id.tv_play_count)

        // 注意：item_video_card.xml 里如果还有 tv_price 和 tv_time_ago，
        // 你需要把它们删掉或者设为 gone，因为新的 Video 实体没这两个字段了
        val tvPrice: TextView? = view.findViewById(R.id.tv_price) //以此防止空指针
        val tvTimeAgo: TextView? = view.findViewById(R.id.tv_time_ago)

        fun bind(video: Video) {
            tvTitle.text = video.title
            tvDuration.text = video.duration
            tvDuration.isVisible = video.duration.isNotEmpty()
            tvPlayCount.text = video.playCount

            // 隐藏旧布局中可能还存在的控件
            tvPrice?.visibility = View.GONE
            tvTimeAgo?.visibility = View.GONE

            // 加载封面
            Glide.with(itemView.context)
                .load(video.coverUrl)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(16)))
                .placeholder(android.R.drawable.ic_menu_gallery) // 建议换成你的占位图
                .error(android.R.drawable.stat_notify_error)
                .into(ivCover)

            // 点击事件
            itemView.setOnClickListener {
                onVideoClick(video)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(videoList[position])
    }

    // 新增：安全获取数据的方法
    fun getItem(position: Int): Video? {
        if (position in videoList.indices) {
            return videoList[position]
        }
        return null
    }

    override fun getItemCount() = videoList.size
}