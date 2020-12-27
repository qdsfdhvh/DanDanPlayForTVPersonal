package com.seiko.common.router

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.chenenyu.router.Router
import com.seiko.common.util.VlcUtils


object Navigator {

    /**
     * 跳转到DanDan数据展示页面
     */
    fun navToPlayTV(activity: Activity) {
        Router.build(Routes.DanDanPlay.PATH_TV).go(activity)
    }

    /**
     * 跳转种子页面
     */
    fun navToTorrent(activity: Activity) {
        Router.build(Routes.Torrent.PATH).go(activity)
    }

    /**
     * 跳转种子信息页面
     */
    fun navToAddTorrent(activity: Activity, torrentUri: Uri) {
        Router.build(Routes.Torrent.PATH_ADD)
            .setData(torrentUri)
            .go(activity)
    }

    /**
     * 跳转种子信息页面
     * PS: ARouter默认的navigation不支持fragment
     */
    fun navToAddTorrent(fragment: Fragment, magnetUri: Uri, requestCode: Int) {
        Router.build(Routes.Torrent.PATH_ADD)
            .setData(magnetUri)
            .requestCode(requestCode)
            .go(fragment)
    }

    /**
     * 跳转媒体库
     */
    fun navToPlayerMedia(activity: Activity) {
        if (VlcUtils.isInstall(activity)) {
            VlcUtils.launchMedia(activity)
        }
    }

    /**
     * 跳转播放
     */
    fun navToPlayer(fragment: Fragment, videoUri: Uri, videoTitle: String) {
        val context = fragment.requireActivity()
        if (VlcUtils.isInstall(context)) {
            VlcUtils.launchVideo(context, videoUri, videoTitle)
        } else {
            navToSystemPlayer(fragment.requireContext(), videoUri)
        }
    }

    /**
     * 调用系统种子下载
     */
    private fun navToSystemAddTorrent(context: Context, torrentUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = torrentUri
        context.startActivity(intent)
    }

    /**
     * 调用系统播放器
     */
    private fun navToSystemPlayer(context: Context, videoUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(videoUri, "video/*")
        context.startActivity(intent)
    }
}