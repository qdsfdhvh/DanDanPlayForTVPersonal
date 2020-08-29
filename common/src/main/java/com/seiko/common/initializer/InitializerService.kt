package com.seiko.common.initializer

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.seiko.common.BuildConfig
import com.seiko.common.router.Navigator

class InitializerService : IntentService("InitializerService") {

    companion object {
        private const val ACTION_START = "ACTION_START"
        fun start(context: Context) {
            val intent = Intent(context, InitializerService::class.java)
            intent.action = ACTION_START
            context.startService(intent)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        when(intent?.action) {
            ACTION_START -> startInitial()
        }
    }

    private fun startInitial() {
        // 初始化路由
        Navigator.init(application, BuildConfig.DEBUG)
    }

}