package com.seiko.common.router

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.exception.NoRouteFoundException
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.launcher.ARouter
import timber.log.Timber


object Navigator {

    /**
     * 跳转到DanDan数据展示页面
     */
    fun navToPlayTV(activity: Activity) {
//        Timber.d("Navigator - navToPlayTV")
        ARouter.getInstance().build(Routes.DanDanPlay.PATH_TV).navigation(activity)
    }

    /**
     * 跳转种子页面
     */
    fun navToTorrent(activity: Activity, callback: NavigationCallback? = null) {
        Timber.d("Navigator - navToTorrent")
        ARouter.getInstance().build(Routes.Torrent.PATH).navigation(activity, callback)
    }

    /**
     * 跳转种子信息页面
     */
    fun navToAddTorrent(activity: Activity, uri: Uri) {
        Timber.d("Navigator - navToAddTorrent")
        val postcard = ARouter.getInstance().build(Routes.Torrent.PATH_ADD)
        LogisticsCenter.completion(postcard)

        val intent = Intent(activity, postcard.destination)
        intent.data = uri
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtras(postcard.extras)

        activity.startActivity(intent)
    }

    /**
     * 跳转种子信息页面
     * PS: ARouter默认的navigation不支持fragment
     */
    fun navToAddTorrent(fragment: Fragment, uri: Uri, requestCode: Int) {
        val postcard = ARouter.getInstance().build(Routes.Torrent.PATH_ADD)
        LogisticsCenter.completion(postcard)

        val intent = Intent(fragment.requireContext(), postcard.destination)
        intent.data = uri
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtras(postcard.extras)

        fragment.startActivityForResult(intent, requestCode)
        Timber.d("navToAddTorrent")
    }

    /**
     * 跳转播放
     */
    fun navToPlayer(fragment: Fragment, videoUri: Uri, videoTitle: String, hash: String? = null) {
        val postcard = ARouter.getInstance().build(Routes.Player.PATH)
            .withParcelable(Routes.Player.ARGS_VIDEO_URI, videoUri)
            .withString(Routes.Player.ARGS_VIDEO_TITLE, videoTitle)
            .withString(Routes.Player.ARGS_VIDEO_HASH, hash)

        try {
            LogisticsCenter.completion(postcard)
        } catch (e: NoRouteFoundException) {
            // 没有注册界面，调用系统播放器
            navToSystemPlayer(fragment.requireContext(), videoUri, hash)
            return
        }

        val intent = Intent(fragment.requireContext(), postcard.destination)
        intent.putExtras(postcard.extras)

        fragment.startActivity(intent)
    }

    /**
     * 调用系统播放器
     */
    fun navToSystemPlayer(context: Context, videoUri: Uri, hash: String? = null) {
        val intent = Intent(Intent.ACTION_VIEW)
//        val headers = arrayOf("Cookie", cookie)
//        intent.putExtra("headers", headers)
        intent.setDataAndType(videoUri, "video/*")
        if (!hash.isNullOrEmpty()) {
            intent.putExtra(Routes.Player.ARGS_VIDEO_HASH, hash)
        }
        context.startActivity(intent)
    }
}