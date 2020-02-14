package com.seiko.download.torrent

interface TorrentEngineCallback {

    /**
     * 引擎已开启
     */
    fun onEngineStarted()

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
     * 种子已删除
     */
    fun onTorrentRemoved(hash: String)

    /**
     * 种子已暂停
     */
    fun onTorrentPaused(hash: String)

    /**
     * 种子已重启
     */
    fun onTorrentResumed(hash: String)

    /**
     * 加载磁力信息
     */
    fun onMagnetLoaded(hash: String, bencode: ByteArray)

    /**
     * 种子已加载元数据
     */
    fun onTorrentMetadataLoaded(hash: String, error: Exception?)

    /**
     * 种子重启异常
     */
    fun onRestoreSessionError(hash: String)

    /**
     * 种子发生异常
     */
    fun onTorrentError(hash: String, errorMsg: String)

    /**
     * 会话异常
     */
    fun onSessionError(errorMsg: String)

    /**
     * Nat异常
     */
    fun onNatError(errorMsg: String)

}