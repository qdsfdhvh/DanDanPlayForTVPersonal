package com.seiko.torrent.extensions

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context

/**
 * 获得当前系统复制的内容
 */
fun Context.getClipboard(): String? {
    val clipboard = getSystemService(Activity.CLIPBOARD_SERVICE) as? ClipboardManager ?: return null
    if (!clipboard.hasPrimaryClip()) return null
    val clip = clipboard.primaryClip
    if (clip == null || clip.itemCount == 0) return null
    return clip.getItemAt(0).text.toString()
}
