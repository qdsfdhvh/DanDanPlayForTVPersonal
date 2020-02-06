package com.dandanplay.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.seiko.common.router.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * 暂时使用bilibili TV端上的启动背景
 */
class SplashActivity : FragmentActivity(R.layout.activity_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        lifecycleScope.launch {
            delay(800)
            Navigator.navToPlayTV(this@SplashActivity)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }

}