package com.jin.http_net.retrofit.bean

class TopicResponse(val userId: Int = 0, val id: Int = 0, val title: String = "", val body: String = "") {

    override fun toString(): String {
        return "TopicResponse(userId=$userId, id=$id, title='$title', body='$body')"
    }
}