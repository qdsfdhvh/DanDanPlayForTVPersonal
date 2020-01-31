package com.seiko.torrent

import android.content.Context
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.annotation.Interceptor
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.template.IInterceptor
import com.alibaba.android.arouter.launcher.ARouter
import com.seiko.common.router.Routes

/**
 * 将所有跳转到TorrentMainActivity的改为DebugActivity
 */
@Interceptor(name = "debug", priority = 1)
class DebugInterceptor : IInterceptor {
    override fun process(postcard: Postcard, callback: InterceptorCallback) {
        val path = postcard.path
        if (path == Routes.Torrent.PATH) {
            callback.onInterrupt(null)
            ARouter.getInstance().build("/torrent/torrentDebug").navigation()
        } else {
            callback.onContinue(postcard)
        }
    }

    override fun init(context: Context?) {

    }
}