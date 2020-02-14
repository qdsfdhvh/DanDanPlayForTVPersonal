package com.seiko.download.torrent.extensions

import com.seiko.download.torrent.TorrentEngine
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

private val torrentEngineExecutor by lazy {
    val threadFactory = object : ThreadFactory {
        private val count = AtomicInteger(1)
        override fun newThread(r: Runnable): Thread {
            return Thread(r, "Torrent-Thread-${count.getAndIncrement()}")
        }
    }
    ThreadPoolExecutor(
        0, Int.MAX_VALUE,
        60L, TimeUnit.SECONDS,
        SynchronousQueue<Runnable>(),
        threadFactory)
}

internal fun execute(runnable: Runnable) {
    torrentEngineExecutor.execute(runnable)
}