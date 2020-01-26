package com.seiko.common.router

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.facade.service.PretreatmentService
import com.alibaba.android.arouter.launcher.ARouter
import timber.log.Timber

object Navigator {

    /**
     * 跳转种子页面
     */
    fun navToTorrent(activity: Activity, callback: NavigationCallback? = null) {
        ARouter.getInstance().build(Routes.Torrent.PATH).navigation(activity, callback)
    }

    /**
     * 跳转种子信息页面
     */
    fun navToAddTorrent(activity: Activity, uri: Uri) {
        val postcard = ARouter.getInstance().build(Routes.Torrent.PATH_ADD)
        LogisticsCenter.completion(postcard)

        val intent = Intent(activity, postcard.destination)
        intent.data = uri
        intent.putExtras(postcard.extras)

        activity.startActivity(intent)
    }

    /**
     * 跳转种子信息页面
     * PS: ARouter默认的navigation不支持fragment
     */
    fun navToAddTorrent(fragment: Fragment, uri: Uri, requestCode: Int) {
        val postcard = ARouter.getInstance().build(Routes.Torrent.PATH_ADD)
//            .withParcelable(Routes.Torrent.KEY_TORRENT_URI, uri)
        LogisticsCenter.completion(postcard)

        val intent = Intent(fragment.requireContext(), postcard.destination)
        intent.data = uri
        intent.putExtras(postcard.extras)

        fragment.startActivityForResult(intent, requestCode)
    }

}