package com.seiko.torrent

import com.chenenyu.router.RouteInterceptor
import com.chenenyu.router.RouteResponse
import com.chenenyu.router.Router
import com.chenenyu.router.annotation.Interceptor
import com.seiko.common.router.Routes

/**
 * 将所有跳转到TorrentMainActivity的改为DebugActivity
 */
@Interceptor("debug")
class DebugInterceptor : RouteInterceptor {

    override fun intercept(chain: RouteInterceptor.Chain): RouteResponse {
        val uri = chain.request.uri.toString()
        if (uri == Routes.Torrent.PATH) {
            Router.build(DebugActivity.URI).go(chain.context)
            return chain.intercept()
        }
        return chain.process()
    }
}