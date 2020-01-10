package com.seiko.module.torrent.service

import com.seiko.data.utils.Result
import com.seiko.module.torrent.model.DownloadProgress
import com.seiko.torrent.model.MagnetInfo
import com.seiko.torrent.model.TorrentMetaInfo
import com.seiko.torrent.model.TorrentTask

interface Downloader {

    /**
     * 重启已有的种子任务
     */
    suspend fun restoreDownloads()

    /**
     * 解析磁力
     * PS: 直接返回磁力信息数据，异步返回种子信息数据
     */
    suspend fun fetchMagnet(source: String, function: (item: TorrentMetaInfo) -> Unit): MagnetInfo

    /**
     * 添加种子
     */
    suspend fun start(task: TorrentTask, isFromMagnet: Boolean): Result<Boolean>

    /**
     * 重启/暂停 种子任务
     */
    fun pauseResumeTorrent(hash: String)

    /**
     * 监听目标种子的进度
     */
    fun onProgressChanged(hash: String, function: (item: DownloadProgress) -> Unit)

    /**
     * 注销 监听目标种子的进度
     */
    fun disposeDownload(hash: String)

    /**
     * 释放资源
     */
    fun release()

}