package com.jin.learn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FruitAdapter(private val fruits: List<Fruit>, val context: Context): RecyclerView.Adapter<FruitAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.fruit_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = fruits[position]
        holder.name.text = item.name
        holder.img.setImageResource(item.imageId)
    }

    override fun getItemCount(): Int {
        return fruits.size
    }


    class ViewHolder(itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById<TextView>(R.id.fruit_name)
        val img: ImageView = itemView.findViewById<ImageView>(R.id.fruit_img)
    }


}