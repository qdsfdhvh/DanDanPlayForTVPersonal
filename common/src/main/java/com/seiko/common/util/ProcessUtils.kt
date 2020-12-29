package com.seiko.common.util

import android.app.ActivityManager
import android.content.Context
import android.os.Process

object ProcessUtils {

    /**
     * 是否为主进程
     */
    fun isMainProcess(context: Context): Boolean {
        return context.packageName == getProcessName(context)
    }

    /**
     * 获取当前进程名称
     */
    fun getProcessName(context: Context): String? {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses

        if (appProcesses == null || appProcesses.size == 0) {
            return null
        }

        val myPid = Process.myPid()
        appProcesses.forEach { appProcess ->
            if (appProcess.processName == context.packageName) {
                if (appProcess.pid == myPid) {
                    return appProcess.processName
                }
            }
        }
        return null
    }

}