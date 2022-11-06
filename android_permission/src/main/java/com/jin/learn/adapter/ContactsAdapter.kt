package com.jin.learn.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jin.learn.R

class ContactsAdapter(val context: Context,val contacts: List<String>): RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {


    class ViewHolder(val itemView: View): androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val contact = itemView.findViewById<TextView>(R.id.contact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.contacts_item_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = contacts[position]
        holder.contact.text = item
    }

    override fun getItemCount(): Int {
       return contacts.size
    }
}