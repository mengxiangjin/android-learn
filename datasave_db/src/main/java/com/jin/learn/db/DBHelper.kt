package com.jin.learn.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast



class DBHelper(val context: Context, val dbName: String, val version: Int) :
    SQLiteOpenHelper(context,dbName,null,version) {


    private val createBook = "create table Book(" +
            " id integer primary key autoincrement," +
            "author text," +
            "price real," +
            "pages integer," +
            "name text)"

    //后添加的创建表
    private val createCategory = "create table Category(" +
            " id integer primary key autoincrement," +
            "category_name text," +
            "category_code integer)"

    //创建数据库回调（下次数据库已经存在，不会再次回调此方法）
    override fun onCreate(db: SQLiteDatabase?) {
        db?.let { db ->
            db.execSQL(createBook)
            db.execSQL(createCategory)
            Toast.makeText(context,"create succeeded",Toast.LENGTH_LONG).show()
        }
    }

    //数据库升级回调，传不同版本号回调此方法
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("drop table if exists Book")
        db?.execSQL("drop table if exists Category")
        onCreate(db)
    }
}