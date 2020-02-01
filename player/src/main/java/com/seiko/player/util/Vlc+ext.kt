package com.seiko.player.util

import android.app.Activity
import android.app.Service
import android.content.Context
import org.videolan.libvlc.util.VLCUtil

fun Context.checkCpuCompatibility() {
    runBackground(Runnable {
        if (!VLCUtil.hasCompatibleCPU(this)) {
            runOnMainThread(Runnable {
                when(this) {
                    is Service -> stopSelf()
                    is Activity -> finish()
                }
            })
        }
    })
}