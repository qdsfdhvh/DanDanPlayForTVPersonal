package com.seiko.player

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.AttributeSet
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewStub
import android.view.WindowManager
import android.widget.FrameLayout
import com.google.android.exoplayer2.DefaultControlDispatcher
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.ffmpeg.ExoFFmpegPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.SubtitleView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.seiko.player.explayer.R
import com.seiko.player.subtitle.SubtitleParser

class ExoPlayerView(context: Context,
                    private val isSurfaceView: Boolean = false
) : FrameLayout(context), IPlayerView {

    // 视频View
    private lateinit var mVideoView: PlayerView
    // 字幕View
    private lateinit var mSubtitleView: SubtitleView

    // 关联的Activity
    private lateinit var mAttachActivity: Activity
    // 原生ExoPlayer
    private lateinit var mExoPlayer: SimpleExoPlayer
    // 音量控制
    private lateinit var mAudioManager: AudioManager
    //流选择器
    private val mTrackSelector = DefaultTrackSelector()
    //player控制器
    private val mControlDispatcher = DefaultControlDispatcher()
    // 屏幕旋转角度监听
    private lateinit var mOrientationListener: OrientationEventListener
    // 外部监听器
    private var mOutsideListener: OnOutsideListener? = null

    // 最大音量
    private var mMaxVolume = 0
    // 锁屏
    private var mIsForbidTouch = false
    // 是否正在拖拽进度条
    private var mIsSeeking = false
    // 目标进度
    private var mTargetPosition = IPlayerView.INVALID_VALUE
    // 当前进度
    private var mCurPosition = IPlayerView.INVALID_VALUE
    // 是否还未播放
    private var isNeverPlay = true
    // 初始高度
    private var mInitHeight = 0
    // 屏幕宽/高度
    private var mWidthPixels = 0
    // 上次播放跳转时间
    private var mSkipPosition = IPlayerView.INVALID_VALUE

    private var isShowSubtitle = false
    private var mHandler = Handler()

    init {
        require(context is Activity) { "Context must be AppCompatActivity" }

        initViewBefore(context)
        initView(context)
        initViewCallback()
        initViewAfter()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (mInitHeight == 0) {
            mInitHeight = height
            mWidthPixels = resources.displayMetrics.widthPixels
        }
    }

    private fun initViewBefore(context: Context) {
        // 获得绑定的Activity实例
        mAttachActivity = context as Activity
        // 加载布局
        View.inflate(context, R.layout.layout_exo_player_view, this)
        // 获取播放器实例 PS: ffmpeg扩展不支持TextureView
        mExoPlayer = if (isSurfaceView) {
            ExoFFmpegPlayer(context, mTrackSelector)
        } else {
            ExoPlayerFactory.newSimpleInstance(context, mTrackSelector)
        }
        // 屏幕翻转控制
        mOrientationListener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (isNeverPlay) return
                // 根据角度进行横屏切换
                if (orientation in 60..120) {
                    mAttachActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
                } else if (orientation in 240..300) {
                    mAttachActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            }
        }
        // 声音管理器
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // 最大音量
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        // 亮度管理
        try {
            val brightness = Settings.System.getInt(mAttachActivity.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
            val progress = 1.0f * brightness.toFloat() / 255.0f
            val layout = mAttachActivity.window.attributes
            layout.screenBrightness = progress
            mAttachActivity.window.attributes = layout
        } catch (var7: Settings.SettingNotFoundException) {
            var7.printStackTrace()
        }
    }

    private fun initView(context: Context) {
        // 主要控件：视频、字幕
        mVideoView = if (isSurfaceView) {
            findViewById(R.id.exo_player_surface_view)
        } else {
            findViewById(R.id.exo_player_texture_view)
        }
        mSubtitleView = findViewById(R.id.subtitle_view)

    }

    private fun initViewCallback() {

    }

    private fun initViewAfter() {
        // 由于是根据渲染器，动态的实例化视频View，所以默认为GONE，要手动切换为VISIBLE
        mVideoView.visibility = View.VISIBLE
        // 不适用ExoPlayer默认控制
        mVideoView.useController = false
        // 添加播放器到View
        mVideoView.player = mExoPlayer
    }

    /**
     *
     */

    /**
     * 设置外部回调接口
     */
    override fun setOnInfoListener(listener: OnOutsideListener) {
        mOutsideListener = listener
    }

    override fun setSkipTip(position: Int) {

    }

    /**
     * 跳转
     */
    override fun seekTo(position: Long) {
        mExoPlayer.seekTo(position)
    }

    override fun setTitle(title: String) {

    }

    /**
     * 设置视频资源
     */
    override fun setVideoPath(videoPath: String) {
        val dataSourceFactory = DefaultDataSourceFactory(mAttachActivity,
            Util.getUserAgent(mAttachActivity, "com.seiko.dandanplay.player"))
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(videoPath))
        mExoPlayer.prepare(videoSource)
        seekTo(0)
    }

    /**
     * 设置字幕
     */
    override fun setSubtitlePath(subtitlePath: String) {
        isShowSubtitle = false
        Thread {
            val subtitleObj = SubtitleParser(subtitlePath).parser()
            if (subtitleObj != null) {
                val message = Message.obtain()
                message.what = IPlayerView.MSG_SET_SUBTITLE_SOURCE
                message.obj = subtitleObj
                handler.sendMessage(message)
            }
        }.start()
    }

    /**
     * 开始
     */
    override fun start() {
        if (isNeverPlay) {
            isNeverPlay = false

            //
        }
        // 设置可以播放
        mExoPlayer.playWhenReady = true
        // 启动播放
        mControlDispatcher.dispatchSetPlayWhenReady(mExoPlayer, true)
        // 更新进度
        handler.obtainMessage(IPlayerView.MSG_UPDATE_SEEK)


    }

    /**
     * 暂停
     */
    override fun pause() {
        if (isVideoPlay()) {
            mControlDispatcher.dispatchSetPlayWhenReady(mExoPlayer, false)
        }

    }

    /**
     * 停止
     */
    override fun stop() {
        pause()
        mExoPlayer.stop(false)
    }

    override fun onResume() {
        if (mCurPosition != IPlayerView.INVALID_VALUE) {
            seekTo(mCurPosition)
            mCurPosition = IPlayerView.INVALID_VALUE
        }
    }

    override fun onPause() {
        mCurPosition = mExoPlayer.currentPosition
        pause()
    }

    override fun onDestroy() {
        // 发起外部回调 -> 记录播放进度
        if (mOutsideListener != null) {
            mOutsideListener!!.onAction(OnOutsideListener.INTENT_PLAY_END, 0)
            mOutsideListener!!.onAction(OnOutsideListener.INTENT_SAVE_CURRENT, mExoPlayer.currentPosition)
            mOutsideListener = null
        }
        // 注销ExoPlayer
        mExoPlayer.release()
        // 关闭屏幕常亮
        mAttachActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    /******************************************/


    /**
     * 是否启用旋屏
     */
    private fun setOrientationEnable(enable: Boolean) {

    }

    /**
     * 视频是否正在播放
     */
    private fun isVideoPlay(): Boolean {
        if (mVideoView.player != null) {
            if (mVideoView.player.playWhenReady) {
                return mVideoView.player.playbackState == Player.STATE_READY
            }
        }
        return false
    }

}