package com.seiko.common.service

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.seiko.common.router.Routes

/**
 * 从Torrent服务中获得hash的文件下载路径
 */
interface AppTvService : IProvider {

    companion object {
        fun get(): AppTvService? {
            return ARouter.getInstance().build(Routes.Service.APP_TV_INFO)
                .navigation() as? AppTvService
        }
    }

    /**
     * 通过hash获得的集数id
     * 无结果返回-1
     *
     * TODO 如果是追新番，一个hash对应一集，那episodeId有效；
     *     但是hash可能是下载了一整集等其他情况，这样的episodeId无效，后续待调整。
     */
    suspend fun findEpisodeId(hash: String): Int

}