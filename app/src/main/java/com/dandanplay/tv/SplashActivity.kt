package com.dandanplay.tv

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.seiko.common.util.helper.AutoAdaptStrategyIgnore

/**
 * 暂时使用bilibili TV端上的启动背景
 */
class SplashActivity : FragmentActivity()
    , AutoAdaptStrategyIgnore {

    companion object {
        private const val MAIN_ACTIVITY_CLASS_NAME = "com.seiko.tv.ui.main.MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ARouter已放入子线程初始化，这次不能使用ARouter跳转
        val intent = Intent()
        intent.setClassName(applicationContext, MAIN_ACTIVITY_CLASS_NAME)
        startActivity(intent)
        finish()
    }

}