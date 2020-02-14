package com.seiko.download.torrent.extensions

import com.seiko.download.torrent.TorrentEngine
import com.seiko.download.torrent.utils.log
import org.libtorrent4j.AlertListener
import org.libtorrent4j.alerts.*
import java.lang.ref.WeakReference

private val ENGINE_INNER_LISTENER_TYPES = intArrayOf(
    AlertType.METADATA_RECEIVED.swig(),

    AlertType.PIECE_FINISHED.swig(),

    AlertType.BLOCK_FINISHED.swig(),
    AlertType.STATE_CHANGED.swig(),
    AlertType.STATS.swig(),

    AlertType.TORRENT_REMOVED.swig(),
    AlertType.TORRENT_RESUMED.swig(),
    AlertType.TORRENT_PAUSED.swig(),
    AlertType.TORRENT_FINISHED.swig(),

    AlertType.ADD_TORRENT.swig(),
    AlertType.SAVE_RESUME_DATA.swig()

) + ERROR_INNER_LISTENER_TYPES


internal class InnerListener(engine: TorrentEngine) : AlertListener {

    private val torrentEngine = WeakReference(engine)

    override fun types() = ENGINE_INNER_LISTENER_TYPES

    override fun alert(alert: Alert<*>?) {
        if (alert == null) return

        if (alert.isTorrentAlert() && !alert.hasValidTorrentHandle()) {
            log("Ignoring alert with invalid torrent handle: ${alert.type()}")
            return
        }

        when(alert.type()) {
            AlertType.METADATA_RECEIVED -> torrentEngine.get()?.onMetadataReceived(alert as MetadataReceivedAlert)

            AlertType.PIECE_FINISHED -> torrentEngine.get()?.onPieceFinished(alert as PieceFinishedAlert)

            AlertType.STATE_CHANGED -> torrentEngine.get()?.onStateChanged(alert as StateChangedAlert)
            AlertType.BLOCK_FINISHED -> torrentEngine.get()?.onBlockFinished(alert as BlockFinishedAlert)
            AlertType.STATS -> torrentEngine.get()?.onStats(alert as StatsAlert)

            AlertType.TORRENT_REMOVED -> torrentEngine.get()?.onTorrentRemoved(alert as TorrentRemovedAlert)
            AlertType.TORRENT_RESUMED -> torrentEngine.get()?.onTorrentResumed(alert as TorrentResumedAlert)
            AlertType.TORRENT_PAUSED -> torrentEngine.get()?.onTorrentPaused(alert as TorrentPausedAlert)
            AlertType.TORRENT_FINISHED -> torrentEngine.get()?.onTorrentFinished(alert as TorrentFinishedAlert)

            AlertType.ADD_TORRENT -> torrentEngine.get()?.onAddTorrent(alert as TorrentAlert)
            AlertType.SAVE_RESUME_DATA -> torrentEngine.get()?.onSaveResumeDataAlert(alert as SaveResumeDataAlert)

            else -> torrentEngine.get()?.checkError(alert)
        }
    }
}
