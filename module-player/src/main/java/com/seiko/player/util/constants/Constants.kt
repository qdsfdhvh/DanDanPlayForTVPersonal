package com.seiko.player.util.constants

/**
 * 域名
 */
internal const val DANDAN_API_BASE_URL = "https://api.acplay.net/"
internal const val SUBTITLE_BASE_URL = "https://dandanplay.com/"

/**
 * 数据库名称
 */
internal const val DB_NAME_DEFAULT = "Seiko_Player_Database"

/**
 * 本地配置文件名
 */
internal const val PREFS_NAME_DEFAULT = "Seiko_Player_Prefs"

/**
 * 进度条最大值
 */
internal const val MAX_VIDEO_SEEK = 1000

/**
 * 无效值
 */
internal const val INVALID_VALUE = -1


const val INTENT_TYPE_VIDEO = "video"

// StartActivity
const val PREF_FIRST_RUN = "first_run"
const val EXTRA_FIRST_RUN = "extra_first_run"
const val EXTRA_UPGRADE = "extra_upgrade"
const val EXTRA_PARSE = "extra_parse"
const val EXTRA_TARGET = "extra_parse"

// VideoPlayerActivity
const val PLAY_FROM_VIDEOGRID = "com.seiko.player.gui.video.PLAY_FROM_VIDEOGRID"
const val PLAY_FROM_SERVICE = "com.seiko.player.gui.video.PLAY_FROM_SERVICE"
const val EXIT_PLAYER = "com.seiko.player.gui.video.EXIT_PLAYER"
const val SLEEP_INTENT = "com.seiko.player.SleepIntent"
const val PLAY_EXTRA_ITEM_LOCATION = "item_location"
const val PLAY_EXTRA_SUBTITLES_LOCATION = "subtitles_location"
const val PLAY_EXTRA_ITEM_TITLE = "title"
const val PLAY_EXTRA_FROM_START = "from_start"
const val PLAY_EXTRA_START_TIME = "position"
const val PLAY_EXTRA_OPENED_POSITION = "opened_position"
const val PLAY_DISABLE_HARDWARE = "disable_hardware"

// MediaParsingService
const val ACTION_INIT = "medialibrary_init"

// PlaybackService
const val ACTION_REMOTE_GENERIC = "com.seiko.player.remote."
const val EXTRA_SEARCH_BUNDLE = "${ACTION_REMOTE_GENERIC}extra_search_bundle"
const val ACTION_PLAY_FROM_SEARCH = "${ACTION_REMOTE_GENERIC}play_from_search"
const val ACTION_REMOTE_SWITCH_VIDEO = "${ACTION_REMOTE_GENERIC}SwitchToVideo"
const val ACTION_REMOTE_LAST_PLAYLIST = "${ACTION_REMOTE_GENERIC}LastPlaylist"
const val ACTION_REMOTE_FORWARD = "${ACTION_REMOTE_GENERIC}Forward"
const val ACTION_REMOTE_STOP = "${ACTION_REMOTE_GENERIC}Stop"
const val ACTION_REMOTE_PLAYPAUSE = "${ACTION_REMOTE_GENERIC}PlayPause"
const val ACTION_REMOTE_PLAY = "${ACTION_REMOTE_GENERIC}Play"
const val ACTION_REMOTE_BACKWARD = "${ACTION_REMOTE_GENERIC}Backward"
const val ACTION_CAR_MODE_EXIT = "android.app.action.EXIT_CAR_MODE"
const val PLAYLIST_TYPE_AUDIO = 0
const val PLAYLIST_TYPE_VIDEO = 1
const val MEDIALIBRARY_PAGE_SIZE = 500