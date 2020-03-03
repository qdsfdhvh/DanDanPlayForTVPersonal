package com.seiko.player.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.seiko.player.R
import com.seiko.player.util.helper.NotificationHelper
import kotlinx.coroutines.*
import org.videolan.medialibrary.interfaces.DevicesDiscoveryCb
import org.videolan.medialibrary.interfaces.Medialibrary
import timber.log.Timber
import java.io.File

private val EXTERNAL_PUBLIC_DIRECTORY: String = Environment.getExternalStorageDirectory().path

class MediaParsingService : LifecycleService(), DevicesDiscoveryCb {

    companion object {
        const val ACTION_INIT = "media_library_init"
        const val ACTION_RELOAD = "media_library_reload"
//        const val ACTION_FORCE_RELOAD = "media_library_force_reload"
//        const val ACTION_DISCOVER = "media_library_discover"
//        const val ACTION_DISCOVER_DEVICE = "media_library_discover_device"
//        const val ACTION_CHECK_STORAGES = "media_library_check_storages"

        private const val EXTRA_UPGRADE = "extra_upgrade"
        private const val EXTRA_PARSE = "extra_parse"
        private const val EXTRA_PATH = "extra_path"

        @JvmStatic
        fun startMediaLibrary(context: Context,
                              upgrade: Boolean = false,
                              parse: Boolean = true) {
            if (Medialibrary.getInstance().isStarted) return
            val intent = Intent(context, MediaParsingService::class.java)
            intent.action = ACTION_INIT
            intent.putExtra(EXTRA_UPGRADE, upgrade)
            intent.putExtra(EXTRA_PARSE, parse)
            ContextCompat.startForegroundService(context, intent)
        }

        /**
         * 扫描设备中新增或删除的媒体文件
         */
        @JvmStatic
        fun scanDiscovery(context: Context, path: String? = null) {
            val intent = Intent(context, MediaParsingService::class.java)
            intent.action = ACTION_RELOAD
            if (!path.isNullOrEmpty()) {
                intent.putExtra(EXTRA_PATH, path)
            }
            ContextCompat.startForegroundService(context, intent)
        }
    }

    private lateinit var mediaLibrary: Medialibrary

    /**
     * 是否已经扫描过媒体库
     */
    @Volatile private var scanActivated = false

    override fun onCreate() {
        super.onCreate()
        // 安卓8.0以上，必须对通知添加channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper.createNotificationChannels(applicationContext)
        }
        // 获取Vlc媒体库单列
        mediaLibrary = Medialibrary.getInstance()
        mediaLibrary.addDeviceDiscoveryCb(this)
        // 监听Vlc媒体库运行状态
        Medialibrary.getState().observe(this::getLifecycle) { running ->
            if (!running) {
                exitCommand()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaLibrary.removeDeviceDiscoveryCb(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (intent == null) {
            exitCommand()
            return START_NOT_STICKY
        }

        // 安卓8.0以上 启动后台服务5s内必须创建一个通知
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notification = NotificationHelper.createScanNotification(
                applicationContext, getString(R.string.loading_medialibrary))
            startForeground(43, notification)
        }

        when(intent.action) {
            ACTION_INIT -> {
                val upgrade = intent.getBooleanExtra(EXTRA_UPGRADE, false)
                val parse = intent.getBooleanExtra(EXTRA_PARSE, true)
                setupMediaLibrary(upgrade, parse)
            }
            ACTION_RELOAD -> {
                val path = intent.getStringExtra(EXTRA_PATH)
                reload(path)
            }
        }
        return START_NOT_STICKY
    }

    /**
     * 初始化媒体库
     */
    private fun setupMediaLibrary(upgrade: Boolean, parse: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (mediaLibrary.isInitiated) {
                mediaLibrary.resumeBackgroundOperations()
                if (parse && !scanActivated) {
                    addDevices()
                    startScan(false, upgrade)
                }
            } else {
                initMediaLibrary(upgrade, parse)
            }
        }
    }

    /**
     * 创建媒体库
     */
    private fun initMediaLibrary(upgrade: Boolean, parse: Boolean) {
        if (mediaLibrary.isInitiated) {
            exitCommand()
            return
        }

        var shouldInit = !dbExists()
        val initCode = mediaLibrary.init(this)
        if (initCode != Medialibrary.ML_INIT_ALREADY_INITIALIZED) {
            shouldInit = shouldInit or
                    (initCode == Medialibrary.ML_INIT_DB_RESET) or
                    (initCode == Medialibrary.ML_INIT_DB_CORRUPTED)
            if (initCode != Medialibrary.ML_INIT_FAILED) {
                initMediaLib(parse, shouldInit, upgrade)
                return
            }
        }

        exitCommand()
    }

    /**
     * 启动Vlc媒体库
     */
    private fun initMediaLib(parse: Boolean, shouldInit: Boolean, upgrade: Boolean) {
        addDevices()
        if (upgrade) {
            mediaLibrary.forceParserRetry()
        }
        mediaLibrary.start()
        if (parse) {
            startScan(shouldInit, upgrade)
        } else {
            exitCommand()
        }
    }

    /**
     * 开始扫描
     */
    private fun startScan(shouldInit: Boolean, upgrade: Boolean) {
        scanActivated = true
        when {
            shouldInit -> {
                for (folder in Medialibrary.getBlackList()) {
                    mediaLibrary.banFolder(EXTERNAL_PUBLIC_DIRECTORY + folder)
                }
                mediaLibrary.discover(EXTERNAL_PUBLIC_DIRECTORY)
            }
            upgrade -> {
                mediaLibrary.unbanFolder("${EXTERNAL_PUBLIC_DIRECTORY}/WhatsApp/")
                mediaLibrary.banFolder("${EXTERNAL_PUBLIC_DIRECTORY}/WhatsApp/Media/WhatsApp Animated Gifs/")
            }
            else -> {
                exitCommand()
            }
        }
    }

    /**
     * 添加目录
     */
    private fun addDevices() {
        val mainStorage = EXTERNAL_PUBLIC_DIRECTORY
        mediaLibrary.addDevice("main-storage", mainStorage, true)
    }

    /**
     * 重新扫描xx文件夹媒体文件
     */
    private fun reload(path: String?) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (path.isNullOrEmpty()) {
                mediaLibrary.reload()
            } else {
                mediaLibrary.reload(path)
            }
        }
    }

    /**
     * 退出命令
     */
    private fun exitCommand() {
        if (!mediaLibrary.isWorking) {
            stopForeground(true)
            lifecycleScope.launch {
                delay(100)
                stopService(Intent(applicationContext, MediaParsingService::class.java))
            }
        }
    }

    override fun onReloadStarted(entryPoint: String?) {
        Timber.d("onReloadStarted: %s", entryPoint)
    }

    override fun onReloadCompleted(entryPoint: String?) {
        Timber.d("onReloadCompleted: %s", entryPoint)
    }

    override fun onParsingStatsUpdated(percent: Int) {
        Timber.d("onParsingStatsUpdated: %s", percent.toString())
    }

    override fun onDiscoveryStarted(entryPoint: String?) {
        Timber.d("onDiscoveryStarted: %s", entryPoint)
    }

    override fun onDiscoveryProgress(entryPoint: String?) {
        Timber.d("onDiscoveryProgress: %s", entryPoint)
    }

    override fun onDiscoveryCompleted(entryPoint: String?) {
        Timber.d("onDiscoveryCompleted: %s", entryPoint)
    }

}

/**
 * VLC Media 数据库是否存在
 */
private fun Context.dbExists(): Boolean {
    return File(getDir("db", Context.MODE_PRIVATE).toString() + Medialibrary.VLC_MEDIA_DB_NAME).exists()
}