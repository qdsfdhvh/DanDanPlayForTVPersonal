package com.dandanplay.tv.data.prefs

import com.dandanplay.tv.util.constants.DEFAULT_DOWNLOAD_PATH
import com.seiko.common.util.string
import com.tencent.mmkv.MMKV

class PrefDataSourceImpl(prefs: MMKV): PrefDataSource {

    override var token by prefs.string("PREF_USER_TOKEN", "")

    override var downloadFolder by prefs.string("PREF_DOWNLOAD_FOLDER", DEFAULT_DOWNLOAD_PATH)

}