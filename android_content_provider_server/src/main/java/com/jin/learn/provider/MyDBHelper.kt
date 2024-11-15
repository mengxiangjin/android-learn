package com.jin.learn.provider

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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