package com.seiko.torrent.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.seiko.common.eventbus.EventBusScope
import com.seiko.common.util.toast.toast
import com.seiko.common.data.Result
import com.seiko.download.torrent.TorrentEngineOptions
import com.seiko.torrent.data.model.torrent.AddTorrentParams
import com.seiko.torrent.data.model.PostEvent
import com.seiko.torrent.domain.GetTorrentTrackersUseCase
import com.seiko.torrent.download.Downloader
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class TorrentTaskService : IntentService("TorrentTaskService"), CoroutineScope {

    companion object {
        private const val ACTION_LOAD_TRACKERS = "ACTION_LOAD_TRACKERS"
        private const val ACTION_ADD_TORRENT = "ACTION_ADD_TORRENT"
        private const val ACTION_DEL_TORRENT = "ACTION_DEL_TORRENT"
        private const val ACTION_SHUT_DOWN = "ACTION_SHUT_DOWN"

        private const val EXTRA_ADD_TORRENT_PARAMS = "EXTRA_ADD_TORRENT_PARAMS"
        private const val EXTRA_DEL_HASH = "EXTRA_DEL_HASH"
        private const val EXTRA_WITH_FILE = "EXTRA_WITH_FILE"

        /**
         * 加载Tracker跟踪器
         */
        fun loadTrackers(context: Context) {
            val intent = Intent(context, TorrentTaskService::class.java)
            intent.action = ACTION_LOAD_TRACKERS
            context.startService(intent)
        }

        /**
         * 添加种子任务
         */
        @JvmStatic
        fun addTorrent(context: Context, params: AddTorrentParams) {
            val intent = Intent(context, TorrentTaskService::class.java)
            intent.action = ACTION_ADD_TORRENT
            intent.putExtra(EXTRA_ADD_TORRENT_PARAMS, params)
            context.startService(intent)
        }

        /**
         * 删除种子任务
         */
        @JvmStatic
        fun delTorrent(context: Context, hash: String, withFile: Boolean) {
            val intent = Intent(context, TorrentTaskService::class.java)
            intent.action = ACTION_DEL_TORRENT
            intent.putExtra(EXTRA_DEL_HASH, hash)
            intent.putExtra(EXTRA_WITH_FILE, withFile)
            context.startService(intent)
        }

        /**
         * 关闭服务
         */
        @JvmStatic
        fun shutDown(context: Context) {
            val intent = Intent(context, TorrentTaskService::class.java)
            intent.action = ACTION_SHUT_DOWN
            context.startService(intent)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) return
        when(intent.action) {
            ACTION_LOAD_TRACKERS -> {
                loadTrackers()
            }
            ACTION_ADD_TORRENT -> {
                if (intent.hasExtra(EXTRA_ADD_TORRENT_PARAMS)) {
                    val params: AddTorrentParams = intent.getParcelableExtra(EXTRA_ADD_TORRENT_PARAMS)!!
                    addTorrent(params)
                }
            }
            ACTION_DEL_TORRENT -> {
                if (intent.hasExtra(EXTRA_DEL_HASH)) {
                    val hash = intent.getStringExtra(EXTRA_DEL_HASH)!!
                    val withFile = intent.getBooleanExtra(EXTRA_WITH_FILE, true)
                    delTorrent(hash, withFile)
                }
            }
            ACTION_SHUT_DOWN -> {
                shutDown()
            }
        }
    }

    @Inject lateinit var downloader: Lazy<Downloader>
    @Inject lateinit var options: Lazy<TorrentEngineOptions>
    @Inject lateinit var getTorrentTrackers: Lazy<GetTorrentTrackersUseCase>

    /**
     * 加载Tracker跟踪器
     */
    private fun loadTrackers() = runBlocking(coroutineContext) {
        when(val result = getTorrentTrackers.get().invoke()) {
            is Result.Success -> options.get().trackers.addAll(result.data)
            is Result.Error -> Timber.e(result.exception)
        }
    }

    /**
     * 添加 种子任务
     */
    private fun addTorrent(params: AddTorrentParams) = runBlocking(coroutineContext) {
        val task = params.entity
        when(val result = downloader.get().addTorrent(task, params.fromMagnet)) {
            is Result.Success -> {
                EventBusScope.getDefault().post(PostEvent.TorrentAdded(task))
            }
            is Result.Error -> {
                Timber.e(result.exception)
                launch(Dispatchers.Main) {
                    toast(result.exception.message)
                }
            }
        }
    }

    /**
     * 删除 种子任务
     */
    private fun delTorrent(hash: String, withFile: Boolean) = runBlocking(coroutineContext) {
        downloader.get().deleteTorrent(hash, withFile)
    }

    /**
     * 关闭种子下载引擎
     */
    private fun shutDown() {
        downloader.get().release()
    }

}