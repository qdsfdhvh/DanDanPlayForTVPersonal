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
        fun get(): AppTvService {
            return ARouter.getInstance().build(Routes.Service.APP_TV_INFO)
                .navigation() as AppTvService
        }
    }

    /**
     * 通过hash获得的集数id
     * 无结果返回-1
     */
    fun findEpisodeId(hash: String): Int

}