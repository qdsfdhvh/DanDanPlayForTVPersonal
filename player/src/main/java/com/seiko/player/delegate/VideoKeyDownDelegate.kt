package com.seiko.player.delegate

import android.view.KeyEvent
import com.seiko.player.media.IPlayerController
import com.seiko.player.ui.VideoPlayerHandler

class VideoKeyDownDelegate(private val handler: VideoPlayerHandler) {

    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode) {
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_BUTTON_B -> {
                return false
            }
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
//                touchDelegate?.seekDelta(10000)
                return true
            }
            KeyEvent.KEYCODE_MEDIA_REWIND -> {
//                touchDelegate?.seekDelta(-10000)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_R1 -> {
//                touchDelegate?.seekDelta(60000)
                return true
            }
            KeyEvent.KEYCODE_BUTTON_L1 -> {
//                touchDelegate?.seekDelta(-60000)
                return true
            }
            else -> return false
        }
    }



}