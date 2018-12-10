package com.hardrubic.music.service

import android.app.ActivityManager
import android.content.Context


object ServiceHelper {

    fun isServiceWork(context: Context, serviceClassName: String): Boolean {
        var isWork = false
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = am.getRunningServices(Integer.MAX_VALUE)
        if (runningServices.size <= 0) {
            return false
        }
        for (index in runningServices.indices) {
            val name = runningServices[index].service.className
            if (name == serviceClassName) {
                isWork = true
                break
            }
        }
        return isWork
    }
}