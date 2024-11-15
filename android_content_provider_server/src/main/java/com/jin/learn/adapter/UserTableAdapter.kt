package com.jin.learn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jin.learn.R
import com.jin.learn.bean.User

class UserTableAdapter(val context: Context, var datas: List<User>): RecyclerView.Adapter<UserTableAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val rootView = LayoutInflater.from(context).inflate(R.layout.item_table, parent, false)
        return MyViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvId.text = datas[position].id
        holder.tvName.text = datas[position].name
    }

    fun updateDatas(datas: List<User>) {
        this.datas = datas
        notifyDataSetChanged()
    }


    class MyViewHolder(val rootView: View): ViewHolder(rootView) {
        val tvId = rootView.findViewById<TextView>(R.id.tv_id)
        val tvName = rootView.findViewById<TextView>(R.id.tv_name)
    }


}