package com.seiko.player.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.seiko.player.ui.CompatErrorActivity
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.videolan.libvlc.FactoryManager
import org.videolan.libvlc.interfaces.ILibVLC
import org.videolan.libvlc.interfaces.ILibVLCFactory
import org.videolan.libvlc.util.VLCUtil
import timber.log.Timber


@ObsoleteCoroutinesApi
object VLCInstance {
    const val TAG = "VLC/UiTools/VLCInstance"

    @SuppressLint("StaticFieldLeak")
    private var sLibVLC: ILibVLC? = null

    private val libVLCFactory = FactoryManager.getFactory(ILibVLCFactory.factoryId) as ILibVLCFactory

    /** A set of utility functions for the VLC application  */
    @Synchronized
    @Throws(IllegalStateException::class)
    operator fun invoke(ctx: Context): ILibVLC {
        if (sLibVLC == null) {
            Thread.setDefaultUncaughtExceptionHandler(VLCCrashHandler(ctx.getExternalFilesDir(null)!!.absolutePath))

            val context = ctx.applicationContext
            if (!VLCUtil.hasCompatibleCPU(context)) {
                Timber.tag(TAG).e(VLCUtil.getErrorMsg())
                throw IllegalStateException("LibVLC initialisation failed: " + VLCUtil.getErrorMsg())
            }

            // TODO change LibVLC signature to accept a List instead of an ArrayList
            sLibVLC = libVLCFactory.getFromOptions(context, VLCOptions.getLibOptions(context))
        }
        return sLibVLC!!
    }

    @Synchronized
    @Throws(IllegalStateException::class)
    fun restart(context: Context) {
        if (sLibVLC != null) {
            sLibVLC!!.release()
            sLibVLC = libVLCFactory.getFromOptions(context.applicationContext, VLCOptions.getLibOptions(context))
        }
    }

    @Synchronized
    fun testCompatibleCPU(context: Context): Boolean {
        return if (sLibVLC == null && !VLCUtil.hasCompatibleCPU(context)) {
            if (context is Activity) {
                val i = Intent(context, CompatErrorActivity::class.java)
                context.startActivity(i)
            }
            false
        } else
            true
    }
}