package com.seiko.common.provider

import android.app.Application
import com.seiko.common.di.moshiModule
import com.seiko.common.di.networkModule
import com.seiko.common.util.initFresco
import com.seiko.common.util.prefs.initMMKV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jessyan.autosize.AutoSizeConfig
import org.koin.core.context.loadKoinModules
import timber.log.Timber

/**
 * @description：插件自动加载该类，实现服务注册，需要指定在gradle.properties中application
 */
class ProviderApplication : IProviderApplication {

    override fun onCreate(application: Application) {
        Timber.tag("Provider").d("start register common.")

        // 关闭AutoSize日志
        AutoSizeConfig.getInstance().setLog(false)

        loadKoinModules( listOf(
            // JSON
            moshiModule,
            // 本地存储、网络请求
            networkModule
        ))

        GlobalScope.launch(Dispatchers.IO) {
            // 初始化MMKV
            application.initMMKV()

            // 初始化Fresco
            application.initFresco()
        }
    }

    override fun onTerminate() {

    }
}