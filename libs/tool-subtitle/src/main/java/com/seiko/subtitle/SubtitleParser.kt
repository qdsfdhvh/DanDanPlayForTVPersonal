package com.seiko.subtitle

import com.seiko.subtitle.format.*
import com.seiko.subtitle.model.TimedTextObject
import com.seiko.subtitle.util.FileUtil
import com.seiko.subtitle.util.log
import org.mozilla.universalchardet.UniversalDetector
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

internal fun parseSubTitle(file: File): Result<TimedTextObject> {
    if (!file.exists()) {
        return Result.failure(FileNotFoundException("SubTitle not found: " + file.absolutePath))
    }

    val ext = FileUtil.getFileExt(file.absolutePath).toLowerCase(Locale.US)
    val format: TimedTextFileFormat
    format = when(ext) {
        "ass" -> FormatASS()
        "scc" -> FormatSCC()
        "srt" -> FormatSRT()
        "stl" -> FormatSTL()
        "xml" -> FormatTTML()
        else -> {
            return Result.failure(Exception("Not support ext: $ext"))
        }
    }

    return try {
        // 解析编码
        val buf = ByteArray(4096)
        val fis = FileInputStream(file)
        val detector = UniversalDetector(null)
        var size = fis.read(buf)
        while(size > 0 && !detector.isDone) {
            detector.handleData(buf, 0, size)
            size = fis.read(buf)
        }
        detector.dataEnd()
        val charsetName = detector.detectedCharset ?: "UTF-8"
        log("Parse SubTitle Charset：$charsetName")

        // 解析字幕
        val tto = format.parseFile(file.absolutePath, FileInputStream(file), charsetName)
        Result.success(tto)
    } catch (e: IOException) {
        Result.failure(e)
    }
}