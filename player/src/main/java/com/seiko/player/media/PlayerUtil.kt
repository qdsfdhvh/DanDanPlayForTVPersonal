package com.seiko.player.media

import android.app.Activity
import android.app.Service
import android.content.Context
import com.seiko.player.util.runBackground
import com.seiko.player.util.runOnMainThread
import org.videolan.libvlc.FactoryManager
import org.videolan.libvlc.LibVLCFactory
import org.videolan.libvlc.MediaFactory
import org.videolan.libvlc.interfaces.ILibVLCFactory
import org.videolan.libvlc.interfaces.IMediaFactory
import org.videolan.libvlc.util.VLCUtil

fun Context.checkCpuCompatibility() {
    runBackground(Runnable {
        if (!VLCUtil.hasCompatibleCPU(this)) {
            runOnMainThread(Runnable {
                when (this) {
                    is Service -> stopSelf()
                    is Activity -> finish()
                }
            })
        }
    })
}

internal fun initVlc() {
    // Service loaders
    FactoryManager.registerFactory(IMediaFactory.factoryId, MediaFactory())
    FactoryManager.registerFactory(ILibVLCFactory.factoryId, LibVLCFactory())
}