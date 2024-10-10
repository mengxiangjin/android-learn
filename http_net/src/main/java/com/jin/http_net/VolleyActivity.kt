package com.jin.http_net

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.util.LruCache
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request.Method
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.jin.http_net.bean.Article
import com.jin.http_net.databinding.ActivityVolleyBinding
import kotlin.math.min
import kotlin.math.round

class VolleyActivity: AppCompatActivity() {


    private lateinit var binding: ActivityVolleyBinding

    private lateinit var requestQueue: RequestQueue
    private val testNet = "https://jsonplaceholder.typicode.com/posts"
    private val imgNet = "https://via.placeholder.com/600/f66b97"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolleyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestQueue = Volley.newRequestQueue(this)
        val mapParams = mutableMapOf<String,String>()
        mapParams["userId"] = "1"
        mapParams["title"] = "title~"
        mapParams["body"] = "body~"

//        stringRequest(Method.GET,"${testNet}?userId=2")
//        stringRequest(Method.POST,testNet,mapParams)
//
//        jsonArrayRequest(Method.GET,"${testNet}?userId=2",mapParams)
//        jsonArrayRequest(Method.POST,testNet,mapParams)
//        jsonObjectRequest(Method.GET,"${testNet}/3")
//        jsonObjectRequest(Method.POST,testNet,mapParams)

//        imgRequest(imgNet,mapParams)
//        imgLoader(imgNet)
//        imgLoaderOfCache(imgNet)
//        imgLoaderOfNetView(imgNet)


        gsonRequest(Method.GET,"${testNet}/3")
    }

    private fun stringRequest(method: Int,url: String,params: MutableMap<String,String>? = null) {
        val request = object : StringRequest(method,url,{
            Log.d("TAG", "stringRequest: success " + it) },{
            Log.d("TAG", "stringRequest:error " + it)
        }) {
            override fun getParams(): MutableMap<String, String>? {
                return params
            }
        }
        //设置超时时间5s
        request.retryPolicy =
            DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(request)
    }

    private fun jsonArrayRequest(method: Int,url: String,params: MutableMap<String, String>? = null) {
        val request = object : JsonArrayRequest(method,url,{
            Log.d("TAG", "jsonArrayRequest:success " + it)
        },{
            Log.d("TAG", "jsonArrayRequest:error " + it)
        }) {
            override fun getParams(): MutableMap<String, String>? {
                return params
            }
        }
        //设置超时时间5s
        request.retryPolicy =
            DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(request)
    }

    private fun jsonObjectRequest(method: Int,url: String,params: MutableMap<String, String>? = null) {
        val request = object : JsonObjectRequest(method,url,{
            Log.d("TAG", "jsonObjectRequest:success " + it)
        },{
            Log.d("TAG", "jsonObjectRequest:error " + it)
        }) {
            override fun getParams(): MutableMap<String, String>? {
                return params
            }
        }
        //设置超时时间5s
        request.retryPolicy =
            DefaultRetryPolicy(10000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(request)
    }


    private fun imgRequest(url: String,params: MutableMap<String, String>? = null) {
        val request = object : ImageRequest(url,{
            binding.img.setImageBitmap(it)
        },0,0,ImageView.ScaleType.CENTER_INSIDE,Bitmap.Config.ARGB_8888,{
            Log.d("TAG", "imgRequest: " + it)
        }) {
            override fun getParams(): MutableMap<String, String>? {
                return params
            }
        }
        //设置超时时间5s
        request.retryPolicy =
            DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(request)
    }


    /*
    * 默认显示getImageListener中设置的图片，等到网络请求完成替换
    * */
    private fun imgLoader(url: String) {
        val imageLoader = ImageLoader(requestQueue,object : ImageLoader.ImageCache {
            override fun getBitmap(url: String?): Bitmap? {
                return null
            }

            override fun putBitmap(url: String?, bitmap: Bitmap?) {
            }
        })
        val imageListener = ImageLoader.getImageListener(
            binding.img,
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher
        )
        imageLoader.get(url,imageListener,0,0)
    }

    private fun imgLoaderOfCache(url: String) {
        //ImageLoader + ImageCache 内存缓存
        val imageLoader = ImageLoader(requestQueue,object : ImageLoader.ImageCache {

            private val cacheMap = object : LruCache<String,Bitmap>(10 * 1024 * 1024) {

                //返回每个value所占的内存数量
                override fun sizeOf(key: String?, value: Bitmap?): Int {
                    return if (getBitmap(key) != null) {
                        getBitmap(key)!!.byteCount / 1024
                    } else {
                        0
                    }
                }
            }

            override fun getBitmap(url: String?): Bitmap? {
                return cacheMap[url]
            }

            override fun putBitmap(url: String?, bitmap: Bitmap?) {
                cacheMap.put(url,bitmap)
            }
        })
        val imageListener = ImageLoader.getImageListener(
            binding.img,
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher
        )
        imageLoader.get(url,imageListener,0,0)
    }

    private fun imgLoaderOfNetView(url: String) {
        binding.netImg.setDefaultImageResId(R.mipmap.ic_launcher)
        binding.netImg.setDefaultImageResId(R.mipmap.ic_launcher_round)
        binding.netImg.setImageUrl(url,object : ImageLoader(requestQueue,object : ImageCache {
            override fun getBitmap(url: String?): Bitmap? {
                return null
            }

            override fun putBitmap(url: String?, bitmap: Bitmap?) {
            }

        }) {

        })

    }

    /*
    * Bitmap内存溢出问题
    * 多图片同时加载可考虑LruCache
    * */
    private fun introduceBitmap() {
        val options = BitmapFactory.Options()
        //先不从加载bitmap到内存中仅仅测量出该bitmap的宽度高度等信息
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources,R.mipmap.ic_launcher,options)

        //设置inSampleSize值 使目标图像适应源图像尺寸
        options.inSampleSize = calSampleSize(options,20,20)
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher, options)
    }

    private fun calSampleSize(options: BitmapFactory.Options,reqWidth: Int,reqHeight: Int): Int {
        var sampleSize = 1
        if (reqWidth < options.outWidth || reqHeight < options.outHeight) {
            val widthRatio = round(options.outWidth / 1f / reqWidth)
            val heightRatio = round(options.outHeight / 1f / reqHeight)
            sampleSize = min(widthRatio,heightRatio).toInt()
        }
        return sampleSize
    }


    private fun gsonRequest(method: Int,url: String,params: MutableMap<String, String>? = null) {
        val gsonRequest = object : GsonRequest<Article>(Article::class.java,method,url,{
            Log.d("TAG", "gsonRequest:success " + it)
        },{
            Log.d("TAG", "gsonRequest:error " + it)
        }) {
            override fun getParams(): MutableMap<String, String>? {
                return params
            }
        }
        requestQueue.add(gsonRequest)
    }

}