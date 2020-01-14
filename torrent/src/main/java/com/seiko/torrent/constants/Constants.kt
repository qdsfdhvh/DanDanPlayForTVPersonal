package com.seiko.torrent.constants

const val INFINITY_SYMBOL = "\u221e"
const val MAGNET_PREFIX = "magnet"
const val HTTP_PREFIX = "http"
const val HTTPS_PREFIX = "https"
const val UDP_PREFIX = "udp"
const val INFOHASH_PREFIX = "magnet:?xt=urn:btih:"
const val FILE_PREFIX = "file"
const val CONTENT_PREFIX = "content"
const val TRACKER_URL_PATTERN = "^(https?|udp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
const val HASH_PATTERN = "\\b[0-9a-fA-F]{5,40}\\b"
const val MAX_HTTP_REDIRECTION = 10
const val MIME_TORRENT = "application/x-bittorrent"

internal const val ASSETS_TRACKER_NAME = "tracker.txt"
internal const val TORRENT_CONFIG_FILE_NAME = "config.txt"