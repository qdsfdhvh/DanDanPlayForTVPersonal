package com.dandanplay.tv

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.seiko.common.router.Navigator
import com.seiko.common.util.helper.AutoAdaptStrategyIgnore

/**
 * 暂时使用bilibili TV端上的启动背景
 */
class SplashActivity : FragmentActivity()
    , AutoAdaptStrategyIgnore {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Navigator.navToPlayTV(this@SplashActivity)
        finish()
    }

}