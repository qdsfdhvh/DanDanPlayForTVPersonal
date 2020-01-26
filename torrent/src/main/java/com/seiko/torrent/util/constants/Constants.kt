package com.seiko.torrent.util.constants

const val INFINITY_SYMBOL = "\u221e"
const val MAGNET_PREFIX = "magnet"
const val HTTP_PREFIX = "http"
const val HTTPS_PREFIX = "https"
const val UDP_PREFIX = "udp"
const val FILE_PREFIX = "file"
const val CONTENT_PREFIX = "content"
const val TRACKER_URL_PATTERN = "^(https?|udp)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
const val HASH_PATTERN = "\\b[0-9a-fA-F]{5,40}\\b"
const val MAX_HTTP_REDIRECTION = 10
const val MIME_TORRENT = "application/x-bittorrent"

/**
 * 本地配置文件名
 */
internal const val DB_NAME_DEFAULT = "Torrent_Database"

/**
 * 默认种子下载域名
 */
internal const val DOWNLOAD_BASE_URL = "https://m2t.chinacloudsites.cn/"

/**
 * Trackers文件名
 */
internal const val ASSETS_TRACKER_NAME = "tracker.txt"

/**
 * Torrent
 */
internal const val TORRENT_DOWNLOAD_DIR = "TORRENT_DOWNLOAD_DIR"
internal const val TORRENT_DATA_DIR = "TORRENT_DATA_DIR"
internal const val TORRENT_TEMP_DIR = "TORRENT_TEMP_DIR"
internal const val TORRENT_CONFIG_DIR = "TORRENT_CONFIG_DIR"

internal const val DATA_TORRENT_INFO_FILE_NAME = "torrent"
