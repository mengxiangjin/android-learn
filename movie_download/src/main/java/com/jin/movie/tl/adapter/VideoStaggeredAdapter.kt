package com.jin.movie.tl.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.jin.movie.R
import com.jin.movie.tl.bean.EncryptedImage
import com.jin.movie.tl.bean.VideoBean

class VideoStaggeredAdapter(private val videos: MutableList<VideoBean>) :
    RecyclerView.Adapter<VideoStaggeredAdapter.VideoViewHolder>() {

    /**
     * 下拉刷新时调用：清空旧数据，设置新数据
     */
    fun setList(newVideos: List<VideoBean>) {
        videos.clear()
        videos.addAll(newVideos)
        notifyDataSetChanged()
    }

    /**
     * 上拉加载时调用：追加新数据到末尾
     */
    fun addData(newVideos: List<VideoBean>) {
        val startPos = videos.size
        videos.addAll(newVideos)
        // 使用局部刷新，避免画面闪烁，体验更好
        notifyItemRangeInserted(startPos, newVideos.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video_staggered, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int = videos.size

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvLikes: TextView = itemView.findViewById(R.id.tv_likes)

        fun bind(item: VideoBean) {
            tvTitle.text = item.videoTitle ?: ""
            tvLikes.text = item.videoPraises.toString()



            // 点击事件
            itemView.setOnClickListener {
                // TODO: 这里写点击跳转播放视频的逻辑
            }

            if (item.converImage == null) return
            // 现在的加载方式
            val imageModel = if (item.converImage.endsWith(".tlenc")) {
                EncryptedImage(item.converImage) // 触发自定义解密流程
            } else {
                item.converImage // 走普通加载
            }


            // 加载封面
            Glide.with(itemView.context)
                .load(imageModel)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.color.black)
                .into(ivCover)

        }
    }
}