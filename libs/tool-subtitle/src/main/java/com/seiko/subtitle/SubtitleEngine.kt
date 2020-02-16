package com.seiko.subtitle

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.seiko.subtitle.cache.SubtitleCache
import com.seiko.subtitle.model.Subtitle
import com.seiko.subtitle.util.log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import tv.danmaku.ijk.media.player.IMediaPlayer
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

class SubtitleEngine : ISubtitleEngine, CoroutineScope {

    companion object {
        private const val TAG = "SubtitleEngine"
        private const val REFRESH_INTERVAL = 200L
    }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + Dispatchers.IO

    private val subtitleCache = SubtitleCache()
    private var subtitlePath: String? = null
    private var subtitles: List<Subtitle>? = null
    private var listener: ISubtitleEngine.OnSubtitleListener? = null

    private var mediaPlayer: IMediaPlayer? = null

    private var isWorking = AtomicBoolean(false)

    override fun bindToMediaPlayer(mediaPlayer: IMediaPlayer?) {
        this.mediaPlayer = mediaPlayer
    }

    override fun setSubtitlePath(path: String) {
        if (subtitlePath == path) return
        stop()

        log("Parse subtitle begin...")
        val start = System.currentTimeMillis()

        subtitles = subtitleCache.get(path)
        if (!subtitles.isNullOrEmpty()) {
            log(TAG, "Parse subtitle from cache，time=${System.currentTimeMillis() - start}")
            notifyPrepared()
            return
        }

        launch {
            val result = parseSubTitle(File(path))
            if (result.isSuccess) {
                val tto = result.getOrNull() ?: return@launch
                val captions = tto.captions
                if (captions == null) {
                    log(TAG, "Parse subtitle failed: captions is null.")
                    return@launch
                }
                subtitlePath = path
                subtitles = ArrayList(captions.values)

                log(TAG, "Parse subtitle finish，time=${System.currentTimeMillis() - start}")
                notifyPrepared()

                subtitleCache.put(path, ArrayList(captions.values))
            } else {
                log(TAG, result.exceptionOrNull())
            }
        }
    }

    override fun start() {
        if (isWorking.compareAndSet(false, true)) {
            launch {
                workFlow().collect { notifyRefreshUI(it) }
            }
        }
    }

    override fun stop() {
         if (isWorking.compareAndSet(true, false)) {

         }
    }

    override fun release() {
        subtitlePath = null
        subtitles = null
        cancel()
    }

    override fun setOnSubtitleListener(listener: ISubtitleEngine.OnSubtitleListener?) {
        this.listener = listener
    }

    private fun workFlow(): Flow<Subtitle?> = flow {
        var delay: Long
        while (isWorking.get()) {
            delay = REFRESH_INTERVAL

            val position = withContext(Dispatchers.Main) {
                if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                    mediaPlayer!!.currentPosition
                } else -1
            }

            if (position > 0) {
                val subtitle = SubtitleFinder.find(position, subtitles)
                emit(subtitle)
                if (subtitle != null) {
                    delay = subtitle.end.mseconds - position
                }
            }
            delay(delay)
        }
    }

    private fun notifyRefreshUI(subtitle: Subtitle?) {
        listener?.onSubtitleChanged(subtitle)
    }

    private fun notifyPrepared() {
        listener?.onSubtitlePrepared()
    }

}