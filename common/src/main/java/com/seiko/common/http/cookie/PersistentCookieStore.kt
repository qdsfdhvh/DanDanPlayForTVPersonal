package com.seiko.common.http.cookie

import android.text.TextUtils
import com.seiko.common.util.prefs.MmkvPreferenceDataStore
import com.seiko.common.util.toHexString
import com.seiko.common.util.toModBusByteArray
import okhttp3.Cookie
import okhttp3.HttpUrl
import java.io.*

private const val SEP = ","

class PersistentCookieStore(private val prefs: MmkvPreferenceDataStore) {

    private val cookies: HashMap<String, HashMap<String, Cookie>> = HashMap()

    init {

        var cookieNames: Array<String>
        var encodedCookie: String?
        var decodedCookie: Cookie?
        val keys = prefs.allKeys()
        if (keys != null) {
            for (key in keys) {
                cookieNames = TextUtils.split(prefs.getString(key, ""), SEP)
                for (name in cookieNames) {
                    encodedCookie = prefs.getString(name, null) ?: continue
                    decodedCookie = decodeCookie(encodedCookie) ?: continue
                    if (!cookies.containsKey(key)) {
                        cookies[key] = HashMap()
                    }
                    cookies[key]!![name] = decodedCookie
                }
            }
        }
    }

    fun add(url: HttpUrl, cookie: Cookie) {
        val key = url.host
        val name = getCookieToken(cookie)

        // 将cookies缓存到内存中 如果缓存过期 就重置此cookie
        if (!cookie.persistent) {
            if (!cookies.containsKey(key)) {
                cookies[key] = HashMap()
            }
            cookies[key]!![name] = cookie
        } else {
            if (cookies.containsKey(key)) {
                cookies[key]?.remove(name)
            }
        }

        if (!cookies.containsKey(key)) return

        val cookieHashMap = cookies[key]
        if (cookieHashMap == null || cookieHashMap.isEmpty()) return

        val encodeCookie = encodeCookie(cookie.toSerializable()) ?: return
        prefs.putString(key, cookieHashMap.keys.joinToString(SEP))
        prefs.putString(name, encodeCookie)
    }

    fun add(host: String, token: String, cookieString: String) {
        val cookie = decodeCookie(cookieString) ?: return
        if (!cookies.containsKey(host)) {
            cookies[host] = HashMap()
        }
        cookies[host]!![token] = cookie

        prefs.putString(host, token)
        prefs.putString(token, cookieString)
    }

    fun remove(url: HttpUrl, cookie: Cookie): Boolean {
        val key = url.host
        val name = getCookieToken(cookie)

        if (cookies.containsKey(key) && cookies[key]?.containsKey(name) == true) {
            cookies[key]!!.remove(name)
            prefs.remove(name)

            if (!cookies.containsKey(key)) return true
            val cookieHashMap = cookies[key]!!
            prefs.putString(key, cookieHashMap.keys.joinToString(SEP))
            return true
        }
        return false
    }

    fun get(url: HttpUrl): MutableList<Cookie> {
        val list = ArrayList<Cookie>()
        val key = url.host
        if (cookies.containsKey(key)) {
            list.addAll(cookies[key]!!.values)
        }
        return list
    }

    fun getCookies(): MutableList<Cookie> {
        val list = ArrayList<Cookie>()
        for (key in cookies.keys) {
            list.addAll(cookies[key]!!.values)
        }
        return list
    }
}

private fun getCookieToken(cookie: Cookie): String {
    return cookie.name + "@" + cookie.domain
}

@Suppress("NOTHING_TO_INLINE") // Syntactic sugar.
private inline fun Cookie.toSerializable(): SerializableOkHttpCookies {
    return SerializableOkHttpCookies(this)
}

/**
 * cookies 序列化成 string
 *
 * @param cookie 要序列化的cookie
 * @return 序列化之后的string
 */
private fun encodeCookie(cookie: SerializableOkHttpCookies): String? {
    val os = ByteArrayOutputStream()
    try {
        val outputStream = ObjectOutputStream(os)
        outputStream.writeObject(cookie)
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
    return os.toByteArray().toHexString()
}

/**
 * 将字符串反序列化成cookies
 *
 * @param cookieString cookies string
 * @return cookie object
 */
private fun decodeCookie(cookieString: String): Cookie? {
    val bytes = cookieString.toModBusByteArray()
    val input = ByteArrayInputStream(bytes)
    var cookie: Cookie? = null
    try {
        val inputStream = ObjectInputStream(input)
        cookie = (inputStream.readObject() as? SerializableOkHttpCookies)?.cookies
    } catch (e: IOException) {
        e.printStackTrace()
    }catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
    return cookie
}