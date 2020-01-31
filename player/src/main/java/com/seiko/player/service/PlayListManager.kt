package com.seiko.player.service

import android.content.Context
import android.net.Uri
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.player.data.db.SlaveRepository
import com.seiko.player.util.AppScope
import com.seiko.player.util.VLCInstance
import com.seiko.player.util.VLCOptions
import com.seiko.player.util.getUri
import kotlinx.coroutines.*
import org.videolan.libvlc.FactoryManager
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.RendererItem
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.libvlc.interfaces.IMediaFactory
import org.videolan.medialibrary.MLServiceLocator
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import timber.log.Timber
import java.util.*

class PlayListManager(
    private val context: Context,
    private val playerController: PlayerController,
    private val slaveRepository: SlaveRepository
) : CoroutineScope by MainScope(),
    IPlayerController by playerController,
    MediaWrapperList.EventListener,
    IMedia.EventListener {

    companion object {
        private const val TAG = "VLC/PlaylistManager"
    }

    private val mediaFactory by lazyAndroid {
        FactoryManager.getFactory(IMediaFactory.factoryId) as IMediaFactory
    }

    private var mediaList = MediaWrapperList()
    private var previous = Stack<Int>()

    private var newMedia = false

    var stopAfter = -1
    var savedTime = 0L
    var videoBackground = false
        private set

    var shuffling = false
    private var expanding = false
    var currentIndex = -1
    private var nextIndex = -1
    private var prevIndex = -1
    private var random = Random(System.currentTimeMillis())
    var repeating = PlaybackStateCompat.REPEAT_MODE_NONE
    private var entryUrl : String? = null

    private val mediaPlayerEventListener = object : PlayerController.MediaPlayerEventListener {
        override fun onEvent(event: MediaPlayer.Event) {

        }
    }

    init {
        launch {
            playerController.init()
        }
    }

    fun hasMedia(): Boolean {
        return mediaList.size() != 0
    }

    fun stop(systemExit: Boolean = false) {
//        clearABRepeat()
        stopAfter = -1
        videoBackground = false
        savePosition()
        mediaList.setEventListener(null)
        previous.clear()
        if (systemExit) {
            release()
        } else {
            restart()
        }
        mediaList.clear()
    }

    fun isValidPosition(position: Int): Boolean {
        return position in 0 until mediaList.size()
    }

    suspend fun load(
        list: List<MediaWrapper>,
        position: Int,
        update: Boolean = false
    ): Boolean {
        saveMediaList()
        savePosition()
        mediaList.setEventListener(null)
        previous.clear()
        videoBackground = false
        mediaList.setItems(list)
        if (!hasMedia()) {
            Timber.tag(TAG).w("Warning: empty media list, nothing to play !")
            return false
        }
//            currentIndex = if (isValid(position)) position else 0

        mediaList.setEventListener(this@PlayListManager)
        stopAfter = -1
//            clearABRepeat()
        playerController.setRate(1.0f, false)
//            service.onPlaylistLoaded()
//            if (update) {
//                mediaList.replaceWith(withContext(Dispatchers.IO) {
//                    mediaList.copy.updateWithMLMeta() }
//                )
//            }
        return playIndex(position)
    }

    fun play() {
        if (hasMedia()) {
            playerController.play()
        }
    }

    fun pause() {

    }

    private suspend fun saveMediaList() {
        withContext(Dispatchers.Default) {

        }
    }

    private fun savePosition(reset: Boolean = false) {

    }

    private suspend fun playIndex(index: Int, flags: Int = 0): Boolean {
        videoBackground = videoBackground
                || (!playerController.isVideoPlaying() && playerController.canSwitchToVideo())
        if (mediaList.size() == 0) {
            Timber.tag(TAG).w("Warning: empty media list, nothing to play !")
            return false
        }
        currentIndex = if (isValidPosition(index)) {
            index
        } else {
            Timber.tag(TAG).w("Warning: index $index out of bounds")
            0
        }
        val media = mediaList.getMedia(index) ?: return false
        val isVideoPlaying =
            media.type == MediaWrapper.TYPE_VIDEO && playerController.isVideoPlaying()
        if (!videoBackground && isVideoPlaying) {
            media.addFlags(MediaWrapper.MEDIA_VIDEO)
        } else if (videoBackground) {
            media.addFlags(MediaWrapper.MEDIA_FORCE_AUDIO)
        }
        if (media.type != MediaWrapper.TYPE_VIDEO
            || isVideoPlaying
//            || playerController.hasRenderer
            || media.hasFlag(MediaWrapper.MEDIA_FORCE_AUDIO)
        ) {
            var uri = withContext(Dispatchers.IO) { getUri(context, media.uri) }
            if (uri == null) {
                skipMedia()
                return false
            }
            val title = media.getMetaLong(MediaWrapper.META_TITLE)
            if (title > 0) uri = Uri.parse("$uri#$title")
            val chapter = media.getMetaLong(MediaWrapper.META_CHAPTER)
            if (chapter > 0) uri = Uri.parse("$uri:$chapter")
            val start = getStartTime(media)
            val iMedia = mediaFactory.getFromUri(VLCInstance.invoke(context), uri)
            iMedia.addOption(":start-time=${start/1000L}")
            VLCOptions.setMediaOptions(iMedia, context, flags or media.flags)
            iMedia.setEventListener(this)
            playerController.startPlayback(iMedia, mediaPlayerEventListener, start)
            setSlaves(iMedia, media)
            newMedia = true
            determinePrevAndNextIndices()
//            service.onNewPlayback()
            return true
        } else {
            if (playerController.isPlaying()) {
                playerController.stop()
            }
//            VideoPlayerActivity.startOpened(ctx, mw.uri, currentIndex)
        }
        return false
    }

    private suspend fun setSlaves(media: IMedia, mw: MediaWrapper) {
        if (playerController.isReleased()) return
        val slaves = mw.slaves
        slaves?.forEach { slave ->
            media.addSlave(slave)
        }
        media.release()
        playerController.addSlaves(slaveRepository.getSlaves(mw.location)
            .filter { !slaves.contains(it) })
        if (slaves != null) {
            slaveRepository.saveSlaves(mw)
        }
    }

    private fun skipMedia() {

    }

    private suspend fun getStartTime(media: MediaWrapper): Long {
        val start = when {
            media.hasFlag(MediaWrapper.MEDIA_FROM_START) -> {
                media.removeFlags(MediaWrapper.MEDIA_FROM_START)
                0L
            }
            savedTime <= 0L -> when {
                media.time > 0L -> media.time
                media.type == MediaWrapper.TYPE_VIDEO
                        || media.isPodcast -> {
                    withContext(Dispatchers.IO) {
                        Medialibrary.getInstance().findMedia(media)
                            .getMetaLong(MediaWrapper.META_PROGRESS)
                    }
                }
                else -> 0L
            }
            else -> savedTime
        }
        savedTime = 0L
        return start
    }

    fun getCurrentMedia() = mediaList.getMedia(currentIndex)

    private suspend fun determinePrevAndNextIndices(expand: Boolean = false) {
        val media = mediaList.getMedia(currentIndex)
        if (expand && media !== null) {
            expanding = true
            nextIndex = expand(media.type == MediaWrapper.TYPE_STREAM)
            expanding = false
        } else {
            nextIndex = -1
        }
        prevIndex = -1

        if (nextIndex == -1) {
            // No subitems; play the next item.
            val size = mediaList.size()
            shuffling = shuffling and (size > 2)

            if (shuffling) {
                if (!previous.isEmpty()) {
                    prevIndex = previous.peek()
                    while (!isValidPosition(prevIndex)) {
                        previous.removeAt(previous.size - 1)
                        if (previous.isEmpty()) {
                            prevIndex = -1
                            break
                        }
                        prevIndex = previous.peek()
                    }
                }
                // If we've played all songs already in shuffle, then either
                // reshuffle or stop (depending on RepeatType).
                if (previous.size + 1 == size) {
                    if (repeating == PlaybackStateCompat.REPEAT_MODE_NONE) {
                        nextIndex = -1
                        return
                    } else {
                        previous.clear()
                        random = Random(System.currentTimeMillis())
                    }
                }
                random = Random(System.currentTimeMillis())
                // Find a new index not in previous.
                do {
                    nextIndex = random.nextInt(size)
                } while (nextIndex == currentIndex || previous.contains(nextIndex))
            } else {
                // normal playback
                if (currentIndex > 0) prevIndex = currentIndex - 1
                nextIndex = when {
                    currentIndex + 1 < size -> currentIndex + 1
                    repeating == PlaybackStateCompat.REPEAT_MODE_NONE -> -1
                    else -> 0
                }
            }
        }
    }

    /**
     * Expand the current media.
     * @return the index of the media was expanded, and -1 if no media was expanded
     */
    @MainThread
    private suspend fun expand(updateHistory: Boolean): Int {
        val index = currentIndex
        val expandedMedia = getCurrentMedia()
        val stream = expandedMedia?.type == MediaWrapper.TYPE_STREAM
        val ml = playerController.expand()
        var ret = -1

        if (ml != null && ml.count > 0) {
            val mrl = if (updateHistory) expandedMedia?.location else null
            mediaList.setEventListener(null)
            mediaList.remove(index)
            for (i in 0 until ml.count) {
                val child = ml.getMediaAt(i)
                withContext(Dispatchers.IO) { child.parse() }
                mediaList.insert(index+i, MLServiceLocator.getAbstractMediaWrapper(child))
                child.release()
            }
            mediaList.setEventListener(this)
//            addUpdateActor.offer(Unit)
//            service.onMediaListChanged()
            if (mrl !== null && ml.count == 1) {
                getCurrentMedia()?.apply {
                    AppScope.launch(Dispatchers.IO) {
                        if (stream) {
                            type = MediaWrapper.TYPE_STREAM
                            entryUrl = mrl
                            Medialibrary.getInstance().getMedia(mrl)?.run {
                                if (id > 0) Medialibrary.getInstance().removeExternalMedia(id)
                            }
                        } else if (uri.scheme != "fd") {
                            Medialibrary.getInstance().addToHistory(mrl, title)
                        }
                    }
                }
            }
            ret = index
        }
        ml?.release()
        return ret
    }

    override fun onItemAdded(index: Int, mrl: String) {

    }

    override fun onItemRemoved(index: Int, mrl: String) {

    }

    override fun onItemMoved(prevIndex: Int, nextIndex: Int, mrl: String) {

    }

    override fun onEvent(event: IMedia.Event?) {

    }

}

private fun Array<IMedia.Slave>?.contains(item: IMedia.Slave) : Boolean {
    if (this == null) return false
    for (slave in this) if (slave.uri == item.uri) return true
    return false
}