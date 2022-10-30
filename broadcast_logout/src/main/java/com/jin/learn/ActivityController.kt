package com.jin.learn

import android.app.Activity

object ActivityController {

    private val activities = ArrayList<Activity>()


    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun removeActivity(activity: Activity) {
        activities.remove(activity)
    }

    fun clearActivity() {
        for (item in activities) {
            if (!item.isFinishing) {
                item.finish()
            }
        }
        activities.clear()
    }
}