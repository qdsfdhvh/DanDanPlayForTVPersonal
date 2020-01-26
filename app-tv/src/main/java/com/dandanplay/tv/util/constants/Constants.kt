package com.dandanplay.tv.util.constants

import android.os.Environment
import java.io.File

/**
 * DanDan域名
 */
internal const val DANDAN_API_BASE_URL = "https://api.acplay.net/"
internal const val DANDAN_RES_BASE_URL = "http://res.acplay.net/"

/**
 * 数据库名称
 */
internal const val DB_NAME_DEFAULT = "DanDanPlayForTV_Database"

/**
 * 本地配置文件名
 */
internal const val PREFS_NAME_DEFAULT = "DanDanPlayForTV_Prefs"
internal const val PREFS_NAME_COOKIES = "DanDanPlayForTV_Cookies_Prefs"

/**
 * 路径配置
 */
// 分割符
private val SEP = File.separator
// 下载路径
val DEFAULT_DOWNLOAD_PATH = "${Environment.getExternalStorageDirectory().absolutePath}${SEP}DanDanPlay$SEP"
// 缓存路径
val DEFAULT_CACHE_FOLDER_PATH = "$DEFAULT_DOWNLOAD_PATH.cache$SEP"
