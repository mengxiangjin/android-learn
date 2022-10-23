package com.jin.learn

class Msg(val content: String,val type: Int) {

    companion object {
        const val TYPE_SENT = 0
        const val TYPE_RECEIVED = 1
    }
}