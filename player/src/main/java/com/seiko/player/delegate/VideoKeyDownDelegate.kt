package com.seiko.player.delegate

import android.view.KeyEvent
import com.seiko.player.ui.VideoPlayerHandler
import timber.log.Timber

class VideoKeyDownDelegate(private val handler: VideoPlayerHandler) {

    fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) return false
        Timber.d("keyCode=${event.keyCode}")
        when(event.keyCode) {
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_BUTTON_B -> {
                return false
            }
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                handler.seekDelta(10000)
                return true
            }
            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                handler.seekDelta(-10000)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_R1 -> {
                handler.seekDelta(60000)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_L1 -> {
                handler.seekDelta(-60000)
                return true
            }

            else -> return false
        }
    }



}