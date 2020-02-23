package com.seiko.player.ui.media

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.player.R
import com.seiko.player.service.MediaParsingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Route(path = Routes.Player.PATH_MEDIA)
class MediaActivity : FragmentActivity(R.layout.player_activity_media) {
    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.IO) {
            MediaParsingService.reloadMediaLibrary(this@MediaActivity)
        }
    }
}