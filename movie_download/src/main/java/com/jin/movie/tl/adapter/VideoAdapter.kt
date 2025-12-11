package com.jin.movie.tl.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.jin.movie.R
import com.jin.movie.tl.bean.VideoRecord

class VideoAdapter(private var list: MutableList<VideoRecord> = mutableListOf()) :
    RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {


    // ================== 【新增 1】 定义点击回调变量 ==================
    // 这是一个函数变量，接收 VideoRecord 参数，没有返回值
    private var onItemClickListener: ((VideoRecord) -> Unit)? = null

    // ================== 【新增 2】 暴露设置监听的方法 ==================
    fun setOnItemClickListener(listener: (VideoRecord) -> Unit) {
        this.onItemClickListener = listener
    }

    // 1. 下拉刷新时调用：清空旧数据，设置新数据
    fun setNewData(newList: List<VideoRecord>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    // 2. 上拉加载更多时调用：追加数据到末尾
    fun addData(moreList: List<VideoRecord>) {
        val startPos = list.size
        list.addAll(moreList)
        notifyItemRangeInserted(startPos, moreList.size)
    }

    fun updateData(newList: MutableList<VideoRecord>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tl_item_video_card, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(list[position])
        // ================== 【新增 3】 绑定点击事件 ==================
        // 当 Item 被点击时，调用回调函数，把当前数据传出去
        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(list[position])
        }
    }

    override fun getItemCount(): Int = list.size

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCover: ImageView = itemView.findViewById(R.id.iv_cover)
        private val ivAvatar: ImageView = itemView.findViewById(R.id.iv_avatar)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_title)
        private val tvNickname: TextView = itemView.findViewById(R.id.tv_nickname)
        private val tvHeat: TextView = itemView.findViewById(R.id.tv_heat)
        private val tvCoin: TextView = itemView.findViewById(R.id.tv_coin)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)

        fun bind(item: VideoRecord) {
            tvTitle.text = item.videoTitle
            tvNickname.text = item.nickName
            tvCoin.text = item.videoCoin.toString()
            tvDate.text = item.startTime

            // 格式化热度，例如 37843 -> 3.78w
            val heatText = if (item.heat > 10000) {
                String.format("%.2fw", item.heat / 10000f)
            } else {
                item.heat.toString()
            }
            tvHeat.text = heatText

            val fixUrl = item.coverImage?.replace("[", "%5B")?.replace("]", "%5D")


            Log.d("TAG", "bind: " + item.coverImage)
            // 加载封面图
            Glide.with(itemView.context)
                .load(fixUrl) // 注意字段名
                .placeholder(android.R.color.darker_gray)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // 【关键】这里会打印具体的错误原因
                        e?.logRootCauses("GlideError")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                })
                .into(ivCover)

            // 加载头像
            Glide.with(itemView.context)
                .load(item.userLogo)
                .into(ivAvatar)
        }
    }
}