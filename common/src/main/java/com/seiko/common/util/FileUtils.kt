package com.seiko.common.util

import okio.buffer
import okio.sink
import okio.source
import java.io.ByteArrayInputStream
import java.io.File
import java.io.IOException
import java.io.InputStream


@Throws(IOException::class)
fun File.writeByteArray(bytes: ByteArray) {
    val sink = this.sink().buffer()
    val source = ByteArrayInputStream(bytes).source().buffer()
    sink.writeAll(source)
    sink.flush()
    sink.close()
    source.close()
}

@Throws(IOException::class)
fun File.writeInputStream(inputStream: InputStream?) {
    if (inputStream == null) return

    val sink = this.sink().buffer()
    val source = inputStream.source().buffer()
    sink.writeAll(source)
    sink.flush()
    sink.close()
    source.close()
}