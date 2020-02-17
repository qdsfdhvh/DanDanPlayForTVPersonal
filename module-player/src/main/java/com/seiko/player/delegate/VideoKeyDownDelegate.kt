package com.seiko.player.delegate

import android.view.KeyEvent
import androidx.leanback.widget.BaseGridView
import com.seiko.player.ui.video.VideoPlayerHandler
import com.seiko.player.vm.PlayerViewModel
import timber.log.Timber

class VideoKeyDownDelegate(
    private val viewModel: PlayerViewModel,
    private val handler: VideoPlayerHandler
): BaseGridView.OnKeyInterceptListener {

    // ok 23
    // ↑ 19
    // ↓ 20
    // ← 21
    // → 22
    // 菜单 82
    override fun onInterceptKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) return false
        val keyCode = event.keyCode
        Timber.d("keyCode=${event.keyCode}")
        when(keyCode) {
            KeyEvent.KEYCODE_BACK -> return false
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                if (KeyEvent.ACTION_UP == event.action) {
                    // 切换播放状态
                    viewModel.setVideoPlay()
                    return true
                }
                return false
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                return true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                handler.overlayShow()
                return true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
//                handler.seekDelta(-10000)
                return true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
//                handler.seekDelta(10000)
                return true
            }
            KeyEvent.KEYCODE_MENU -> {
                // 切换状态
                handler.optionsShow()
                return true
            }
            else -> return false
        }
    }

}