package com.seiko.common.util

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
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

fun File.getRealUri(context: Context): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", this)
    } else {
        Uri.fromFile(this)
    }
}