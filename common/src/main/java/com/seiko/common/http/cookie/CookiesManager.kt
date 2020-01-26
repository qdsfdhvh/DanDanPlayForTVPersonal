package com.seiko.common.http.cookie

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl


class CookiesManager(private val cookieStore: PersistentCookieStore) : CookieJar {

//    private val cookieStore = PersistentCookieStore(MMKV.mmkvWithID(PREFS_COOKIES))

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        if (cookies.isNotEmpty()) {
            for (cookie in cookies) {
                cookieStore.add(url, cookie)
            }
        }
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        val cookies = cookieStore.get(url)
//        val sb = StringBuilder()
//        for ((i, cookie) in cookies.withIndex()) {
//            if (i > 0) sb.append("; ")
//            sb.append(cookie.name()).append("=").append(cookie.value())
//
//        }
        return cookies
    }

//    private object Holder {
//        val INSTANCE = CookiesManager()
//    }
//
//    companion object {
//        fun getInstance() = Holder.INSTANCE
//    }

}