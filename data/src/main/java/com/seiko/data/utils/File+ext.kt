package com.seiko.data.utils

import com.blankj.utilcode.util.LogUtils
import java.io.File
import java.util.*

//const val TYPE_FOLDER = 0
const val TYPE_FILE = 1
const val TYPE_VIDEO = 2
const val TYPE_AUDIO = 13
const val TYPE_PICTURE = 14
const val TYPE_SUBTITLE = 15
//const val TYPE_TXT = 25

/** 视频格式  */
val MIME_TYPE_VIDEO_ALL = arrayOf(
    "3gp", "3gpp", "divx", "h264", "avi", "m2ts", "mkv", "mov", "mp2", "mp4", "mpg",
    "mpeg", "wmv", "ts", "tp", "vob", "flv", "vc1", "m4v", "f4v", "asf", "lst",
    "lsf", "lsx", "mng", "asx", "wm", "wmx", "wvx", "movie", "3g2", "dl", "dif",
    "dv", "fli", "qt", "mxu", "webm", "mkv", "rmvb")

/** 音频格式 */
val MIME_TYPE_MUSIC = arrayOf("mp3", "wma", "m4a",
    "aac", "ape", "ogg", "flac", "alac", "wav", "mid", "xmf",
    "mka", "pcm", "adpcm", "snd", "midi", "kar", "mpga",
    "mpega", "mp2", "m3u", "sid", "aif", "aiff", "aifc", "gsm",
    "m3u", "wax", "ra", "ram", "pls", "sd2","amr","wv","mmf","m4r")

/** 图片格式 */
val MIME_TYPE_PHOTO = arrayOf("jpg", "jpeg", "bmp",
    "tif", "tiff", "png", "gif", "giff", "jfi", "jpe", "jif",
    "jfif", "cur", "ico", "ief", "jpe", "pcx", "svg", "svgz",
    "djvu", "djv", "wbmp", "ras", "cdr", "pat", "cdt", "cpt",
    "art", "jng", "psd", "pnm", "pbm", "pgm", "ppm", "rgb",
    "xbm", "xpm", "xwd")

/** 字幕格式 */
val MIME_TYPE_SUBTITLE = arrayOf("ssa", "ass", "smi",
    "srt", "sub", "lrc", "sst", "xss", "psb", "ssb", "txt")

/**
 * 获取文件类型类型
 */
fun String.getFileType(): Int {
    val lastPoi = lastIndexOf('.')
    val lastSep = lastIndexOf(File.separator)
    if (lastPoi >= 0 && lastSep < lastPoi) {
        return when(substring(lastPoi + 1).toLowerCase(Locale.US)) {
            in MIME_TYPE_VIDEO_ALL -> TYPE_VIDEO
            in MIME_TYPE_SUBTITLE -> TYPE_SUBTITLE
            in MIME_TYPE_MUSIC -> TYPE_AUDIO
            in MIME_TYPE_PHOTO -> TYPE_PICTURE
            else -> TYPE_FILE
        }
    }
    return TYPE_FILE
}