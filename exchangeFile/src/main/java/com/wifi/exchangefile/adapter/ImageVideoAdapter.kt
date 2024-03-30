package com.wifi.exchangefile.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.wifi.exchangefile.R
import com.wifi.exchangefile.bean.FileTypeBean
import com.wifi.exchangefile.databinding.ListitemVideoImageBinding
import com.wifi.exchangefile.utils.GlideRoundTransform
import java.io.File

class ImageVideoAdapter(val context: Context, var files: List<String>) :
    RecyclerView.Adapter<ImageVideoAdapter.MyHolder>() {


    var onItemClickAction: ((FileTypeBean) -> Unit)? = null

    var chooseSet = mutableSetOf<String>()


    class MyHolder(val binding: ListitemVideoImageBinding) : ViewHolder(binding.root) {
        val coverView = binding.coverView
        val imgChoose = binding.imgChoose
        val imgView = binding.imageView

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflate = ListitemVideoImageBinding.inflate(LayoutInflater.from(context))
        return MyHolder(inflate)
    }

    override fun getItemCount(): Int {
        return files.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        loadRoundCorner(context,9,files[position],holder.imgView)
        holder.imgChoose.setOnClickListener {
            holder.imgChoose.isSelected = !holder.imgChoose.isSelected

            if (!chooseSet.remove(files[position])) {
                chooseSet.add(files[position])
            }
        }
    }

    fun loadRoundCorner(context: Context, corner: Int, path: String, imageView: ImageView) {
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.color.load_img_bg) //预加载图片
            .error(R.color.load_img_bg) //加载失败图片
            .priority(Priority.HIGH) //优先级、
            .diskCacheStrategy(DiskCacheStrategy.ALL) //缓存
            .dontAnimate()
            .transform(GlideRoundTransform(corner)) //圆角
        Glide.with(context).load(path).apply(options).into(imageView)
    }

    fun setNewDatas(imgPaths: List<String>) {
        this.files = imgPaths
        notifyDataSetChanged()

    }
}