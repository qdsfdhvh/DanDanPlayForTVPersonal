package com.seiko.player.service

import android.content.Context
import android.os.Environment
import org.videolan.medialibrary.interfaces.DevicesDiscoveryCb
import org.videolan.medialibrary.interfaces.Medialibrary
import timber.log.Timber
import java.io.File

private val EXTERNAL_PUBLIC_DIRECTORY: String = Environment.getExternalStorageDirectory().path

class MediaParsingDelegate {

    private lateinit var mediaLibrary: Medialibrary

    private var listener: DevicesDiscoveryCb? = null

    /**
     * 是否已经扫描过媒体库
     */
    @Volatile private var scanActivated = false

    fun onCreate() {
        // 获取Vlc媒体库单列
        mediaLibrary = Medialibrary.getInstance()
    }

    fun onDestroy() {
        if (this.listener != null) {
            mediaLibrary.removeDeviceDiscoveryCb(this.listener)
        }
    }

    /**
     * 监听目录扫描事件
     */
    fun setDeviceDiscoveryCb(listener: DevicesDiscoveryCb?) {
        if (this.listener != null) {
            mediaLibrary.removeDeviceDiscoveryCb(this.listener)
        }
        this.listener = listener
        if (this.listener != null) {
            mediaLibrary.addDeviceDiscoveryCb(this.listener)
        }
    }

    /**
     * 初始化媒体库
     */
    fun setupMediaLibrary(context: Context, upgrade: Boolean, parse: Boolean) {
        Timber.d("setupMediaLibrary upgrade=$upgrade, parse=$parse")
        if (mediaLibrary.isInitiated) {
            mediaLibrary.resumeBackgroundOperations()
            if (parse && !scanActivated) {
                addDevices()
                startScan(false, upgrade)
            }
        } else {
            initMediaLibrary(context, upgrade, parse)
        }
    }

    /**
     * 创建媒体库
     */
    private fun initMediaLibrary(context: Context, upgrade: Boolean, parse: Boolean) {
        Timber.d("initMediaLibrary upgrade=$upgrade parse=$parse")
        if (mediaLibrary.isInitiated) {
            return
        }

        var shouldInit = !context.dbExists()
        val initCode = mediaLibrary.init(context)
        if (initCode != Medialibrary.ML_INIT_ALREADY_INITIALIZED) {
            shouldInit = shouldInit or
                    (initCode == Medialibrary.ML_INIT_DB_RESET) or
                    (initCode == Medialibrary.ML_INIT_DB_CORRUPTED)
            if (initCode != Medialibrary.ML_INIT_FAILED) {
                initMediaLib(parse, shouldInit, upgrade)
                return
            }
        }
    }

    /**
     * 启动Vlc媒体库
     */
    private fun initMediaLib(parse: Boolean, shouldInit: Boolean, upgrade: Boolean) {
        Timber.d("initMediaLib parse=$parse shouldInit=$shouldInit upgrade=$upgrade")
        addDevices()
        if (upgrade) {
            mediaLibrary.forceParserRetry()
        }
        mediaLibrary.start()
        if (parse) {
            startScan(shouldInit, upgrade)
        }
    }

    /**
     * 绑定&扫描目录，第一次装机时运行
     */
    private fun startScan(shouldInit: Boolean, upgrade: Boolean) {
        Timber.d("startScan  shouldInit=$shouldInit, upgrade=$upgrade")
        scanActivated = true
        when {
            // 第一次启动 or 扫描目录集为空
            shouldInit || mediaLibrary.foldersList.isEmpty() -> {
                for (folder in Medialibrary.getBlackList()) {
                    mediaLibrary.banFolder(EXTERNAL_PUBLIC_DIRECTORY + folder)
                    Timber.d("banFolder:${EXTERNAL_PUBLIC_DIRECTORY + folder}")
                }
                mediaLibrary.discover(EXTERNAL_PUBLIC_DIRECTORY)
            }
            upgrade -> {
                mediaLibrary.unbanFolder("${EXTERNAL_PUBLIC_DIRECTORY}/WhatsApp/")
                mediaLibrary.banFolder("${EXTERNAL_PUBLIC_DIRECTORY}/WhatsApp/Media/WhatsApp Animated Gifs/")
            }
        }
    }

    /**
     * 添加目录
     */
    private fun addDevices() {
        val mainStorage = EXTERNAL_PUBLIC_DIRECTORY
        mediaLibrary.addDevice("main-storage", mainStorage, true)
        Timber.d("add device: $mainStorage")
    }

    /**
     * 重新扫描xx文件夹媒体文件
     */
    fun reload(path: String?) {
        if (path.isNullOrEmpty()) {
            Timber.d("reload all path")
            mediaLibrary.reload()
        } else {
            Timber.d("reload path=$path")
            mediaLibrary.reload(path)
        }
    }

    val isWorking get() = mediaLibrary.isWorking
}

/**
 * VLC Media 数据库是否存在
 */
private fun Context.dbExists(): Boolean {
    return File(getDir("db", Context.MODE_PRIVATE).toString() + Medialibrary.VLC_MEDIA_DB_NAME).exists()
}