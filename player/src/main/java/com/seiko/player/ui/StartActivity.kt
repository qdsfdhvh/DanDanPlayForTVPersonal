package com.seiko.player.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.player.data.model.PlayParam
import com.seiko.player.util.getFileName
import com.seiko.player.util.getRealFilePath
import timber.log.Timber

@Route(path = Routes.Player.PATH)
class StartActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }

    private fun resume() {
        val openIntent = intent
        val action = openIntent?.action

        var videoPath: String? = null

        if (Intent.ACTION_VIEW == action) {
            startPlayback(openIntent)
            //获取视频地址
            val data = intent.data
            if (data != null) {
                videoPath = getRealFilePath(this, data)
            }
        }

        if (videoPath.isNullOrEmpty()) {
            finish()
            return
        }

        val videoTitle = getFileName(videoPath) ?: ""

        VideoPlayerActivity.launch(this, PlayParam(
            videoTitle = videoTitle,
            videoUri = Uri.parse(videoPath)
        ))
        finish()
    }


    private fun startPlayback(intent: Intent) {
        when {
            intent.type?.startsWith("video") == true -> {
                startActivity(intent.setClass(this, VideoPlayerActivity::class.java))
                finish()
            }
        }
    }

}