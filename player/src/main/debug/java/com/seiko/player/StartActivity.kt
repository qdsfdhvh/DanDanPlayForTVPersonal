package com.seiko.player

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.seiko.player.service.PlaybackService
import timber.log.Timber

class StartActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        resume()
    }

    private fun resume() {
        val intent = intent
        val action = intent?.action

        if (Intent.ACTION_SEND == action) {
            val cd = intent.clipData
            val item = if (cd != null && cd.itemCount > 0) {
                cd.getItemAt(0)
            } else null
            if (item != null) {
                var uri = item.uri
                Timber.d("uri = $uri")
                if (uri == null && item.text != null) {
                    uri = Uri.parse(item.text.toString())
                }
                if (uri != null) {
                    PlaybackService.openMediaNoUi(this, uri)
                    finish()
                    return
                }
            }
        }
    }


}