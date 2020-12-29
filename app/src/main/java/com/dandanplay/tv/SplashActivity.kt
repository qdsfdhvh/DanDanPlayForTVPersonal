package com.dandanplay.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.seiko.common.router.Navigator
import com.seiko.common.util.helper.AutoAdaptStrategyIgnore

class SplashActivity : FragmentActivity()
    , AutoAdaptStrategyIgnore {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Navigator.navToPlayTV(this)
        finish()
    }

}