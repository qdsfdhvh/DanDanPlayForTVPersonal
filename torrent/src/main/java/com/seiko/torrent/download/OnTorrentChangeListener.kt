package com.seiko.torrent.download

interface OnTorrentChangeListener {
    fun onChange(@Action action: Int, hash: String)

    annotation class Action {
        companion object {
            const val ERROR = 1
            const val ADDED = 10
            const val REMOVED = 11
            const val CHANGED = 12
            const val PAUSED = 13
            const val FINISHED = 14
            const val RESUMED = 15
        }
    }
}