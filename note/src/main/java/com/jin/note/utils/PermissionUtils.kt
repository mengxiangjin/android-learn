package com.jin.note.utils

import android.app.Activity
import android.content.pm.PackageManager

object PermissionUtils {


    const val PERMISSION_CODE_READ_WRITE = 100

    fun applyPermission(activity: Activity,list: List<String>,requestCode: Int) {
        if (!hasPermission(activity,list)) {
            activity.requestPermissions(list.toTypedArray(),requestCode)
        }
    }

    fun hasPermission(activity: Activity,list: List<String>): Boolean {
        var result = true
        list.forEach {
            if (activity.checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED) {
                result = false
            }
        }
        return result
    }
}