package com.seiko.tv.data.prefs

import androidx.preference.PreferenceDataStore
import com.seiko.tv.util.constants.DEFAULT_DOWNLOAD_PATH
import com.seiko.common.util.prefs.string

class PrefDataSourceImpl(prefs: PreferenceDataStore): PrefDataSource {

    override var token by prefs.string("PREF_USER_TOKEN", "")

    override var downloadFolder by prefs.string("PREF_DOWNLOAD_FOLDER", DEFAULT_DOWNLOAD_PATH)

}