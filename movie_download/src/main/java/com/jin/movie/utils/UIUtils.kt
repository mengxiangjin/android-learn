package com.jin.movie.utils

import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatActivity

object UIUtils {


    fun setActivityBarStyle(activity: AppCompatActivity) {
        val decorView = activity.window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN //注释掉这行代码
                //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        decorView.systemUiVisibility = option
        //设置导航栏（顶部和底部）颜色为透明，注释掉这行代码
        //getWindow().setNavigationBarColor(Color.TRANSPARENT);
        //设置通知栏颜色为透明
        activity.window.statusBarColor = Color.TRANSPARENT
        val actionBar = activity.supportActionBar
        actionBar?.hide()
    }
}