package com.seiko.download.torrent.annotation

import androidx.annotation.IntDef


@IntDef(
   value = [
       TorrentStateCode.UNKNOWN,
       TorrentStateCode.ERROR,
       TorrentStateCode.SEEDING,
       TorrentStateCode.DOWNLOADING,
       TorrentStateCode.PAUSED,
       TorrentStateCode.STOPPED,
       TorrentStateCode.CHECKING,
       TorrentStateCode.DOWNLOADING_METADATA,
       TorrentStateCode.FINISHED,
       TorrentStateCode.ALLOCATING
   ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class TorrentStateCode {
    companion object {
        const val UNKNOWN = -1
        const val ERROR = 0
        /*
         * In this state the torrent has finished downloading and
         * is a pure seeder.
         */
        const val SEEDING = 1
        /*
         * The torrent is being downloaded. This is the state
         * most torrents will be in most of the time. The progress
         * meter will tell how much of the files that has been
         * downloaded.
         */
        const val DOWNLOADING = 2
        const val PAUSED = 3
        const val STOPPED = 4
        /*
         * The torrent has not started its download yet, and is
         * currently checking existing files.
         */
        const val CHECKING = 5
        /*
         * The torrent is trying to download metadata from peers.
         * This assumes the metadata_transfer extension is in use.
         */
        const val DOWNLOADING_METADATA = 6
        /*
         * In this state the torrent has finished downloading but
         * still doesn't have the entire torrent. i.e. some pieces
         * are filtered and won't get downloaded.
         */
        const val FINISHED = 7
        /*
         * If the torrent was started in full allocation mode, this
         * indicates that the (disk) storage for the torrent is
         * allocated.
         */
        const val ALLOCATING = 8
    }
}