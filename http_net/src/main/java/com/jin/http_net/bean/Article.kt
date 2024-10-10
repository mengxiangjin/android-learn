package com.jin.http_net.bean

class Article {
    var userId: Int = 0
    var id: Int = 0
    var title: String = ""
    var body: String = ""


    override fun toString(): String {
        return "Article(userId=$userId, id=$id, title='$title', body='$body')"
    }
}

