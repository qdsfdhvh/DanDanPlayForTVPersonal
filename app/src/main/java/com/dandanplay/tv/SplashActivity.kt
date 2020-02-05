package com.dandanplay.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.seiko.common.router.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : FragmentActivity(R.layout.activity_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            delay(400)
            Navigator.navToPlayTV(this@SplashActivity)
        }
    }
}