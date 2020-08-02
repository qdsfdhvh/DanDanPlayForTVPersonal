package com.seiko.player.service

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.BaseApplication
import com.seiko.danma.DanmakuEngineOptions
import com.seiko.player.data.comments.SmbMrlRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ApplicationComponent
import master.flame.danmaku.danmaku.model.IDisplayer
import org.videolan.libvlc.interfaces.IMedia
import org.videolan.vlc.danma.DanmaResultBean
import org.videolan.vlc.danma.DanmaService

@Route(path = DanmaService.PATH)
class DanmaServiceImpl : DanmaService {

    private lateinit var helper: DanmaServiceHelper

    override fun init(context: Context) {
        helper = DanmaServiceHelper.get(context)
    }

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
        return helper.danmaManager.getDanmaResult(media, true)
    }

    override suspend fun saveSmbServer(mrl: String, account: String, password: String) {
        helper.smbMrlRepo.saveSmbMrl(mrl, account, password)
    }

}

/**
 * 目前hilt无法捕获DanmaService进行编译，暂时新建一个类进行注入
 */
internal class DanmaServiceHelper private constructor(context: Context) {

    @InstallIn(ApplicationComponent::class)
    @EntryPoint
    interface DanmaInitializerEntryPoint {
        var danmaManager: DanmaManager
        var smbMrlRepo: SmbMrlRepository
    }

    val danmaManager: DanmaManager
    val smbMrlRepo: SmbMrlRepository

    init {
        val entryPoint = EntryPointAccessors.fromApplication(context, DanmaInitializerEntryPoint::class.java)
        danmaManager = entryPoint.danmaManager
        smbMrlRepo = entryPoint.smbMrlRepo
    }

    companion object {
        fun get(context: Context): DanmaServiceHelper {
            return DanmaServiceHelper(context)
        }
    }
}