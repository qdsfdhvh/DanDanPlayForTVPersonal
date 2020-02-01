package com.seiko.player.media

import org.videolan.medialibrary.interfaces.media.MediaWrapper
import java.util.concurrent.CopyOnWriteArrayList

class MediaWrapperList {

    companion object {
        private const val TAG = "VLC/MediaWrapperList"

        private const val EVENT_ADDED = 0
        private const val EVENT_REMOVED = 1
        private const val EVENT_MOVED = 2
    }

    private val internalList = CopyOnWriteArrayList<MediaWrapper>()
    private var eventListener: EventListener? = null
    private var videoCount = 0

    fun setEventListener(listener: EventListener?) {
        eventListener = listener
    }

    fun setItems(list: List<MediaWrapper>) {
        if (internalList.isNotEmpty()) internalList.clear()
        internalList.addAll(list)
    }

    fun addAll(list: List<MediaWrapper>) {
        internalList.addAll(list)
    }

    fun clear() {
        if (internalList.isEmpty()) return
        for (i in internalList.indices) {
            signalEventListeners(EVENT_REMOVED, i, -1, internalList[i].location)
        }
        internalList.clear()
        videoCount = 0
    }

    fun insert(position: Int, media: MediaWrapper) {
        if (position < 0) return
        internalList.add(position.coerceAtMost(internalList.size), media)
        signalEventListeners(EVENT_ADDED, position, -1, media.location)
        if (media.type == MediaWrapper.TYPE_VIDEO) {
            ++videoCount
        }
    }

    fun add(media: MediaWrapper) {
        internalList.add(media)
        signalEventListeners(EVENT_ADDED, internalList.size - 1, -1, media.location)
        if (media.type == MediaWrapper.TYPE_VIDEO) {
            ++videoCount
        }
    }

    fun remove(position: Int) {
        if (!isValid(position)) return
        if (internalList[position].type == MediaWrapper.TYPE_VIDEO) {
            --videoCount
        }
        val uri = internalList[position].location
        internalList.removeAt(position)
        signalEventListeners(EVENT_REMOVED, position, -1, uri)
    }

    fun remove(location: String) {
        var i = 0
        while(i < internalList.size) {
            val uri = internalList[i].location
            if (uri == location) {
                if (internalList[i].type == MediaWrapper.TYPE_VIDEO) {
                    --videoCount
                }
                internalList.removeAt(i)
                signalEventListeners(EVENT_REMOVED, i, -1, uri)
                i--
            }
            ++i
        }
    }

    private fun signalEventListeners(event: Int, arg1: Int, arg2: Int, mrl: String) {
        eventListener?.let { listener ->
            when(event) {
                EVENT_ADDED -> listener.onItemAdded(arg1, mrl)
                EVENT_REMOVED -> listener.onItemRemoved(arg1, mrl)
                EVENT_MOVED -> listener.onItemMoved(arg1, arg2, mrl)
            }
        }
    }

    private fun isValid(position: Int): Boolean {
        return position >= 0 && position < internalList.size
    }

    fun getMedia(position: Int): MediaWrapper? {
        return if (isValid(position)) internalList[position] else null
    }

    fun getMRL(position: Int): String? {
        return if (isValid(position)) internalList[position].location else null
    }

    fun size(): Int {
        return internalList.size
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("LibVLC Media List: {")
        for (i in 0 until size()) {
            sb.append(i.toString())
            sb.append(": ")
            sb.append(getMRL(i))
            sb.append(", ")
        }
        sb.append("}")
        return sb.toString()
    }

    interface EventListener {
        fun onItemAdded(index: Int, mrl: String)
        fun onItemRemoved(index: Int, mrl: String)
        fun onItemMoved(prevIndex: Int, nextIndex: Int, mrl: String)
    }
}