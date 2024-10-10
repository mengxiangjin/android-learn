## 网络请求

### HttpClient（已被废弃）

Android中进行网络请求的工具包

#### Gradle引入

- ```
  implementation 'org.apache.httpcomponents:httpclient:4.2.5'
  ```

#### 使用

- 无论Get请求或者Post请求本质都是通过**HttpClient对象**调用execute方法返回结果

- ```
  public HttpResponse execute(HttpUriRequest request)
  ```

- HttpUriRequest接口实现类：HttpGet、HttpPost、HttpDelete、HttpPut等等对应不同的Http请求

- **创建HttpClient对象**

- ```kotlin
  /*
  * 创建执行器HttpClient
  * */
  private fun createHttpClient(): HttpClient {
      val basicHttpParams = BasicHttpParams()
  
      //设置连接超时
      HttpConnectionParams.setConnectionTimeout(basicHttpParams,1500)
   //tcp非延迟
   HttpConnectionParams.setTcpNoDelay(basicHttpParams,true)
   //设置请求超时
   HttpConnectionParams.setSoTimeout(basicHttpParams,1500)
      HttpProtocolParams.setVersion(basicHttpParams,HttpVersion.HTTP_1_1)
      //设置持续连接
      HttpProtocolParams.setUseExpectContinue(basicHttpParams,true)
      HttpProtocolParams.setContentCharset(basicHttpParams, HTTP.UTF_8)
  
      return DefaultHttpClient(basicHttpParams)
  }
  ```

#### 	Get请求

- **httpResponse.statusLine.statusCode：获取状态码**
- **httpResponse.statusLine.content：获取返回结果InputStream**

- ```kotlin
  private fun httpClientForHttpGet(url: String) {
      Thread {
          val httpClient = createHttpClient()
  
          //创建Get请求
          val httpGet = HttpGet(url)
          httpGet.addHeader("Connection","Keep-Alive")
  
          //执行器执行该请求处理返回结果
          val httpResponse = httpClient.execute(httpGet)
          val statusCode = httpResponse.statusLine.statusCode
          Log.d("HTTP-GET", "statusCode: " + statusCode)
          val inputStream = httpResponse.entity.content
  
          val bufferedReader = BufferedReader(InputStreamReader(inputStream))
  
          var content = bufferedReader.readLine()
          while (content != null) {
              Log.d("HTTP-GET", "content: " + content)
              content = bufferedReader.readLine()
          }
          inputStream.close()
          bufferedReader.close()
      }.start()
  }
  ```

- 调用方法 httpClientForHttpGet("https://www.baidu.com")：
  - ![image.png](https://s2.loli.net/2024/08/28/Wsu5FztrHlUchfK.png)

#### Post请求

- **httpResponse.statusLine.statusCode：获取状态码**

- **httpResponse.statusLine.content：获取返回结果InputStream**

- **Post请求携带参数Key-Value**

  - **httpPost.setEntity(HttpEntity)携带参数**

  - **HttpEntity接口实现类：UrlEncodedFormEntity对象即可**

  - **UrlEncodedFormEntity（List<* extend NameValuePair>）构造对象**

  - **NameValuePair接口，其实现类BasicNameValuePair（String name，String value）。内部维护key、value**

  - 携带id = 10、name = 张三

    - ```kotlin
      //POST请求携带参数NameValuePair形式
      val params = mutableListOf<NameValuePair>()
      params.add(BasicNameValuePair("id","10"))
      params.add(BasicNameValuePair("name","张三"))
      httpPost.entity = UrlEncodedFormEntity(params)
      ```

- ```kotlin
  private fun httpClientForHttpPost(url: String) {
      Thread{
          val httpClient = createHttpClient()
  
          val httpPost = HttpPost(url)
          httpPost.addHeader("Connection","Keep-Alive")
  
          //POST请求携带参数NameValuePair形式
          val params = mutableListOf<NameValuePair>()
  
          params.add(BasicNameValuePair("userId","1"))
              params.add(BasicNameValuePair("title","title~"))
              params.add(BasicNameValuePair("body","body~"))
          httpPost.entity = UrlEncodedFormEntity(params)
  
          val httpResponse = httpClient.execute(httpPost)
          val statusCode = httpResponse.statusLine.statusCode
          Log.d("HTTP-POST", "statusCode: " + statusCode)
          val inputStream = httpResponse.entity.content
  
          val bufferedReader = BufferedReader(InputStreamReader(inputStream))
  
          var content = bufferedReader.readLine()
          while (content != null) {
              Log.d("HTTP-POST", "content: " + content)
              content = bufferedReader.readLine()
          }
          inputStream.close()
          bufferedReader.close()
      }.start()
  }
  ```

- 调用方法 httpClientForHttpPost("http://jsonplaceholder.typicode.com/posts")  并携带了参数userId = 1、title = title~、body = body~
  - ![image.png](https://s2.loli.net/2024/08/28/rFQCxMkA2TNgbEI.png)

### HttpUrlConnection

java.net包下的类，无须额外引入

#### 使用

- **URL(url: String)对象.openConnection强制转换为HttpUrlConnection对象即可**
- **设置HttpUrlConnection对象的参数设置**
- **进行connect读取返回值**

#### Get请求

- **httpURLConnection.responseCode：获取返回状态码**

- **httpURLConnection.inputStream：获取返回内容InputStream**

- ```kotlin
  private fun httpUrlConnectionForHttpGet(url: String) {
      Thread {
          val url = URL(url)
          val httpURLConnection = url.openConnection() as HttpURLConnection
          httpURLConnection.requestMethod = "GET"
          httpURLConnection.readTimeout = 1500
          httpURLConnection.connectTimeout = 150
  
          //是否向httpURLConnection输出
          httpURLConnection.doOutput = true
          //是否从httpURLConnection输入
          httpURLConnection.doInput = true
          //是否使用缓存
          httpURLConnection.useCaches = false
          //是否自动重定向
          httpURLConnection.instanceFollowRedirects = true
  
          httpURLConnection.connect()
  
          val responseCode = httpURLConnection.responseCode
          Log.d("HTTPURL-GET", "onCreate:responseCode " + responseCode)
          if (responseCode == 200) {
              //成功
              val bufferedReader = BufferedReader(InputStreamReader(httpURLConnection.inputStream))
              var content = bufferedReader.readLine()
              while (content != null) {
                  Log.d("HTTPURL-GET", "onCreate: " + content)
                  content = bufferedReader.readLine()
              }
              bufferedReader.close()
          }
          httpURLConnection.disconnect()
      }.start()
  }
  ```

- 调用方法 httpUrlConnectionForHttpGet("https://www.baidu.com")
- ![image.png](https://s2.loli.net/2024/08/28/8IsY1QAjLGqPp6T.png)

#### Post请求

- **httpURLConnection.responseCode：获取返回状态码**

- **httpURLConnection.inputStream：获取返回内容InputStream**

- **Post请求携带参数**

  - **httpURLConnection.outputStream 写入参数**

  - **传递普通参数：字符串拼接**

    - **注意：首个参数无&拼接**、**无需设置Content-Type默认即可application/x-www-form-urlencoded** 

    - **userId="1"&title="title~"&body="body~"**

    - ```kotlin
      val param = URLEncoder.encode("userId","UTF-8") + "=" + URLEncoder.encode("1","UTF-8") +
              "&" + URLEncoder.encode("title","UTF-8") + "=" + URLEncoder.encode("title~","UTF-8") +
                  URLEncoder.encode("body","UTF-8") + "=" + URLEncoder.encode("body~","UTF-8")
      httpURLConnection.outputStream.write(param.toByteArray())
      httpURLConnection.outputStream.flush()
      ```

  - 传递Json参数

    - **注意：需要先设置Content-Type**

      - ```kotlin
        httpURLConnection.setRequestProperty("Content-Type","application/json")
        ```

    - ```kotlin
      val jsonStr = "{\"userId\" : \"1\",\"title\" : \"title~\",\"body\" : \"body~\"}"
      httpURLConnection.outputStream.write(jsonStr.toByteArray())
      httpURLConnection.outputStream.flush()
      httpURLConnection.outputStream.close()
      ```

- ```kotlin
  private fun httpUrlConnectionForHttpPost(url: String,params: Map<String,String>) {
          Thread{
              val url = URL(url)
              val httpURLConnection = url.openConnection() as HttpURLConnection
              // httpURLConnection.setRequestProperty("Connection","Keep-Alive")
              httpURLConnection.requestMethod = "POST"
              httpURLConnection.connectTimeout = 1500
              httpURLConnection.readTimeout = 1500
              httpURLConnection.doOutput = true
              httpURLConnection.doInput = true
  
              httpURLConnection.setRequestProperty("Content-Type","application/json")
              /*
              * 携带参数 body中
              * */
              val bufferedWriter = BufferedWriter(OutputStreamWriter(httpURLConnection.outputStream))
              val content = StringBuilder()
  
  //            val jsonStr = "{\"userId\" : \"1\",\"title\" : \"title~\",\"body\" : \"body~\"}"
  //            httpURLConnection.outputStream.write(jsonStr.toByteArray())
  //            httpURLConnection.outputStream.flush()
  //            httpURLConnection.outputStream.close()
  
  
              params.keys.forEach {
                  if (content.isNotEmpty()) {
                      content.append("&")
                  }
                  content.append(URLEncoder.encode(it,"UTF-8"))
                  content.append("=")
                  content.append(URLEncoder.encode(params[it],"UTF-8"))
              }
              bufferedWriter.write(content.toString())
              bufferedWriter.flush()
              bufferedWriter.close()
  
              httpURLConnection.connect()
              val responseCode = httpURLConnection.responseCode
              Log.d("HTTPURL-POST", "onCreate:responseCode " + responseCode)
              if (responseCode == 200 || responseCode == 201) {
                  //成功
                  val bufferedReader = BufferedReader(InputStreamReader(httpURLConnection.inputStream))
                  var content = bufferedReader.readLine()
                  while (content != null) {
                      Log.d("HTTPURL-POST", "onCreate: " + content)
                      content = bufferedReader.readLine()
                  }
                  bufferedReader.close()
              }
              httpURLConnection.disconnect()
  
  
          }.start()
      }
  ```

- 调用：httpUrlConnectionForHttpPost("http://jsonplaceholder.typicode.com/posts", mapOf(Pair("userId", "1"),
              Pair("title","title~"), Pair("body","body~")))
- ![image.png](https://s2.loli.net/2024/08/28/PMpwKt5BQSEqzAH.png)

### Volley

Android中网络底层封装框架

#### Gradle引入

```kotlin
implementation 'com.mcxiaoke.volley:library:1.0.19'
```

#### 使用

- 创建**RequestQueue**对象requestQueue
  - **Volley.newRequestQueue(Context) : RequestQueue**
  - **Volley.newRequestQueue(Context,HttpStack) : RequestQueue**
- 创建**Request**对象request
  - Request抽象类
  - 根据其实现类，返回的结果解析成对应实现类的泛型
    - StringRequest ->  String
    - JsonArrayRequest -> JsonArray
    - JsonObjectRequest -> JsonObject
    - ImageRequest -> Bitmap
    - 自定义Request<T> -> T
- **requestQueue.add(request)即可**

#### StringRequest

##### Get请求

- **StringRequest(int method,String url,Listener<String> listener,ErrorListener errorListener)**

- ```kotlin
  val request = StringRequest(method,url,{
      Log.d("TAG", "stringRequest: success " + it) },{
      Log.d("TAG", "stringRequest:error " + it)
  })
  requestQueue.add(request)
  ```

- ```kotlin
  stringRequest(Method.GET,"https://jsonplaceholder.typicode.com/posts?userId=2")
  ```

- ![image.png](https://s2.loli.net/2024/08/28/xzRAOcigYQ1KJvB.png)

- 返回结果来看实际是个JsonArray，应该用JsonArrayRequest，但是StringRequest也可以去接。

##### Post请求

- **继承StringRequest类重写其中的getParams方法返回需要传递的参数即可**

- ```kotlin
  private fun stringRequest(method: Int,url: String,params: MutableMap<String,String>? = null) {
      val request = object : StringRequest(method,url,{
          Log.d("TAG", "stringRequest: success " + it) },{
          Log.d("TAG", "stringRequest:error " + it)
      }) {
          override fun getParams(): MutableMap<String, String>? {
              return params
          }
      }
      requestQueue.add(request)
  }
  ```

- ```kotlin
  val mapParams = mutableMapOf<String,String>()
  mapParams["userId"] = "1"
  mapParams["title"] = "title~"
  mapParams["body"] = "body~"
  stringRequest(Method.POST,testNet,mapParams)
  ```

- ![image.png](https://s2.loli.net/2024/08/28/habIirSMXG4YNFl.png)

#### JsonArrayRequest

##### Get请求

- JsonArrayRequest（int method,String url,Listener<JsonArray> listener, ErrorListener errorListener）

- ```kotlin
  val request = JsonArrayRequest(method,url,{
      Log.d("TAG", "jsonArrayRequest:success " + it)
  },{
      Log.d("TAG", "jsonArrayRequest:error " + it)
  })
  requestQueue.add(request)
  ```

- ```kotlin
  jsonArrayRequest（Method.GET,"https://jsonplaceholder.typicode.com/posts?userId=2"）
  ```

- ![image.png](https://s2.loli.net/2024/08/28/1kvFJZtMeIXuW6g.png)

##### Post请求

- 继承JsonArrayRequest类重写getParams方法返回需要传递的参数即可

- ```kotlin
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
  ```

- ```kotlin
  val mapParams = mutableMapOf<String,String>()
  mapParams["userId"] = "1"
  mapParams["title"] = "title~"
  mapParams["body"] = "body~"
  jsonArrayRequest(Method.POST,"https://jsonplaceholder.typicode.com/posts",mapParams)
  ```

- ![image.png](https://s2.loli.net/2024/08/28/UvzZoG13aHchrOk.png)

- 类型转换错误：返回结果为JsonObject，却用例JsonArray去接导致，需要用JsonObjectRequest

#### JsonObjectRequest

- JsonObjectRequest（int method,String url,Listener<JSONObject> listener, ErrorListener errorListener）

##### Get请求

- ```kotlin
  val request = JsonObjectRequest(method,url,{
      Log.d("TAG", "jsonObjectRequest:success " + it)
  },{
      Log.d("TAG", "jsonObjectRequest:error " + it)
  })
  //设置超时时间5s
  request.retryPolicy =
      DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
  requestQueue.add(request)
  ```

- ```kotlin
  jsonObjectRequest(Method.GET,"https://jsonplaceholder.typicode.com/posts/3")
  ```

- ![image.png](https://s2.loli.net/2024/08/28/shb2rFzZntjwAIu.png)

##### Post请求

- 继承JsonObjectRequest类重新getParams方法返回需要携带参数即可

- ```kotlin
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
  ```

- ```kotlin
  val mapParams = mutableMapOf<String,String>()
  mapParams["userId"] = "1"
  mapParams["title"] = "title~"
  mapParams["body"] = "body~"
  jsonObjectRequest(Method.POST,"https://jsonplaceholder.typicode.com/posts",mapParams)
  ```

- ![image.png](https://s2.loli.net/2024/08/28/RNghOIJfCTl4Sq3.png)

#### ImageRequest

- ImageRequest（String url，Response.Listener<Bitmap> listener，int maxWidth，int maxHeight，ScaleType scaleType，Config decodeConfig, Response.ErrorListener errorListener）
  - url：图片的URL
  - listener：响应成功的回调
  - maxWidth：设置图片的最大宽度（0代表无限制）
  - maxHeight：设置图片的最大高度（0代表无限制）
  - scaleType：图片的缩放形式，同ImageView中的scaleType属性相似
  - decodeConfig：图片的像素编码方式
  - errorListener：响应错误的回调

##### Get请求

- **将请求到的bitmap设置到ImageView中**

- ```kotlin
  val request = ImageRequest(url,{
      binding.img.setImageBitmap(it)
  },0,0,ImageView.ScaleType.CENTER_INSIDE,Bitmap.Config.ARGB_8888,{
      Log.d("TAG", "imgRequest: " + it)
  })
  //设置超时时间5s
  request.retryPolicy =
      DefaultRetryPolicy(5000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
  requestQueue.add(request)
  ```

- ```kotlin
  imgRequest("https://via.placeholder.com/600/f66b97")
  ```

##### Post请求

- ```kotlin
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
  ```

- ```kotlin
  val mapParams = mutableMapOf<String,String>()
  mapParams["userId"] = "1"
  mapParams["title"] = "title~"
  mapParams["body"] = "body~"
  imgRequest(imgNet,mapParams)
  ```

#### ImageLoader

- 网络请求图片得到结果之前显示默认图，拿到结果后进行替换
- 创建对象：ImageLoader（RequestQueue，ImageCache）
  - RequestQueue：Volley创建的请求队列对象
  - ImageCache：图片缓存类相关
- 获取ImageListener对象
  - ImageLoader.getImageListener（ImageView，defaultResId，errResId）：ImageListener
    - ImageView：绑定的视图
    - defaultResId：视图的默认资源（网络请求未完成之前显示）
    - errResId：网络请求错误视图资源
- 调用方法
  - imageLoader.get(String url,ImageListener,int maxWidth,int maxHeight)
    - url：图片资源的请求地址
    - imageListener：绑定的listener
    - maxWidth：期望图片最大宽度
    - maxHeight：期望图片最大高度

- ```kotlin
  imgLoader("https://via.placeholder.com/600/f66b97")
  ```

- ```kotlin
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
  ```

##### ImageLoader+ImageCache

- ImageLoader的构造函数需要传递ImageCache对象，可自定义ImageCache对象实现图片缓存的效果

- 内存缓存：LruCache

- 磁盘缓存：DiskLruCache

- ImageLoader从网络中请求图片时，会首先自动去缓存中查找bitmap

- ```kotlin
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
  ```

#### NetworkImageView

- com.android.volley.toolbox.NetworkImageView继承与ImageView

- ```xml
  <com.android.volley.toolbox.NetworkImageView
      android:id="@+id/net_img"
      android:layout_width="200dp"
      android:layout_height="200dp"
      app:layout_constraintBottom_toBottomOf="parent"
      app:layout_constraintStart_toStartOf="parent"
      app:layout_constraintEnd_toEndOf="parent"/>
  ```

- 设置其对应属性资源即可

- ```kotlin
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
  ```

#### 自定义Request返回T

- 自定义类GsonRequest继承Request

  - ```java
    class GsonRequest<T> extends Request<T>
    ```

- 重写构造函数

  - ```java
    	private Response.Listener<T> mListener;
        private Gson gson;
        private Class mClass;
    
    public GsonRequest(Class aClass,int method, String url, Response.Listener<T> listener,
                       Response.ErrorListener errorListener) {
        //重点
        super(method, url, errorListener);
        gson = new Gson();
        mListener = listener;
        mClass = aClass;
    }
    
    public GsonRequest(Class aClass,String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(aClass,Method.GET, url, listener, errorListener);
    }
    ```

- 重写deliverResponse

  - ```java
    @Override
    protected void deliverResponse(T response) {
        if (mListener != null) {
            mListener.onResponse(response);
        }
    }
    ```

- 解析返回结果（Gson解析返回出去）

  - ```java
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        T obj = (T) gson.fromJson(parsed, mClass);
        return Response.success(obj, HttpHeaderParser.parseCacheHeaders(response));
    }
    ```

- Bean类：Article

  - ```kotlin
    class Article {
        var userId: Int = 0
        var id: Int = 0
        var title: String = ""
        var body: String = ""
    
    
        override fun toString(): String {
            return "Article(userId=$userId, id=$id, title='$title', body='$body')"
        }
    }
    ```

- 发出请求

  - ```kotlin
    gsonRequest(Method.GET,"https://jsonplaceholder.typicode.com/posts/3")
    ```

  - ```kotlin
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
    ```

- 自定义的GsonRequest

  - ```java
    public class GsonRequest<T> extends Request<T> {
    
        private Response.Listener<T> mListener;
        private Gson gson;
        private Class mClass;
    
        public GsonRequest(Class aClass,int method, String url, Response.Listener<T> listener,
                           Response.ErrorListener errorListener) {
            super(method, url, errorListener);
            gson = new Gson();
            mListener = listener;
            mClass = aClass;
        }
    
        public GsonRequest(Class aClass,String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
            this(aClass,Method.GET, url, listener, errorListener);
        }
    
        @Override
        protected void onFinish() {
            super.onFinish();
            if (mListener != null) {
                mListener = null;
            }
        }
    
        @Override
        protected Response<T> parseNetworkResponse(NetworkResponse response) {
            String parsed;
            try {
                parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            } catch (UnsupportedEncodingException e) {
                parsed = new String(response.data);
            }
            T obj = (T) gson.fromJson(parsed, mClass);
            return Response.success(obj, HttpHeaderParser.parseCacheHeaders(response));
        }
    
        @Override
        protected void deliverResponse(T response) {
            if (mListener != null) {
                mListener.onResponse(response);
            }
        }
    }
    ```

### OkHttp

#### Gradle引入

```groovy
//OkHttp
implementation 'com.squareup.okhttp3:okhttp:4.9.1'
implementation 'com.squareup.okhttp3:logging-interceptor:4.9.1'
implementation 'com.squareup.okio:okio:1.17.4'
```

#### 使用

- **创建OkHttpClient对象**

  - **空构造函数**

    - ```kotlin
      val okHttpClient = OkHttpClient()
      ```

  - **OkHttpClient下的Builder内部类去build**

    - **同时设置连接超时、读写超时**

    - ```kotlin
      val httpClient = OkHttpClient.Builder()
          .connectTimeout(2000,TimeUnit.MILLISECONDS)
          .readTimeout(2000,TimeUnit.MILLISECONDS)
          .writeTimeout(2000,TimeUnit.MILLISECONDS)
          .build()
      ```

- **创建Request对象**

  - Request下的Builder内部类去build

    - url：请求的地址

    - method（String，RequestBody）：请求方式、请求体

    - header（String，String）：请求头（key-value）

    - ```kotlin
      val request = Request.Builder()     			  .url("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png")
                  .method("GET",null)
      //            .header("Content-type","application/octet-stream")
                  .build()
      ```

- **okhttpClient.newCall（Request request）： Call**

  - 返回值是RealCall，Call接口的实现类

  - ```kotlin
    val newCall = okHttpClient.newCall(request)
    ```

- **Call对象的execute方法与newCall方法获取返回结果**

  - 同步：返回值Response对象

    - ```kotlin
      //同步
      val response = newCall.execute()
      ```

  - 异步

    - newCall.enqueue（Callback）

    - ```kotlin
      newCall.enqueue(object : Callback {
          override fun onFailure(call: Call, e: IOException) {
              Log.d("TAG", "onFailure: $e")
          }
          override fun onResponse(call: Call, response: Response) {
              Log.d("TAG", "onResponse: " + response.body?.string())
          }
      })
      ```

#### Get请求

- ```kotlin
  private fun asyncOfGet() {
      val request = Request.Builder()
          .url("https://www.baidu.com")
          .method("GET",null)
          .build()
      
      val okhttpClient = OkHttpClient.Builder()
          .connectTimeout(2000,TimeUnit.MILLISECONDS)
          .readTimeout(2000,TimeUnit.MILLISECONDS)
          .writeTimeout(2000,TimeUnit.MILLISECONDS)
          .build()
      
      val newCall = okhttpClient.newCall(request)
      newCall.enqueue(object : Callback {
          override fun onFailure(call: Call, e: IOException) {
              Log.d("TAG", "onFailure: $e")
          }
  
          override fun onResponse(call: Call, response: Response) {
              Log.d("TAG", "onResponse: " + response.body?.string())
          }
      })
  }
  ```

- ![image.png](https://s2.loli.net/2024/09/10/2EcWSZCo5h6wuRy.png)

#### Post请求

- **Post请求中请求头中的 Content-Type**

  - **application/x-www-form-urlencoded（默认）：**

    - **参数urlencoded编码和序列化，类似与Get请求key-value字符串拼接**

  - **multipart/form-data：**

    - **表单上传文件**
    - **请求体被分割成多部分，每部分以--boundary分割![image.png](https://s2.loli.net/2024/09/10/6pVZNjoGv9fw5Dd.png)**

    - **请求体参数以随机生成的boundary分割**

  - **application/json：**

    - **参数以json进行传递**

- **表单提交（不包含文件上传）**

  - **FormBody类添加参数（FormBody.Builder类构造）**

  - **request中携带请求体**

  - **Content-Type = multipart/form-data**

  - ```kotlin
    private fun asyncOfPost() {
            val requestBody = FormBody.Builder()
                .add("userId","1")
                .add("title","title~")
                .add("body","body~")
                .build()
    
    //        val jsonStr = "{\"userId\" : \"1\",\"title\" : \"title~\",\"body\" : \"body~\"}"
    //        val requestBody = jsonStr.toRequestBody("application/json;charset=utf-8".toMediaTypeOrNull())
    
    
            val request = Request.Builder().url("https://jsonplaceholder.typicode.com/posts")
        //请求头携带Content-Type
                .header("Content-Type","multipart/form-data")
                .method("POST",requestBody)
                .build()
    
            val okhttpClient = OkHttpClient.Builder()
               .connectTimeout(2000,TimeUnit.MILLISECONDS)
                .readTimeout(2000,TimeUnit.MILLISECONDS)
                .writeTimeout(2000,TimeUnit.MILLISECONDS)
                .build()
    
            val newCall = okhttpClient.newCall(request)
            newCall.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("TAG", "onFailure: $e")
                }
                override fun onResponse(call: Call, response: Response) {
                    Log.d("TAG", "onResponse: " + response.body?.string())
                }
            })
        }
    ```

  - ![image.png](https://s2.loli.net/2024/09/10/leswM4mp3QNnUJj.png)

- **表单提交（包含文件上传）**

  - MultipartBody类
  - **Content-Type = multipart/form-data**

- **Json请求**

  - toRequestBody方法转化携带该参数即可

  - ```kotlin
    val jsonStr = "{\"userId\" : \"1\",\"title\" : \"title~\",\"body\" : \"body~\"}"
    val requestBody = jsonStr.toRequestBody("application/json;charset=utf-8".toMediaTypeOrNull())
    ```

#### Get请求下载文件到本地

- 发送请求

  - ```kotlin
    private fun asyncOfDownloadGet() {
        val request = Request.Builder()
        .url("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png")
            .method("GET",null)
            .header("Content-type","application/octet-stream")
            .build()
    
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2000,TimeUnit.MILLISECONDS)
            .readTimeout(2000,TimeUnit.MILLISECONDS)
            .writeTimeout(2000,TimeUnit.MILLISECONDS)
            .build()
    
        val newCall = okHttpClient.newCall(request)
        newCall.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("TAG", "onFailure: $e")
            }
    
            override fun onResponse(call: Call, response: Response) {
                writeToSDCard(response)
            }
        })
    }
    ```

- 写入本地

  - ```kotlin
    private fun writeToSDCard(response: Response) {
        val inputStream = response.body!!.byteStream()
        val dir = this@OkHttpActivity.getExternalFilesDir("img")?:return
        val file = File(dir,"a.png")
    
        val fileSize = response.body!!.contentLength()
    
        val fileOutputStream = FileOutputStream(file)
        val fileReader = ByteArray(1024)
        var read = inputStream.read(fileReader)
    
    
        var sum = 0L
        while (read != -1) {
            fileOutputStream.write(fileReader,0,read)
            sum += read.toLong()
            val progress = sum * 1.0 / fileSize * 100
            Log.d("TAG", "writeToSDCard: $progress")
            read = inputStream.read(fileReader)
        }
        fileOutputStream.flush()
        inputStream.close()
        fileOutputStream.close()
    }
    ```

#### 源码（异步请求为例）

- **okhttpClient.newCall(request)：Call**

  - **实际返回的是Call的实现类RealCall**
  - **newCall ==》 RealCall对象（OkhttpClient，Request，Boolean）**
    - **OkhttpClient：调用newCall的okhttpClient对象**
    - **Request：调用newCall方法传递的Request**
    - **Boolean：是否是WebSocket链接（默认false）**
  - **newCall instance RealCall**
    - **val client：OkhttpClient**
    - **val originalRequest：Request**
    - **val forWebSocket：Boolean**

- ```kotlin
  val newCall = okhttpClient.newCall(request)
  newCall.enqueue(object : Callback {
      override fun onFailure(call: Call, e: IOException) {
          Log.d("TAG", "onFailure: $e")
      }
  
      override fun onResponse(call: Call, response: Response) {
          Log.d("TAG", "onResponse: " + response.body?.string())
      }
  })
  ```

- newCall.enqueue（Callback）

  - RealCall对象的enqueue方法

  - executed：AtomicBoolean（CAS并发检测值）

  - ```kotlin
    override fun enqueue(responseCallback: Callback) {
        //检测该请求是否已经被执行过
      check(executed.compareAndSet(false, true)) { "Already Executed" }
    
      callStart()
     //传递的OkhttpClient client.dispatcher.enqueue(AsyncCall(responseCallback))
    }
    ```

  - AsyncCall（Callback）

    - RealCall内部类（实现了Runnable方法）、实际后期线程池执行请求的时候就是进入了该类的run方法

  - client.dispatch：Dispatcher

    - 利用OkHttpClient.Build构造OkhttpClient对象时，该Build内部存在

      - ```kotlin
        internal var dispatcher: Dispatcher = Dispatcher()
        ```

    - client.dispatch实际

      - ```kotlin
        val dispatcher: Dispatcher = builder.dispatcher
        ```

- client.dispatcher.enqueue(AsyncCall(responseCallback))：

  - Dispatch类下的enqueue方法

  - Dispatch类下成员变量

    - **readyAsyncCalls = ArrayDeque<AsyncCall>()：准备执行的异步队列**
    - **runningAsyncCalls = ArrayDeque<AsyncCall>()：正在执行的异步队列**
    - **runningSyncCalls = ArrayDeque<RealCall>()：正在执行的同步队列**

  - ```kotlin
    internal fun enqueue(call: AsyncCall) {
      synchronized(this) {
          //异步任务加入到准备队列中
        readyAsyncCalls.add(call)
    
        // Mutate the AsyncCall so that it shares the AtomicInteger of an existing running call to
        // the same host.
          //注释1开始
        if (!call.call.forWebSocket) {
          val existingCall = findExistingCallWithHost(call.host)
          if (existingCall != null) call.reuseCallsPerHostFrom(existingCall)
        }
          //注释1结束
      }
      promoteAndExecute()
    }
    ```

  - 注释1：

    - 从准备执行的异步队列与正在执行的异步队列循环查找当前是否存在同当前AsyncCall相同host的异步任务，若存在，将当前的异步任务中callsPerHost赋值为存在的callsPerHost
    - callsPerHost：当前主机Host的服务请求数

  -   promoteAndExecute()方法

    - ```kotlin
      private fun promoteAndExecute(): Boolean {
        this.assertThreadDoesntHoldLock()
      
        val executableCalls = mutableListOf<AsyncCall>()
        val isRunning: Boolean
        synchronized(this) {
          val i = readyAsyncCalls.iterator()
          while (i.hasNext()) {
            val asyncCall = i.next()
      
              //注释1开始
            if (runningAsyncCalls.size >= this.maxRequests) break // Max capacity.
            if (asyncCall.callsPerHost.get() >= this.maxRequestsPerHost) continue // Host max capacity.
              //注释1结束
      
            i.remove()
              //注释2开始
            asyncCall.callsPerHost.incrementAndGet()
              //注释2结束
              //添加到队列
            executableCalls.add(asyncCall)
            runningAsyncCalls.add(asyncCall)
          }
          isRunning = runningCallsCount() > 0
        }
      	//注释3开始
        for (i in 0 until executableCalls.size) {
          val asyncCall = executableCalls[i]
          asyncCall.executeOn(executorService)
        }
      	//注释3结束
        return isRunning
      }
      ```

    - 注释1：

      - 判断当前正在运行的异步任务队列是否大于最大并发请求数maxRequests（Dispatch的成员变量）
      - 判断此异步任务所属主机请求数是否大于最大单个主机请求数

    - 注释2：

      - 将该异步任务所属主机请求数+1

    - 注释3：

      - 遍历可执行任务队列，调用executeOn（executorService）
      - executorService：线程池类

  -  asyncCall.executeOn(executorService)饭饭

    - ```kotlin
      fun executeOn(executorService: ExecutorService) {
        client.dispatcher.assertThreadDoesntHoldLock()
      
        var success = false
        try {
            //注释1开始
          executorService.execute(this)
            //注释1结束
          success = true
        } catch (e: RejectedExecutionException) {
          val ioException = InterruptedIOException("executor rejected")
          ioException.initCause(e)
          noMoreExchanges(ioException)
          responseCallback.onFailure(this@RealCall, ioException)
        } finally {
          if (!success) {
            client.dispatcher.finished(this) // This call is no longer running!
          }
        }
      }
      ```

    - 注释1：线程池执行任务 this即AsynCall对象，该类实现了Runnable接口，触发其run方法

  - AsynCall中的run方法

    - ```kotlin
      override fun run() {
          threadName("OkHttp ${redactedUrl()}") {
            var signalledCallback = false
            timeout.enter()
            try {
                //注释1开始
              val response = getResponseWithInterceptorChain()
                //注释1结束
              signalledCallback = true
              responseCallback.onResponse(this@RealCall, response)
            } catch (e: IOException) {
              if (signalledCallback) {
                // Do not signal the callback twice!
                Platform.get().log("Callback failure for ${toLoggableString()}", Platform.INFO, e)
              } else {
                responseCallback.onFailure(this@RealCall, e)
              }
            } catch (t: Throwable) {
              cancel()
              if (!signalledCallback) {
                val canceledException = IOException("canceled due to $t")
                canceledException.addSuppressed(t)
                responseCallback.onFailure(this@RealCall, canceledException)
              }
              throw t
            } finally {
                //注释2开始
              client.dispatcher.finished(this)
                //注释2结束
            }
          }
        }
      }
      ```

    - 注释1：**getResponseWithInterceptorChain（）方法获取响应结果，通过responseCallback回调出去**

    - 注释2：回调请求结束

- getResponseWithInterceptorChain（）： Response（责任链模式）

- 责任链模式

  - 拦截链：InterceptorOne、InterceptorTwo、InterceptorThree

  - 拦截链每一个（除了最后一个）都会创建RealChain对象（index + 1），将创建完成的RealChain对象带入到下一个拦截器中

  - ```kotlin
    interface Interceptor {
    
        fun intercept(chain: Chain): String
    
        interface Chain {
            val request: String
            fun processed(request: String): String
        }
    }
    ```

  - ```kotlin
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
    ```

  - ```kotlin
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
    ```

  - ```kotlin
    fun main() {
        val list = listOf(InterceptorOne(),InterceptorTwo(),InterceptorThree())
        val startIndex = 0
    
        val realChain = RealChain(list,startIndex,"Http-Start")
        val result = realChain.processed("Http-Start")
        println("Http-End$result")
    }
    ```

  - ![image.png](https://s2.loli.net/2024/09/11/jn9vTfkA15LxmWg.png)

- **Okhttp中的拦截器链**

  - **RealCall中的getResponseWithInterceptorChain方法返回Response**

  - **OKHttpClient自带的拦截器（用户自定义）**

  - **RetryAndFollowUpInterceptor：失败重试与重定向拦截器**

  - **BridgeInterceptor：桥接拦截器**

  - **CacheInterceptor：缓存拦截器**

  - **ConnectInterceptor：连接拦截器**

  - **OKHttpClient自带的网络拦截器（如果是http协议的话）（用户自定义）**

  - **CallServerInterceptor：真正发出请求的拦截器（最后一个）**

  - **RealInterceptorChain：拦截链条对象**（**流程**）

    - **创建首个拦截链条对象，将请求传入proceed方法中（Request）**
    - **proceed方法中对Request进行通过一层一层的拦截器，然后一层一层的将其返回出来**
    - **interceptors：拦截器数组**
    - **index：当前拦截器在数组的下标（每个RealInterceptorChain对象维护各自的index下标）**
    - **创建第一个index=0的RealInterceptorChain对象，调用其proceed方法（Request）**
      - **proceed方法中创建index=1的RealInterceptorChain对象：nextRealInterceptorChain**
      - **proceed方法中获取当前需要执行的拦截器对象interceptors[index]（此时index = 0）：InterceptOne**
      - **调用获得的拦截器对象InterceptOne，调用其拦截器方法intercept（nextRealInterceptorChain）将下一个RealInterceptorChain对象传递**
      - **intercept方法中，执行自己的拦截器对应职责逻辑，后通过传递过来的nextRealInterceptorChain，再次调用proceed方法，形成层层传递**
      - **注意：最后一个拦截器链不会调用proceed方法，应正常返回结果，否则造成死循环**
  
  - ```kotlin
    internal fun getResponseWithInterceptorChain(): Response {
      // Build a full stack of interceptors.
      val interceptors = mutableListOf<Interceptor>()
      interceptors += client.interceptors
      interceptors += RetryAndFollowUpInterceptor(client)
      interceptors += BridgeInterceptor(client.cookieJar)
      interceptors += CacheInterceptor(client.cache)
      interceptors += ConnectInterceptor
      if (!forWebSocket) {
        interceptors += client.networkInterceptors
      }
      interceptors += CallServerInterceptor(forWebSocket)
    
      val chain = RealInterceptorChain(
          call = this,
          interceptors = interceptors,
          index = 0,
          exchange = null,
          request = originalRequest,
          connectTimeoutMillis = client.connectTimeoutMillis,
          readTimeoutMillis = client.readTimeoutMillis,
          writeTimeoutMillis = client.writeTimeoutMillis
      )
    
      var calledNoMoreExchanges = false
      try {
        val response = chain.proceed(originalRequest)
        if (isCanceled()) {
          response.closeQuietly()
          throw IOException("Canceled")
        }
        return response
      } catch (e: IOException) {
        calledNoMoreExchanges = true
        throw noMoreExchanges(e) as Throwable
      } finally {
        if (!calledNoMoreExchanges) {
          noMoreExchanges(null)
        }
      }
    }
    ```
  
  - RealInterceptorChain中的proceed方法
  
  - ```kotlin
    override fun proceed(request: Request): Response {
      check(index < interceptors.size)
    
      calls++
    
      if (exchange != null) {
        check(exchange.finder.sameHostAndPort(request.url)) {
          "network interceptor ${interceptors[index - 1]} must retain the same host and port"
        }
        check(calls == 1) {
          "network interceptor ${interceptors[index - 1]} must call proceed() exactly once"
        }
      }
    
      // Call the next interceptor in the chain.
      val next = copy(index = index + 1, request = request)
      val interceptor = interceptors[index]
    
      //将下一个拦截器传递过去
      val response = interceptor.intercept(next) ?: throw NullPointerException(
          "interceptor $interceptor returned null")
    
      if (exchange != null) {
        check(index + 1 >= interceptors.size || next.calls == 1) {
          "network interceptor $interceptor must call proceed() exactly once"
        }
      }
    
      check(response.body != null) { "interceptor $interceptor returned a response with no body" }
    
      return response
    }
    ```

##### RetryAndFollowUpInterceptor

- **失败重试与重定向拦截器**

- ```kotlin
  override fun intercept(chain: Interceptor.Chain): Response {
    val realChain = chain as RealInterceptorChain
    var request = chain.request
    val call = realChain.call
      
      //重定向次数
    var followUpCount = 0
      //重试后上一次获取到response
    var priorResponse: Response? = null
    var newExchangeFinder = true
      //重试错误信息记录
    var recoveredFailures = listOf<IOException>()
      
      //while循环重试+重定向
    while (true) {
      call.enterNetworkInterceptorExchange(request, newExchangeFinder)
  
      var response: Response
      var closeActiveExchange = true
      try {
          //判断请求是否已经被取消
        if (call.isCanceled()) {
          throw IOException("Canceled")
        }
  
        try {
            //执行拦截器请求
          response = realChain.proceed(request)
          newExchangeFinder = true
        } catch (e: RouteException) {
          // The attempt to connect via a route failed. The request will not have been sent.
            //是否路由异常，recover函数判断是否失败应该重试
          if (!recover(e.lastConnectException, call, request, requestSendStarted = false)) {
            throw e.firstConnectException.withSuppressed(recoveredFailures)
          } else {
            recoveredFailures += e.firstConnectException
          }
          newExchangeFinder = false
          continue
        } catch (e: IOException) {
          // An attempt to communicate with a server failed. The request may have been sent.
            //是否IO异常，recover函数判断是否失败应该重试
          if (!recover(e, call, request, requestSendStarted = e !is ConnectionShutdownException)) {
            throw e.withSuppressed(recoveredFailures)
          } else {
            recoveredFailures += e
          }
          newExchangeFinder = false
          continue
        }
  
        // Attach the prior response if it exists. Such responses never have a body.
          //如果上一次请求不为空，将上一次的response的响应体置空，并放入到当前响应中
        if (priorResponse != null) {
          response = response.newBuilder()
              .priorResponse(priorResponse.newBuilder()
                  .body(null)
                  .build())
              .build()
        }
  
        val exchange = call.interceptorScopedExchange
          //followUp新的重定向Request
          //followUpRequest通过响应码判断是否需要进行重定向，并构造新的重定向Request返回
        val followUp = followUpRequest(response, exchange)
  
          //无需重定向直接返回响应结果即可
        if (followUp == null) {
          if (exchange != null && exchange.isDuplex) {
            call.timeoutEarlyExit()
          }
          closeActiveExchange = false
          return response
        }
  
          //响应体有值且用户设置的请求体最多传输一次，也无需重定向，直接返回响应结果即可
        val followUpBody = followUp.body
        if (followUpBody != null && followUpBody.isOneShot()) {
          closeActiveExchange = false
          return response
        }
  
        response.body?.closeQuietly()
  
          //判断当前重定向的次数
        if (++followUpCount > MAX_FOLLOW_UPS) {
          throw ProtocolException("Too many follow-up requests: $followUpCount")
        }
  
          //重定向的Request赋予新的Request用来发起请求，记录本次response
        request = followUp
        priorResponse = response
      } finally {
        call.exitNetworkInterceptorExchange(closeActiveExchange)
      }
    }
  }
  ```

- 路由异常、IO异常判断是否需要进行重试的recover方法

- ```kotlin
  private fun recover(
    e: IOException,
    call: RealCall,
    userRequest: Request,
    requestSendStarted: Boolean
  ): Boolean {
    // 用户设置的是否允许连接失败重试
    if (!client.retryOnConnectionFailure) return false
  
    // We can't send the request body again.
    if (requestSendStarted && requestIsOneShot(e, userRequest)) return false
  
    // This exception is fatal.
    if (!isRecoverable(e, requestSendStarted)) return false
  
    // No more routes to attempt.
    if (!call.retryAfterFailure()) return false
  
    // For failure recovery, use the same route selector with a new connection.
    return true
  }
  ```

- **val followUp = followUpRequest(response, exchange)，获取新的重定向Request（null即无需重定向）**

  - **根据response返回的响应码去判断是否需要重定向并构建新的重定向Request返回**

- 总结：

  - **while循环+异常捕捉实现失败重试+重定向**
  - **获取response**
    - **若一切顺利，首次即return response 循环结束**
    - **RouteException，判断是否能重试，不能重试即抛出异常，否则continue本次循环**
    - **IOException，判断是否能重试，不能重试即抛出异常，否则continue本次循环**
    - **无异常，拿到响应Response，判断是否重定向上次是否存在响应结果，若存在，将其添加到本次响应Response中，并将上次的响应结果响应体置空**
    - **拿到响应Response，根据响应状态码判断是否需要重定向，若需要，构造新的重定向Request，continue本次循环，发送重定向的Request。若不需要，则直接返回响应Response，循环结束（中途会有各种检测，判断重定向次数，用户是否允许重定向等）**

##### BridgeInterceptor

- 桥接拦截器：

  - 用户的请求转换成发送给服务器的请求，添加一些服务器需要的header信息
  - 将服务器的响应转换为用户理解的响应（gzip相关）

- ```kotlin
  override fun intercept(chain: Interceptor.Chain): Response {
    val userRequest = chain.request()
      //新建请求（真正发送给服务器的请求）根据用户请求添加或者移除各种请求头信息
    val requestBuilder = userRequest.newBuilder()
  
    val body = userRequest.body
    if (body != null) {
      val contentType = body.contentType()
      if (contentType != null) {
        requestBuilder.header("Content-Type", contentType.toString())
      }
  
      val contentLength = body.contentLength()
      if (contentLength != -1L) {
        requestBuilder.header("Content-Length", contentLength.toString())
        requestBuilder.removeHeader("Transfer-Encoding")
      } else {
        requestBuilder.header("Transfer-Encoding", "chunked")
        requestBuilder.removeHeader("Content-Length")
      }
    }
  
    if (userRequest.header("Host") == null) {
      requestBuilder.header("Host", userRequest.url.toHostHeader())
    }
  
    if (userRequest.header("Connection") == null) {
      requestBuilder.header("Connection", "Keep-Alive")
    }
  
    // If we add an "Accept-Encoding: gzip" header field we're responsible for also decompressing
    // the transfer stream.
      //添加客户端期望接收到的内容编码（压缩）
    var transparentGzip = false
    if (userRequest.header("Accept-Encoding") == null && userRequest.header("Range") == null) {
      transparentGzip = true
      requestBuilder.header("Accept-Encoding", "gzip")
    }
  
     //cookieJar构造该拦截器时OkhttpClient.cookieJar传递过来的
      //默认是无cookie的
    val cookies = cookieJar.loadForRequest(userRequest.url)
    if (cookies.isNotEmpty()) {
      requestBuilder.header("Cookie", cookieHeader(cookies))
    }
  
    if (userRequest.header("User-Agent") == null) {
      requestBuilder.header("User-Agent", userAgent)
    }
  
      //正式发出请求并获取响应结果
    val networkResponse = chain.proceed(requestBuilder.build())
  
      //对响应结果进行相关cookie保存
      //默认未保存cookie，用户需自己实现cookie逻辑
    cookieJar.receiveHeaders(userRequest.url, networkResponse.headers)
  
    val responseBuilder = networkResponse.newBuilder()
        .request(userRequest)
  
      //响应结果进行处理转换为用户所需要的Response返回
    if (transparentGzip &&
        "gzip".equals(networkResponse.header("Content-Encoding"), ignoreCase = true) &&
        networkResponse.promisesBody()) {
      val responseBody = networkResponse.body
      if (responseBody != null) {
        val gzipSource = GzipSource(responseBody.source())
        val strippedHeaders = networkResponse.headers.newBuilder()
            .removeAll("Content-Encoding")
            .removeAll("Content-Length")
            .build()
        responseBuilder.headers(strippedHeaders)
        val contentType = networkResponse.header("Content-Type")
        responseBuilder.body(RealResponseBody(contentType, -1L, gzipSource.buffer()))
      }
    }
  
    return responseBuilder.build()
  }
  ```

- **总结：**
  - **拿到用户的srcRequest创建新的dstRequest**
    - **srcRequest请求体Content-Type放入dstRequest请求头中**
    - **srcRequest请求体大小判断添加Content-Length还是Transfer-Encoding**
    - **dstRequest请求头中默认添加Host：url（未被覆盖的情况下）**
    - **dstRequest请求头中默认添加Connection：Keep-Alive（未被覆盖的情况下）**
    - **判断是否需要dstRequest请求头中是否需要添加Accept-Encoding=gzip**
    - **判断是否需要添加cookie相关道请求头中**
    - **dstRequest请求头中默认添加User-Agent ：okhttp/${OkHttp.VERSION}（未被覆盖的情况下）**
  - **拿道响应srcResponse转换为用户需要的dstResponse**
    - **将srcResponse的响应头与url通过cookie存起来**
    - **判断dstRequest请求头是否Accept-Encoding = gzip以及dstResponse响应头中Accept-Encoding = gzip以及dstResponse响应体非空**
      - **若满足gzip解压srcResponse.source后放入dstResponse返回**
      - **不满足直接返回**

##### Http缓存

- 强制缓存

  - ![image.png](https://s2.loli.net/2024/09/12/STEb385p9hgcDrK.png)
  - Expires/Cache-Control：浏览器第一次向服务器发送请求，服务器会在**响应头**中添加Expires或者Cache-Control用以表示该资源的缓存失效规则
    - Expires：缓存的到期时间（Http1.1基本不再使用），服务器与客户端可能存在时间不一致情况导致误差
    - Cache-Control：
      - no_cache：使用对比缓存
      - no_store：不使用任何缓存
      - max_age：缓存过期最大倒计时
      - public：客户端可以缓存
      - private：客户端与代理服务器可以缓存
    - Cache_Control:max-age=3153600
      - 该资源缓存失效时间为3153600秒，在此倒计时结束之前都可使用浏览器缓存中的信息
      - 浏览器将资源及相关倒计时信息保存到本地，下次请求判断是否命中即可
    - ![image.png](https://s2.loli.net/2024/09/12/5h8maflDEHgYBxy.png)

- 对比缓存![image.png](https://s2.loli.net/2024/09/12/OcJbUB8iyE7nv3g.png)

  - **不管缓存是否存在，都需要向服务器发起请求（缓存的意义？）**
  - **第一次浏览器向服务器发送请求，服务器将缓存标识以及响应返回，浏览器将缓存标识，响应保存到本地。第二次浏览器向服务器发送请求时，若本次存在该缓存，浏览器携带此缓存标识向服务器发送请求，服务器根据缓存标识判断缓存是否有效，若有效返回304响应，不返回任何响应体，浏览器直接用本地的即可。若缓存无效，服务器会重新对其进行响应**
  - **Last-Modified / If-Modified-Since**
    - **Last-Modified：服务器响应在响应头中内容。最后修改的时间**
    - **If-Modified-Since：浏览器在请求头中携带该资源最后修改的时间**
    - **服务器通过请求头中的If-Modified-Since与当前资源目前的Last-Modified进行比对，选择是否使用缓存，使用即返回304响应，否则重新对其响应，发送新的Last-Modified交给浏览器保存到本地便于下次请求携带**

  - **Etag / If-None-Match**（优先级大于Last-Modified / If-Modified-Since）
    - **Etag ：服务器响应在响应头的内容。缓存标识字符串**
    - **If-None-Match：浏览器在请求中携带的标识字符串**
    - **服务器判断二者是否相等，相等即可使用该浏览器的缓存即可，返回304响应否则重新对其进行200响应**
  - ![image.png](https://s2.loli.net/2024/09/12/9kNMTBYf7mgbWwu.png)

- **强制缓存优先级大于对比缓存**

##### CacheInterceptor

- 缓存拦截器

- ```kotlin
  override fun intercept(chain: Interceptor.Chain): Response {
    val call = chain.call()
      
      //从Cache对象中拿到该Request的缓存Response
    val cacheCandidate = cache?.get(chain.request())
  
    val now = System.currentTimeMillis()
  
      //根据当前时间，Request，缓存Response构建缓存策略CacheStrategy
    val strategy = CacheStrategy.Factory(now, chain.request(), cacheCandidate).compute()
      //networkRequest == null ? 无需网络请求：需要网络请求
    val networkRequest = strategy.networkRequest
      //cacheResponse == null ? 未命中缓存：缓存命中
    val cacheResponse = strategy.cacheResponse
  
    cache?.trackResponse(strategy)
    val listener = (call as? RealCall)?.eventListener ?: EventListener.NONE
  
    if (cacheCandidate != null && cacheResponse == null) {
      // The cache candidate wasn't applicable. Close it.
      cacheCandidate.body?.closeQuietly()
    }
  
    // 未命中缓存且无需进行网络请求，直接返回错误Response
    if (networkRequest == null && cacheResponse == null) {
      return Response.Builder()
          .request(chain.request())
          .protocol(Protocol.HTTP_1_1)
          .code(HTTP_GATEWAY_TIMEOUT)
          .message("Unsatisfiable Request (only-if-cached)")
          .body(EMPTY_RESPONSE)
          .sentRequestAtMillis(-1L)
          .receivedResponseAtMillis(System.currentTimeMillis())
          .build().also {
            listener.satisfactionFailure(call, it)
          }
    }
  
    // 缓存命中，无需网络请求，直接返回缓存命中Response
    if (networkRequest == null) {
      return cacheResponse!!.newBuilder()
          .cacheResponse(stripBody(cacheResponse))
          .build().also {
            listener.cacheHit(call, it)
          }
    }
  
    if (cacheResponse != null) {
      listener.cacheConditionalHit(call, cacheResponse)
    } else if (cache != null) {
      listener.cacheMiss(call)
    }
  
    var networkResponse: Response? = null
    try {
      networkResponse = chain.proceed(networkRequest)
    } finally {
      // If we're crashing on I/O or otherwise, don't leak the cache body.
      if (networkResponse == null && cacheCandidate != null) {
        cacheCandidate.body?.closeQuietly()
      }
    }
  
    // 有缓存命中
    if (cacheResponse != null) {
        //网络请求判断是否为304，即缓存是否有效（对比缓存）
      if (networkResponse?.code == HTTP_NOT_MODIFIED) {
          //缓存有效，混合网络响应头与缓存响应头，构建新的Response返回即可
        val response = cacheResponse.newBuilder()
            .headers(combine(cacheResponse.headers, networkResponse.headers))
            .sentRequestAtMillis(networkResponse.sentRequestAtMillis)
            .receivedResponseAtMillis(networkResponse.receivedResponseAtMillis)
            .cacheResponse(stripBody(cacheResponse))
            .networkResponse(stripBody(networkResponse))
            .build()
  
        networkResponse.body!!.close()
  
        // 新的Response，去更新缓存相关
        cache!!.trackConditionalCacheHit()
        cache.update(cacheResponse, response)
        return response.also {
          listener.cacheHit(call, it)
        }
      } else {
        cacheResponse.body?.closeQuietly()
      }
    }
  
      //缓存失效，使用网络响应的结果
    val response = networkResponse!!.newBuilder()
        .cacheResponse(stripBody(cacheResponse))
        .networkResponse(stripBody(networkResponse))
        .build()
  
      //判断响应是否需要缓存，加入到缓存中便于下次使用
    if (cache != null) {
      if (response.promisesBody() && CacheStrategy.isCacheable(response, networkRequest)) {
        // Offer this request to the cache.
        val cacheRequest = cache.put(response)
        return cacheWritingResponse(cacheRequest, response).also {
          if (cacheResponse != null) {
            // This will log a conditional cache miss only.
            listener.cacheMiss(call)
          }
        }
      }
  
      if (HttpMethod.invalidatesCache(networkRequest.method)) {
        try {
          cache.remove(networkRequest)
        } catch (_: IOException) {
          // The cache cannot be written.
        }
      }
    }
  
    return response
  }
  ```

- 总结：

  - **以Request为Key去Cache中读取候选缓存**
  - **依据候选缓存、当前时间、Request构建缓存策略CacheStrategy**
    - **strategy.networkRequest：是否需要请求网络**
    - **strategy.cacheResponse：是否存在缓存（缓存命中）**
  - **倘若无需请求网络&&未命中缓存，直接返回错误Response**
  - **倘若无需请求网络&&命中缓存，直接返回缓存Response（无须进行与服务器通信判断缓存有效性，因为缓存策略中无需请求网络）**
  - **走到这一步，说明网络请求非空，缓存是否命中未知，将请求交由下一级拦截器，获取网络Response**
  - **拿到网络的Response后，判断缓存是否命中**
    - **缓存命中&&网络Response.code == 304**
      - **缓存有效，混合缓存的响应头与网络Response的响应头，构造真正Response，同时更新Cache中的缓存信息，直接返回真正Response**
    - **网络Response.code != 304**
      - **不管缓存是否命中，此时缓存都是不可靠的，此时构建真正Response，判断是否需要缓存，将新的Response写入到缓存中并返回，否则移除该缓存直接返回新的Response**
  - 缓存策略主要是根据`CacheStrategy`中的`networkRequest`和`cacheResponse`来决定的：![image.png](https://s2.loli.net/2024/09/13/MUYgoJ9iBtOdWS2.png)

  - CacheStrategy对象的获取

    - 内部类工厂去根据Request、当前时间、缓存响应去compute构造

    - ```kotlin
      val strategy = CacheStrategy.Factory(now, chain.request(), cacheCandidate).compute()
      ```

    - ```kotlin
      fun compute(): CacheStrategy {
        val candidate = computeCandidate()
      
        // We're forbidden from using the network and the cache is insufficient.
        if (candidate.networkRequest != null && request.cacheControl.onlyIfCached) {
          return CacheStrategy(null, null)
        }
      
        return candidate
      }
      ```

    - ```kotlin
      private fun computeCandidate(): CacheStrategy {
        //没有缓存需要网络请求
        if (cacheResponse == null) {
          return CacheStrategy(request, null)
        }
      
        // 是https请求但是缓存无tls相关信息，需要网络请求
        if (request.isHttps && cacheResponse.handshake == null) {
          return CacheStrategy(request, null)
        }
      
        //依据缓存响应的响应码判断是否允许被缓存
        if (!isCacheable(cacheResponse, request)) {
          return CacheStrategy(request, null)
        }
      
          //Request 是不是要求noCache 或者请求包含If-Modified-Since或者If-None-Match 需要发起网络请求
        val requestCaching = request.cacheControl
        if (requestCaching.noCache || hasConditions(request)) {
          return CacheStrategy(request, null)
        }
      
        val responseCaching = cacheResponse.cacheControl
      
        val ageMillis = cacheResponseAge()
        var freshMillis = computeFreshnessLifetime()
      
        if (requestCaching.maxAgeSeconds != -1) {
          freshMillis = minOf(freshMillis, SECONDS.toMillis(requestCaching.maxAgeSeconds.toLong()))
        }
      
        var minFreshMillis: Long = 0
        if (requestCaching.minFreshSeconds != -1) {
          minFreshMillis = SECONDS.toMillis(requestCaching.minFreshSeconds.toLong())
        }
      
        var maxStaleMillis: Long = 0
        if (!responseCaching.mustRevalidate && requestCaching.maxStaleSeconds != -1) {
          maxStaleMillis = SECONDS.toMillis(requestCaching.maxStaleSeconds.toLong())
        }
      
          //响应缓存非noCache，（即不需要对比缓存） && 缓存有效
          //直接返回缓存信息
        if (!responseCaching.noCache && ageMillis + minFreshMillis < freshMillis + maxStaleMillis) {
          val builder = cacheResponse.newBuilder()
          if (ageMillis + minFreshMillis >= freshMillis) {
            builder.addHeader("Warning", "110 HttpURLConnection \"Response is stale\"")
          }
          val oneDayMillis = 24 * 60 * 60 * 1000L
          if (ageMillis > oneDayMillis && isFreshnessLifetimeHeuristic()) {
            builder.addHeader("Warning", "113 HttpURLConnection \"Heuristic expiration\"")
          }
          return CacheStrategy(null, builder.build())
        }
      
        // 加入etag和lastModifiedString到If-None-Match、If-Modified-Since Request请求中（对比缓存）
        val conditionName: String
        val conditionValue: String?
        when {
          etag != null -> {
            conditionName = "If-None-Match"
            conditionValue = etag
          }
      
          lastModified != null -> {
            conditionName = "If-Modified-Since"
            conditionValue = lastModifiedString
          }
      
          servedDate != null -> {
            conditionName = "If-Modified-Since"
            conditionValue = servedDateString
          }
      
          else -> return CacheStrategy(request, null) // No condition! Make a regular request.
        }
      
          
        val conditionalRequestHeaders = request.headers.newBuilder()
        conditionalRequestHeaders.addLenient(conditionName, conditionValue!!)
      
          //构造新的Request，返回网络请求与缓存响应
        val conditionalRequest = request.newBuilder()
            .headers(conditionalRequestHeaders.build())
            .build()
        return CacheStrategy(conditionalRequest, cacheResponse)
      }
      ```

    - 总结：

    - 该请求没有对应的缓存响应，直接进行网络请求

    - 该请求为`HTTPS`请求但是缓存响应中没有保存`TLS`握手相关数据，忽略缓存，进行网络请求

    - 该响应是否允许被缓存，若不允许，直接进行网络请求

    - 若请求头中含有`noCache`指令或`If-Modified-Since/If-None-Match` Header，则忽略缓存响应，进行网络请求

    - 如果缓存响应没有noCache指令，并且缓存响应还未过期，则直接使用缓存响应，不需要进行网络请求

    - 若缓存响应过期，且没有保存`etag/lastModified/servedDate`信息，直接进行网络请求

    - 若缓存响应过期，且缓存响应中保存了`etag/Modified/servedDate`信息，将信息添加到请求头中，进行网络请求，同时将该缓存响应保存至缓存策略中。

##### ConnectInterceptor

- **创建管理复用Socket连接**

- ```kotlin
  override fun intercept(chain: Interceptor.Chain): Response {
    val realChain = chain as RealInterceptorChain
      //获取Exchange对象，将exchange对象放入RealInterceptorChain交由下一个拦截器处理
    val exchange = realChain.call.initExchange(chain)
    val connectedChain = realChain.copy(exchange = exchange)
    return connectedChain.proceed(realChain.request)
  }
  ```

- **Exchange类：**

  - **网络报文读写管理员**
  - **构造函数：**
    - **RealCall：发起请求的RealCall**
    - **EventListener：事件监听EventListener**
    - **ExchangeFinder：Exchange的寻找者**
    - **ExchangeCodec：报文读写实际行动者**
      - **RealConnection对象的newCodec方法返回**
        - **ExchangeFider对象的findHealthyConnection方法获取RealConnection对象**

- **Realcall类中initExchange方法**

  - ```kotlin
    internal fun initExchange(chain: RealInterceptorChain): Exchange {
      val exchangeFinder = this.exchangeFinder!!
        //获取读写网络报文的工具类对象ExchangeCodec
      val codec = exchangeFinder.find(client, chain)
        //根据ExchangeCodec与ExchangeFinder构造Exchange对象管理类传递给下一个拦截器
      val result = Exchange(this, eventListener, exchangeFinder, codec)
      this.interceptorScopedExchange = result
      this.exchange = result
      synchronized(this) {
        this.requestBodyOpen = true
        this.responseBodyOpen = true
      }
    
      if (canceled) throw IOException("Canceled")
      return result
    }
    ```

- **exchangeFinder的初始化：Realcall中存在成员变量**

  - ```kotlin
    private var exchangeFinder: ExchangeFinder? = null
    ```

  - **在RetryAndFollowUpInterceptor中的intercept方法会被初始化**

    - ```kotlin
      call.enterNetworkInterceptorExchange(request, newExchangeFinder)
      ```

    - ```kotlin
      fun enterNetworkInterceptorExchange(request: Request, newExchangeFinder: Boolean) {
        if (newExchangeFinder) {
            //初始化
          this.exchangeFinder = ExchangeFinder(
              connectionPool,
              createAddress(request.url),
              this,
              eventListener
          )
        }
      }
      ```

- **ExchangeFider的find方法：ExchangeCodec**

  - **获取RealConnection调用其newCodec方法返回ExchangeCodec**

  - **调用findHealthyConnection方法获取RealConnection**

  - ```kotlin
    fun find(
      client: OkHttpClient,
      chain: RealInterceptorChain
    ): ExchangeCodec {
      try {
        val resultConnection = findHealthyConnection(
            connectTimeout = chain.connectTimeoutMillis,
            readTimeout = chain.readTimeoutMillis,
            writeTimeout = chain.writeTimeoutMillis,
            pingIntervalMillis = client.pingIntervalMillis,
            connectionRetryEnabled = client.retryOnConnectionFailure,
            doExtensiveHealthChecks = chain.request.method != "GET"
        )
        return resultConnection.newCodec(client, chain)
      } catch (e: RouteException) {
        trackFailure(e.lastConnectException)
        throw e
      } catch (e: IOException) {
        trackFailure(e)
        throw RouteException(e)
      }
    }
    ```

- **ExchangeFider的findHealthyConnection方法：RealConnection**

  - **循环获取健康的socket链接（后续分析）**

  - ```kotlin
    private fun findHealthyConnection(
      connectTimeout: Int,
      readTimeout: Int,
      writeTimeout: Int,
      pingIntervalMillis: Int,
      connectionRetryEnabled: Boolean,
      doExtensiveHealthChecks: Boolean
    ): RealConnection {
      while (true) {
        val candidate = findConnection(
            connectTimeout = connectTimeout,
            readTimeout = readTimeout,
            writeTimeout = writeTimeout,
            pingIntervalMillis = pingIntervalMillis,
            connectionRetryEnabled = connectionRetryEnabled
        )
    
        // Confirm that the connection is good.
        if (candidate.isHealthy(doExtensiveHealthChecks)) {
          return candidate
        }
    
        // If it isn't, take it out of the pool.
        candidate.noNewExchanges()
    
        // Make sure we have some routes left to try. One example where we may exhaust all the routes
        // would happen if we made a new connection and it immediately is detected as unhealthy.
        if (nextRouteToTry != null) continue
    
        val routesLeft = routeSelection?.hasNext() ?: true
        if (routesLeft) continue
    
        val routesSelectionLeft = routeSelector?.hasNext() ?: true
        if (routesSelectionLeft) continue
    
        throw IOException("exhausted all routes")
      }
    }
    ```

- **通过健康的连接RealConnection去构造读写报文工具类ExchangeCodec**

  - ```kotlin
    return resultConnection.newCodec(client, chain)
    ```

- **将ExchangeCodec、ExchangeFinder获取完成封装进管理类Exchange中放入RealInterceptorChain中传递给下一个拦截器**

- https://blog.csdn.net/zjm807778317/article/details/125582698?spm=1001.2014.3001.5502（ConnectInterceptor源码解析）

##### CallServerInterceptor

- 利用上述Exchange中的ExchangeCodeC去读写网络报文

- 最后一道拦截器

  ```kotlin
  override fun intercept(chain: Interceptor.Chain): Response {
      val realChain = chain as RealInterceptorChain
      val exchange = realChain.exchange!!
      val request = realChain.request
      val requestBody = request.body
      val sentRequestMillis = System.currentTimeMillis()
  	// 发送请求头，在上个拦截器ConnectInterceptor中我们已经知道Exchange的codec属性是指向输入输出流的因此下面方法的本质也是调用codec的方法去完成流的操作，具体分析看下1.（Exchange#writeRequestHeaders）
      
      exchange.writeRequestHeaders(request)
  var invokeStartEvent = true
  var responseBuilder: Response.Builder? = null
  // 判断请求方法是否支持请求体，看下（2.HttpMethod#permitsRequestBody）
  if (HttpMethod.permitsRequestBody(request.method) && requestBody != null) {
      // If there's a "Expect: 100-continue" header on the request, wait for a "HTTP/1.1 100
      // Continue" response before transmitting the request body. If we don't get that, return
      // what we did get (such as a 4xx response) without ever transmitting the request body.
      // 请求头中存在Expect: 100-continue，此字段意味着先往服务器发送请求头，若服务器返回100则继续发送请求体，目的是询问服务器是否可以接受此次请求体，比如请求体过大时，需要先询问服务器是否接收
      if ("100-continue".equals(request.header("Expect"), ignoreCase = true)) {
          // 刷新缓冲区，将缓冲区的内容发送到对端
          exchange.flushRequest()
          // 读取响应头并根据响应头的响应码构建ResponseBuilder，若服务器返回100则responseBuilder为null
          // 具体解析看下（3.Exchange#readResponseHeaders）
          responseBuilder = exchange.readResponseHeaders(expectContinue = true)
          exchange.responseHeadersStart()
          invokeStartEvent = false
      }
      // responseBuilder为null有两种情况
      // 1.存在请求体，但是请求头不存在Expect: 100-continue，此时需要立即发送请求头
      // 2.存在请求头，且请求头存在Expect: 100-continue，意味着命中了上面的if，此时responseBuilder为null意味着服务端返回了100响应码
      if (responseBuilder == null) {
          // 是否支持双工通信，双工通信并不是HTTP标准，需要程序员重写RequestBody并覆盖isDuplex()返回true才会命中if，默认情况下不会命中此分支
          if (requestBody.isDuplex()) {
              // Prepare a duplex body so that the application can send a request body later.
              exchange.flushRequest()
              // 发送请求体，createRequestBody()方法看下（4.Exchange#createRequestBody）
              val bufferedRequestBody = exchange.createRequestBody(request, true).buffer()
              // 写入输出流中发送数据
              requestBody.writeTo(bufferedRequestBody)
          } else {
              // 将请求体发送到服务器
              // Write the request body if the "Expect: 100-continue" expectation was met.
              val bufferedRequestBody = exchange.createRequestBody(request, false).buffer()
              requestBody.writeTo(bufferedRequestBody)
              bufferedRequestBody.close()
          }
      } else {
          // 命中此分支意味着请求头存在Expect: 100-continue，且服务器返回的响应码并不是100，对于此种情况OkHttp则认为请求体是无效的
          exchange.noRequestBody()
          if (!exchange.connection.isMultiplexed) {
              // If the "Expect: 100-continue" expectation wasn't met, prevent the HTTP/1 connection
              // from being reused. Otherwise we're still obligated to transmit the request body to
              // leave the connection in a consistent state.
              exchange.noNewExchangesOnConnection()
          }
      }
  } else {
      // 命中此分支意味着请求方法不支持请求体或者请求体本身就不存在
      exchange.noRequestBody()
  }
  // 结束请求
  if (requestBody == null || !requestBody.isDuplex()) {
      exchange.finishRequest()
  }
  // 开始读取真正的响应，之前在请求头存在Expect: 100-continue时读取的响应是服务器是否允许客户端继续上传的答复，并不包含真正的数据 
  // 不命中下述if只有一种情况，responseBuilder仅在请求头存在Expect: 100-continue且服务器返回非100响应码时才不为null，对于OkHttp而言只要是其他响应码则意味着是有效的响应
  if (responseBuilder == null) {
      responseBuilder = exchange.readResponseHeaders(expectContinue = false)!!
      if (invokeStartEvent) {
          exchange.responseHeadersStart()
          invokeStartEvent = false
      }
  }
  var response = responseBuilder
  .request(request)
  .handshake(exchange.connection.handshake())
  .sentRequestAtMillis(sentRequestMillis)
  .receivedResponseAtMillis(System.currentTimeMillis())
  .build()
  var code = response.code
  // 此处响应码为100就很怪异了，只可能是一种情况，即使我们没有请求，服务器也发送了 100-continue。再次尝试读取实际响应状态。
  if (code == 100) {
      // 在响应码为100的情况下再次读取响应头，此时响应头是真实包含数据的响应的头
      responseBuilder = exchange.readResponseHeaders(expectContinue = false)!!
      if (invokeStartEvent) {
          exchange.responseHeadersStart()
      }
      response = responseBuilder
      .request(request)
      .handshake(exchange.connection.handshake())
      .sentRequestAtMillis(sentRequestMillis)
      .receivedResponseAtMillis(System.currentTimeMillis())
      .build()
      code = response.code
  }
  
  exchange.responseHeadersEnd(response)
  // 若响应码为101，则意味着需要切换协议，若请求头如下:
  // HTTP/1.1 101 Switching Protocols
  // Upgrade: websocket
  // Connection: Upgrade
  // 则需要将协议切换到websocket
  response = if (forWebSocket && code == 101) {
      // Connection is upgrading, but we need to ensure interceptors see a non-null response body.
      response.newBuilder()
      .body(EMPTY_RESPONSE)
      .build()
  } else {
      // 若是正常的HTTP协议则读取响应体数据
      response.newBuilder()
      .body(exchange.openResponseBody(response))
      .build()
  }
  // 若响应头的Connection字段为close则需要立即关闭此次连接
  if ("close".equals(response.request.header("Connection"), ignoreCase = true) ||
      "close".equals(response.header("Connection"), ignoreCase = true)) {
      exchange.noNewExchangesOnConnection()
  }
  // 204，205表明服务端没有数据，若响应码为204，205且响应体有数据则抛出协议异常
  if ((code == 204 || code == 205) && response.body?.contentLength() ?: -1L > 0L) {
      throw ProtocolException(
          "HTTP $code had non-zero Content-Length: ${response.body?.contentLength()}")
  }
  return response
  ```
  1. **exchange.writeRequestHeaders(request)发送请求头**

     - **实则是ExchangeCodeC中流发送请求头**
     - **初始化响应responseBuilder：Response.Builder? = null**

  2. **判断是否存在请求体且请求方式支持携带请求体（满足转到3，否则转到5）**

     - **if (HttpMethod.permitsRequestBody(request.method) && requestBody != null)**
     - **Get、Head方式不支持**

  3. **判断请求头是否存在Expect = 100-continue**

     - **此key-value出现在请求头中，可能代表请求体携带数据过大，第一次仅仅携带请求头先询问服务器是否接收，若服务器响应100，即再发送请求体给服务器否则代表服务器不接受**
     - **倘若上述满足，则利用exchangeCodeC发送请求头到服务器，读取服务器响应赋值responseBuilder**
       - **若响应为100，则返回responseBuilder仍然==100，代表需要再次发送请求**

  4. **判断responseBuilder是否为空**

     - **responseBuilder==null**
       - **请求头存在Expect = 100-continue且服务器返回100响应或者请求头不存在Expect = 100-continue**
       - **此时需要将请求体封装发送给服务器exchange.createRequestBody(request, false)**
     - **responseBuilder!= null**
       - **请求头存在Expect = 100-continue但服务器未返回100响应，意味着请求体是无效的**

  5. **代表请求没有请求体**

  6. **结束请求，关闭请求流**

  7. **判断responseBuilder是否为空**

     - **responseBuilder == null**
       - **不存在请求体**
       - **存在请求体，请求头中携带Expect = 100-continue 且服务器响应100**
     - **responseBuilder != null**
       - **请求头中携带Expect = 100-continue 且服务器未响应100**

  8. **读取响应头、构造响应结果**

     - ```kotlin
       var response = responseBuilder
           .request(request)
           .handshake(exchange.connection.handshake())
           .sentRequestAtMillis(sentRequestMillis)
           .receivedResponseAtMillis(System.currentTimeMillis())
           .build()
       ```

  9. **判断响应码**

     - **code == 100**
       - **没有请求，服务器也发送了100-continue。再次尝试读取实际响应状态。需要再次尝试读取响应结果,构造responseBuilder与response**
     - **code != 100**

  10. **是否是websocket协议且code == 101**

      - **空响应返回**

      - ```kotlin
        response.newBuilder()
            .body(EMPTY_RESPONSE)
            .build()
        ```

  11. **否则、读取正常响应体，封装进response返回即可**

      -  **response.newBuilder()**
                **.body(exchange.openResponseBody(response))**
                **.build()**

### Retrofit2

#### Gradle引入

- **converter-gson：Gson转换器，可自行选择是否引入。该转换器作用是将网络请求返回的ResponseBody可自动转换成想要的JavaBean对象，无需手动进行转换**
  - **fun getType():  Call<ResponseBody> (原始)**
  - **fun getType():  Call<JAVABEAN>  若未添加转换器会报错**
  - **fun getType():  Call<JAVABEAN>  添加转换器后会自动转换为所需要的JAVABEAN对象**

```groovy
//retrofit
api 'com.squareup.retrofit2:retrofit:2.5.0'
api 'com.squareup.retrofit2:converter-gson:2.4.0'
```

#### 使用

- 创建Retrofit对象retrofit

  - 建造者模式Retrofit.Builder()去构建

  - 设置基础请求URL、添加Gson转换器

  - ```kotlin
    val retrofit = Retrofit.Builder()
    	//设置基础请求Url
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    ```

- 创建接口对象 Interface interface = retrofit.create（class: Class）

  - retrofitBaseInterface是通过发射生成的接口对象

  - retrofitBaseInterface自定义接口，定义了网络请求的一系列方法

    - ```kotlin
      @GET("posts")
      fun getTopic(): Call<List<TopicResponse>>
      
      @GET("posts/{path}")
      fun getTopicFromPath(@Path("path") path: String): Call<TopicResponse>
      
      @GET
      fun getTopicFromUrl(@Url url: String): Call<TopicResponse>
      ```

  - ```kotlin
    retrofitBaseInterface = retrofit.create(RetrofitBaseInterface::class.java)
    ```

- 调用接口对象准备发起网络请求

  - 调用对象定义网络请求的方法，拿到预请求
  - Call<ResponseBody> call = interface.getTopic()

- 发起网络请求，监听请求结果

  - 预请求发出，监听请求结果
  - call.enqueue(Callback监听)

#### 注解

- **@GET**

  - BASE_URL = "https://jsonplaceholder.typicode.com/"

  - method = GET、URL = posts 

  - ```kotlin
    @GET("posts")
    fun getTopic(): Call<List<TopicResponse>>
    ```

    ![image.png](https://s2.loli.net/2024/09/25/O2cX1q3AbRzJdjI.png)

  - **JsonArray  每个元素为一个JsonObject  ===> List<TopicResponse>**

- **@Path**

  - 动态拼接url

  - ```kotlin
    @GET("posts/{path}")
    fun getTopicFromPath(@Path("path") path: String): Call<TopicResponse>
    ```

    ![image.png](https://s2.loli.net/2024/09/25/9LlzOMhSmfcAru6.png)

  - **JsonObject ===> TopicResponse**

- **@Url**

  - **访问指定的URL，无需再写请求地址（仍然是BaseUrl后面拼接）**

  - ```kotlin
    @GET
    fun getTopicFromUrl(@Url url: String): Call<TopicResponse>
    ```

- **@Query**

  - GET方法传入的参数

  - 传参userId=xxx

  - ```kotlin
    @GET("posts")
    fun getTopicForQuery(@Query("userId") userId: Int): Call<List<TopicResponse>>
    ```

    ![image.png](https://s2.loli.net/2024/09/25/tRKs96pYzUq3GCX.png)

  - **JsonArray  每个元素为一个JsonObject  ===> List<TopicResponse>**

- **@QueryMap**

  - GET方法传入多个参数,map接收

  - ```kotlin
    @GET("posts")
    fun getTopicForQueryMap(@QueryMap map: Map<String,@JvmSuppressWildcards Any>): Call<List<TopicResponse>>
    ```

- **@Header**

  - **添加动态请求头**

  - channel = xxx、androidVersionCode = xxx、packageName = xxx

  - ```kotlin
    @GET("system/getSwitch")
    fun getSystemSwitchForAddHead(@Header("channel")channel: Int,@Header("androidVersionCode")androidVersionCode: Int,@Header("packageName")packageName: String): Call<SystemSwitchResponse>
    ```

- **@Headers**

  - **添加固定请求头**

  - channel=1、androidVersionCode=1、packageName=com.huanji.android

  - ```kotlin
    @GET("system/getSwitch")
    @Headers("channel:1","androidVersionCode:1","packageName:com.huanji.android")
    fun getSystemSwitchForAddHeads(): Call<SystemSwitchResponse>
    ```

- **@FormUrlEncoded、@Field**

  - POST请求中搭配使用

  -  **===> application/x-www-form-urlencoded**

  - 将参数编码成键值对格式传递

  - ```kotlin
    @POST("user/register")
    @FormUrlEncoded
    fun register(@Field("username") username: String,@Field("password") password: String,@Field("repassword") repassword: String): Call<UserResponse>
    ```

    ![image.png](https://s2.loli.net/2024/09/25/s5yUtBrJNgoazZP.png)

- **@FieldMap**

  - Post请求中多个参数，搭配FormUrlEncoded

  - 等同于多个@Field注解

  - ```kotlin
    @POST("user/register")
    @FormUrlEncoded
    fun registerForMap(@FieldMap map: Map<String,@JvmSuppressWildcards Any>): Call<UserResponse>
    ```

- **@Streaming**

  - 下载

  - ```kotlin
    @Streaming
    @GET("android-studio-2024.1.2.12-windows.exe")
    fun getResource(): Call<ResponseBody>
    ```

- **@Body**

  - 手动传递请求体

  - 一般POST请求、GET请求无请求体

  - **RequestBody对象的创建：**

    - 扩展函数（String、ByteArray、ByteString）中的toRequestBody去创建

    - ```kotlin
      fun String.toRequestBody(contentType: MediaType? = null): RequestBody
      ```

  - **MediaType对象的创建：**

    - **请求头中设置请求体的content-type**

    - 扩展函数（String）中的toMediaType去创建

    - ```kotlin
      fun String.toMediaType(): MediaType
      ```

  - ```kotlin
    val jsonBody = Gson().toJson(mapOf(Pair("a", "a"), Pair("b", "b")))
    val requestBody = jsonBody.toRequestBody("application/json".toMediaTypeOrNull())
    ```

  - ```kotlin
    @POST("wx/login")
    @Headers("channel:1","androidVersionCode:1","packageName:com.huanji.android")
    fun login(@Body body: RequestBody): Call<ResponseBody>
    ```

- **@Multipart、@Part**

  - **POST请求中content-type=form-data 表单**

  - **用于上传文件注解**

  - ```kotlin
    @Multipart
    @POST("upload")
    fun uploadResource(@Part("fileName")fileName: RequestBody,@Part file: MultipartBody.Part): Call<ResponseBody>
    ```

  - **参数：RequestBody与MultipartBody.Part**

  - ```kotlin
    val fileNameRequestBody = "文件名称".toRequestBody("text/plain".toMediaTypeOrNull())
    val dir = this.getExternalFilesDir("download") ?: return
    val file = File(dir,"a.png")
    if (file.exists()) {
        file.createNewFile()
    }
    val fileRequestBody = file.asRequestBody("image/png".toMediaTypeOrNull())
    val createFormData =
        MultipartBody.Part.createFormData("file", "文件名称", fileRequestBody)
    retrofitBaseInterface.uploadResource(fileNameRequestBody,createFormData)
    ```
