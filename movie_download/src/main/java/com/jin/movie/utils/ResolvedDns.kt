package com.jin.movie.utils

import okhttp3.Dns
import java.net.InetAddress

// 1. 定义一个静态 DNS 映射器
class ResolvedDns : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        // 如果是报错的那个域名，直接返回你查到的正确 IP
//        if (hostname == "www.zimuquan23.uk") {
//            // 假设你查到的 IP 是 104.21.x.x (替换为真实 IP)
//            return listOf(InetAddress.getByName("103.115.64.27"))
//        }
        // 其他域名走系统默认解析
        return Dns.SYSTEM.lookup(hostname)
    }
}