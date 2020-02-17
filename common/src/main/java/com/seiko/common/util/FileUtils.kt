package com.seiko.common.util

import okio.buffer
import okio.sink
import okio.source
import java.io.*


@Throws(IOException::class)
fun File.writeByteArray(bytes: ByteArray) {
    val source = ByteArrayInputStream(bytes).source().buffer()
    val sink = this.sink().buffer()
    sink.writeAll(source)
    sink.flush()
    sink.close()
    source.close()
}

@Throws(IOException::class)
fun File.writeInputStream(inputStream: InputStream?) {
    if (inputStream == null) return

    val source = inputStream.source().buffer()
    val sink = this.sink().buffer()
    sink.writeAll(source)
    sink.flush()
    sink.close()
    source.close()
}