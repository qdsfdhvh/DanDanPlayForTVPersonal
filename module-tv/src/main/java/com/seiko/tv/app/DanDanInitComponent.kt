package com.seiko.tv.app

import android.app.Application
import android.content.res.Configuration
import com.seiko.tv.di.*
import com.seiko.common.app.InitComponent
import com.seiko.tv.util.clearFrescoMemory
import com.seiko.tv.util.fix.InputMethodManagerFix
import com.seiko.tv.util.initFresco
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import timber.log.Timber

/**
 * @description：插件自动加载该类，实现服务注册，需要指定在gradle.properties中application
 */
class DanDanInitComponent : InitComponent {

    override fun onCreate(application: Application) {
        Timber.tag("Provider").d("start register app-tv.")

        loadKoinModules(listOf(
            // 本地配置、数据库、网络请求
            prefModule, dbModule, networkModule,
            // ...
            repositoryModule,
            // 实例
            useCaseModule,
            // viewModel
            viewModelModule
        ))

        GlobalScope.launch(Dispatchers.IO) {
            // 初始化Fresco
            application.initFresco()
        }

        InputMethodManagerFix.fixFocusedViewLeak(application)
    }

    override fun onLowMemory() {
        clearFrescoMemory()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {

    }
}