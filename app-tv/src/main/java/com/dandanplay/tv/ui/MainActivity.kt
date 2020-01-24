package com.dandanplay.tv.ui

import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.dandanplay.tv.R
import com.dandanplay.tv.extensions.hideSoftInput
import com.dandanplay.tv.extensions.isSoftInputMethodShowing
import androidx.activity.DispatchKeyEventDispatcher
import androidx.activity.DispatchKeyEventDispatcherOwner
import com.seiko.common.service.TorrentService

class MainActivity : FragmentActivity(R.layout.activity_main),
    DispatchKeyEventDispatcherOwner {

    private val dispatchKeyEventDispatcher =
        DispatchKeyEventDispatcher { event ->
            if (event?.action == KeyEvent.ACTION_DOWN
                && event.keyCode == KeyEvent.KEYCODE_BACK
            ) {
                if (isSoftInputMethodShowing()) {
                    hideSoftInput()
                    return@DispatchKeyEventDispatcher true
                }
            }
            return@DispatchKeyEventDispatcher super@MainActivity.dispatchKeyEvent(event)
        }

    /**
     * PS: Navigation在返回时，Fragment的View会重新绘制。
     */
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = this.findNavController(R.id.myNavHostFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 关闭Torrent下载
        TorrentService.get().shutDown(this)
    }

    /**
     * 当软键盘弹出时，关闭软键盘。
     */
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return getDispatchKeyEventDispatcher().dispatchKeyEvent(event)
    }

    override fun getDispatchKeyEventDispatcher(): DispatchKeyEventDispatcher {
        return dispatchKeyEventDispatcher
    }

}
