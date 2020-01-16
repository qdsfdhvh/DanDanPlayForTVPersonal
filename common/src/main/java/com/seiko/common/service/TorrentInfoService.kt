package com.seiko.common.service

/**
 * 从Torrent服务中获得hash的文件下载路径
 */
interface TorrentInfoService {

    /**
     * 获得指定hash的下载路径
     */
    fun findDownloadPaths(hash: String): List<String>

    /**
     * 获得指定hash集合的下载路径
     */
    fun findDownloadPaths(hashSet: Set<String>): List<String>

}