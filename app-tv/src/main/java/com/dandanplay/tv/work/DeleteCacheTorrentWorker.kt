package com.dandanplay.tv.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.blankj.utilcode.util.LogUtils
import com.seiko.data.usecase.DeleteCacheTorrentUseCase
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * 删除下载目录下过期的的种子文件
 */
class DeleteCacheTorrentWorker(context: Context,
                               parameters: WorkerParameters
) : CoroutineWorker(context, parameters), KoinComponent {

    override suspend fun doWork(): Result {
        LogUtils.d("清空过期种子。")
        val deleteCacheTorrent: DeleteCacheTorrentUseCase by inject()
        deleteCacheTorrent.invoke()
        return Result.success()
    }

}