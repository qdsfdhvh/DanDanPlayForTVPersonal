package debug

import android.app.Application
import com.blankj.utilcode.util.LogUtils
import com.seiko.data.di.*
import com.seiko.module.torrent.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        //Logs
        LogUtils.getConfig()
            .setLogHeadSwitch(false)
            .setBorderSwitch(false)

        startKoin {
            androidLogger()
            androidContext(this@App)

            modules(commonModule + listOf(
                viewModelModule
            ))
        }
    }

}