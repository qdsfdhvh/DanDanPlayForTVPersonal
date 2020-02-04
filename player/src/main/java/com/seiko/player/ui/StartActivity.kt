package com.seiko.player.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.common.util.toast.toast
import com.seiko.player.data.model.PlayParam
import com.seiko.player.util.FileUtil
import com.seiko.player.util.constants.INTENT_TYPE_VIDEO
import timber.log.Timber

@Route(path = Routes.Player.PATH)
class StartActivity : FragmentActivity() {

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
            finish()
            return
        }

        var videoUri: Uri? = null
        var videoTitle: String? = null

        //外部打开
        if (Intent.ACTION_VIEW == openIntent.action) {
            if (openIntent.type?.startsWith(INTENT_TYPE_VIDEO) != true) {
                toast("Bad Intent：$openIntent")
                finish()
                return
            }

            // 获取真实地址
            val data = intent.data
            if (data != null) {
                val videoPath = FileUtil.getRealFilePath(this, data)
                videoUri = Uri.parse(videoPath)
                videoTitle = FileUtil.getFileName(videoPath)
            }

        } else {
            videoUri = openIntent.getParcelableExtra(Routes.Player.ARGS_VIDEO_URI)
            videoTitle = openIntent.getStringExtra(Routes.Player.ARGS_VIDEO_TITLE)
        }

        Timber.d(videoUri.toString())
        Timber.d(videoTitle)

        if (videoUri == null) {
            finish()
            return
        }

        VideoPlayerActivity.launch(this, PlayParam(
            videoUri = videoUri
        ))
        finish()
    }

}