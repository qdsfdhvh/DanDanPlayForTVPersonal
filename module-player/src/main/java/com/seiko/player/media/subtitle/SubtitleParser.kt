package com.seiko.player.media.subtitle

import android.os.Handler
import android.os.HandlerThread
import com.seiko.common.data.Result
import com.seiko.player.media.subtitle.format.*
import com.seiko.player.media.subtitle.model.TimedTextObject
import com.seiko.player.util.FileUtil
import org.mozilla.universalchardet.UniversalDetector
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

typealias OnParsedListener = (Result<TimedTextObject>) -> Unit

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

private fun parseSubTitle(file: File): Result<TimedTextObject> {
    if (!file.exists()) {
        return Result.Error(FileNotFoundException("SubTitle not found: " + file.absolutePath))
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
            return Result.Error(RuntimeException("Not support ext: $ext"))
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
        Timber.d("Parse SubTitle Charset：$charsetName")
        // 解析字幕
        val tto = format.parseFile(file.absolutePath, FileInputStream(file), charsetName)
        Result.Success(tto)
    } catch (e: IOException) {
        Result.Error(e)
    }
}