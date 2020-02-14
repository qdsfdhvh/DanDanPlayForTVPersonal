package com.seiko.download.torrent.extensions

import org.libtorrent4j.alerts.Alert
import org.libtorrent4j.alerts.TorrentAlert

/**
 * Check if the [Alert] is of type [TorrentAlert].
 */
internal fun Alert<*>.isTorrentAlert() = this is TorrentAlert


/**
 * Check if the [Alert] has a valid [@see TorrentHandle].
 */
internal fun Alert<*>.hasValidTorrentHandle() = (this as? TorrentAlert)
    ?.handle()
    ?.isValid
    ?: false