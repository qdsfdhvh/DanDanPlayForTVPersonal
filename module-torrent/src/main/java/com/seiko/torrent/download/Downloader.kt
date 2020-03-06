package com.seiko.torrent.download

import com.seiko.common.data.Result
import com.seiko.download.torrent.model.MagnetInfo
import com.seiko.download.torrent.model.TorrentMetaInfo
import com.seiko.download.torrent.model.TorrentTask
import com.seiko.torrent.data.model.TorrentListItem
import kotlinx.coroutines.flow.Flow

interface Downloader {

    /**
     * 重启已有的种子任务
     */
    fun restoreDownloads(tasks: Collection<TorrentTask>)

    /**
     * 解析磁力
     * PS: 直接返回磁力信息数据，异步返回种子信息数据
     */
    fun fetchMagnet(source: String, function: (item: TorrentMetaInfo) -> Unit): MagnetInfo

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
    fun deleteTorrent(hash: String, withFile: Boolean)

    /**
     * 重启/暂停 种子任务
     */
    fun pauseResumeTorrent(hash: String)

    /**
     * 释放资源
     */
    fun release()

    /**
     * 获取种子信息
     */
    fun getTorrentMetaInfo(hash: String): TorrentMetaInfo?

    /**
     * 获得持续的种子下载状态
     */
    fun getTorrentStatusList(): Flow<List<TorrentListItem>>
}