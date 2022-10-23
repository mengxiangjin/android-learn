package com.jin.learn

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.jin.learn.Msg.Companion.TYPE_SENT
import com.jin.learn.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val msgList = ArrayList<Msg>()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var manager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initMsgList()
        initRecycleView()
        initListener()
    }

    private fun initListener() {
        binding.send.setOnClickListener {
            val content = binding.content.text.toString()
            if (content.isNotBlank()) {
                val msg = Msg(content,TYPE_SENT)
                msgList.add(msg)
                chatAdapter.notifyItemInserted(msgList.size - 1)
                manager.scrollToPosition(msgList.size - 1)
                binding.content.setText("")
            }
        }
    }

    private fun initRecycleView() {
        manager = LinearLayoutManager(this)
        chatAdapter = ChatAdapter(this,msgList)
        binding.chatRec.layoutManager = manager
        binding.chatRec.adapter = chatAdapter
    }

    private fun initMsgList() {
        val msg1 = Msg("Hello guy.", Msg.TYPE_RECEIVED)
        msgList.add(msg1)
        val msg2 = Msg("Hello. Who is that?", Msg.TYPE_SENT)
        msgList.add(msg2)
        val msg3 = Msg("This is Tom. Nice talking to you. ", Msg.TYPE_RECEIVED)
        msgList.add(msg3)
    }
}