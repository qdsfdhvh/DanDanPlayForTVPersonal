package com.seiko.player.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes

@Route(path = Routes.Player.PATH)
class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        org.videolan.vlc.StartActivity()
    }

}