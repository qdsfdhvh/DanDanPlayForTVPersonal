package com.seiko.player.data.api.model

import com.squareup.moshi.JsonClass
import java.io.File

//MatchRequest {
//fileName (string, optional): 视频文件名，不包含文件夹名称和扩展名，特殊字符需进行转义。 ,
//fileHash (string, optional): 文件前16MB (16x1024x1024 Byte) 数据的32位MD5结果，不区分大小写。 ,
//fileSize (integer): 文件总长度，单位为Byte。 ,
//videoDuration (integer): [可选]32位整数的视频时长，单位为秒。默认为0。 ,
//matchMode (string): [可选]匹配模式。 = ['hashAndFileName', 'fileNameOnly', 'hashOnly']
//}
@JsonClass(generateAdapter = true)
class MatchRequest(
    var fileName: String = "",
    var fileHash: String = "",
    var fieSize: Long = 0,
    var videoDuration: Int = 0,
    var matchMode: String = ""
) {
    companion object {
        private const val MATCH_MODE_DETAIL = "hashAndFileName"
        private const val MATCH_MODE_FILE_NAME_ONLY = "fileNameOnly"
        private const val MATCH_MODE_HASH_ONLY = "hashOnly"

        fun hash(fileHash: String) = MatchRequest(
            fileHash = fileHash,
            fileName = "a",
            matchMode = MATCH_MODE_HASH_ONLY)

        fun name(fileName: String) = MatchRequest(
            fileName = fileName,
            fileHash = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
            matchMode = MATCH_MODE_FILE_NAME_ONLY)

        fun detail(fileName: String, fileHash: String, fileSize: Long, videoDuration: Int) = MatchRequest(
            fileName = fileName,
            fileHash = fileHash,
            fieSize = fileSize,
            videoDuration = videoDuration,
            matchMode = MATCH_MODE_DETAIL)
    }
}