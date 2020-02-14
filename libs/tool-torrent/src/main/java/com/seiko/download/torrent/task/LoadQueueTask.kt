package com.seiko.download.torrent.task

import com.seiko.download.torrent.TorrentEngine
import com.seiko.download.torrent.model.TorrentTask
import java.lang.ref.WeakReference

internal class LoadQueueTask(
    engine: TorrentEngine,
    private val task: TorrentTask
) : Runnable {

    private val engine = WeakReference(engine)

    override fun run() {
        engine.get()?.run {
            download(task)
        }
    }

}