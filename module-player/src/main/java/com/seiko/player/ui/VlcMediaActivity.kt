package com.seiko.player.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import org.videolan.resources.EXTRA_FIRST_RUN
import org.videolan.resources.EXTRA_UPGRADE
import org.videolan.resources.PREF_FIRST_RUN
import org.videolan.tools.Settings
import org.videolan.vlc.BuildConfig

@Route(path = Routes.Player.PATH_MEDIA)
class MediaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settings = Settings.getInstance(this)
        val currentVersionNumber = BuildConfig.VERSION_CODE
        val savedVersionNumber = settings.getInt(PREF_FIRST_RUN, -1)

        val firstRun = savedVersionNumber == -1
        val upgrade = firstRun || savedVersionNumber != currentVersionNumber

        // 直接跳TV页
        val intent = Intent(this, org.videolan.television.ui.MainTvActivity::class.java)
            .putExtra(EXTRA_FIRST_RUN, firstRun)
            .putExtra(EXTRA_UPGRADE, upgrade)

        startActivity(intent)
        finish()
    }
}