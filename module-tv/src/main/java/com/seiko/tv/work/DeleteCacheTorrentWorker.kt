package com.seiko.tv.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.seiko.tv.domain.DeleteCacheTorrentUseCase
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

/**
 * 删除下载目录下过期的的种子文件
 */
class DeleteCacheTorrentWorker(context: Context,
                               parameters: WorkerParameters
) : CoroutineWorker(context, parameters), KoinComponent {

    override suspend fun doWork(): Result {
        Timber.d("清空过期种子。")
        val deleteCacheTorrent: DeleteCacheTorrentUseCase by inject()
        deleteCacheTorrent.invoke()
        return Result.success()
    }

}