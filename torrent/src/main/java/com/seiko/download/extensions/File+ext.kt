package com.seiko.download.extensions

import java.io.File
import java.io.FileOutputStream

fun File.saveData(data: ByteArray) {
    val output = FileOutputStream(this, false)
    try {
        output.write(data, 0, data.size)
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            output.close()
        } catch (ignored: Exception) {
        }
    }

}