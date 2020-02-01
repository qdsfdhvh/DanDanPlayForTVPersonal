package com.seiko.player.media

import com.seiko.player.util.extensions.retry
import org.videolan.libvlc.RendererDiscoverer
import org.videolan.libvlc.RendererItem
import org.videolan.libvlc.interfaces.ILibVLC
import java.util.concurrent.atomic.AtomicBoolean

class RendererDelegate(private val libVlc: ILibVLC) : RendererDiscoverer.EventListener {

    private val discoverers = ArrayList<RendererDiscoverer>()
    private val renderers = ArrayList<RendererItem>()
    private val renderStarted = AtomicBoolean(false)

    suspend fun start() {
        if (renderStarted.compareAndSet(false, true)) {
            for (discoverer in RendererDiscoverer.list(libVlc)) {
                val rd = RendererDiscoverer(libVlc, discoverer.name)
                discoverers.add(rd)
                rd.setEventListener(this)
                retry(5, 1000L) {
                    if (!rd.isReleased) rd.start() else false
                }
            }
        }
    }

    fun stop() {
        if (renderStarted.compareAndSet(true, false)) {
            for (discoverer in discoverers) {
                discoverer.stop()
            }
            discoverers.clear()
            renderers.clear()
        }
    }

    fun getRenderer(): RendererItem? {
        return if (renderers.isNotEmpty()) renderers[0] else null
    }

    override fun onEvent(event: RendererDiscoverer.Event?) {
        when (event?.type) {
            RendererDiscoverer.Event.ItemAdded -> {
                renderers.add(event.item)
            }
            RendererDiscoverer.Event.ItemDeleted -> {
                renderers.remove(event.item)
            }
        }
    }

}