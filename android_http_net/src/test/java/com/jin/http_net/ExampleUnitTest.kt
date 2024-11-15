package com.jin.http_net

import org.apache.http.client.methods.HttpGet
import org.junit.Test
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy
import java.util.Arrays


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        println(1)
    }

    @Test
    fun aaa() {
        println("-----------------------1111")
        Thread {
            println("-----------------------2222")
//            val httpClient = HttpClients.createDefault()
            println("-----------------------3333")
            val httpGet = HttpGet("www.baidu.com")
//            val httpResponse = httpClient.execute(httpGet)
//            println(httpResponse.toString())
        }.start()
    }


}