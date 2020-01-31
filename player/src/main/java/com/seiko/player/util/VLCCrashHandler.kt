package com.seiko.player.util

import android.os.Environment
import android.text.format.DateFormat
import android.util.Log
import timber.log.Timber
import java.io.*


class VLCCrashHandler(private val path: String) : Thread.UncaughtExceptionHandler {

    companion object {
        private const val TAG = "VLC/VlcCrashHandler"

        @Throws(IOException::class)
        fun writeLogcat(filename: String) {
            val args = arrayOf("logcat", "-v", "time", "-d")

            val process = Runtime.getRuntime().exec(args)

            val input = InputStreamReader(process.inputStream)

            val fileStream: FileOutputStream
            try {
                fileStream = FileOutputStream(filename)
            } catch (e: FileNotFoundException) {
                return
            }

            val output = OutputStreamWriter(fileStream)
            val br = BufferedReader(input)
            val bw = BufferedWriter(output)

            try {
                var line = br.readLine()
                while (line != null) {
                    bw.write(line)
                    bw.newLine()
                    line = br.readLine()
                }
            } catch (e: Exception) {
            } finally {
                CloseableUtils.close(bw)
                CloseableUtils.close(output)
                CloseableUtils.close(br)
                CloseableUtils.close(input)
            }
        }
    }

    private val defaultUEH: Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()!!

    override fun uncaughtException(thread: Thread, ex: Throwable) {

        val result = StringWriter()
        val printWriter = PrintWriter(result)

        // Inject some info about android version and the device, since google can't provide them in the developer console
        val trace = ex.stackTrace
        val trace2 = arrayOfNulls<StackTraceElement>(trace.size + 3)
        System.arraycopy(trace, 0, trace2, 0, trace.size)
        trace2[trace.size + 0] = StackTraceElement("Android", "MODEL", android.os.Build.MODEL, -1)
        trace2[trace.size + 1] = StackTraceElement("Android", "VERSION", android.os.Build.VERSION.RELEASE, -1)
        trace2[trace.size + 2] = StackTraceElement("Android", "FINGERPRINT", android.os.Build.FINGERPRINT, -1)
        ex.stackTrace = trace2

        ex.printStackTrace(printWriter)
        val stacktrace = result.toString()
        printWriter.close()
        Timber.tag(TAG).e(stacktrace)

        // Save the log on SD card if available
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            writeLog(stacktrace, "$path/vlc_crash")
            writeLogcat("$path/vlc_logcat")
        }

        defaultUEH.uncaughtException(thread, ex)
    }


    private fun writeLog(log: String, name: String) {
        val timestamp = DateFormat.format("yyyyMMdd_kkmmss", System.currentTimeMillis())
        val filename = name + "_" + timestamp + ".log"

        val stream: FileOutputStream
        try {
            stream = FileOutputStream(filename)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return
        }

        val output = OutputStreamWriter(stream)
        val bw = BufferedWriter(output)

        try {
            bw.write(log)
            bw.newLine()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            CloseableUtils.close(bw)
            CloseableUtils.close(output)
        }
    }

    private fun writeLogcat(name: String) {
        val timestamp = DateFormat.format("yyyyMMdd_kkmmss", System.currentTimeMillis())
        val filename = name + "_" + timestamp + ".log"
        try {
            VLCCrashHandler.writeLogcat(filename)
        } catch (e: IOException) {
            Timber.tag(TAG).e("Cannot write logcat to disk")
        }

    }
}
