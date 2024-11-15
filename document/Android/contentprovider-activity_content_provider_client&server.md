#### FileProvider

##### 定义

- **FileProvider类继承ContentProvider类，androidx.core.content.FileProvider包下**
- **Android7.0之前，文件的Uri以file://形式提供给其他app访问，此种形式不安全，会暴露出文件的真实地址，故在Android7.0之后，使用FileProvider生成新的Uri去替代供给其他app访问**

##### Uri

- **通用资源标志符**
- **Android的Uri一般由content://应用名称/数据路径content://com.example.app.provider/table1** 
- **FileProvider中的Uri格式content://com.example.app.provider/路径别名name/filename**

##### 使用

- **AndroidManifes声明FileProvider**

  - **参数**

    - **name：声明provider所在的位置，一般官方的FileProvider已够用，也可自定义**
    - **authorities：provider生成Uri的认证域名，一般包名.provder**
    - **exported：是否公开**
    - **grantUriPermissions：是否授予临时权限给接收方**
    - **resource：暴露出去文件路径配置信息**

  - ```xml
    <provider
        android:authorities="${applicationId}.provider"
        android:name="androidx.core.content.FileProvider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
            android:resource="@xml/file_paths" />
    </provider>
    ```

- **配置resource="@xml/file_paths"**

  - **name：文件夹生成Uri的虚拟名称**

    - **file文件为外置存储包目录下的file文件夹下的b.png**
    - **此时该file转换为uri：content://com.example.app.provider/aaa/b.png**
    - **<external-files-path
              name="aaa"
              path="."/>**

  - **path：暴露出来允许外部访问的的文件路径**

  - 各标签对应的目录

    - **external-files-path：外部存储下的包目录/file**
      - **/storage/emulated/0/Android/data/包名/files/**
      - **Context.getExternalFilesDir("")**
    - **external-cache-path：外部存储下的包目录/cache**
      - **/storage/emulated/0/Android/data/包名/cache/**
      - **Context.getExternalCacheDir("")**
    - **external-path：外部存储下的目录**
      - **/storage/emulated/0/**
      - **Environment.getExternalStorageDirectory()**
    - **files-path：内部存储下的包目录/files（一般需要root权限查看）**
      - **/data/user/0/包名/files**
      - **Context.getFilesDir()**
    - **cache-path：内部存储下的包目录/cache（一般需要root权限查看）**
      - **/data/user/0/包名/cache**
      - **Context.getCacheDir()**

  - ```xml
    <paths>
        <external-files-path
            name="aaa"
            path="."/>
        <files-path
            name="bbb"
            path="."/>
    
        <external-path
            name="ccc"
            path="Music"/>
    
        <external-path
            name="ddd"
            path="Download"/>
    
    </paths>
    ```

- /storage/emulated/0/Android/data/包名/files/a.png 需要将此File暴露出去允许系统相册App访问

  - 配置暴露出去的文件路径，name为aaa

  - <external-files-path
            name="aaa"
            path="."/>

  - 注意：

    - **需要兼容版本判断，Android7.0之前直接可通过file://去访问，7.0之后需要通过content://**
      - **Uri.fromFile(file)**
      - **FileProvider.getUriForFile(this, "com.jin.learn.provider", file)**
        - **com.jin.learn.provider为AndroidManifest中声明的author令牌**
    - **intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)**
      - **需要授予访问的app临时读权限，否则data会传递不过去**
      - **此案例暴露的是外部存储包目录下的文件，所以不需要声明读权限，若是访问外部存储下的其他文件（Download），AndroidManifest还需要声明读权限并动态申请**

  - ```kotlin
    val externalFilesDir = getExternalFilesDir("")
    val file = File(externalFilesDir,"img.png")
    
    val intent = Intent(Intent.ACTION_VIEW)
    val uri =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(this, "com.jin.learn.provider", file)
    } else {
         Uri.fromFile(file)
    }
    
    intent.setDataAndType(uri,"image/png")
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    
    startActivity(intent)
    ```
  
- **通过文件后缀名获取对应的mimeType**

  - **MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix：String)**


##### 原理

- **继承ContentProvider**

- **getUriForFile（Context,String authority,File）FileProvider（实际上调用的是SimplePathStrategy对象的）**

  - **文件转换为content://格式的uri**

  - ```kotlin
    FileProvider.getUriForFile(this, "com.jin.learn.provider", file)
    ```

- **内部维护一个缓存HashMap**

  - **key为authority：String类型**
  - **value为PathStrategy类型对象（实际为SimplePathStrategy对象）**

- **根据传递过来的authority从缓存HashMap中寻找value（PathStrategy对象），调用PathStrategy.getUriForFile（File）：Uri**

- **若缓存HashMap中没有此authority对应的key，则解析xml路径，生成新的PathStrategy对象（实际为SimplePathStrategy对象），将其放入的HashMap中**

- **SimplePathStrategy类**

  - **String mAuthority：存放对应的Authority**
  - **HashMap<String, File> mRoots = new HashMap<>()：key实际存放FileProvider配置路径xml中写的name，value实际存放配置路径xml中写的path（提前转换为了完整路径）**

- SimplePathStrategy对象的初始化

  - 根据传递进来的authority初始化mAuthority变量

    - 构造函数

    - ```java
      SimplePathStrategy strat = new SimplePathStrategy(authority);
      ```

  - 解析FileProvider下的xml路径配置，初始化mRoots ：HashMap<String,File>

    - 获取Xml解析对象输入流

      - XmlResourceParser in

      - ```Java
        final ProviderInfo info = context.getPackageManager()
                .resolveContentProvider(authority, PackageManager.GET_META_DATA);
        final XmlResourceParser in = getFileProviderPathsMetaData(context, authority, info,
                resourceId);
        ```

    - 循环遍历获取tag、name、path值

      - tag即声明的files-path、cache-path、external-files-path等

      - name即自定义的名称（name对应的value）

      - path即path对应的value

      - tag = external-files-path、name=aaa、path=.

      - ```
        <external-files-path
            name="aaa"
            path="."/>
        ```

      - ```java
        final String tag = in.getName();
        final String name = in.getAttributeValue(null, ATTR_NAME);
        String path = in.getAttributeValue(null, ATTR_PATH);
        ```

    - **根据解析到的tag值、path值，遍历判断，构造target：File**

      - **可以根据这些判断，明白xml中tag对应真实路径的映射**

      - **倘若target=external-files-path，此时target路径为/storage/emulated/0/Android/data/包名/files/**

      - ```java
        File target = null;
        if (TAG_ROOT_PATH.equals(tag)) {
            target = DEVICE_ROOT;
        } else if (TAG_FILES_PATH.equals(tag)) {
            target = context.getFilesDir();
        } else if (TAG_CACHE_PATH.equals(tag)) {
            target = context.getCacheDir();
        }......
        ```

      - 根据path值，再次追加到target中

      - **倘若path=abc，此时taget路径为/storage/emulated/0/Android/data/包名/files/abc**

      - ```java
        for (String segment : segments) {
            if (segment != null) {
                target = new File(target, segment);
            }
        }
        ```

      - 将xml中配置的name作为key，target作为value，放入到mRoots ：HashMap<String,File>中

      - ![image.png](https://s2.loli.net/2024/10/15/fzmbgtWvxCTBVn6.png)
  
      
  
- **获取到新的SimplePathStrategy对象，将其与Authority放入到FileProvider的HashMap中，调用SimplePathStrategy对象的getUriForFile（File）：Uri**

  - 依据传入进来的文件（需要转换为uri的文件）的path，从mRoots寻找最佳匹配的键值对

  - ```java
    // Find the most-specific root path
    Map.Entry<String, File> mostSpecific = null;
    for (Map.Entry<String, File> root : mRoots.entrySet()) {
        final String rootPath = root.getValue().getPath();
        if (path.startsWith(rootPath) && (mostSpecific == null
                || rootPath.length() > mostSpecific.getValue().getPath().length())) {
            mostSpecific = root;
        }
    }
    ```

  - 构造出Uri

  - ```java
    final String rootPath = mostSpecific.getValue().getPath();
    if (rootPath.endsWith("/")) {
        path = path.substring(rootPath.length());
    } else {
        path = path.substring(rootPath.length() + 1);
    }
    
    // Encode the tag and path separately
    path = Uri.encode(mostSpecific.getKey()) + '/' + Uri.encode(path, "/");
    return new Uri.Builder().scheme("content")
         .authority(mAuthority).encodedPath(path).build();
    ```

- 其他程序拿到content://格式的uri，如何去寻找到对应的文件File？

  - openFileDescriptor拿到ParcelFileDescriptor文件描述符，去读取文件

  - ```java
    ContentResolver.openFileDescriptor
    ```

- **反而我觉得应该更多的了解`ContentResolver`，`FileDescriptor`，`ParcelFileDescriptor`的使用，这是`FileProvider`的基础知识**

#### ContentProvider

##### 定义

- **Android提供的专门用于不同应用之间、不同进程之间数据共享交互的方式**
- **系统本事预置了很多ContentProvider、如联系人ContentProvider，访问这些信息需要借助ContentResolve对数据进行增删改查即可**

##### 使用

###### 同一个进程下访问

1. 创建sqlite数据库、构建user表

   - DB_NAME = test.db

   - DB_VERSION = 1

   - TABLE_NAME = user  //表名称

     - column_id ： user_id

     - column_name ：name

       ```kotlin
       class MyDBHelper(val context: Context): SQLiteOpenHelper(context, DB_NAME,null, DB_VERSION) {
       
           companion object {
               const val DB_NAME = "test.db"
               const val DB_VERSION = 1
       
               const val TABLE_NAME = "user"
               const val column_id = "user_id"
               const val column_name = "name"
       
           }
       
           override fun onCreate(db: SQLiteDatabase?) {
               db?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME($column_id INTEGER PRIMARY KEY AUTOINCREMENT,$column_name VARCHAR NOT NULL);")
           }
       
           override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
               db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME;");
               onCreate(db);
           }
       }
       ```

2. **自定义ContentProvider实现对User表的增删改查**

   - **自定义MyContentProvider类继承ContentProvider、需要重写方法**

     - **onCreate()：Boolean**

       - **contentProvider是否初始化成功**
       - **注意：此回调会先于Application中的onCreate回调，故不可再此回调中引用Myapplication中的context，导致空指针异常。可在Myapplication中的attachBaseContext对全局context对象赋值，attachBaseContext早于ContentProvider的onCreate方法**

     - **query（Uri，projection：Array<out String>?，selection: String?，selectionArgs: Array<out String>?,sortOrder: String?）**：**Cursor**

       - **查询**
       - **uri：查询的uri**
       - **projection：查询的列名，null即查询全部列**
       - **selection：where条件，null即不过滤**
       - **selectionArgs：where条件中带？号实际参数**
       - **sortOrder：排序方式**
       - **Cursor：返回游标对象**

     - **insert（Uri，ContentValues）：Uri**

       - **插入**

       - **uri：插入的uri**

       - **ContentValues：插入的值**

         - **ContentValues实际内部维护ArrayMap、HashMap**

         - **插入列名称name=张三的数据**

           ```kotlin
           val contentValue = ContentValues()
           contentValue.put("name",张三)
           ```

       - **uri：返回插入成功的新数据的uri**

     - **delete（uri，selection: String?, selectionArgs: Array<out String>?）：Int**

       - **uri：删除的uri**
       - **selselection：where条件**
       - **selectionArgs：where条件中？替代参数**
       - **int：返回受影响的行数**

     - **update（uri，ContentValues？，selection: String?,**
               **selectionArgs: Array<out String>?）：Int**

       - **uri：更新的uri**
       - **ContentValues：更新的列值**
       - **selselection：where条件**
       - **selectionArgs：where条件中？替代参数**
       - **int：返回受影响的行数**

     - getType（uri）：String

       - uri：获取类型的uri
       - String：根据uri获取对应的类型字符串
       - 一般对于数据表操作记录
         - 如果是单条记录应该返回以vnd.android.cursor.item/ 为首的字符串
         - 如果是多条记录，应该返回vnd.android.cursor.dir/ 为首的字符串
       - 一般对于文件操作（FileProvider）
         - application/octet-stream
       - 回调时机
         - **当我们startActivity时，隐式跳转（未显示设置跳转的class），设置了data，并没有设置type，此时系统需要知道应该用哪些程序去打开，就会回调contentProvider中的此回调函数，获取具体的type，来用相应的应用去跳转**

     - UriMatcher类

       - Uri匹配命中工具类

       - **对数据表的操作需要传递对应操作uri，此时可能会对某个行进行修改，可能会对整个表进行操作，传递过来的uri是不一致的，此时我们就需要匹配出对应的操作码，执行对应的逻辑操作**

       - 查询整张表、查询id = 2的行

         ```kotlin
         content://com.jin.learn.mycontentprovider/user/2
         content://com.jin.learn.mycontentprovider/user
         ```

         - **此时在自定义ContentProvider中的query方法需要区分uri，以此来书写不同的查询逻辑（借助uriMatcher类）**

         - **uri匹配到了user 即返回code 0、uri匹配到了user/# 即返回code 1**

           ```kotlin
           private var uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
                 addURI("com.jin.learn.mycontentprovider","user",0)
               addURI("com.jin.learn.mycontentprovider","user/#",1)
           }
           ```

         - **此时在query方法中即可以通过匹配响应码code，来判断是查询单条记录还是整张表**

           - ```kotlin
             return when(uriMatcher.match(uri)) {
                 1 -> {
                     db?.query(
                         MyDBHelper.TABLE_NAME,
                         projection,
                         selection,
                         selectionArgs,
                         null,
                         null,
                         sortOrder
                     )
             
                 }
                 0 -> {
                 db?.query(MyDBHelper.TABLE_NAME,projection,"${MyDBHelper.column_id}=${uri.lastPathSegment}",selectionArgs,null,null,sortOrder)
                 }
                 else -> {
                     null
                 }
             }
             ```

   - **onCreate回调中对数据库操作工具对象进行初始化**

     - **myDBHelper：MyDBHelper、   db：SQLiteDatabase（增删改查媒介）**

     - ```kotlin
       override fun onCreate(): Boolean {
           myDBHelper = context?.let { MyDBHelper(it) }
           db = myDBHelper?.readableDatabase
           return true
       }
       ```

   - 完整代码

     - ```kotlin
       class MyContentProvider: ContentProvider() {
       
           private var myDBHelper: MyDBHelper? = null
           private var db: SQLiteDatabase? = null
       
           companion object {
               const val AUTOHORITY = "com.jin.learn.mycontentprovider"
       
               const val OPERATION_ITEM = 0
               const val OPERATION_GROUP = 1
           }
       
           private var uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
               addURI(AUTOHORITY,MyDBHelper.TABLE_NAME,OPERATION_GROUP)
               addURI(AUTOHORITY,"${MyDBHelper.TABLE_NAME}/#",OPERATION_ITEM)
           }
       
       
           override fun onCreate(): Boolean {
               myDBHelper = context?.let { MyDBHelper(it) }
               db = myDBHelper?.readableDatabase
               return true
           }
       
           override fun query(
               uri: Uri,
               projection: Array<out String>?,
               selection: String?,
               selectionArgs: Array<out String>?,
               sortOrder: String?
           ): Cursor? {
               return when(uriMatcher.match(uri)) {
                   OPERATION_GROUP -> {
                       db?.query(
                           MyDBHelper.TABLE_NAME,
                           projection,
                           selection,
                           selectionArgs,
                           null,
                           null,
                           sortOrder
                       )
       
                   }
                   OPERATION_ITEM -> {
                       db?.query(MyDBHelper.TABLE_NAME,projection,"${MyDBHelper.column_id}=${uri.lastPathSegment}",selectionArgs,null,null,sortOrder)
                   }
                   else -> {
                       null
                   }
               }
           }
       
           override fun getType(uri: Uri): String? {
               return when(uriMatcher.match(uri)) {
                   OPERATION_GROUP -> {
                       "vnd.android.cursor.dir/user"
                   }
       
                   OPERATION_ITEM -> {
                       "vnd.android.cursor.item/user"
                   }
       
                   else -> {
                       throw IllegalArgumentException("getType unknown match uri$uri")
                   }
               }
           }
       
           override fun insert(uri: Uri, values: ContentValues?): Uri? {
               when(uriMatcher.match(uri)) {
                   OPERATION_GROUP -> {
                       var insertId = db?.insert(MyDBHelper.TABLE_NAME, null, values)
                       if (insertId != null && insertId > 0) {
                           //插入成功
                           var newDataUri = ContentUris.withAppendedId(uri, insertId)
                           context?.contentResolver?.notifyChange(newDataUri,null)
                           return newDataUri
                       }
                   }
               }
               throw IllegalArgumentException("insert unknown match uri:$uri")
           }
       
           override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
               return 0
           }
       
           override fun update(
               uri: Uri,
               values: ContentValues?,
               selection: String?,
               selectionArgs: Array<out String>?
           ): Int {
               return 0
           }
       }
       ```

3. **AndroidManifest中配置自定义ContentProvider**

   - **authorities：授权字符串，提供访问的授权字符串，访问者需携带该授权码访问数据**

   - **name：提供访问的具体操作逻辑类**

   - **exported：是否对外暴露该contentProvider**

   - ```kotlin
     <provider
      android:authorities="com.jin.learn.mycontentprovider"
         android:name=".provider.MyContentProvider"
         android:exported="true"
         />
     ```

4. **本地进程内访问该contentProvider对user表进行增删改查（本质都是调用context.getContextResolver()对象的query、delete、update、insert方法）**

   - 插入数据

   - ```kotlin
     private fun insertData(name: String) {
         var uri = Uri.parse("content://com.jin.learn.mycontentprovider/user")
         val contentValue = ContentValues()
         contentValue.put(MyDBHelper.column_name,name)
         contentResolver.insert(uri,contentValue)
     }
     ```

   - 查询全部数据

   - ```kotlin
     private fun queryData() {
         val userDatas = mutableListOf<User>()
     
         var uri = Uri.parse("content://com.jin.learn.mycontentprovider/user")
         var cursor = contentResolver.query(uri, null, null, null, null, null)
         cursor?.let {
             while (it.moveToNext()) {
                 var name = it.getString(it.getColumnIndex(MyDBHelper.column_name))
                 var id = it.getInt(it.getColumnIndex(MyDBHelper.column_id))
                 val user = User(id.toString(),name)
                 userDatas.add(user)
             }
         }
         userTableAdapter?.updateDatas(userDatas)
     }
     ```

   - 查询单条数据

   - ```kotlin
     private fun queryDataFor2() {
         val userDatas = mutableListOf<User>()
         var uri = Uri.parse("content://com.jin.learn.mycontentprovider/user/2")
         var cursor = contentResolver.query(uri, null, null, null, null, null)
         cursor?.let {
             while (it.moveToNext()) {
                 var name = it.getString(it.getColumnIndex(MyDBHelper.column_name))
                 var id = it.getInt(it.getColumnIndex(MyDBHelper.column_id))
                 val user = User(id.toString(),name)
                 userDatas.add(user)
             }
         }
         userTableAdapter?.updateDatas(userDatas)
     }
     ```

   ###### 进程间通信

   - 客户端进程需要在AndroidManifest中声明客户端暴露出来的contentProvider

     - ```xml
       <!--Android版本升级后权限细化-->
       <queries>
           <provider android:authorities="com.jin.learn.mycontentprovider"/>
       </queries>
       ```

   - 客户端插入数据基本与同一个进程下操作代码一致

     - ```kotlin
       private fun insertData(name: String) {
           var uri = Uri.parse("content://com.jin.learn.mycontentprovider/user")
           val contentValue = ContentValues()
           contentValue.put("name",name)
           var newDataUri = contentResolver.insert(uri, contentValue)
           if (newDataUri == null) {
               Toast.makeText(this,"插入失败",Toast.LENGTH_LONG).show()
           } else {
               Toast.makeText(this,"插入成功",Toast.LENGTH_LONG).show()
           }
       }
       ```

   - **注意：~~提供自定义的ContentProvider的服务端需要启动运行中，此时服务端再次查询全部数据会发现客户端新插入的数据~~（服务端无需启动运行）**

   - ![tutieshi_640x382_31s.gif](https://s2.loli.net/2024/10/15/K7GUolFuJADX2gd.gif)

   - **数据表插入记录监听**

     - **自定义ContentProvider中插入方法需调用（数据提供者即服务端）**

       - **notifyChange将通知发送**

       - ```kotlin
         //插入成功
         var newDataUri = ContentUris.withAppendedId(uri, insertId)
         context?.contentResolver?.notifyChange(newDataUri,null)
         return newDataUri
         ```

     - **客户端注册监听**

       - **contentResolver.registerContentObserver（uri，Boolean，ContentObserver）**

         - **uri：需要监听的uri**
         - **Boolean**
           - **false：精确匹配、只匹配此uri的数据监听**
           - **true：模糊匹配，此uri以及此uri下的路径数据监听变化**
         - **ContentObserver：监听逻辑类**

       - ```kotlin
         var uri = Uri.parse("content://com.jin.learn.mycontentprovider/user")
         contentResolver.registerContentObserver(uri,true,object : ContentObserver(Handler(Looper.getMainLooper())) {
             override fun onChange(selfChange: Boolean) {
                 super.onChange(selfChange)
                 Toast.makeText(this@MainActivity,"onChange1",Toast.LENGTH_LONG).show()
             }
         })
         ```

