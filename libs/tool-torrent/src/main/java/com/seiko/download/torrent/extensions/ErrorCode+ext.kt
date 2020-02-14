package com.seiko.download.torrent.extensions

import org.libtorrent4j.ErrorCode


internal fun ErrorCode?.getErrorMsg(): String {
    return if (this == null) "" else "${message()}, code ${value()}"
}