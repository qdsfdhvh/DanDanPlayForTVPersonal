package com.seiko.player.util

import android.app.Activity
import android.app.Service
import android.content.Context

fun Context.checkCpuCompatibility() {
    runBackground(Runnable {
        if (!VLCInstance.testCompatibleCPU(this)) {
            runOnMainThread(Runnable {
                when(this) {
                    is Service -> stopSelf()
                    is Activity -> finish()
                }
            })
        }
    })
}