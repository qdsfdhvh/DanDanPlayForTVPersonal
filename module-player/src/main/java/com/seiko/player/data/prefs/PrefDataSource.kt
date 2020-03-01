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

    /**
     * 显示弹幕
     */
    var showDanma: Boolean

    /**
     * 是否已经创建vlc媒体库
     */
    var isInitVlcMedia: Boolean

}