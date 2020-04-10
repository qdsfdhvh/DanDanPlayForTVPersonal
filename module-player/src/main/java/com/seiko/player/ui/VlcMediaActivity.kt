package com.seiko.player.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes

@Route(path = Routes.Player.PATH_MEDIA)
class MediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(applicationContext, org.videolan.vlc.StartActivity::class.java)
        intent.action = Intent.ACTION_MAIN
        startActivity(intent)
        finish()
    }
}