package com.seiko.subtitle

import android.os.Handler
import android.os.HandlerThread
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

typealias OnParsedListener = (Result<TimedTextObject>) -> Unit

private const val TAG = "SubtitleParser"

class SubtitleParser(
    private val subtitlePath: String,
    private val listener: OnParsedListener
) : Runnable {

    fun parse() {
        val handlerThread = HandlerThread("SubtitleParser")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)
        handler.post(this)
    }

    override fun run() {
        parseSubTitle(File(subtitlePath)).let(listener)
    }
}

@Throws(FileNotFoundException::class)
private fun parseSubTitle(file: File): Result<TimedTextObject> {
    if (!file.exists()) {
        throw FileNotFoundException("SubTitle not found: " + file.absolutePath)
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
        log(TAG, "Parse SubTitle Charset：$charsetName")

        // 解析字幕
        val tto = format.parseFile(file.absolutePath, FileInputStream(file), charsetName)
        Result.success(tto)
    } catch (e: IOException) {
        Result.failure(e)
    }
}