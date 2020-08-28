package com.seiko.common.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri

object VlcUtils {

    private const val VLC_PKG_NAME = "org.videolan.vlc"
    private const val VLC_START_ACTIVITY = "org.videolan.vlc.StartActivity"
    private const val VLC_VIDEO_ACTIVITY = "org.videolan.vlc.gui.video.VideoPlayerActivity"
    private const val VLC_INTENT_ACTION_RESULT = "org.videolan.vlc.player.result"

    private const val EXTRA_TITLE = "title"

    private const val VIDEO_MIME_TYPE = "video/*"

    /**
     * 已安装的vlc包名
     */
    private var installVlcPackageName: String? = null

    /**
     * 是否安装了VLC
     */
    fun isInstall(context: Context): Boolean {
        if (installVlcPackageName != null) {
            return true
        }

        installVlcPackageName = findVlcPackageName(context)
        return installVlcPackageName != null
    }

    fun launchMedia(context: Context) {
        if (installVlcPackageName != null) {
            launchMedia(context, installVlcPackageName!!)
            return
        }

        installVlcPackageName = findVlcPackageName(context)
        if (installVlcPackageName != null) {
            launchMedia(context, installVlcPackageName!!)
        }
    }

    private fun launchMedia(context: Context, packageName: String) {
        val intent = Intent()
        intent.component = ComponentName(packageName, VLC_START_ACTIVITY)
        context.startActivity(intent)
    }

    fun launchVideo(context: Context, uri: Uri, title: String) {
        if (installVlcPackageName != null) {
            launchVideo(context, installVlcPackageName!!, uri, title)
            return
        }

        installVlcPackageName = findVlcPackageName(context)
        if (installVlcPackageName != null) {
            launchVideo(context, installVlcPackageName!!, uri, title)
        }
    }

    private fun launchVideo(context: Context, packageName: String, uri: Uri, title: String) {
        val intent = Intent(VLC_INTENT_ACTION_RESULT)
        intent.component = ComponentName(packageName, VLC_VIDEO_ACTIVITY)
        intent.setDataAndTypeAndNormalize(uri, VIDEO_MIME_TYPE)
        intent.putExtra(EXTRA_TITLE, title)
        context.startActivity(intent)
    }

    /**
     * 寻找VLC
     */
    private fun findVlcPackageName(context: Context): String? {
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledPackages(0)
        packages.forEach { info ->
            if (info.packageName.contains(VLC_PKG_NAME)) {
                return info.packageName
            }
        }
        return null
    }

}