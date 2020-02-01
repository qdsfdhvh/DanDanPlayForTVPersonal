package com.seiko.player.data.prefs

interface PrefDataSource {
    /**
     * For VLC
     */
    var timeStretch: Boolean
    var subtitlesEncoding: String
    var enableFrameSkip: Boolean
    var chromaFormat: String
    var enableVerboseMode: Boolean
    var deBlocking: Int
    var networkCaching: Long
    var freeTypeRelFontSize: Int
    var freeTypeBold: Boolean
    var freeTypeColor: Int
    var freeTypeBackground: Boolean
    var openGL: Int
    var castingPassThrough: Boolean
    var castingQuality: Int
    var customVLCOptions: String
}