package com.seiko.core.data.prefs

import com.seiko.core.constants.DEFAULT_DOWNLOAD_PATH
import com.tencent.mmkv.MMKV

class PrefDataSourceImpl(prefs: MMKV): PrefDataSource, MMKVProperty {

    override var token by prefs.string("PREF_USER_TOKEN", "")

    override var downloadFolder by prefs.string("PREF_DOWNLOAD_FOLDER", DEFAULT_DOWNLOAD_PATH)

}