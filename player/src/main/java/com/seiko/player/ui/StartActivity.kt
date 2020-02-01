package com.seiko.player.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import timber.log.Timber

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
        val intent = intent
        val action = intent?.action

        if (Intent.ACTION_VIEW == action) {
            startPlayback(intent)
        } else if (Intent.ACTION_SEND == action) {
            val cd = intent.clipData
            val item = if (cd != null && cd.itemCount > 0) {
                cd.getItemAt(0)
            } else null
            if (item != null) {
                var uri = item.uri
                if (uri == null && item.text != null) {
                    uri = Uri.parse(item.text.toString())
                }
                if (uri != null) {
//                    PlaybackService.openMediaNoUi(this, uri)
                    finish()
                    return
                }
            }
        }
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