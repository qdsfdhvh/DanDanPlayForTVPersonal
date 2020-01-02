package com.seiko.data.usecase

import com.seiko.data.constants.DEFAULT_TORRENT_FOLDER
import com.seiko.domain.pref.PrefHelper
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File

/**
 * 生成种子储存路径
 */
class GetTorrentPathUseCase : KoinComponent {

    private val prefHelper: PrefHelper by inject()

    /**
     * @param parentPathName
     * @param magnet 磁力链接 magnet:?xt=urn:btih:WEORDPJIJANN54BH2GNNJ6CSN7KB7S34
     * 将'/'换成'\'
     */
    operator fun invoke(parentPathName: String, magnet: String): String {
        return prefHelper.downloadFolder +
                parentPathName.replace('/', '\\') + File.separator +
                DEFAULT_TORRENT_FOLDER + File.separator +
                magnet.substring(20) + ".torrent"
    }
}