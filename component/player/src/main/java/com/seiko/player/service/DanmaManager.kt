package com.seiko.player.service

import com.seiko.common.data.Result
import com.seiko.player.domain.GetDanmaResultWithFileUseCase
import com.seiko.player.domain.GetDanmaResultWithFtpUseCase
import com.seiko.player.domain.GetDanmaResultWithNetUseCase
import com.seiko.player.domain.GetDanmaResultWithSmbUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.vlc.danma.DanmaResultBean
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DanmaManager @Inject constructor(
    private val getDanmaResultWithFile: GetDanmaResultWithFileUseCase,
    private val getDanmaResultWithSmb: GetDanmaResultWithSmbUseCase,
    private val getDanmaResultWithNet: GetDanmaResultWithNetUseCase,
    private val getDanmaResultWithFtp: GetDanmaResultWithFtpUseCase
) {
    suspend fun getDanmaResult(media: IMedia, isMatched: Boolean): DanmaResultBean? {
        return withContext(Dispatchers.IO) {
            val result = when(val scheme = media.uri.scheme) {
                "file" -> getDanmaResultWithFile.invoke(File(media.uri.path!!), isMatched)
                "smb" -> getDanmaResultWithSmb.invoke(media.uri, isMatched)
                "http", "https" -> getDanmaResultWithNet.invoke(media.uri.toString(), isMatched)
                "ftp", "sftp" -> getDanmaResultWithFtp.invoke(media.uri, isMatched, scheme)
                else -> {
                    Timber.d("danma service do not support url -> ${media.uri}")
                    return@withContext null
                }
            }
            when(result) {
                is Result.Success -> result.data
                is Result.Error -> {
                    Timber.e(result.exception)
                    null
                }
            }
        }
    }
}