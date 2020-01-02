package com.seiko.data.constants

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
//// 番剧封面缓存路径
//val DEFAULT_IMAGE_PATH = "${DEFAULT_DOWNLOAD_PATH}_image$SEP"
// 配置路径
private val DEFAULT_CONFIG_PATH = "${DEFAULT_DOWNLOAD_PATH}_config$SEP"

// 下载任务恢复文件路径
val DEFAULT_TORRENT_RESUME_FILE = "$DEFAULT_CONFIG_PATH.resume"
// 下载引擎配置路径
val DEFAULT_TORRENT_SESSION_FILE = "$DEFAULT_CONFIG_PATH.session"
// Tracker储存位置
val DEFAULT_TRACKER_FILE = "${DEFAULT_CONFIG_PATH}config.txt"

// 默认种子下文文件名
const val DEFAULT_TORRENT_FOLDER = "_torrent"
// 默认弹幕下载文件夹名
const val DEFAULT_DANMU_FOLDER  = "_danmuku"
// 默认字幕下载文件夹名
const val DEFAULT_SUBTITLE_FOLDER = "_subtitle"

//
internal const val PREFS_NAME_DEFAULT = "DanDanPlayForTV_Prefs"
internal const val PREFS_NAME_COOKIES = "DanDanPlayForTV_Cookies_Prefs"
internal const val DB_NAME_DEFAULT = "DanDanPlayForTV_Database"