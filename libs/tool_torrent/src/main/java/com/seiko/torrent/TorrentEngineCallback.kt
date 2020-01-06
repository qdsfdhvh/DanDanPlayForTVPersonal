package com.seiko.torrent

interface TorrentEngineCallback {

    /**
     * 种子已添加
     */
    fun onTorrentAdded(hash: String)

    /**
     * 种子状态发生变化
     */
    fun onTorrentStateChanged(hash: String)

    /**
     *  种子下载完成
     */
    fun onTorrentFinished(hash: String)

    /**
     * 种子以删除
     */
    fun onTorrentRemoved(hash: String)

    /**
     * 种子以暂停
     */
    fun onTorrentPaused(hash: String)

    /**
     * 种子以下载
     */
    fun onTorrentResumed(hash: String)

    /**
     * 开启种子下载引擎
     */
    fun onEngineStarted()

    /**
     * 移动种子
     */
    fun onTorrentMoved(hash: String, success: Boolean)

    /**
     * ip过滤已解析完成
     */
    fun onIpFilterParsed(success: Boolean)

    /**
     * 加载磁力信息
     */
    fun onMagnetLoaded(hash: String, bencode: ByteArray)

    /**
     * 种子已加载元数据
     */
    fun onTorrentMetadataLoaded(hash: String, error: Exception?)

    /**
     * 重启异常
     */
    fun onRestoreSessionError(hash: String)

    /**
     * 种子发生异常
     */
    fun onTorrentError(hash: String, errorMsg: String)

    fun onSessionError(errorMsg: String)

    fun onNatError(errorMsg: String)

}