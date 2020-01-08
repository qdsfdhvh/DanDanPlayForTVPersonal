package com.seiko.module.torrent.service

import com.seiko.torrent.model.TorrentTask
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel

interface Downloader {
    suspend fun pause(task: TorrentTask)
    suspend fun resumeQueue()
    suspend fun pauseQueue()

    fun download(task: TorrentTask)
//    fun onProgressChanged(hash: String, function: (item: String) -> Unit)
    fun disposeDownload(hash: String)
    fun disposeAll()

    @ExperimentalCoroutinesApi
    fun getChannel(url: String): BroadcastChannel<String>?
}