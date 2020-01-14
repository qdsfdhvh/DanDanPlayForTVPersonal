package com.seiko.download.torrent.extensions

import com.seiko.download.torrent.TorrentEngine
import com.seiko.download.torrent.utils.log
import org.libtorrent4j.alerts.*

internal val ERROR_INNER_LISTENER_TYPES = intArrayOf(
    AlertType.SESSION_ERROR.swig(),
    AlertType.LISTEN_FAILED.swig(),
    AlertType.PORTMAP_ERROR.swig(),
    AlertType.TORRENT_ERROR.swig(),
    AlertType.METADATA_FAILED.swig(),
    AlertType.FILE_ERROR.swig()
)

fun TorrentEngine.checkError(alert: Alert<*>) {
    when(alert.type()) {
        AlertType.SESSION_ERROR -> {
            val sessionErrorAlert = alert as? SessionErrorAlert ?: return
            val error = sessionErrorAlert.error()
            if (error.isError) {
                getCallback()?.onSessionError(error.getErrorMsg())
            }
        }
        AlertType.LISTEN_FAILED -> {
            val listenFailedAlert = alert as? ListenFailedAlert ?: return
            val errorMsg = "Could not listen %:%d, type: %s (error: %s)".format(
                listenFailedAlert.address(),
                listenFailedAlert.port(),
                listenFailedAlert.socketType(),
                listenFailedAlert.error().getErrorMsg())
            getCallback()?.onSessionError(errorMsg)
        }
        AlertType.PORTMAP_ERROR -> {
            val portMapErrorAlert = alert as? PortmapErrorAlert ?: return
            val error = portMapErrorAlert.error()
            if (error.isError) {
                getCallback()?.onNatError(error.getErrorMsg())
            }
        }
        AlertType.TORRENT_ERROR -> {
            val errorAlert = alert as? TorrentErrorAlert ?: return

            val error = errorAlert.error()
            if (error.isError) {
                var filename = errorAlert.filename()
                val errorMsg = if (!filename.isNullOrEmpty()) {
                    filename = filename.substring(filename.lastIndexOf("/") + 1)
                    "$filename ${error.getErrorMsg()}"
                } else {
                    error.getErrorMsg()
                }

                val hash = errorAlert.handle().infoHash().toHex()
                getCallback()?.onTorrentError(hash, errorMsg)
            }
        }
        AlertType.METADATA_FAILED -> {
            val metadataFailedAlert = alert as? MetadataFailedAlert ?: return

            val error = metadataFailedAlert.error
            if (!error.isError) return

            val hash = metadataFailedAlert.handle().infoHash().toHex()
            getCallback()?.onTorrentError(hash, error.getErrorMsg())
        }
        AlertType.FILE_ERROR -> {
            val fileErrorAlert = alert as? FileErrorAlert ?: return

            val error = fileErrorAlert.error()
            if (!error.isError) return

            var filename = fileErrorAlert.filename()
            val errorMsg = if (!filename.isNullOrEmpty()) {
                filename = filename.substring(filename.lastIndexOf("/") + 1)
                "[$filename] ${error.getErrorMsg()}"
            } else {
                error.getErrorMsg()
            }

            val hash = fileErrorAlert.handle().infoHash().toHex()
            getCallback()?.onTorrentError(hash, errorMsg)
        }
        else -> log("Unhandled alert: $alert")
    }
}