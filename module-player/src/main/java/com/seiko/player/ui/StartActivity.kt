package com.seiko.player.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Navigator
import com.seiko.common.router.Routes
import com.seiko.common.util.toast.toast
import com.seiko.player.data.model.PlayParam
import com.seiko.player.ui.video.VideoPlayerActivity
import com.seiko.player.util.FileUtils
import com.seiko.player.util.extensions.getRealPath
import com.seiko.player.util.getFileNameFromPath
import timber.log.Timber

@Route(path = Routes.Player.PATH)
class StartActivity : FragmentActivity() {

    companion object {
        private const val INTENT_TYPE_VIDEO = "video"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        checkIntent()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }

    private fun checkIntent() {
        val openIntent = intent
        if (openIntent == null) {
            openMediaActivity()
            return
        }

        //外部打开
        val videoUri = if (Intent.ACTION_VIEW == openIntent.action) {
            if (openIntent.type?.startsWith(INTENT_TYPE_VIDEO) != true) {
                toast("Bad Intent：$openIntent")
                openMediaActivity()
                return
            }
            intent.data
        } else {
            openIntent.getParcelableExtra(Routes.Player.ARGS_VIDEO_URI)
        }

        // 没有uri
        if (videoUri == null) {
            openMediaActivity()
            return
        }

        // 获取真实地址
        val videoPath = videoUri.getRealPath(this)
        if (videoPath == null) {
            openMediaActivity()
            return
        }

        // 获取视频标题
        var videoTitle: String? = openIntent.getStringExtra(Routes.Player.ARGS_VIDEO_TITLE)
        if (videoTitle.isNullOrEmpty()) {
            videoTitle = videoPath.getFileNameFromPath()
        }

        VideoPlayerActivity.launch(this, PlayParam(
            videoPath = videoPath,
            videoTitle = videoTitle
        ))
        finish()
    }

    private fun openMediaActivity() {
        Navigator.navToPlayerMedia(this)
        finish()
    }
}