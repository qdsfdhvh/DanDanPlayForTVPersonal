package com.seiko.player.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.data.Result
import com.seiko.player.domain.danma.GetDanmaResultWithFileUseCase
import com.seiko.player.util.SmbUtils
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.vlc.danma.DanmaResultBean
import org.videolan.vlc.danma.DanmaService
import timber.log.Timber
import java.io.File
import java.util.*

@Route(path = DanmaService.PATH)
class DanmaServiceImpl : DanmaService, KoinComponent {

    private val getDanmaResult: GetDanmaResultWithFileUseCase by inject()

    override suspend fun getDanmaResult(media: IMedia): DanmaResultBean? {
        return when(media.uri.scheme) {
            "file" -> getDanmaResultWithFile(media)
            "smb" -> getDanmaResultWithSmb(media)
            else -> null
        }
    }

    private suspend fun getDanmaResultWithFile(media: IMedia): DanmaResultBean? {
        val videoFile = File(media.uri.path!!)
        return when (val result = getDanmaResult.file(videoFile, true)) {
            is Result.Success -> result.data
            is Result.Error -> {
                Timber.e(result.exception)
                null
            }
        }
    }

    private suspend fun getDanmaResultWithSmb(media: IMedia): DanmaResultBean? {
        val smbFile = SmbUtils.getFileWithUri(media.uri!!) ?: return null

        return when (val result = getDanmaResult.smbFile(smbFile, true)) {
            is Result.Success -> result.data
            is Result.Error -> {
                Timber.e(result.exception)
                null
            }
        }
    }

    override fun init(context: Context?) {
        Timber.d("初始化DanmaService")
    }

}