package com.seiko.torrent.service

import com.seiko.common.data.Result
import com.seiko.download.torrent.model.MagnetInfo
import com.seiko.download.torrent.model.TorrentMetaInfo
import com.seiko.download.torrent.model.TorrentSessionStatus
import com.seiko.download.torrent.model.TorrentTask

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
     * 停止解析磁力
     * PS: 引擎不能同时解析多个磁力
     */
    fun cancelFetchMagnet(hash: String)

    /**
     * 添加种子
     */
    suspend fun addTorrent(task: TorrentTask, isFromMagnet: Boolean): Result<Boolean>

    /**
     * 删除种子任务
     */
    suspend fun deleteTorrent(hash: String, withFile: Boolean)

    /**
     * 重启/暂停 种子任务
     */
    fun pauseResumeTorrent(hash: String)

    /**
     * 监听目标种子的进度
     */
    fun onProgressChanged(hash: String, function: (item: TorrentSessionStatus) -> Unit)

    /**
     * 注销 监听目标种子的进度
     */
    fun disposeDownload(hash: String)

    /**
     * 释放资源
     */
    fun release()

    /**
     * 获取种子信息
     */
    fun getTorrentMetaInfo(hash: String): TorrentMetaInfo?

}