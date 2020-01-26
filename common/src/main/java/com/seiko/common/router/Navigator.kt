package com.seiko.common.router

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.launcher.ARouter

object Navigator {

    /**
     * 跳转种子页面
     */
    fun navToTorrent(activity: Activity) {
        ARouter.getInstance().build(Routes.Torrent.PATH)
            .navigation(activity)
    }

    fun navToAddTorrent(activity: Activity, uri: Uri) {
        ARouter.getInstance().build(Routes.Torrent.PATH_ADD)
            .withParcelable(Routes.Torrent.KEY_TORRENT_URI, uri)
            .navigation(activity)
    }

    /**
     * 跳转种子页面
     * PS: ARouter默认的navigation不支持fragment
     */
    fun navToAddTorrent(fragment: Fragment, uri: Uri, requestCode: Int) {
        val postcard = ARouter.getInstance().build(Routes.Torrent.PATH_ADD)
            .withParcelable(Routes.Torrent.KEY_TORRENT_URI, uri)
        LogisticsCenter.completion(postcard)

        val intent = Intent(fragment.requireContext(), postcard.destination)
        intent.putExtras(postcard.extras)

        fragment.startActivityForResult(intent, requestCode)
    }

}