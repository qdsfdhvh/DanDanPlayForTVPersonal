package com.seiko.player.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.seiko.common.util.extensions.lazyAndroid
import org.videolan.medialibrary.interfaces.Medialibrary

class MediaParsingIntentService : IntentService("MediaParsingIntentService") {

    companion object {
        private const val ACTION_INIT = "media_library_init"
        private const val ACTION_RELOAD = "media_library_reload"

        private const val EXTRA_UPGRADE = "extra_upgrade"
        private const val EXTRA_PARSE = "extra_parse"
        private const val EXTRA_PATH = "extra_path"

        @JvmStatic
        fun startMediaLibrary(context: Context,
                              upgrade: Boolean = false,
                              parse: Boolean = true) {
            if (Medialibrary.getInstance().isStarted) return
            val intent = Intent(context, MediaParsingIntentService::class.java)
            intent.action = ACTION_INIT
            intent.putExtra(EXTRA_UPGRADE, upgrade)
            intent.putExtra(EXTRA_PARSE, parse)
            context.startService(intent)
        }

        /**
         * 扫描设备中新增或删除的媒体文件
         */
        @JvmStatic
        fun scanDiscovery(context: Context, path: String? = null) {
            val intent = Intent(context, MediaParsingIntentService::class.java)
            intent.action = ACTION_RELOAD
            if (!path.isNullOrEmpty()) {
                intent.putExtra(EXTRA_PATH, path)
            }
            context.startService(intent)
        }
    }

    private val delegate by lazyAndroid { MediaParsingDelegate() }

    override fun onCreate() {
        super.onCreate()
        delegate.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate.onDestroy()
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return
        when(intent.action) {
            ACTION_INIT -> {
                val upgrade = intent.getBooleanExtra(EXTRA_UPGRADE, false)
                val parse = intent.getBooleanExtra(EXTRA_PARSE, true)
                delegate.setupMediaLibrary(this, upgrade, parse)
            }
            ACTION_RELOAD -> {
                val path = intent.getStringExtra(EXTRA_PATH)
                delegate.reload(path)
            }
        }
    }

}