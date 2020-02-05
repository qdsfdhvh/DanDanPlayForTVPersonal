package com.seiko.player.media

interface PlayerViewListener {
    fun onResume()

    fun onPause()

    fun onDestroy()
}