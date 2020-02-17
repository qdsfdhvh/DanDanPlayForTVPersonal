package com.seiko.player.ui.media

import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.player.R

@Route(path = Routes.Player.PATH_MEDIA)
class MediaActivity : FragmentActivity(R.layout.player_activity_media)