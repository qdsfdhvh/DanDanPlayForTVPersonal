package com.seiko.player.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes

@Route(path = Routes.Player.PATH)
class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIntent()
    }

    private fun checkIntent() {
        val openIntent = intent
        val intent = Intent(applicationContext, org.videolan.vlc.StartActivity::class.java)

        val videoUri: Uri? = openIntent?.getParcelableExtra(Routes.Player.ARGS_VIDEO_URI)
        if (videoUri != null) {
            intent.action = Intent.ACTION_VIEW
            intent.setDataAndType(videoUri, "video/*")
        }

        startActivity(intent)
        finish()
    }

}