package com.seiko.common.router

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.launcher.ARouter

/**
 * 路由路径管理
 */
object Routes {

    /**
     * 种子下载
     */
    object Torrent {
        const val PATH = "/torrent/torrentActivity"

        const val KEY_TORRENT_PATH = "torrentPath"

        const val RESULT_KEY_ADD_SUCCESS = "addSuccess"
        const val RESULT_KEY_ADD_HASH = "addHash"
    }

    /**
     * 播放器
     */
    object Player {
        const val PATH = "/player/playerManagerActivity"
    }

    /**
     * 跳转种子页面
     * PS: ARouter默认的navigation不支持fragment
     */
    fun navigationToTorrent(fragment: Fragment, uri: Uri, requestCode: Int) {
        val postcard = ARouter.getInstance().build(Torrent.PATH)
            .withParcelable(Torrent.KEY_TORRENT_PATH, uri)
        LogisticsCenter.completion(postcard)

        val intent = Intent(fragment.requireContext(), postcard.destination)
        intent.putExtras(postcard.extras)

        fragment.startActivityForResult(intent, requestCode)
    }

    private val services = HashMap<String, Any>()

    @Synchronized
    fun addService(serviceName: String?, serviceImpl: Any?) {
        if (serviceName == null || serviceImpl == null) {
            return
        }
        services[serviceName] = serviceImpl
    }

    @Synchronized
    fun getService(serviceName: String?): Any? {
        return if (serviceName == null) {
            null
        } else services[serviceName]
    }

    @Synchronized
    fun removeService(serviceName: String?) {
        if (serviceName == null) {
            return
        }
        services.remove(serviceName)
    }

}