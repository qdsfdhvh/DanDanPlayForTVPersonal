package com.seiko.player.data.comments

import android.net.Uri
import com.seiko.player.data.db.dao.SmbMrlDao
import com.seiko.player.data.db.model.SmbMrl
import timber.log.Timber

/**
 * VLC的smb等通讯都是最底层完成的，看都看不懂，不自欺欺人了；暂时使用这种不是很好的方式间接处理。
 * PS：保存和加载都不完善，待优化。
 */
class SmbMrlRepository(private val dao: SmbMrlDao) {

    suspend fun saveSmbMrl(mrl: String, account: String, password: String): Boolean {
        Timber.d("保存smb -> mrl=$mrl, account=$account, password=$password")
        return dao.insert(SmbMrl(
            mrl = mrl,
            account = account,
            password = password
        )) > 0
    }

    suspend fun getSmbMrl(url: String, scheme: String): SmbMrl? {
        val list = dao.all()
        if (list.isEmpty()) return null

        var uri: Uri
        var host: String

        for (bean in list) {
            uri = Uri.parse(bean.mrl) ?: continue
            if (uri.scheme == null || uri.scheme != scheme) continue

            host = uri.host ?: continue
            Timber.d("比较：url=$url, host=${host}")
            if (url.contains(host)) {
                Timber.d("匹配到host=$host")
                return bean
            }
        }
        return null
    }

}