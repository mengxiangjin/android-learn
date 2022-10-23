package com.jin.learn

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.jin.learn.Msg.Companion.TYPE_RECEIVED

class ChatAdapter(val context: Context,val msgList: List<Msg>): RecyclerView.Adapter<ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == TYPE_RECEIVED) {
            var view =
                LayoutInflater.from(context).inflate(R.layout.chat_left_item, parent, false)
            LeftMsgHolder(view)
        } else {
            var view =
                LayoutInflater.from(context).inflate(R.layout.chat_right_item, parent, false)
            RightMsgHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var msg = msgList[position]
        when(holder) {
            is LeftMsgHolder -> {
                holder.leftMsg.text = msg.content
            }
            is RightMsgHolder -> {
                holder.rightMsg.text = msg.content
            }
            else -> {
                throw java.lang.IllegalArgumentException()
            }
        }
    }

    override fun getItemCount(): Int {
        return msgList.size
    }

    override fun getItemViewType(position: Int): Int {
        return msgList[position].type
    }

    class LeftMsgHolder(private val itemView: View): ViewHolder(itemView) {
        val leftMsg = itemView.findViewById<TextView>(R.id.left_msg)
    }

    class RightMsgHolder(private val itemView: View): ViewHolder(itemView) {
        val rightMsg = itemView.findViewById<TextView>(R.id.right_msg)
    }


}