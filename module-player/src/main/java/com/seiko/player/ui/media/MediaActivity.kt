package com.seiko.player.ui.media

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.player.R
import com.seiko.player.service.MediaParsingIntentService
import com.seiko.player.util.bitmap.BitmapCache

//@Route(path = Routes.Player.PATH_MEDIA)
//class MediaActivity : FragmentActivity(R.layout.player_activity_media) {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        lifecycleScope.launchWhenStarted {
//            MediaParsingIntentService.scanDiscovery(this@MediaActivity)
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        BitmapCache.clear()
//    }
//
//}