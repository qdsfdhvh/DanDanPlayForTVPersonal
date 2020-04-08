package com.seiko.player.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.data.Result
import com.seiko.player.data.model.PlayParam
import com.seiko.player.domain.danma.GetDanmaResultUseCase
import org.koin.core.KoinComponent
import org.koin.core.inject
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.vlc.danma.DanmaResultBean
import org.videolan.vlc.danma.DanmaService
import timber.log.Timber

@Route(path = DanmaService.PATH)
class DanmaServiceImpl : DanmaService, KoinComponent {

    private val getDanma: GetDanmaResultUseCase by inject()

    override suspend fun getDanmaResult(media: IMedia): DanmaResultBean? {
        // TODO 待完善
        val result = getDanma.invoke(PlayParam(
            media.uri.path!!,
            ""
        ))

        return when (result) {
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