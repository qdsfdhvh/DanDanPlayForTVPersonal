package com.seiko.player.delegate

import android.view.KeyEvent
import com.seiko.player.data.model.PlayerOption
import com.seiko.player.ui.VideoPlayerHandler
import timber.log.Timber

class VideoKeyDownDelegate(private val handler: VideoPlayerHandler) {

    // ok 23
    // ↑ 19
    // ↓ 20
    // ← 21
    // → 22
    // 菜单 82
    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event == null) return false
        Timber.d("keyCode=${event.keyCode}")
        when(keyCode) {
            KeyEvent.KEYCODE_BACK -> return false
            KeyEvent.KEYCODE_DPAD_UP -> {
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                handler.overlayShow()
                return true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                handler.seekDelta(-10000)
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                handler.seekDelta(10000)
                return true
            }
            KeyEvent.KEYCODE_MENU -> {
                handler.optionsShow(true, PlayerOption.PlayerOptionType.ADVANCED)
                return true
            }
//            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
//                handler.seekDelta(10000)
//                return true
//            }
//            KeyEvent.KEYCODE_MEDIA_REWIND -> {
//                handler.seekDelta(-10000)
//                return true
//            }
//            KeyEvent.KEYCODE_BUTTON_R1 -> {
//                handler.seekDelta(60000)
//                return true
//            }
//            KeyEvent.KEYCODE_BUTTON_L1 -> {
//                handler.seekDelta(-60000)
//                return true
//            }
            else -> return false
        }
    }



}