package com.jin.http_net.duty


class InterceptorOne : Interceptor{
    override fun intercept(chain: Interceptor.Chain): String {
        println("InterceptorOne发起请求之前")
        val result = chain.processed(chain.request)
        println("InterceptorOne发起请求之后得到的$result")
        return result
    }
}

class InterceptorTwo : Interceptor{
    override fun intercept(chain: Interceptor.Chain): String {
        println("InterceptorTwo发起请求之前")
        val result = chain.processed(chain.request)
        println("InterceptorTwo发起请求之后得到的$result")
        return result
    }
}

class InterceptorThree : Interceptor{
    override fun intercept(chain: Interceptor.Chain): String {
        println("InterceptorThree发起请求之前")
        val result = "我去拿到了请求结果"
        println("InterceptorThree发起请求之后得到的$result")
        return result
    }
}


interface Interceptor {

    fun intercept(chain: Chain): String

    interface Chain {
        val request: String
        fun processed(request: String): String
    }
}


class RealChain(val list: List<Interceptor>, val index: Int, override val request: String): Interceptor.Chain{

    override fun processed(request: String): String {
        if (index > list.size - 1) {
            return "返回结束了"
        }
        val nextRealChain = RealChain(list,index + 1,request)
        println("RealChain开始执行之前" + list[index].javaClass.name)
        val result = list[index].intercept(nextRealChain)
        println("RealChain开始执行之后$result" + list[index].javaClass.name)
        return result
    }
}

fun main() {
    val list = listOf(InterceptorOne(),InterceptorTwo(),InterceptorThree())
    val startIndex = 0

    val realChain = RealChain(list,startIndex,"Http-Start")
    val result = realChain.processed("Http-Start")
    println("Http-End$result")
}