package com.seiko.player.media.player

import com.seiko.player.data.prefs.PrefDataSource
import tv.danmaku.ijk.media.player.IMediaPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import tv.danmaku.ijk.media.player.TextureMediaPlayer

class IjkMediaPlayerCreator(private val prefs: PrefDataSource) :
    MediaPlayerCreator {

    override fun createPlayer(): IMediaPlayer {
        val ijkMediaPlayer = IjkMediaPlayer()
        // 输出日志
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_INFO)

        // 硬解码
        var value: Long = if (prefs.mIsUsingMediaCodec) 1 else 0
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", value)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", value)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", value)
        if (value == 1L) {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "analyzeduration", "2000000")
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "probsize", "4096")
        }

        // H265硬解码
        value = if (prefs.mIsUsingMediaCodecH265) 1 else 0
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-hevc", value)

        // 是否开启OpenSL ES - 精心优化的硬件音频加速API
        value = if (prefs.mIsUsingOpenSLES) 1 else 0
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", value)

        // 像素格式:
        //   AUTO = ""
        //   RGB565 = "fcc-rv16"
        //   RGB888 = "fcc-rv24"
        //   RGBX8888 = "fcc-rv32"
        //   YV12 = "fcc-yv12"
        //   OPENGL_ES2 = "fcc-_es2"
        // 默认为RGB 888X
        val pixelFormat = prefs.mPixelFormat
        if (pixelFormat.isEmpty()) {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32.toLong())
        } else {
            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", pixelFormat)
        }

        // SeekTo能够快速seek到指定位置并播放
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek")
        // 最大fps
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-fps", 60)
        // 跳帧处理,CPU处理较慢时，进行跳帧处理，保证播放流程，画面和声音同步
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0)

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 1)
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 10000000)
        // 播放重连次数
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1)

        // 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 0)
        // 设置播放前的最大探测时间
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzemaxduration", 100)
        // 设置播放前的探测时间 1,达到首屏秒开效果
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1)
        // 播放前的探测Size，默认是1M, 改小一点会出画面更快
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024)
//        // 每处理一个packet之后刷新io上下文
//        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "flush_packets", 1)
        // 否开启字幕
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "subtitle", 1)
        // 否开启变调
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 0)

        if (prefs.mIsUsingDetachedSurfaceTextureView) {
            return TextureMediaPlayer(ijkMediaPlayer)
        }

        return ijkMediaPlayer
    }
}