package com.seiko.player.util

import android.net.Uri
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import timber.log.Timber
import java.nio.charset.Charset

object FtpUtils {

    fun getVideoMd5WithUri(uri: Uri, account: String, password: String): String? {
        var port = uri.port
        if (port <= 0) port = 21

        val client = FTPClient()
        client.connect(uri.host, port)
        client.login(account, password)

        client.enterLocalPassiveMode()            // 被动模式
        client.setFileType(FTP.BINARY_FILE_TYPE)  // 设置文件传输模式
        client.restartOffset = 0                  // 设置起始点

        val ins = client.retrieveFileStream(
            String(uri.path!!.toByteArray(), Charset.forName("ISO-8859-1")))

        val reply = client.replyCode
        if (!FTPReply.isPositivePreliminary(reply)) {
            Timber.d("获取文件信息错误，错误码为：%d", reply)
            client.disconnect()
            return null
        }

        val md5 = ins.buffered().use { it.getVideoMd5() }

        client.logout()
        client.disconnect()

        return md5
    }
}