package com.seiko.subtitle

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.seiko.subtitle.cache.SubtitleCache
import com.seiko.subtitle.model.Subtitle
import com.seiko.subtitle.util.log
import tv.danmaku.ijk.media.player.IMediaPlayer

class SubtitleEngine : ISubtitleEngine {

    companion object {
        private const val TAG = "SubtitleEngine"
        private const val MSG_REFRESH = 888
        private const val REFRESH_INTERVAL = 200L
    }

    private val subtitleCache = SubtitleCache()
    private var subtitlePath: String? = null
    private var subtitles: List<Subtitle>? = null
    private var listener: ISubtitleEngine.OnSubtitleListener? = null

    private var mediaPlayer: IMediaPlayer? = null

    private var handlerThread: HandlerThread? = null
    private var workHandler: Handler? = null

    override fun bindToMediaPlayer(mediaPlayer: IMediaPlayer?) {
        this.mediaPlayer = mediaPlayer
    }

    override fun setSubtitlePath(path: String) {
        if (subtitlePath == path) return

        stop()
        createWorkThread()

        subtitles = subtitleCache.get(path)
        if (!subtitles.isNullOrEmpty()) {
            log(TAG, "load subtitle from cache")
            notifyPrepared()
            return
        }

        SubtitleParser(path) { result ->
            if (result.isSuccess) {
                val tto = result.getOrNull() ?: return@SubtitleParser
                val captions = tto.captions
                if (captions == null) {
                    log(TAG, "onSuccess: captions is null.")
                    return@SubtitleParser
                }
                subtitlePath = path
                subtitles = ArrayList(captions.values)
                notifyPrepared()
                subtitleCache.put(path, ArrayList(captions.values))
            } else {
                log(TAG, result.exceptionOrNull())
            }
        }.parse()
    }

    override fun start() {
//        if (mediaPlayer == null) return
        if (workHandler != null) {
            workHandler!!.removeMessages(MSG_REFRESH)
            workHandler!!.sendEmptyMessageDelayed(MSG_REFRESH, REFRESH_INTERVAL)
        }
    }

    override fun stop() {
        workHandler?.removeMessages(MSG_REFRESH)
    }

    override fun release() {
        stopWorkThread()
        subtitlePath = null
        subtitles = null
    }

    override fun setOnSubtitleListener(listener: ISubtitleEngine.OnSubtitleListener?) {
        this.listener = listener
    }

    private fun createWorkThread() {
        handlerThread = HandlerThread("SubtitleFindThread")
        handlerThread!!.start()
        workHandler = object : Handler(handlerThread!!.looper) {
            override fun handleMessage(msg: Message?) {
                if (msg == null || msg.what != MSG_REFRESH) return
                try {
                    var delay = REFRESH_INTERVAL
                    if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                        val position = mediaPlayer!!.currentPosition
                        val subtitle = SubtitleFinder.find(position, subtitles)
                        notifyRefreshUI(subtitle)
                        if (subtitle != null) {
                            delay = subtitle.end.mseconds - position
                        }
                    }
                    workHandler?.sendEmptyMessageDelayed(MSG_REFRESH, delay)
                } catch (ignored: Exception) {
                }
            }
        }
    }

    private fun stopWorkThread() {
        if (handlerThread != null) {
            handlerThread!!.quit()
            handlerThread = null
        }
        if (workHandler != null) {
            workHandler!!.removeCallbacksAndMessages(null)
            workHandler = null
        }
    }

    private fun notifyRefreshUI(subtitle: Subtitle?) {
        listener?.onSubtitleChanged(subtitle)
    }

    private fun notifyPrepared() {
        listener?.onSubtitlePrepared()
    }

}