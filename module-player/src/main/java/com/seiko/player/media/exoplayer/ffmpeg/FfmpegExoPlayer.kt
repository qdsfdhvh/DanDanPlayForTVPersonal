package com.seiko.player.media.exoplayer.ffmpeg

//import android.content.Context
//import android.graphics.Point
//import android.graphics.SurfaceTexture
//import android.os.Looper
//import android.view.Surface
//import android.view.SurfaceHolder
//import android.view.SurfaceView
//import android.view.TextureView
//import androidx.annotation.CallSuper
//import androidx.annotation.VisibleForTesting
//import com.google.android.exoplayer2.*
//import com.google.android.exoplayer2.analytics.AnalyticsCollector
//import com.google.android.exoplayer2.ext.Constant
//import com.google.android.exoplayer2.ext.Constant.*
//import com.google.android.exoplayer2.ext.ffmpeg.video.FrameScaleType
//import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
//import com.google.android.exoplayer2.trackselection.TrackSelector
//import com.google.android.exoplayer2.upstream.BandwidthMeter
//import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
//import com.google.android.exoplayer2.util.Assertions
//import com.google.android.exoplayer2.util.Clock
//import com.google.android.exoplayer2.util.Util
//
///**
// * @since 18/9/14
// * @author joffychim
// */
//class FfmpegExoPlayer private constructor(
//    context: Context,
//    rendererFactory: RenderersFactory,
//    trackSelector: TrackSelector,
//    loadControl: LoadControl,
//    bandwidthMeter: BandwidthMeter,
//    analyticsCollector: AnalyticsCollector,
//    clock: Clock,
//    looper: Looper
//) : SimpleExoPlayer(
//    context,
//    rendererFactory,
//    trackSelector,
//    loadControl,
//    bandwidthMeter,
//    analyticsCollector,
//    clock,
//    looper
//) {
//
//    private var origSurfaceTextureListener: TextureView.SurfaceTextureListener? = null
//    private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
//        override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
//            origSurfaceTextureListener?.onSurfaceTextureSizeChanged(surface, width, height)
//            onSurfaceSizeChanged(width, height)
//        }
//
//        override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
//            origSurfaceTextureListener?.onSurfaceTextureUpdated(surface)
//        }
//
//        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
//            return origSurfaceTextureListener?.onSurfaceTextureDestroyed(surface) ?: true
//        }
//
//        override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
//            origSurfaceTextureListener?.onSurfaceTextureAvailable(surface, width, height)
//            onSurfaceSizeChanged(width, height)
//        }
//    }
//
//    private val surfaceCallback = object : SurfaceHolder.Callback {
//        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
//            onSurfaceSizeChanged(width, height)
//        }
//
//        override fun surfaceDestroyed(holder: SurfaceHolder?) {
//        }
//
//        override fun surfaceCreated(holder: SurfaceHolder?) {
//        }
//
//    }
//
//    private var textureView: TextureView? = null
//    private var surfaceHolder: SurfaceHolder? = null
//
//    override fun release() {
//        clearListener()
//
//        onPlayReleased()
//        super.release()
//    }
//
//    private fun onSurfaceSizeChanged(width: Int, height: Int) {
//        val messages = mutableListOf<PlayerMessage>()
//        val size = Point(width, height);
//
//        renderers.firstOrNull { it.trackType == C.TRACK_TYPE_VIDEO }?.let {
//            val message = createMessage(it).setType(Constant.MSG_SURFACE_SIZE_CHANGED).setPayload(size).send()
//            messages.add(message)
//        }
//
//        messages.forEach { it.blockUntilDelivered() }
//    }
//
//    @CallSuper
//    protected fun onPlayReleased() {
//        val messages = mutableListOf<PlayerMessage>()
//        renderers.forEach {
//            messages.add(createMessage(it).setType(MSG_PLAY_RELEASED).send())
//        }
//
//        messages.forEach { it.blockUntilDelivered() }
//    }
//
//    override fun setVideoSurface(surface: Surface?) {
//        throw IllegalAccessError("please call setVideoTextureView or setVideoSurfaceView or setVideoSurfaceHolder")
//    }
//
//    override fun setVideoSurfaceHolder(surfaceHolder: SurfaceHolder?) {
//        super.setVideoSurfaceHolder(surfaceHolder)
//
//        clearListener()
//        this.surfaceHolder = surfaceHolder
//        this.textureView = null
//        surfaceHolder?.addCallback(surfaceCallback)
//    }
//
//    override fun setVideoSurfaceView(surfaceView: SurfaceView?) {
//        super.setVideoSurfaceView(surfaceView)
//
//        clearListener()
//        this.surfaceHolder = surfaceView?.holder
//        this.textureView = null
//        surfaceHolder?.addCallback(surfaceCallback)
//    }
//
//    override fun setVideoTextureView(textureView: TextureView?) {
//        super.setVideoTextureView(textureView)
//
//        clearListener()
//        this.textureView = textureView
//        this.surfaceHolder = null
//        // fix bug
//        if (textureView?.isAvailable == true) {
//            onSurfaceSizeChanged(textureView.width, textureView.height)
//        }
//
//        origSurfaceTextureListener = textureView?.surfaceTextureListener
//        textureView?.surfaceTextureListener = surfaceTextureListener
//    }
//
//    private fun clearListener() {
//        if (this.textureView?.surfaceTextureListener == surfaceTextureListener) {
//            this.textureView?.surfaceTextureListener = origSurfaceTextureListener
//        }
//        origSurfaceTextureListener = null
//
//        this.surfaceHolder?.removeCallback(surfaceCallback)
//    }
//
//    fun setBackgroundColor(color: Int) {
//        val messages = mutableListOf<PlayerMessage>()
//        renderers.firstOrNull { it.trackType == C.TRACK_TYPE_VIDEO }?.let {
//            messages.add(createMessage(it).setType(MSG_SET_BACKGROUND_COLOR).setPayload(color).send())
//        }
//
//        messages.forEach { it.blockUntilDelivered() }
//    }
//
//    fun setScaleType(scaleType: FrameScaleType) {
//        val messages = mutableListOf<PlayerMessage>()
//        renderers.firstOrNull { it.trackType == C.TRACK_TYPE_VIDEO }?.let {
//            messages.add(createMessage(it).setType(MSG_SET_SCALE_TYPE).setPayload(scaleType).send())
//        }
//
//        messages.forEach { it.blockUntilDelivered() }
//    }
//
//    /**
//     * A builder for [SimpleExoPlayer] instances.
//     *
//     *
//     * See [.Builder] for the list of default values.
//     */
//    class Builder
//    /**
//     * Creates a builder.
//     *
//     *
//     * Use [.Builder] instead, if you intend to provide a custom
//     * [RenderersFactory]. This is to ensure that ProGuard or R8 can remove ExoPlayer's [ ] from the APK.
//     *
//     *
//     * The builder uses the following default values:
//     *
//     *
//     *  * [RenderersFactory]: [DefaultRenderersFactory]
//     *  * [TrackSelector]: [DefaultTrackSelector]
//     *  * [LoadControl]: [DefaultLoadControl]
//     *  * [BandwidthMeter]: [DefaultBandwidthMeter.getSingletonInstance]
//     *  * [Looper]: The [Looper] associated with the current thread, or the [       ] of the application's main thread if the current thread doesn't have a [       ]
//     *  * [AnalyticsCollector]: [AnalyticsCollector] with [Clock.DEFAULT]
//     *  * `useLazyPreparation`: `true`
//     *  * [Clock]: [Clock.DEFAULT]
//     *
//     *
//     * @param context A [Context].
//     */ @JvmOverloads constructor(
//        private val context: Context,
//        private val renderersFactory: RenderersFactory = DefaultRenderersFactory(context),
//        private var trackSelector: TrackSelector = DefaultTrackSelector(context),
//        private var loadControl: LoadControl = DefaultLoadControl(),
//        private var bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter.getSingletonInstance(context),
//        private var looper: Looper = Util.getLooper(),
//        private var analyticsCollector: AnalyticsCollector = AnalyticsCollector(Clock.DEFAULT),
//        private var useLazyPreparation: Boolean = true,
//        private var clock: Clock = Clock.DEFAULT
//    ) {
//        private var buildCalled = false
//        /**
//         * Sets the [TrackSelector] that will be used by the player.
//         *
//         * @param trackSelector A [TrackSelector].
//         * @return This builder.
//         * @throws IllegalStateException If [.build] has already been called.
//         */
//        fun setTrackSelector(trackSelector: TrackSelector): Builder {
//            Assertions.checkState(!buildCalled)
//            this.trackSelector = trackSelector
//            return this
//        }
//
//        /**
//         * Sets the [LoadControl] that will be used by the player.
//         *
//         * @param loadControl A [LoadControl].
//         * @return This builder.
//         * @throws IllegalStateException If [.build] has already been called.
//         */
//        fun setLoadControl(loadControl: LoadControl): Builder {
//            Assertions.checkState(!buildCalled)
//            this.loadControl = loadControl
//            return this
//        }
//
//        /**
//         * Sets the [BandwidthMeter] that will be used by the player.
//         *
//         * @param bandwidthMeter A [BandwidthMeter].
//         * @return This builder.
//         * @throws IllegalStateException If [.build] has already been called.
//         */
//        fun setBandwidthMeter(bandwidthMeter: BandwidthMeter): Builder {
//            Assertions.checkState(!buildCalled)
//            this.bandwidthMeter = bandwidthMeter
//            return this
//        }
//
//        /**
//         * Sets the [Looper] that must be used for all calls to the player and that is used to
//         * call listeners on.
//         *
//         * @param looper A [Looper].
//         * @return This builder.
//         * @throws IllegalStateException If [.build] has already been called.
//         */
//        fun setLooper(looper: Looper): Builder {
//            Assertions.checkState(!buildCalled)
//            this.looper = looper
//            return this
//        }
//
//        /**
//         * Sets the [AnalyticsCollector] that will collect and forward all player events.
//         *
//         * @param analyticsCollector An [AnalyticsCollector].
//         * @return This builder.
//         * @throws IllegalStateException If [.build] has already been called.
//         */
//        fun setAnalyticsCollector(analyticsCollector: AnalyticsCollector): Builder {
//            Assertions.checkState(!buildCalled)
//            this.analyticsCollector = analyticsCollector
//            return this
//        }
//
//        /**
//         * Sets whether media sources should be initialized lazily.
//         *
//         *
//         * If false, all initial preparation steps (e.g., manifest loads) happen immediately. If
//         * true, these initial preparations are triggered only when the player starts buffering the
//         * media.
//         *
//         * @param useLazyPreparation Whether to use lazy preparation.
//         * @return This builder.
//         * @throws IllegalStateException If [.build] has already been called.
//         */
//        fun setUseLazyPreparation(useLazyPreparation: Boolean): Builder {
//            Assertions.checkState(!buildCalled)
//            this.useLazyPreparation = useLazyPreparation
//            return this
//        }
//
//        /**
//         * Sets the [Clock] that will be used by the player. Should only be set for testing
//         * purposes.
//         *
//         * @param clock A [Clock].
//         * @return This builder.
//         * @throws IllegalStateException If [.build] has already been called.
//         */
//        @VisibleForTesting
//        fun setClock(clock: Clock): Builder {
//            Assertions.checkState(!buildCalled)
//            this.clock = clock
//            return this
//        }
//
//        /**
//         * Builds a [SimpleExoPlayer] instance.
//         *
//         * @throws IllegalStateException If [.build] has already been called.
//         */
//        fun build(): SimpleExoPlayer {
//            Assertions.checkState(!buildCalled)
//            buildCalled = true
//            return FfmpegExoPlayer(
//                context,
//                renderersFactory,
//                trackSelector,
//                loadControl,
//                bandwidthMeter,
//                analyticsCollector,
//                clock,
//                looper
//            )
//        }
//        /**
//         * Creates a builder with the specified custom components.
//         *
//         *
//         * Note that this constructor is only useful if you try to ensure that ExoPlayer's default
//         * components can be removed by ProGuard or R8. For most components except renderers, there is
//         * only a marginal benefit of doing that.
//         *
//         * @param context A [Context].
//         * @param renderersFactory A factory for creating [Renderers][Renderer] to be used by the
//         * player.
//         * @param trackSelector A [TrackSelector].
//         * @param loadControl A [LoadControl].
//         * @param bandwidthMeter A [BandwidthMeter].
//         * @param looper A [Looper] that must be used for all calls to the player.
//         * @param analyticsCollector An [AnalyticsCollector].
//         * @param useLazyPreparation Whether media sources should be initialized lazily.
//         * @param clock A [Clock]. Should always be [Clock.DEFAULT].
//         */
//        /**
//         * Creates a builder with a custom [RenderersFactory].
//         *
//         *
//         * See [.Builder] for a list of default values.
//         *
//         * @param context A [Context].
//         * @param renderersFactory A factory for creating [Renderers][Renderer] to be used by the
//         * player.
//         */
//    }
//}