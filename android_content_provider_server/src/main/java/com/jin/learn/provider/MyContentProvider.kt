package com.jin.learn.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

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