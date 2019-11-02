package com.seiko.download.status

import com.frostwire.jlibtorrent.TorrentHandle
import com.frostwire.jlibtorrent.TorrentStatus
import com.seiko.download.extensions.*
import com.seiko.download.extensions.getDownloadRate
import com.seiko.download.extensions.getProgress
import com.seiko.download.extensions.getSeederCount
import com.seiko.download.extensions.getUploadRate
import com.seiko.download.utils.formatSize
import com.seiko.download.utils.ratio


class Progress(
    val state: TorrentStatus.State = TorrentStatus.State.UNKNOWN,
    val seederCount: Int = 0,
    val downloadRate: Int = 0,
    val uploadRate: Int = 0,
    val progress: Float = 0f,
    val bytesDownloaded: Long = 0,
    val bytesWanted: Long = 0,
    val buffer: Buffer = Buffer()
) {

    companion object {
        fun createInstance(torrentHandle: TorrentHandle, buffer: Buffer): Progress {
            return Progress(
                state = torrentHandle.status().state(),
                seederCount = torrentHandle.getSeederCount(),
                downloadRate = torrentHandle.getDownloadRate(),
                uploadRate = torrentHandle.getUploadRate(),
                progress = torrentHandle.getProgress(),
                bytesDownloaded = torrentHandle.getTotalDone(),
                bytesWanted = torrentHandle.getTotalWanted(),
                buffer = buffer
            )
        }
    }

    data class Buffer(
        val bufferSize: Int = 0,
        val startIndex: Int = 0,
        val endIndex: Int = 0
    ) {

        /**
         * The total number of pieces.
         */
        val pieceCount = if (startIndex == 0 && endIndex == 0) 0 else (endIndex - startIndex) + 1

        /**
         * The number of pieces downloaded.
         */
        var downloadedPieceCount = 0
            @Synchronized
            get
            private set

        /**
         * The index of the head of the buffer.
         */
        var bufferHeadIndex = startIndex
            @Synchronized
            get
            private set

        /**
         * The index of the tail of the buffer.
         */
        var bufferTailIndex = if (bufferSize == 0) endIndex else startIndex + bufferSize - 1
            @Synchronized
            get
            private set

        /**
         * The index of the last downloaded piece. If no pieces were downloaded, then
         * this value will be -1.
         */
        var lastDownloadedPieceIndex = -1
            @Synchronized
            get
            private set

        private val pieceDownloadStates = BooleanArray(pieceCount)

        /**
         * Determine if all pieces are downloaded.
         */
        @Synchronized
        fun allPiecesAreDownloaded() = !pieceDownloadStates.contains(false)

        /**
         * Check if piece at [index] is downloaded.
         */
        @Synchronized
        fun isPieceDownloaded(index: Int) = pieceDownloadStates[index]

        @Synchronized
        internal fun setPieceDownloaded(index: Int): Boolean {
            // Ignore if less than head or already downloaded.
            if (index < bufferHeadIndex || isPieceDownloaded(index)) {
                return true
            }

            pieceDownloadStates[index] = true
            lastDownloadedPieceIndex = index
            downloadedPieceCount++

            // Buffer head was downloaded, advance the buffer a position.
            if (index == bufferHeadIndex) {
                bufferHeadIndex = index

                while (bufferHeadIndex < pieceDownloadStates.size
                    && pieceDownloadStates[bufferHeadIndex]) {
                    bufferHeadIndex++
                    bufferTailIndex++
                }
            }

            // Don't let the tail of the buffer go past the last piece.
            bufferTailIndex = Math.min(bufferTailIndex, endIndex)

            if (allPiecesAreDownloaded()) {
                bufferHeadIndex = bufferTailIndex
            }

            return false
        }

        @Synchronized
        override fun toString(): String = "Total Pieces: $pieceCount" +
                ", Start: $startIndex" +
                ", End: $endIndex" +
                ", Head: $bufferHeadIndex" +
                ", Tail: $bufferTailIndex" +
                ", Last Piece Downloaded Index: $lastDownloadedPieceIndex" +
                ", All Pieces Downloaded: ${allPiecesAreDownloaded()}"
    }
}