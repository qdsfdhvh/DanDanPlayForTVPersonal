package com.seiko.player.util

import android.net.Uri
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import java.util.*

object SftpUtils {

    private val jsch = JSch()

    fun getVideoMd5WithUri(uri: Uri, account: String, password: String): String {
        var port = uri.port
        if (port <= 0) port = 22

        val session = jsch.getSession(account, uri.host, port)
        session.setPassword(password)

        val properties = Properties()
        properties["StrictHostKeyChecking"] = "no"
        session.setConfig(properties)

        session.connect()

        val channel = session.openChannel("sftp")
            ?: throw RuntimeException("channel connecting failed.")

        channel.connect()
        channel as ChannelSftp

        val path = uri.path!!
        channel.cd(path.substringBeforeLast("/"))
        val md5 = channel.get(path.getFileNameFromPath()).use { it.getVideoMd5() }

        channel.disconnect()
        session.disconnect()

        return md5
    }

}