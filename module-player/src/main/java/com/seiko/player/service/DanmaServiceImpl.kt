package com.seiko.player.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.data.Result
import com.seiko.player.data.comments.SmbMrlRepository
import com.seiko.player.domain.danma.GetDanmaResultWithFileUseCase
import com.seiko.player.domain.danma.GetDanmaResultWithSmbUseCase
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.vlc.danma.DanmaResultBean
import org.videolan.vlc.danma.DanmaService
import timber.log.Timber
import java.io.File

@Route(path = DanmaService.PATH)
class DanmaServiceImpl : DanmaService, KoinComponent {

    private val getDanmaResultWithFile: GetDanmaResultWithFileUseCase by inject()
    private val getDanmaResultWithSmb: GetDanmaResultWithSmbUseCase by inject()

    private val smbMrlRepo: SmbMrlRepository by inject()

    override suspend fun getDanmaResult(media: IMedia): DanmaResultBean? {
        val isMatched = true
        val result = when(media.uri.scheme) {
            "file" -> getDanmaResultWithFile.invoke(File(media.uri.path!!), isMatched)
            "smb" -> getDanmaResultWithSmb.invoke(media.uri, isMatched)
            else -> return null
        }
        return when(result) {
            is Result.Success -> result.data
            is Result.Error -> {
                Timber.e(result.exception)
                null
            }
        }
    }

    override suspend fun saveSmbServer(mrl: String, account: String, password: String) {
        smbMrlRepo.saveSmbMrl(mrl, account, password)
    }

    override fun init(context: Context?) {
        Timber.d("初始化DanmaService")
    }

}