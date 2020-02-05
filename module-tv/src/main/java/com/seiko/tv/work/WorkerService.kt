package com.seiko.tv.work

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class WorkerService {

    private val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.UNMETERED) // 非断网
        .setRequiresCharging(true)  // 充电状态
        .setRequiresBatteryNotLow(true) // 非低电量
        .build()

    private val repeatJob = PeriodicWorkRequestBuilder<DeleteCacheTorrentWorker>(1, TimeUnit.DAYS)
        .setConstraints(constraints)
        .build()

    fun scheduleDeleteCacheTorrent(context: Context) {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "TAG_DELETE_CACHE_TORRENT",
            ExistingPeriodicWorkPolicy.KEEP,
            repeatJob)
    }
}