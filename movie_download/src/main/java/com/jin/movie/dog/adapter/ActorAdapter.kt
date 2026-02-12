package com.jin.movie.dog.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // 假设用Glide
import com.jin.movie.R
import com.jin.movie.bean.Actor // 你的Bean包路径

class ActorAdapter(
    private var data: MutableList<Actor>,
    private val onItemClick: (Actor) -> Unit
) : RecyclerView.Adapter<ActorAdapter.ActorViewHolder>() {

    class ActorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: ImageView = view.findViewById(R.id.iv_avatar)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvCount: TextView = view.findViewById(R.id.tv_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dog_item_actor, parent, false)
        return ActorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActorViewHolder, position: Int) {
        val item = data[position]
        holder.tvName.text = item.name

        // 显示视频数量
        if (item.videoCount.isNotEmpty()) {
            holder.tvCount.visibility = View.VISIBLE
            holder.tvCount.text = item.videoCount
        } else {
            holder.tvCount.visibility = View.GONE
        }

        // 加载图片 (圆形)
        Glide.with(holder.itemView.context)
            .load(item.avatarUrl)
            .placeholder(R.mipmap.ic_launcher) // 占位图
            .error(R.mipmap.ic_launcher)
            .into(holder.ivAvatar)

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount(): Int = data.size

    // 数据操作方法
    fun setNewData(newData: List<Actor>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    fun addData(newData: List<Actor>) {
        val startPos = data.size
        data.addAll(newData)
        notifyItemRangeInserted(startPos, newData.size)
    }
}