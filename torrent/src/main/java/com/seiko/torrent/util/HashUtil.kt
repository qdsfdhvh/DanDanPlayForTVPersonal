package com.seiko.torrent.util

import android.net.Uri
import android.webkit.URLUtil
import com.seiko.torrent.extensions.isHash
import com.seiko.torrent.extensions.isMagnet
import java.io.File

private const val INFO_HASH_PREFIX = "magnet:?xt=urn:btih:"

/**
 * 将字符转成支持的Uri
 * PS: 无效或不支持的字符返回null
 */
internal fun buildTorrentUri(source: String?): Uri? {
    if (source.isNullOrEmpty()) return null

    if (source.isMagnet()) return Uri.parse(source)

    if (source.isHash()) return Uri.parse(INFO_HASH_PREFIX + source)

    if (URLUtil.isNetworkUrl(source)) return Uri.parse(source)

    if (URLUtil.isFileUrl(source)) return Uri.fromFile(File(source))

    if (URLUtil.isContentUrl(source)) return Uri.parse(source)

    return null
}