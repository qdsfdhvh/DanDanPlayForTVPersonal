package com.seiko.core.constants

import android.os.Environment
import java.io.File

/**
 * 路径配置
 */
// 分割符
private val SEP = File.separator

// 下载路径
val DEFAULT_DOWNLOAD_PATH = "${Environment.getExternalStorageDirectory().absolutePath}${SEP}DanDanPlay$SEP"
// 缓存路径
val DEFAULT_CACHE_FOLDER_PATH = "$DEFAULT_DOWNLOAD_PATH.cache$SEP"


/**
 * DanDan域名
 */
internal const val DANDAN_API_BASE_URL = "https://api.acplay.net/"
internal const val DANDAN_RES_BASE_URL = "http://res.acplay.net/"
internal const val SUBTITLE_BASE_URL = "https://dandanplay.com/"

/**
 * 本地配置文件名
 */
internal const val PREFS_NAME_DEFAULT = "DanDanPlayForTV_Prefs"
internal const val PREFS_NAME_COOKIES = "DanDanPlayForTV_Cookies_Prefs"
internal const val DB_NAME_DEFAULT = "DanDanPlayForTV_Database"


/**
 * Torrent
 */
const val TORRENT_DOWNLOAD_DIR = "TORRENT_DOWNLOAD_DIR"
const val TORRENT_DATA_DIR = "TORRENT_DATA_DIR"
const val TORRENT_TEMP_DIR = "TORRENT_TEMP_DIR"
const val TORRENT_CONFIG_DIR = "TORRENT_CONFIG_DIR"

const val DATA_TORRENT_INFO_FILE_NAME = "torrent"