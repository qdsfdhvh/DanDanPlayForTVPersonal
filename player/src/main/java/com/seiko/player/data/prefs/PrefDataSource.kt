package com.seiko.player.data.prefs

interface PrefDataSource {

    /**
     * 硬解码
     */
    var mIsUsingMediaCodec: Boolean

    /**
     * H265硬解码
     */
    var mIsUsingMediaCodecH265: Boolean

    /**
     * openSLES
     */
    var mIsUsingOpenSLES: Boolean

    var mIsUsingDetachedSurfaceTextureView: Boolean

    /**
     * surface渲染器（SurfaceView、TextureView）
     */
    var mIsUsingSurfaceRenders: Boolean

    /**
     * 像素格式 YV12 RV16 RV32
     */
    var mPixelFormat: String

//    /**
//     * For VLC
//     */
//    var timeStretch: Boolean
//    var subtitlesEncoding: String
//    var enableFrameSkip: Boolean
//    var chromaFormat: String
//    var enableVerboseMode: Boolean
//    var deBlocking: Int
//    var networkCaching: Long
//    var freeTypeRelFontSize: Int
//    var freeTypeBold: Boolean
//    var freeTypeColor: Int
//    var freeTypeBackground: Boolean
//    var openGL: Int
//    var castingPassThrough: Boolean
//    var castingQuality: Int
//    var customVLCOptions: String
}