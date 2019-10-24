package com.seiko.data.pref

import com.tencent.mmkv.MMKV

class AppPrefHelper(prefs: MMKV): MMKVProperty(), PrefHelper {

    override var token by prefs.string("PREF_USER_TOKEN", "")
}