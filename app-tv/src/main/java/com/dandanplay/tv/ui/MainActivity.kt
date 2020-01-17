package com.dandanplay.tv.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.LogUtils
import com.dandanplay.tv.R
import com.dandanplay.tv.extensions.hideSoftInput
import com.dandanplay.tv.extensions.isSoftInputMethodShowing
import com.seiko.common.activity.DispatchKeyEventDispatcher
import com.seiko.common.activity.DispatchKeyEventDispatcherOwner
import com.seiko.common.service.TorrentService

class MainActivity : FragmentActivity(), DispatchKeyEventDispatcherOwner {

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
        setContentView(R.layout.activity_main)
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
