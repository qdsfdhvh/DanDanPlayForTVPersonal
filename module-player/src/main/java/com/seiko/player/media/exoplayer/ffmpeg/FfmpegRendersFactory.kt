package com.seiko.player.media.exoplayer.ffmpeg

//import android.content.Context
//import android.os.Handler
//import com.google.android.exoplayer2.DefaultRenderersFactory
//import com.google.android.exoplayer2.Renderer
//import com.google.android.exoplayer2.audio.AudioProcessor
//import com.google.android.exoplayer2.audio.AudioRendererEventListener
//import com.google.android.exoplayer2.drm.DrmSessionManager
//import com.google.android.exoplayer2.drm.FrameworkMediaCrypto
//import com.google.android.exoplayer2.ext.ffmpeg.audio.SoftAudioRenderer
//import com.google.android.exoplayer2.ext.ffmpeg.video.SoftVideoRenderer
//import com.google.android.exoplayer2.mediacodec.MediaCodecSelector
//import com.google.android.exoplayer2.video.VideoRendererEventListener
//import java.util.ArrayList
//
//class FfmpegRendersFactory : DefaultRenderersFactory {
//    constructor(context: Context) : super(context)
//    constructor(context: Context, drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?) : super(context, drmSessionManager)
//    constructor(context: Context, extensionRendererMode: Int) : super(context, extensionRendererMode)
//    constructor(context: Context, drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?, extensionRendererMode: Int) : super(context, drmSessionManager, extensionRendererMode)
//    constructor(context: Context, extensionRendererMode: Int, allowedVideoJoiningTimeMs: Long) : super(context, extensionRendererMode, allowedVideoJoiningTimeMs)
//    constructor(context: Context, drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?, extensionRendererMode: Int, allowedVideoJoiningTimeMs: Long) : super(context, drmSessionManager, extensionRendererMode, allowedVideoJoiningTimeMs)
//
//    override fun buildAudioRenderers(
//        context: Context,
//        extensionRendererMode: Int,
//        mediaCodecSelector: MediaCodecSelector,
//        drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?,
//        playClearSamplesWithoutKeys: Boolean,
//        enableDecoderFallback: Boolean,
//        audioProcessors: Array<out AudioProcessor>,
//        eventHandler: Handler,
//        eventListener: AudioRendererEventListener,
//        out: ArrayList<Renderer>
//    ) {
//        val softRender = SoftAudioRenderer()
//        out.add(softRender)
//        super.buildAudioRenderers(
//            context,
//            extensionRendererMode,
//            mediaCodecSelector,
//            drmSessionManager,
//            playClearSamplesWithoutKeys,
//            enableDecoderFallback,
//            audioProcessors,
//            eventHandler,
//            eventListener,
//            out
//        )
//    }
//
//    override fun buildVideoRenderers(
//        context: Context,
//        extensionRendererMode: Int,
//        mediaCodecSelector: MediaCodecSelector,
//        drmSessionManager: DrmSessionManager<FrameworkMediaCrypto>?,
//        playClearSamplesWithoutKeys: Boolean,
//        enableDecoderFallback: Boolean,
//        eventHandler: Handler,
//        eventListener: VideoRendererEventListener,
//        allowedVideoJoiningTimeMs: Long,
//        out: ArrayList<Renderer>
//    ) {
//        val softRenderer = SoftVideoRenderer(true,
//            allowedVideoJoiningTimeMs, eventHandler, eventListener,
//            MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY,
//            drmSessionManager, false)
//        out.add(softRenderer)
//        super.buildVideoRenderers(
//            context,
//            extensionRendererMode,
//            mediaCodecSelector,
//            drmSessionManager,
//            playClearSamplesWithoutKeys,
//            enableDecoderFallback,
//            eventHandler,
//            eventListener,
//            allowedVideoJoiningTimeMs,
//            out
//        )
//    }
//}