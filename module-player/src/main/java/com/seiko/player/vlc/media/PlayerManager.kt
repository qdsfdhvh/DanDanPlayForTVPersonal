package com.seiko.player.vlc.media

import android.net.Uri
import org.videolan.libvlc.MediaPlayer
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import timber.log.Timber

class PlayerManager(
    private val instance: VlcInstance,
    private val player: PlayerController
) : IPlayerController by player
    , MediaPlayer.EventListener
    , MediaWrapperList.EventListener {

    val mediaPlayer get() = player.mediaPlayer

    val seekable get() = player.seekable

    val pausable get() = player.pausable

    /**
     * 播放列表
     */
    private val mediaList = MediaWrapperList()
    private var currentIndex = -1

    /**
     * 外部MediaListener
     */
    private var listener: MediaPlayer.EventListener? = null

    private fun hasMedia(): Boolean {
        return mediaList.size != 0
    }

    private fun isValidPosition(position: Int): Boolean {
        return position in 0 until mediaList.size
    }

    suspend fun load(media: MediaWrapper,  listener: MediaPlayer.EventListener? = null) {
        load(listOf(media), 0, listener)
    }

    suspend fun load(
        list: List<MediaWrapper>,
        position: Int,
        listener: MediaPlayer.EventListener? = null
    ) {
        this.listener = listener
        mediaList.removeEventListener(this)
        mediaList.replaceWith(list)
        mediaList.addEventListener(this)
        player.setRate(1.0f)
        playIndex(position)
    }

    private suspend fun playIndex(position: Int) {
        if (!hasMedia()) {
            Timber.w("Warning: empty media list, nothing to play !")
            return
        }

        currentIndex = if (isValidPosition(position)) position else {
            Timber.w("Warning: index $position out of bounds")
            0
        }

        val mw = mediaList.getMedia(position)
        if (mw == null) {
            Timber.w("Warning: index $position media is null")
            return
        }

        val isVideoPlaying = mw.type == MediaWrapper.TYPE_VIDEO && player.isVideoPlaying()
        if (isVideoPlaying) {
            mw.addFlags(MediaWrapper.MEDIA_VIDEO)
        }

        var uri = mw.uri
        val title = mw.getMetaLong(MediaWrapper.META_TITLE)
        if (title > 0) uri = Uri.parse("$uri#$title")
        val chapter = mw.getMetaLong(MediaWrapper.META_CHAPTER)
        if (chapter > 0) uri = Uri.parse("$uri:$chapter")
        val start = 0L
        val media = instance.getFromUri(uri)
        media.addOption(":start-time=${start/1000L}")

        player.startPlayback(media, this, start)
    }

    override fun play() {
        if (hasMedia()) {
            player.play()
        }
    }

    override fun stop() {
        mediaList.clear()
    }

    override suspend fun release() {
        listener = null
        mediaList.clear()
        instance.clear()
        player.release()
    }

    override fun onEvent(event: MediaPlayer.Event?) {
        listener?.onEvent(event)
    }

    override fun onItemAdded(index: Int, mrl: String) {

    }

    override fun onItemRemoved(index: Int, mrl: String) {

    }

    override fun onItemMoved(indexBefore: Int, indexAfter: Int, mrl: String) {

    }

}