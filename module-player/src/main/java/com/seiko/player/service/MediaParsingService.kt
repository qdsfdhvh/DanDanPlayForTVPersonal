package com.seiko.player.service

import android.annotation.TargetApi
import android.app.IntentService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.text.TextUtils
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.seiko.common.service.BaseIntentService
import com.seiko.player.R
import com.seiko.player.util.constants.safeOffer
import com.seiko.player.util.helper.NotificationHelper
import com.seiko.player.vlc.util.AndroidDevices
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import org.videolan.medialibrary.interfaces.DevicesDiscoveryCb
import org.videolan.medialibrary.interfaces.Medialibrary
import timber.log.Timber
import java.io.File
import kotlin.coroutines.CoroutineContext

//@ObsoleteCoroutinesApi
//@ExperimentalCoroutinesApi
class MediaParsingService : LifecycleService(), CoroutineScope, DevicesDiscoveryCb {

    companion object {
        const val ACTION_INIT = "medialibrary_init"
        const val ACTION_RELOAD = "medialibrary_reload"
        const val ACTION_FORCE_RELOAD = "medialibrary_force_reload"
        const val ACTION_DISCOVER = "medialibrary_discover"
        const val ACTION_DISCOVER_DEVICE = "medialibrary_discover_device"
        const val ACTION_CHECK_STORAGES = "medialibrary_check_storages"

        private const val EXTRA_FIRST_RUN = "extra_first_run"
        private const val EXTRA_UPGRADE = "extra_upgrade"
        private const val EXTRA_PARSE = "extra_parse"

        @JvmStatic
        fun startMediaLibrary(context: Context,
                              firstRun: Boolean = false,
                              upgrade: Boolean = false,
                              parse: Boolean = true) {
            Timber.d("startMediaLibrary...")
            if (Medialibrary.getInstance().isStarted) return
            val intent = Intent(context, MediaParsingService::class.java)
            intent.action = ACTION_INIT
            intent.putExtra(EXTRA_FIRST_RUN, firstRun)
            intent.putExtra(EXTRA_UPGRADE, upgrade)
            intent.putExtra(EXTRA_PARSE, parse)
            context.startService(intent)
            ContextCompat.startForegroundService(context, intent)
            Timber.d("startMediaLibrary finish")
        }
    }

    private lateinit var mediaLibrary: Medialibrary
//    private lateinit var wakeLock: PowerManager.WakeLock

    private var scanPaused = false
    @Volatile private var scanActivated = false
    @Volatile private var serviceLock = false
    @Volatile private var discoverTriggered = false

//    private lateinit var actions : SendChannel<Action>

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate")
        mediaLibrary = Medialibrary.getInstance()
        mediaLibrary.addDeviceDiscoveryCb(this)
//        val pm = applicationContext.getSystemService(Context.POWER_SERVICE) as PowerManager
//        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Player:MediaParsingService")

//        Medialibrary.getState().observe(this::getLifecycle) { running ->
//            if (!running && !scanPaused) {
//                exitCommand()
//            }
//        }
//        setupScope()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaLibrary.removeDeviceDiscoveryCb(this)
        Timber.d("onDestroy")
        cancel()
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun forceForeground() {
        val notification = NotificationHelper.createScanNotification(applicationContext, getString(R.string.loading_medialibrary), scanPaused)
        startForeground(43, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            forceForeground()
        }
        super.onStartCommand(intent, flags, startId)
        if (intent == null) {
            exitCommand()
            return START_NOT_STICKY
        }
        when(intent.action) {
            ACTION_INIT -> {
                Timber.d("onStartCommand ACTION_INIT")
                val firstRun = intent.getBooleanExtra(EXTRA_FIRST_RUN, false)
                val upgrade = intent.getBooleanExtra(EXTRA_UPGRADE, false)
                val parse = intent.getBooleanExtra(EXTRA_PARSE, true)
                setupMediaLibrary(firstRun, upgrade, parse)
            }
        }
        return START_NOT_STICKY
    }

    private fun setupMediaLibrary(firstRun: Boolean, upgrade: Boolean, parse: Boolean) = launch {
        Timber.d("setupMediaLibrary firstRun=$firstRun, upgrade=$upgrade, parse=$parse")
        if (mediaLibrary.isInitiated) {
            mediaLibrary.resumeBackgroundOperations()
            if (parse && !scanActivated) {
                Timber.d("setupMediaLibrary StartScan")
                actionStartScan(upgrade)
            }
        } else {
            Timber.d("setupMediaLibrary Init")
            actionInit(firstRun, upgrade, parse)
        }
    }

    private fun actionInit(firstRun: Boolean, upgrade: Boolean, parse: Boolean) {
        Timber.d("actions Init")
        if (mediaLibrary.isInitiated) {
            exitCommand()
            return
        }

        val context = this@MediaParsingService
        var shouldInit = firstRun || !dbExists()
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

        exitCommand()
    }

    private fun actionStartScan(upgrade: Boolean) {
        Timber.d("actionStartScan")
        addDevices(true)
        startScan(false, upgrade)
    }

    private fun initMediaLib(parse: Boolean, shouldInit: Boolean, upgrade: Boolean) {
        addDevices(parse)
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

    private fun startScan(shouldInit: Boolean, upgrade: Boolean) {
        Timber.d("startScan shouldInit=$shouldInit")
        scanActivated = true
        when {
            shouldInit -> {
                for (folder in Medialibrary.getBlackList()) {
                    mediaLibrary.banFolder(AndroidDevices.EXTERNAL_PUBLIC_DIRECTORY + folder)
                }
                Timber.d("discover ${AndroidDevices.EXTERNAL_PUBLIC_DIRECTORY}")
                mediaLibrary.discover(AndroidDevices.EXTERNAL_PUBLIC_DIRECTORY)
            }
            upgrade -> {
                mediaLibrary.unbanFolder("${AndroidDevices.EXTERNAL_PUBLIC_DIRECTORY}/WhatsApp/")
                mediaLibrary.banFolder("${AndroidDevices.EXTERNAL_PUBLIC_DIRECTORY}/WhatsApp/Media/WhatsApp Animated Gifs/")
            }
            else -> {
                exitCommand()
            }
        }
    }

    private fun addDevices(addExternal: Boolean) {
        val mainStorage = AndroidDevices.EXTERNAL_PUBLIC_DIRECTORY
        Timber.d("addDevice : $mainStorage")
        mediaLibrary.addDevice("main-storage", mainStorage, true)

        val devices = AndroidDevices.externalStorageDirectories
        for (device in devices) {
            val uuid = device.substringBeforeLast('/')
            Timber.d(uuid)
            if (device.isEmpty() || !device.scanAllowed()) continue

            Timber.d("addDevice : $uuid")
            mediaLibrary.addDevice(uuid, device, false)
        }
    }

//    private fun reload(path: String?) {
//        if (reload > 0) return
//        if (TextUtils.isEmpty(path)) medialibrary.reload()
//        else medialibrary.reload(path)
//    }

    private fun String.scanAllowed(): Boolean {
        val file = File(Uri.parse(this@scanAllowed).path ?: return false)
        if (!file.exists() || !file.canRead()) return false
        return true
    }

    private fun updateStorageList() {
        serviceLock = true
        val devices = AndroidDevices.externalStorageDirectories
        val knownDevices = mediaLibrary.devices
        val missingDevices = knownDevices.toMutableList()
        missingDevices.remove("file://${AndroidDevices.EXTERNAL_PUBLIC_DIRECTORY}")
        for (device in devices) {
            val uuid = device.substringBeforeLast('/')
            if (device.isEmpty() || !device.scanAllowed()) continue
            if (containsDevice(knownDevices, device)) {
                missingDevices.remove("file://$device")
                continue
            }
            mediaLibrary.addDevice(uuid, device, true)
        }
        for (device in missingDevices) {
            val uri = Uri.parse(device)
            mediaLibrary.removeDevice(uri.lastPathSegment, uri.path)
        }
        serviceLock = false
        exitCommand()
    }

    private fun containsDevice(devices: Array<String>, device: String): Boolean {
        if (devices.isNullOrEmpty()) return false
        for (dev in devices) if (device.startsWith(dev.removeFileProtocole())) return true
        return false
    }

    private fun String.removeFileProtocole(): String {
        return if (this.startsWith("file://"))
            this.substring(7)
        else
            this
    }

    /**
     * VLC Media 数据库是否存在
     */
    private fun Context.dbExists(): Boolean {
        return File(getDir("db", Context.MODE_PRIVATE).toString() + Medialibrary.VLC_MEDIA_DB_NAME).exists()
    }

    /**
     * 退出命令
     */
    private fun exitCommand() {
        if (!mediaLibrary.isWorking && !serviceLock && !discoverTriggered) {
//            if (wakeLock.isHeld) {
//                wakeLock.release()
//            }
            stopForeground(true)
            stopService(Intent(applicationContext, MediaParsingService::class.java))
        }
    }

    override fun onReloadStarted(entryPoint: String?) {
        Timber.d(entryPoint)
    }

    override fun onReloadCompleted(entryPoint: String?) {
        Timber.d(entryPoint)
    }

    override fun onDiscoveryStarted(entryPoint: String?) {
        Timber.d(entryPoint)
    }

    override fun onParsingStatsUpdated(percent: Int) {
        Timber.d(percent.toString())
    }

    override fun onDiscoveryCompleted(entryPoint: String?) {
        Timber.d(entryPoint)
    }

    override fun onDiscoveryProgress(entryPoint: String?) {
        Timber.d(entryPoint)
    }

//    private sealed class Action {
//        class Init(val upgrade: Boolean, val parse: Boolean) : Action()
//        class StartScan(val upgrade: Boolean) : Action()
//        object UpdateStorageList : Action()
//        class DiscoverStorage(val path: String) : Action()
//        class DiscoverFolder(val path: String) : Action()
//        class Reload(val path: String?) : Action()
//        object ForceReload : Action()
//    }
}