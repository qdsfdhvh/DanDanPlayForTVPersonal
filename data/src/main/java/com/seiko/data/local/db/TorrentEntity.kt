package com.seiko.data.local.db

import com.seiko.download.task.TorrentTask
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class TorrentEntity(
    @Id var id: Long = 0,
    override val hash: String,
    override var title: String,
    override var torrentPath: String,
    override var saveDirPath: String
): TorrentTask(hash, title, torrentPath, saveDirPath)