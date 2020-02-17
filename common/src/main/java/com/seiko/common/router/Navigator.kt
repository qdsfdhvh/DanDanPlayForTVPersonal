package com.seiko.common.router

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.core.LogisticsCenter
import com.alibaba.android.arouter.exception.NoRouteFoundException
import com.alibaba.android.arouter.facade.Postcard
import com.alibaba.android.arouter.facade.callback.InterceptorCallback
import com.alibaba.android.arouter.facade.callback.NavigationCallback
import com.alibaba.android.arouter.facade.service.InterceptorService
import com.alibaba.android.arouter.launcher.ARouter
import com.seiko.common.util.extensions.lazyAndroid
import timber.log.Timber


object Navigator {

    private val interceptorService by lazyAndroid {
        ARouter.getInstance().build("/arouter/service/interceptor").navigation() as InterceptorService
    }

    /**
     * 跳转到DanDan数据展示页面
     */
    fun navToPlayTV(activity: Activity) {
        val postcard = ARouter.getInstance().build(Routes.DanDanPlay.PATH_TV)

        try {
            LogisticsCenter.completion(postcard)
        } catch (e: NoRouteFoundException) {
            return
        }

        interceptorService.doInterceptions(postcard, object : InterceptorCallback {
            override fun onContinue(postcard: Postcard?) {
                if (postcard == null) return
                val intent = Intent(activity.baseContext, postcard.destination)
                intent.putExtras(postcard.extras)
                activity.startActivity(intent)
            }

            override fun onInterrupt(exception: Throwable?) {

            }
        })
    }

    /**
     * 跳转种子页面
     */
    fun navToTorrent(activity: Activity) {
        Timber.tag("Navigator").d("navToTorrent")
        val postcard = ARouter.getInstance().build(Routes.Torrent.PATH)

        try {
            LogisticsCenter.completion(postcard)
        } catch (e: NoRouteFoundException) {
            return
        }

        interceptorService.doInterceptions(postcard, object : InterceptorCallback {
            override fun onContinue(postcard: Postcard?) {
                if (postcard == null) return
                val intent = Intent(activity.baseContext, postcard.destination)
                intent.putExtras(postcard.extras)
                activity.startActivity(intent)
            }

            override fun onInterrupt(exception: Throwable?) {

            }
        })
    }

    /**
     * 跳转种子信息页面
     */
    fun navToAddTorrent(activity: Activity, torrentUri: Uri) {
        val postcard = ARouter.getInstance().build(Routes.Torrent.PATH_ADD)

        try {
            LogisticsCenter.completion(postcard)
        } catch (e: NoRouteFoundException) {
            // 没有注册界面，调用系统种子下载
            navToSystemAddTorrent(activity.baseContext, torrentUri)
            return
        }

        interceptorService.doInterceptions(postcard, object : InterceptorCallback {
            override fun onContinue(postcard: Postcard?) {
                if (postcard == null) return
                val intent = Intent(activity.baseContext, postcard.destination)
                intent.data = torrentUri
                intent.putExtras(postcard.extras)
                activity.startActivity(intent)
            }

            override fun onInterrupt(exception: Throwable?) {

            }
        })
    }

    /**
     * 跳转种子信息页面
     * PS: ARouter默认的navigation不支持fragment
     */
    fun navToAddTorrent(fragment: Fragment, magnetUri: Uri, requestCode: Int) {
        val postcard = ARouter.getInstance().build(Routes.Torrent.PATH_ADD)

        try {
            LogisticsCenter.completion(postcard)
        } catch (e: NoRouteFoundException) {
            // 没有注册界面，调用系统种子下载
            navToSystemAddTorrent(fragment.requireActivity().baseContext, magnetUri)
            return
        }

        interceptorService.doInterceptions(postcard, object : InterceptorCallback {
            override fun onContinue(postcard: Postcard?) {
                if (postcard == null) return
                val intent = Intent(fragment.requireActivity().baseContext, postcard.destination)
                intent.data = magnetUri
                intent.putExtras(postcard.extras)
                fragment.startActivityForResult(intent, requestCode)
            }

            override fun onInterrupt(exception: Throwable?) {

            }
        })
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
     * 跳转播放
     */
    fun navToPlayer(fragment: Fragment, videoUri: Uri, videoTitle: String) {
        val postcard = ARouter.getInstance().build(Routes.Player.PATH)
            .withParcelable(Routes.Player.ARGS_VIDEO_URI, videoUri)
            .withString(Routes.Player.ARGS_VIDEO_TITLE, videoTitle)

        try {
            LogisticsCenter.completion(postcard)
        } catch (e: NoRouteFoundException) {
            // 没有注册界面，调用系统播放器
            navToSystemPlayer(fragment.requireContext(), videoUri)
            return
        }

        interceptorService.doInterceptions(postcard, object : InterceptorCallback {
            override fun onContinue(postcard: Postcard?) {
                if (postcard == null) return
                val intent = Intent(fragment.requireContext(), postcard.destination)
                intent.putExtras(postcard.extras)
                fragment.startActivity(intent)
            }

            override fun onInterrupt(exception: Throwable?) {

            }
        })
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