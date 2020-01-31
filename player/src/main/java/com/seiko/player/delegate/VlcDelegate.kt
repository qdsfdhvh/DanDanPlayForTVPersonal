package com.seiko.player.delegate

import android.content.Context
import com.seiko.player.util.AppScope
import com.seiko.player.util.VLCInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.videolan.libvlc.Dialog
import org.videolan.libvlc.FactoryManager
import org.videolan.libvlc.LibVLCFactory
import org.videolan.libvlc.MediaFactory
import org.videolan.libvlc.interfaces.ILibVLCFactory
import org.videolan.libvlc.interfaces.IMediaFactory
import timber.log.Timber
import kotlin.concurrent.thread

object VlcDelegate {
    fun init(context: Context) {
        // Service loaders
        FactoryManager.registerFactory(IMediaFactory.factoryId, MediaFactory())
        FactoryManager.registerFactory(ILibVLCFactory.factoryId, LibVLCFactory())

//        AppScope.launch(Dispatchers.IO) {
//            val success = VLCInstance.testCompatibleCPU(context)
////            Dialog.setCallbacks(VLCInstance(context), DialogDelegate)
//        }
    }
}