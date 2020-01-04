package com.seiko.torrent.constants

import androidx.annotation.IntDef

internal val PIECE_SIZE = intArrayOf(
    0, 16, 32, 64, 128, 256, 512,
    1024, 2048, 4096, 8192, 16384, 32768
)

internal const val PEER_FINGERPRINT = "Lr" /* called peer id */
internal const val USER_AGENT = "LibreTorrent %s"

internal const val DATA_TORRENT_SESSION_FILE = "session"
internal const val DATA_TORRENT_FILE_NAME = "torrent"

internal const val DEFAULT_PROXY_PORT = 8080

internal const val META_DATA_MAX_SIZE = 2 * 1024 * 1024

