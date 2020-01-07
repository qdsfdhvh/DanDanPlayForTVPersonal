/*
 * Copyright (C) 2016-2018 Yaroslav Pronin <proninyaroslav@mail.ru>
 *
 * This file is part of LibreTorrent.
 *
 * LibreTorrent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LibreTorrent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LibreTorrent.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.seiko.torrent.model

import com.seiko.torrent.constants.DEFAULT_PROXY_PORT

/*
 * The class encapsulates the proxy settings passed in TorrentEngine.
 */

data class ProxySettingsPack(
    var type: ProxyType = ProxyType.NONE,
    var address: String = "",
    var login: String = "",
    var password: String = "",
    var port: Int = DEFAULT_PROXY_PORT,
    var isProxyPeersToo: Boolean = true
)

enum class ProxyType(private val value: Int) {
    NONE(0), SOCKS4(1), SOCKS5(2), HTTP(3);

    fun value(): Int {
        return value
    }

    companion object {
        fun fromValue(value: Int): ProxyType {
            val enumValues = ProxyType::class.java.enumConstants
            for (ev in enumValues!!) {
                if (ev.value() == value) {
                    return ev
                }
            }
            return NONE
        }
    }

}