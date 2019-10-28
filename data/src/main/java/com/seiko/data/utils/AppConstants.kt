package com.seiko.data.utils

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
// 配置路径
private val DEFAULT_CONFIG_PATH = "${DEFAULT_DOWNLOAD_PATH}_config$SEP"
// 下载任务恢复文件路径
val DEFAULT_TORRENT_RES_PATH = "${DEFAULT_CONFIG_PATH}.resume$SEP"
// 下载引擎配置路径
val DEFAULT_TORRENT_SESSION_PATH = "${DEFAULT_CONFIG_PATH}.session$SEP"
// 番剧封面缓存路径
val DEFAULT_IMAGE_PATH = "${DEFAULT_DOWNLOAD_PATH}_image$SEP"

// Tracker储存位置
val DEFAULT_TRACKER_FILE = "${DEFAULT_CONFIG_PATH}config.txt"

// 默认种子下文文件名
const val DEFAULT_TORRENT_FOLDER = "_torrent"
// 默认弹幕下载文件夹名
const val DEFAULT_DANMU_FOLDER  = "_danmuku"
// 默认字幕下载文件夹名
const val DEFAULT_SUBTITLE_FOLDER = "_subtitle"