package com.dandanplay.tv.data.prefs

interface PrefDataSource {

    /**
     * Token
     */
    var token: String

    /**
     * 番剧下载目录
     */
    var downloadFolder: String


}