package com.seiko.player.ui.media

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.common.util.extensions.checkPermissions
import com.seiko.player.R
import com.seiko.player.service.MediaParsingService
import com.seiko.player.vm.VideosViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import timber.log.Timber

@Route(path = Routes.Player.PATH_MEDIA)
class MediaActivity : FragmentActivity(R.layout.player_activity_media) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            MediaParsingService.reloadMediaLibrary(this@MediaActivity)
        }
    }

}