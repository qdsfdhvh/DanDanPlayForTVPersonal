package com.seiko.player.util

import android.net.Uri
import jcifs.CIFSContext
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.MalformedURLException
import java.util.*
import kotlin.collections.HashMap

object SmbUtils {

    private const val TAG = "SmbUtils"

    private val maps = HashMap<String, CIFSContext>(2)

    fun getFileWithUri(uri: Uri): SmbFile? {
        //TODO 目前测试写死，待从vlc中获取。
        val account = "share"
        val password = "123456"

        val host = uri.host!!
        val path = uri.path
        val authUrl ="smb://$account:$password@$host$path"

        var cifsContext = maps[host]
        if (cifsContext == null) {
            cifsContext = createCifsContext(host, "", account, password)
            maps[host] = cifsContext
        }

        val smbFile = try {
            SmbFile(authUrl, cifsContext)
        } catch (e: MalformedURLException) {
            Timber.tag(TAG).e(e)
            return null
        }

        try {
            smbFile.connect()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
            return null
        }

        return smbFile
    }

    fun clear() {
       GlobalScope.launch(Dispatchers.IO) {
           maps.values.forEach { it.close() }
       }
    }

    private fun createCifsContext(
        host: String,
        domain: String,
        account: String,
        password: String
    ) : CIFSContext {
        val auth = NtlmPasswordAuthenticator(domain, account, password)

        val properties = Properties()
        properties.setProperty("jcifs.smb.client.responseTimeout", "5000")
        properties.setProperty("jcifs.smb.client.dfs.disabled", "true")
        val configuration = PropertyConfiguration(properties)

        val cifsContext = BaseContext(configuration).withCredentials(auth)
        val address = cifsContext.nameServiceClient.getByName(host)
        cifsContext.transportPool.logon(cifsContext, address)
        return cifsContext
    }

}