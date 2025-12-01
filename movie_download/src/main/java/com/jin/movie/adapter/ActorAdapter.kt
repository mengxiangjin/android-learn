package com.jin.movie.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.jin.movie.R
import com.jin.movie.bean.Actor

class ActorAdapter(
    private val list: MutableList<Actor>,
    private val onClick: (Actor) -> Unit
) : RecyclerView.Adapter<ActorAdapter.ViewHolder>() {

    fun setNewData(newData: List<Actor>) {
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }

    fun addData(newData: List<Actor>) {
        val start = list.size
        list.addAll(newData)
        notifyItemRangeInserted(start, newData.size)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivAvatar: ImageView = view.findViewById(R.id.iv_avatar)
        val tvName: TextView = view.findViewById(R.id.tv_name)

        fun bind(item: Actor) {
            tvName.text = item.name

            Glide.with(itemView.context)
                .load(item.avatarUrl)
                // 这里用了圆形剪裁，如果你想要圆角方形，可以用 RoundedCorners(16)
                .apply(RequestOptions.bitmapTransform(CircleCrop()))
                .placeholder(R.drawable.ic_launcher_background) // 记得换成你的占位图
                .into(ivAvatar)

            itemView.setOnClickListener { onClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_actor, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}