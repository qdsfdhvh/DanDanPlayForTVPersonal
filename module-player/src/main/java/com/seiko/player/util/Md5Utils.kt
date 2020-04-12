package com.seiko.player.util

import com.seiko.player.util.constants.MAX_VIDEO_MD5_SIZE_DEFAULT
import jcifs.smb.SmbFile
import okhttp3.ResponseBody
import java.io.File

/**
 * @param maxLength 前*MB数据的md5
 */
fun File.getVideoMd5(maxLength: Int = MAX_VIDEO_MD5_SIZE_DEFAULT): String {
    return Md5Helper.md5ForInputStream(this.inputStream().buffered(), maxLength)
}

fun SmbFile.getVideoMd5(maxLength: Int = MAX_VIDEO_MD5_SIZE_DEFAULT): String {
    return Md5Helper.md5ForInputStream(this.inputStream.buffered(), maxLength)
}

fun ResponseBody.getVideoMd5(maxLength: Int = MAX_VIDEO_MD5_SIZE_DEFAULT): String {
    return Md5Helper.md5ForInputStream(this.byteStream(), maxLength)
}