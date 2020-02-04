package com.seiko.player.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.ui.adapter.OnItemClickListener
import com.seiko.common.ui.dialog.DialogSelectFragment
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.player.R
import com.seiko.player.data.model.PlayParam
import com.seiko.player.data.model.PlayerOption
import com.seiko.player.databinding.PlayerActivityVideoBinding
import com.seiko.player.databinding.PlayerControlBottomBinding
import com.seiko.player.delegate.VideoKeyDownDelegate
import com.seiko.player.delegate.VideoTouchDelegate
import com.seiko.player.media.ijkplayer.MediaPlayerParams
import com.seiko.player.media.creator.MediaPlayerCreatorFactory
import com.seiko.player.ui.adapter.OptionsAdapter
import com.seiko.player.util.Tools
import com.seiko.player.util.constants.INVALID_VALUE
import com.seiko.player.util.constants.MAX_VIDEO_SEEK
import org.koin.android.ext.android.inject
import timber.log.Timber
import tv.danmaku.ijk.media.player.IMediaPlayer

class VideoPlayerActivity: FragmentActivity()
    , View.OnClickListener
    , OnItemClickListener
    , SeekBar.OnSeekBarChangeListener
    , IMediaPlayer.OnPreparedListener
    , IMediaPlayer.OnInfoListener
    , IMediaPlayer.OnErrorListener {

    companion object {

        private const val ARGS_VIDEO_PARAMS = "ARGS_VIDEO_PARAMS"

        private const val ID_AUDIO_TRACK = 31
        private const val ID_AUDIO_DELAY = 32
        private const val ID_PLAYBACK_SPEED = 6
        private const val ID_AB_REPEAT = 13

        fun launch(context: Context, param: PlayParam) {
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra(ARGS_VIDEO_PARAMS, param)
            context.startActivity(intent)
        }
    }

    private lateinit var binding: PlayerActivityVideoBinding
    private lateinit var bindingControlBottom: PlayerControlBottomBinding

    private val handler by lazyAndroid { VideoPlayerHandler(this) }
    private val keyDownDelegate by lazyAndroid { VideoKeyDownDelegate(handler) }
    private val touchDelegate by lazyAndroid { VideoTouchDelegate(handler) }

    private val optionsAdapter by lazyAndroid { OptionsAdapter(this) }

    private val mediaPlayerFactory: MediaPlayerCreatorFactory by inject()

    /**
     * 底部控制界面是否显示中
     */
    private var isOverlayShow = false

    /**
     * 右侧配置界面是否显示中
     */
    private var isOptionsShow = false

    /**
     * 目标进度
     */
    private var mTargetPosition = INVALID_VALUE

    /**
     * 是否在拖动进度条
     */
    internal var isDragging = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PlayerActivityVideoBinding.inflate(layoutInflater)
        bindingControlBottom = binding.playerViewStubHud.playerLayoutControlBottom
        setContentView(binding.root)
        setupUI()
        setVideoUri()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        setVideoUri()
    }

    override fun onResume() {
        super.onResume()
        initUI()
        play()
    }

    override fun onPause() {
        clearUI()
        pause()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.playerVideoViewIjk.stopPlayback()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun setupUI() {
        // 底部控制
        bindingControlBottom.playerBtnOverlayTracks.setOnClickListener(this)
        bindingControlBottom.playerBtnOverlayPlay.setOnClickListener(this)
        bindingControlBottom.playerBtnOverlayAdvFunction.setOnClickListener(this)
        bindingControlBottom.playerOverlaySeekbar.setOnSeekBarChangeListener(this)
        bindingControlBottom.playerOverlaySeekbar.max = MAX_VIDEO_SEEK
        // 左上角进度
        binding.playerViewStubHud.playerTipProgress.max = MAX_VIDEO_SEEK
        // 右侧配置
        binding.playerViewStubHud.playerOptionsList.layoutManager = LinearLayoutManager(this)
        binding.playerViewStubHud.playerOptionsList.adapter = optionsAdapter
        optionsAdapter.setOnItemClickListener(this)
        // 播放器
        val creator = mediaPlayerFactory.getCreator(MediaPlayerCreatorFactory.Type.IJK_PLAYER)
        binding.playerVideoViewIjk.setIsUsingSurfaceRenders(false)
        binding.playerVideoViewIjk.setMediaPlayerCreator(creator)
        binding.playerVideoViewIjk.setOnPreparedListener(this)
        binding.playerVideoViewIjk.setOnInfoListener(this)
        binding.playerVideoViewIjk.setOnErrorListener(this)
    }

    /**
     * 读取播放源
     */
    private fun setVideoUri() {
        val intent = intent ?: return
        val param: PlayParam = intent.getParcelableExtra(ARGS_VIDEO_PARAMS) ?: return
        val videoView = binding.playerVideoViewIjk
        bindingControlBottom.playerOverlayTitle.text = param.videoTitle
        videoView.setVideoURI(param.videoUri)
        videoView.seekTo(0)
        Timber.d(param.videoUri.toString())
        play()
    }

    private fun initUI() {
        binding.root.keepScreenOn = true
    }

    private fun clearUI() {
        binding.root.keepScreenOn = false
    }

    /**
     * 按键点击
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.player_btn_overlay_tracks -> {
                handler.overlayShow(false)
                handler.optionsShow(type = PlayerOption.PlayerOptionType.MEDIA_TRACKS)
            }
            R.id.player_btn_overlay_play -> {
                handler.setVideoPlay()
            }
            R.id.player_btn_overlay_adv_function -> {
                handler.overlayShow(false)
                handler.optionsShow(type = PlayerOption.PlayerOptionType.ADVANCED)
            }
        }
    }

    /**
     * 右侧配置点击
     */
    override fun onClick(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        when(item) {
            is PlayerOption -> {
                when(item.id) {
                    // 点击 回放速度
                    ID_PLAYBACK_SPEED -> {
                        toast("回放速度")
                        handler.optionsShow(false)
                    }
                    // 点击 A-B 循环
                    ID_AB_REPEAT -> {
                        toast("A-B 循环")
                        handler.optionsShow(false)
                    }
                    // 点击 音频轨
                    ID_AUDIO_TRACK -> {
                        toast("音频轨")
                        handler.optionsShow(false)

                    }
                    // 点击 音频延迟
                    ID_AUDIO_DELAY -> {
                        toast("音频延迟")
                        handler.optionsShow(false)
                    }
                }
            }
        }
    }

    /**
     * 用户拖动进度条中...
     */
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (!fromUser) return
        // 计算跳转位置
        val duration = binding.playerVideoViewIjk.duration
        mTargetPosition = progress * duration / MAX_VIDEO_SEEK
    }

    /**
     * 用户开始拖动进度条
     */
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        isDragging = true
        // 停止更新进度
        handler.stopUpdateProgress()
    }

    /**
     * 用户停止拖动进度条
     */
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        isDragging = false
        // 跳转到目标位置
        if (mTargetPosition != INVALID_VALUE) {
            handler.seekTo(mTargetPosition)
            mTargetPosition = INVALID_VALUE
        }
        // 继续更新进度
        handler.startUpdateProgress()
    }

    /**
     * 播放/暂停
     */
    internal fun setVideoPlay() {
        if (isPlaying()) pause() else play()
    }

    /**
     * 跳转
     * @param position 具体位置
     */
    internal fun seekTo(position: Int?) {
        if (position != null && position >= 0) {
            binding.playerVideoViewIjk.seekTo(position)
        }
    }

    /**
     * 快进/快退
     * @param delta 快进or快退时间
     */
    internal fun seekDelta(delta: Int?) {
        if (delta == null || delta == 0) return

        val videoView = binding.playerVideoViewIjk
        // 视频播放的当前进度
        val position = videoView.currentPosition.toLong()
        // 视频总的时长
        val duration = videoView.duration.toLong()

        val deltaPosition = position + delta
        if (deltaPosition > duration || deltaPosition < 0) return
        binding.playerVideoViewIjk.seekTo(deltaPosition.toInt())
    }

    /**
     * 更新进度
     */
    internal fun updateProgress(): Long {
        val videoView = binding.playerVideoViewIjk

        // 视频总的时长
        val duration = videoView.duration.toLong()
        if (duration <= 0) return duration

        // 视频播放的当前进度
        val position = videoView.currentPosition.toLong()
        // 转换为 Seek 显示的进度值
        val pos = MAX_VIDEO_SEEK * position / duration
        // 获取缓冲的进度百分比，并显示在 Seek 的次进度
        val percent = videoView.bufferPercentage

        bindingControlBottom.playerOverlaySeekbar.progress = pos.toInt()
        bindingControlBottom.playerOverlaySeekbar.secondaryProgress = percent * 10

        // 更新播放时间
        val timeFormat = Tools.millisToString(position)
        val lengthFormat = Tools.millisToString(duration)
        bindingControlBottom.playerOverlayLength.text ="%s/%s".format(timeFormat, lengthFormat)
        updateTipProgress()
        // 返回当前播放进度
        return position
    }

    /**
     * 更新左上角进度
     */
    private fun updateTipProgress() {
        val videoView = binding.playerVideoViewIjk
        // 视频总的时长
        val duration = videoView.duration.toLong()
        if (duration <= 0) return
        // 视频播放的当前进度
        val position = videoView.currentPosition.toLong()
        // 转换为 Seek 显示的进度值
        val pos = MAX_VIDEO_SEEK * position / duration
        // 左上角进度
        val timeFormat = Tools.millisToString(position)
        val lengthFormat = Tools.millisToString(duration)
        binding.playerViewStubHud.playerTipTime.text = "%s/%s".format(timeFormat, lengthFormat)
        binding.playerViewStubHud.playerTipProgress.progress = pos.toInt()
    }

    /**
     * 点击屏幕，切换控制相关界面的显示/隐藏
     */
    internal fun setControlShow() {
        if (hideControlView()) {
            return
        }
        // 切换 底部控制界面
        setOverlayShow()
    }

    /**
     * 显示/隐藏 底部控制界面
     * @param show 是否显示，null时切换当前状态
     */
    internal fun setOverlayShow(show: Boolean? = null) {
        if (show == isOverlayShow) return
        if (show ?: !isOverlayShow) {
            isOverlayShow = true
            // 播放按钮获得焦点
            bindingControlBottom.playerBtnOverlayPlay.requestFocus()
            bindingControlBottom.root.visibility = View.VISIBLE
        } else {
            isOverlayShow = false
            bindingControlBottom.root.visibility = View.GONE
        }
    }

    /**
     * 显示/隐藏 右侧配置界面
     * @param show 是否显示，null时切换当前状态
     * @param type 类型 字幕配置 or 其他配置
     */
    internal fun setOptionsShow(show: Boolean?, @PlayerOption.PlayerOptionType type: Int? = null) {
        if (show == isOptionsShow) return
        if (show ?: !isOptionsShow) {
            isOptionsShow = true
            optionsAdapter.submitList(when(type) {
                PlayerOption.PlayerOptionType.ADVANCED -> {
                    listOf(
                        PlayerOption(type, ID_PLAYBACK_SPEED, R.drawable.ic_speed, getString(R.string.playback_speed)),
                        PlayerOption(type, ID_AB_REPEAT, R.drawable.ic_ab_repeat, getString(R.string.ab_repeat))
                    )
                }
                PlayerOption.PlayerOptionType.MEDIA_TRACKS -> {
                    listOf(
                        PlayerOption(type, ID_AUDIO_TRACK, R.drawable.ic_audiotrack_w, getString(R.string.ctx_player_audio_track)),
                        PlayerOption(type, ID_AUDIO_DELAY, R.drawable.ic_audiodelay_w, getString(R.string.audio_delay))
                    )
                }
                else -> emptyList()
            })
            binding.playerViewStubHud.playerOptionsList.visibility = View.VISIBLE
        } else {
            isOptionsShow = false
            binding.playerViewStubHud.playerOptionsList.visibility = View.GONE
        }
    }

    /**
     * 隐藏控制界面
     */
    private fun hideControlView(): Boolean {
        var hasShow = false
        // 控制界面显示中，隐藏
        if (isOverlayShow) {
            setOverlayShow(false)
            hasShow = true
        }
        // 右侧配置界面显示中，隐藏
        if (isOptionsShow) {
            setOptionsShow(false)
            hasShow = true
        }
        return hasShow
    }

    /**
     * 触摸
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return touchDelegate.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyDownDelegate.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    /**
     * 返回
     */
    override fun onBackPressed() {
        if (hideControlView()) {
            return
        }

        if (supportFragmentManager.findFragmentByTag(DialogSelectFragment.TAG) == null) {
            DialogSelectFragment.Builder()
                .setTitle("你真的确认退出播放吗？")
                .setConfirmText(getString(R.string.ok))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener { finish() }
                .build()
                .show(supportFragmentManager)
        }
    }

    /**
     * 播放器解析事件回调
     */
    override fun onPrepared(mp: IMediaPlayer?) {

    }

    /**
     * 播放器播放事件回调
     */
    override fun onInfo(mp: IMediaPlayer?, what: Int, extra: Int): Boolean {
        when(what) {
            IMediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                Timber.d("MEDIA_INFO_BUFFERING_START")
            }
            MediaPlayerParams.STATE_PLAYING -> {
                Timber.d("STATE_PLAYING")
                // 开始更新进度
                handler.startUpdateProgress()
            }
            MediaPlayerParams.STATE_ERROR -> {
                Timber.d("STATE_ERROR")
                if (supportFragmentManager.findFragmentByTag(DialogSelectFragment.TAG) == null) {
                    DialogSelectFragment.Builder()
                        .setTitle("无限异常，无法播放视频。")
                        .hideCancel()
                        .setConfirmText(getString(R.string.shutdown))
                        .setConfirmClickListener { finish() }
                        .build()
                        .show(supportFragmentManager)
                }
            }
            MediaPlayerParams.STATE_COMPLETED -> {
                Timber.d("STATE_COMPLETED")
                // 播放完成后退出
                finish()
            }
        }
        return true
    }

    /**
     * 播放器错误事件回调
     */
    override fun onError(mp: IMediaPlayer?, what: Int, extra: Int): Boolean {
        Timber.d("onError what=$what, extra=$extra")
        return false
    }

    /**
     * 播放
     */
    private fun play() {
        if (!isPlaying()) {
            binding.playerVideoViewIjk.start()
            bindingControlBottom.playerBtnOverlayPlay.setImageResource(R.drawable.ic_pause_player)
        }
    }

    /**
     * 暂停
     */
    private fun pause() {
        if (isPlaying()) {
            binding.playerVideoViewIjk.pause()
            bindingControlBottom.playerBtnOverlayPlay.setImageResource(R.drawable.ic_play_player)
        }
    }

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean {
        return binding.playerVideoViewIjk.isPlaying
    }

}