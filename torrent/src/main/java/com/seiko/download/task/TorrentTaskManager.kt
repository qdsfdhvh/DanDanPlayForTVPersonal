package com.seiko.download.task

import com.frostwire.jlibtorrent.TorrentHandle
import com.seiko.download.status.Progress
import com.seiko.download.status.StatusHandler

class TorrentTaskManager(
    val task: TorrentTask
) {

    val statusHandler by lazy { StatusHandler(task) }


}