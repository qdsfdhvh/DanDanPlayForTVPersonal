package com.dandanplay.tv

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.seiko.common.router.Navigator
import com.seiko.common.util.helper.AutoAdaptStrategyIgnore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal const val FLAGS_FULLSCREEN =
    View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

/**
 * 暂时使用bilibili TV端上的启动背景
 */
class SplashActivity : FragmentActivity(R.layout.activity_splash)
    , AutoAdaptStrategyIgnore {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = FLAGS_FULLSCREEN
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            delay(400)
            Navigator.navToPlayTV(this@SplashActivity)
            finish()
        }
    }

}