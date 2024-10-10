package com.jin.http_net.retrofit.bean

class UserResponse(val data: User,val errorCode: Int,val errorMsg: String) {






    class User(val admin: Boolean,val chapterTops: List<Int>,val coinCount: Int,val collectIds: List<Int>,val email: String,val icon: String,val id: Long,val nickname:String,val password: String,val publicName: String,val token: String,val type: Int,val username: String) {
        override fun toString(): String {
            return "User(admin=$admin, chapterTops=$chapterTops, coinCount=$coinCount, collectIds=$collectIds, email='$email', icon='$icon', id=$id, nickname='$nickname', password='$password', publicName='$publicName', token='$token', type=$type, username='$username')"
        }
    }

    override fun toString(): String {
        return "UserResponse(data=$data, errorCode=$errorCode, errorMsg='$errorMsg')"
    }
}