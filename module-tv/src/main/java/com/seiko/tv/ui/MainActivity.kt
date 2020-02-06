package com.seiko.tv.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.DispatchKeyEventDispatcher
import androidx.activity.DispatchKeyEventDispatcherOwner
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.common.service.TorrentService
import com.seiko.tv.R
import com.seiko.tv.util.extensions.hideSoftInput
import com.seiko.tv.util.extensions.isSoftInputMethodShowing
import timber.log.Timber

@Route(path = Routes.DanDanPlay.PATH_TV)
class MainActivity : FragmentActivity(R.layout.activity_main), DispatchKeyEventDispatcherOwner {

    @SuppressLint("RestrictedApi")
    private val dispatchKeyEventDispatcher = DispatchKeyEventDispatcher { event ->
        // 当软键盘弹出时，关闭软键盘。
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
    }

    /**
     * PS: Navigation在返回时，Fragment的View会重新绘制。
     */

    override fun onDestroy() {
        super.onDestroy()
        // 关闭Torrent下载
        TorrentService.get()?.shutDown(this)
        Timber.d("onCreate")
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return getDispatchKeyEventDispatcher().dispatchKeyEvent(event)
    }

    override fun getDispatchKeyEventDispatcher(): DispatchKeyEventDispatcher {
        return dispatchKeyEventDispatcher
    }

}
