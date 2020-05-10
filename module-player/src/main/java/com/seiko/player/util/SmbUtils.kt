package com.seiko.player.util

import android.net.Uri
import jcifs.CIFSContext
import jcifs.context.SingletonContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.collections.HashMap

class SmbUtils {

    private val maps = HashMap<String, CIFSContext>(2)

    fun getFileWithUri(uri: Uri, account: String, password: String): SmbFile {
        val host = uri.host!!
        val path = uri.path
        val authUrl = "smb://$account:$password@$host$path"

        var cifsContext = maps[host]
        if (cifsContext == null) {
            cifsContext = createCifsContext(host, account, password)
            maps[host] = cifsContext
        }

        val smbFile = SmbFile(authUrl, cifsContext)
        smbFile.connect()
        return smbFile
    }

    fun clear() {
        GlobalScope.launch(Dispatchers.IO) {
            maps.values.forEach { it.close() }
        }
    }

    protected fun finalize() {
        clear()
    }

    private fun createCifsContext(
        host: String,
        account: String,
        password: String
    ): CIFSContext {
        val auth = NtlmPasswordAuthenticator(account, password)
        val baseContext = SingletonContext.getInstance()
        val cifsContext = baseContext.withCredentials(auth)

        val address = cifsContext.nameServiceClient.getByName(host)
        cifsContext.transportPool.logon(cifsContext, address)
        return cifsContext
    }

    companion object : SingletonHolder<SmbUtils>({ SmbUtils() })

}