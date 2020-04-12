package com.seiko.player.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.data.Result
import com.seiko.danma.DanmakuEngineOptions
import com.seiko.player.data.comments.SmbMrlRepository
import com.seiko.player.domain.danma.GetDanmaResultWithFileUseCase
import com.seiko.player.domain.danma.GetDanmaResultWithNetUseCase
import com.seiko.player.domain.danma.GetDanmaResultWithSmbUseCase
import master.flame.danmaku.danmaku.model.IDisplayer
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
    private val getDanmaResultWithNet: GetDanmaResultWithNetUseCase by inject()

    private val smbMrlRepo: SmbMrlRepository by inject()

    override fun loadDanmaOptions(): DanmakuEngineOptions {
        return DanmakuEngineOptions {
            setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 2.5f)
//            //设置防弹幕重叠
//            .preventOverlapping()
            //合并重复弹幕
            isDuplicateMergingEnabled = false
            //弹幕滚动速度
            setScrollSpeedFactor(1.4f)
            //弹幕文字大小
            setScaleTextSize(2.2f)
//        //弹幕文字透明度
//        .setDanmakuTransparency(0.8f)
            // 是否显示滚动弹幕
            r2LDanmakuVisibility = true
            // 是否显示顶部弹幕
            ftDanmakuVisibility = true
            // 是否显示底部弹幕
            fbDanmakuVisibility = false
            // 同屏数量限制
            setMaximumVisibleSizeInScreen(100)

            setDanmakuMargin(40)
        }
    }

    override suspend fun getDanmaResult(media: IMedia): DanmaResultBean? {
        val isMatched = true
        val result = when(media.uri.scheme) {
            "file" -> getDanmaResultWithFile.invoke(File(media.uri.path!!), isMatched)
            "smb" -> getDanmaResultWithSmb.invoke(media.uri, isMatched)
            "http", "https" -> getDanmaResultWithNet.invoke(media.uri.toString(), isMatched)
            else -> {
                Timber.d("danma service do not support url -> ${media.uri}")
                return null
            }
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