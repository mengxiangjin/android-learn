package com.jin.movie.tl.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jin.movie.R
import com.jin.movie.tl.bean.AnchorBean

class AnchorAdapter(private var list: MutableList<AnchorBean> = mutableListOf(),var isFromMine: Boolean = false) :
    RecyclerView.Adapter<AnchorAdapter.AnchorViewHolder>() {

    fun setNewData(newList: List<AnchorBean>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    // 在 AnchorAdapter 类内部添加这个方法
    fun addData(newList: List<AnchorBean>) {
        val startPos = list.size
        list.addAll(newList)
        notifyItemRangeInserted(startPos, newList.size)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnchorViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anchor, parent, false)
        return AnchorViewHolder(view,isFromMine)
    }

    override fun onBindViewHolder(holder: AnchorViewHolder, position: Int) {
        holder.bind(list[position])

        // 点击整个 Item 跳转到详情页
        holder.itemView.setOnClickListener {
            // 确保 AnchorProfileActivity 已经 import
            com.jin.movie.tl.activity.AnchorProfileActivity.start(holder.itemView.context, list[position])
        }
    }

    override fun getItemCount(): Int = list.size

    class AnchorViewHolder(itemView: View,var isFromMine: Boolean) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: ImageView = itemView.findViewById(R.id.iv_anchor_avatar)
        private val tvName: TextView = itemView.findViewById(R.id.tv_anchor_name)
        private val tvSlogan: TextView = itemView.findViewById(R.id.tv_anchor_slogan)
        private val btnFollow: TextView = itemView.findViewById(R.id.btn_follow)

        fun bind(item: AnchorBean) {
            tvName.text = item.nickName ?: "未知用户"
            tvSlogan.text = item.userSlogan ?: "这家伙很懒，什么都没留下"

            // 头像加载
            Glide.with(itemView.context)
                .load(item.userLogo)
                .placeholder(R.drawable.ic_default_avatar) // 请确保有占位图
                .error(R.drawable.ic_default_avatar)
                .into(ivAvatar)

            if (isFromMine) {
                // 关注状态处理
                if (item.followStatus == 0) {
                    btnFollow.text = "已关注"
                    btnFollow.setBackgroundResource(R.drawable.shape_btn_followed_bg) // 灰色背景
                } else {
                    btnFollow.text = "关注"
                    btnFollow.setBackgroundResource(R.drawable.shape_btn_follow_bg) // 粉色背景
                }
            } else {
                // 关注状态处理
                if (item.followStatus == 1) {
                    btnFollow.text = "已关注"
                    btnFollow.setBackgroundResource(R.drawable.shape_btn_followed_bg) // 灰色背景
                } else {
                    btnFollow.text = "关注"
                    btnFollow.setBackgroundResource(R.drawable.shape_btn_follow_bg) // 粉色背景
                }
            }
        }
    }
}