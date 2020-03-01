package com.seiko.player.ui.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.seiko.common.ui.adapter.OnItemClickListener
import com.seiko.common.ui.dialog.DialogSelectFragment
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.danma.IDanmakuEngine
import com.seiko.danma.SimpleDrawHandlerCallback
import com.seiko.player.R
import com.seiko.player.data.model.PlayParam
import com.seiko.player.data.model.PlayOption
import com.seiko.player.databinding.PlayerActivityVideoBinding
import com.seiko.player.media.ijkplayer.MediaPlayerParams
import com.seiko.player.media.creator.MediaPlayerCreatorFactory
import com.seiko.player.ui.adapter.OptionsAdapter
import com.seiko.player.util.Tools
import com.seiko.player.util.constants.MAX_VIDEO_SEEK
import com.seiko.player.vm.PlayerViewModel
import com.seiko.subtitle.ISubtitleEngine
import com.seiko.subtitle.model.Subtitle
import com.seiko.subtitle.widget.setSubtitle
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import tv.danmaku.ijk.media.player.IMediaPlayer
import kotlin.math.abs

class VideoPlayerActivity: FragmentActivity()
    , View.OnClickListener
    , OnItemClickListener
    , SeekBar.OnSeekBarChangeListener {

    companion object {
        private const val ARGS_VIDEO_PARAMS = "ARGS_VIDEO_PARAMS"

        /**
         * 配置界面按键Id
         */
        private const val ID_AUDIO_TRACK = 31
        private const val ID_AUDIO_DELAY = 32
        private const val ID_PLAYBACK_SPEED = 6
        private const val ID_AB_REPEAT = 13

        /**
         * 无效值
         */
        private const val INVALID_VALUE = -1L

        /**
         * 跳转最小值
         */
        private const val SEEK_MIN_VALUE = 2000

        /**
         * 显示按钮2s后自动关闭
         */
        private const val DELAY_HIDE_CONTROL_TIME = 3000L

        fun launch(context: Context, param: PlayParam) {
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra(ARGS_VIDEO_PARAMS, param)
            context.startActivity(intent)
        }
    }

    /**
     * 右侧配置界面数据
     */
    private val optionList: List<PlayOption> by lazyAndroid {
        listOf(
            PlayOption(ID_AUDIO_TRACK, R.drawable.ic_audiotrack_w, getString(R.string.ctx_player_audio_track)),
            PlayOption(ID_AUDIO_DELAY, R.drawable.ic_audiodelay_w, getString(R.string.audio_delay)),
            PlayOption(ID_PLAYBACK_SPEED, R.drawable.ic_speed, getString(R.string.playback_speed)),
            PlayOption(ID_AB_REPEAT, R.drawable.ic_ab_repeat, getString(R.string.ab_repeat))
        )
    }

    private val viewModel: PlayerViewModel by viewModel()
    private val mediaPlayerFactory: MediaPlayerCreatorFactory by inject()
    private val danmakuEngine: IDanmakuEngine by inject()
    private val subtitleEngine: ISubtitleEngine by inject()

    private var _binding: PlayerActivityVideoBinding? = null
    private val binding get() = _binding!!
    private val bindingControlBottom get() = binding.playerViewController.playerLayoutControlBottom
    private val bindingTipLayout get() = binding.playerViewController.playerTipLayout

    private val handler by lazyAndroid { VideoPlayerHandler(this) }

    private val optionsAdapter by lazyAndroid { OptionsAdapter(this) }

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
    private var mTargetPosition =
        INVALID_VALUE

    /**
     * 跳转任务
     */
    private inner class SeekToRunnable : Runnable {
        var position = 0L
        override fun run() {
            // 隐藏左上角进度条
            if (bindingTipLayout.root.visibility == View.VISIBLE) {
                bindingTipLayout.root.visibility = View.GONE
            }

            // 重置参数
            if (mTargetPosition != INVALID_VALUE) {
                mTargetPosition =
                    INVALID_VALUE
            }

            val videoView = binding.playerVideoViewIjk
            // 跳转位置
            if (position >= 0 && abs(videoView.currentPosition - position) > SEEK_MIN_VALUE) {
                // 视频
                binding.playerVideoViewIjk.seekTo(position)
                // 弹幕
                danmakuEngine.seekTo(position)
            }
        }
    }
    private val seekToRunnable = SeekToRunnable()

    /**
     * 快进/快退任务
     */
    private inner class SeekDeltaRunnable : Runnable {
        var delta = 0L
        override fun run() {
            val videoView = binding.playerVideoViewIjk
            // 视频播放的当前进度
            val position = if (mTargetPosition == INVALID_VALUE)
                videoView.currentPosition else mTargetPosition
            // 视频总的时长
            val duration = videoView.duration

            // 移动的位置
            var deltaPosition = position + delta
            if (deltaPosition > duration) {
                deltaPosition = duration
            } else if (deltaPosition < 0) {
                deltaPosition = 0
            }
            // 更新左上角进度条
            updateTipProgress(position)
            // 记录位置
            mTargetPosition = deltaPosition
        }
    }
    private val seekDeltaRunnable = SeekDeltaRunnable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = PlayerActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        bindViewModel()
        setVideoUri()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        setVideoUri()
    }

    override fun onResume() {
        super.onResume()
        viewModel.play()
    }

    override fun onPause() {
        viewModel.pause()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.playerVideoViewIjk.stopPlayback()
        danmakuEngine.release()
        subtitleEngine.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        handler.removeCallbacksAndMessages(null)
    }

    private fun setupUI() {
        // 底部控制
        bindingControlBottom.playerBtnOverlayDanma.setOnClickListener(this)
        bindingControlBottom.playerBtnOverlayPlay.setOnClickListener(this)
        bindingControlBottom.playerBtnOverlayAdvFunction.setOnClickListener(this)
        bindingControlBottom.playerOverlaySeekbar.setOnSeekBarChangeListener(this)
        bindingControlBottom.playerOverlaySeekbar.max = MAX_VIDEO_SEEK
        // 左上角进度
        bindingTipLayout.playerTipProgress.max = MAX_VIDEO_SEEK
        // 右侧配置
        binding.playerViewController.playerOptionsList.layoutManager = LinearLayoutManager(this)
        binding.playerViewController.playerOptionsList.adapter = optionsAdapter
        optionsAdapter.submitList(optionList)
        optionsAdapter.setOnItemClickListener(this)
        // 播放器
        val creator = mediaPlayerFactory.getCreator(MediaPlayerCreatorFactory.Type.EXO_PLAYER)
        binding.playerVideoViewIjk.setIsUsingSurfaceRenders(false)
        binding.playerVideoViewIjk.setMediaPlayerCreator(creator)
        binding.playerVideoViewIjk.setOnPreparedListener {
            // 回到0点
            binding.playerVideoViewIjk.seekTo(0)
            // 开始播放
            viewModel.play()
        }
        binding.playerVideoViewIjk.setOnSeekCompleteListener {
            Timber.d("OnSeekComplete")
        }
        binding.playerVideoViewIjk.setOnInfoListener { mp: IMediaPlayer?, what: Int, extra: Int ->
            when(what) {
                MediaPlayerParams.STATE_PREPARED -> {
                    Timber.d("STATE_PREPARED")
                    // 绑定播放器到弹幕引擎
                    danmakuEngine.bindDanmakuView(binding.playerDanmakuView)
                    // 绑定播放器到字幕引擎
                    subtitleEngine.bindToMediaPlayer(mp)
                }
                MediaPlayerParams.STATE_PLAYING -> {
                    Timber.d("STATE_PLAYING")
                    // 开始更新进度
                    handler.startUpdateProgress()
                }
                MediaPlayerParams.STATE_PAUSED -> {
                    Timber.d("STATE_PAUSED")
                    handler.stopUpdateProgress()
                }
                MediaPlayerParams.STATE_ERROR -> {
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
                    // 播放完成后退出
                    finish()
                }
            }
            return@setOnInfoListener true
        }
        binding.playerVideoViewIjk.setOnErrorListener { mp: IMediaPlayer?, what: Int, extra: Int ->
            Timber.d("onError what=$what, extra=$extra")
            return@setOnErrorListener false
        }
        // 弹幕
        danmakuEngine.setCallback(object : SimpleDrawHandlerCallback() {
            override fun prepared() {
                Timber.d("danma Prepared")
                lifecycleScope.launch {
                    danmakuEngine.seekTo(binding.playerVideoViewIjk.currentPosition)
                }
            }
        })
        // 字幕
        subtitleEngine.setOnSubtitleListener(object : ISubtitleEngine.OnSubtitleListener {
            override fun onSubtitlePrepared() {
                Timber.d("subtitle Prepared")
                subtitleEngine.start()
            }

            override fun onSubtitleChanged(subtitle: Subtitle?) {
                lifecycleScope.launch {
                    binding.playerSubtitleView.setSubtitle(subtitle)
                }
            }
        })
    }

    /**
     * 绑定ViewModel
     */
    private fun bindViewModel() {
        viewModel.isPlaying.observe(this::getLifecycle) { isPlaying ->
            if (isPlaying == true) {
                // 视频
                if (!isPlaying()) {
                    binding.playerVideoViewIjk.start()
                    bindingControlBottom.playerBtnOverlayPlay.setImageResource(R.drawable.ic_pause_player)
                }
                // 弹幕
                danmakuEngine.play()
                // 字幕
                subtitleEngine.start()
                // 屏幕常亮
                binding.root.keepScreenOn = true
            } else {
                // 视频
                if (isPlaying()) {
                    binding.playerVideoViewIjk.pause()
                    bindingControlBottom.playerBtnOverlayPlay.setImageResource(R.drawable.ic_play_player)
                }
                // 弹幕
                danmakuEngine.pause()
                // 字幕
                subtitleEngine.stop()
                // 取消屏幕常亮
                binding.root.keepScreenOn = false
            }
        }
        viewModel.showDanma.observe(this::getLifecycle) { isShowDanma ->
            if (isShowDanma) {
                danmakuEngine.show()
                bindingControlBottom.playerBtnOverlayDanma.isSelected = true
            } else {
                danmakuEngine.hide()
                bindingControlBottom.playerBtnOverlayDanma.isSelected = false
            }
        }
        viewModel.danma.observe(this::getLifecycle, danmakuEngine::setDanmaList)
        viewModel.subtitlePath.observe(this::getLifecycle, subtitleEngine::setSubtitlePath)
    }

    /**
     * 读取播放源
     */
    private fun setVideoUri() {
        val intent = intent ?: return
        val param: PlayParam = intent.getParcelableExtra(ARGS_VIDEO_PARAMS) ?: return
        val videoView = binding.playerVideoViewIjk
        bindingControlBottom.playerOverlayTitle.text = param.videoTitle
        videoView.setVideoURI(Uri.parse(param.videoPath))
        // 尝试下载弹幕字幕
        viewModel.videoParam.value = param
    }

    /**
     * 按键点击
     */
    override fun onClick(v: View?) {
        when(v?.id) {
            // 弹幕
            R.id.player_btn_overlay_danma -> {
                viewModel.setDanmaShow()
            }
            // 播放/暂停
            R.id.player_btn_overlay_play -> {
                viewModel.setVideoPlay()
            }
            // 配置
            R.id.player_btn_overlay_adv_function -> {
                handler.overlayShow(false)
                handler.optionsShow()
            }
        }
    }

    /**
     * 右侧配置点击
     */
    override fun onClick(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        when(item) {
            is PlayOption -> {
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
        seekToDelay(mTargetPosition)
    }

    /**
     * 用户开始拖动进度条
     */
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
//        Timber.d("onStartTrackingTouch")
//        // 停止更新进度
//        handler.stopUpdateProgress()
    }

    /**
     * 用户停止拖动进度条
     * TODO TV端不触发，待找原因
     */
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
//        Timber.d("onStopTrackingTouch")
//        // 跳转到目标位置
//        handler.seekTo(mTargetPosition)
//        // 继续更新进度；由于seek走了view post，需要加点延时
//        handler.startUpdateProgress(1400)
    }

    /**
     * 延时跳转
     */
    private fun seekToDelay(position: Long) {
        // 停止更新进度条/ 500ms后跳转目标位置 / 1500ms后继续更新进度条
//        handler.stopUpdateProgress()
        handler.seekTo(position, 600)
//        handler.startUpdateProgress(1600)
    }

    /**
     * 跳转
     * @param position 具体位置
     */
    internal fun seekTo(position: Long?) {
        seekToRunnable.position = position ?: return
        binding.root.post(seekToRunnable)
    }

    /**
     * 快进/快退
     * @param delta 快进or快退时间
     */
    private fun seekDelta(delta: Long?) {
        if (delta == null || delta == 0L) return
        seekDeltaRunnable.delta = delta
        binding.root.post(seekDeltaRunnable)
    }

    /**
     * 更新进度
     */
    internal fun updateProgress(): Long {
        // 隐藏左上角进度条
        if (bindingTipLayout.root.visibility == View.VISIBLE) {
            bindingTipLayout.root.visibility = View.GONE
        }

        val videoView = binding.playerVideoViewIjk
        // 视频总的时长
        val duration = videoView.duration
        if (duration <= 0) return duration

        // 视频播放的当前进度
        val position = videoView.currentPosition
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
        // 返回当前播放进度
        return position
    }

    /**
     * 更新左上角进度
     */
    private fun updateTipProgress(position: Long) {
        // 显示左上角进度条
        if (bindingTipLayout.root.visibility == View.GONE) {
            bindingTipLayout.root.visibility = View.VISIBLE
        }

        val videoView = binding.playerVideoViewIjk
        // 视频总的时长
        val duration = videoView.duration
        if (duration <= 0) return

        // 转换为 Seek 显示的进度值
        val pos = MAX_VIDEO_SEEK * position / duration
        bindingTipLayout.playerTipProgress.progress = pos.toInt()
        // 左上角进度
        val timeFormat = Tools.millisToString(position)
        val lengthFormat = Tools.millisToString(duration)
        bindingTipLayout.playerTipTime.text = "%s/%s".format(timeFormat, lengthFormat)
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

        // 切换成了显示状态，5秒后关闭
        if (isOverlayShow) {
            handler.overlayShow(false,
                DELAY_HIDE_CONTROL_TIME
            )
        }
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
     */
    internal fun setOptionsShow(show: Boolean?) {
        if (show == isOptionsShow) return
        if (show ?: !isOptionsShow) {
            isOptionsShow = true
            binding.playerViewController.playerOptionsList.visibility = View.VISIBLE
        } else {
            isOptionsShow = false
            binding.playerViewController.playerOptionsList.visibility = View.GONE
        }
    }

    /**
     * 触摸
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false
        when(event.action) {
            MotionEvent.ACTION_UP -> {
                handler.controlShow()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 按键
     */
    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event == null) return false

        val keyCode = event.keyCode
        Timber.d("keyCode=${event.keyCode}")

        // 控制界面是否显示
        if (isControlViewShow()) {
            when(keyCode) {
                // 四个方向键 延长控制界面显示
                KeyEvent.KEYCODE_DPAD_UP,
                KeyEvent.KEYCODE_DPAD_LEFT,
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    if (KeyEvent.ACTION_DOWN == event.action) {
                        handler.overlayShow(false,
                            DELAY_HIDE_CONTROL_TIME
                        )
//                        return true
                    }
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if (KeyEvent.ACTION_DOWN == event.action) {
                        // ↓键特殊，如果是播放按钮获得焦点，按下直接关闭控制界面
                        if (bindingControlBottom.playerBtnOverlayPlay.isFocused) {
                            handler.overlayShow(false)
                        } else {
                            handler.overlayShow(false,
                                DELAY_HIDE_CONTROL_TIME
                            )
                        }
//                        return true
                    }
                }
            }
        } else {
            when(keyCode) {
                // ok键
                KeyEvent.KEYCODE_DPAD_CENTER -> {
                    if (KeyEvent.ACTION_UP == event.action) {
                        // 切换播放状态
                        viewModel.setVideoPlay()
                        return true
                    }
                    return false
                }
                // ↑键： TODO 显示/隐藏 播放列表
                KeyEvent.KEYCODE_DPAD_UP -> {
                    return true
                }
                // ↓键 显示 底部控制界面
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    if (KeyEvent.ACTION_UP == event.action) {
                        handler.overlayShow(true)
                        return true
                    }
                }
                // →键： 快退10秒
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    when(event.action) {
                        KeyEvent.ACTION_DOWN -> {
                            // 移动
                            seekDelta(-10000)
                        }
                        KeyEvent.ACTION_UP -> {
                            // 跳转
                            seekToDelay(mTargetPosition)
                        }
                    }
                    return true
                }
                // ←键： 快进10秒
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    when(event.action) {
                        KeyEvent.ACTION_DOWN -> {
                            // 移动
                            seekDelta(10000)
                        }
                        KeyEvent.ACTION_UP -> {
                            // 跳转
                            seekToDelay(mTargetPosition)
                        }
                    }
                    return true
                }
                // 菜单键： 显示/ 隐藏 菜单
                KeyEvent.KEYCODE_MENU -> {
                    // 切换状态
                    handler.optionsShow()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
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
                .setTitle(getString(R.string.dialog_exit_player))
                .setConfirmText(getString(R.string.ok))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener { finish() }
                .build()
                .show(supportFragmentManager)
        }
    }

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean {
        return binding.playerVideoViewIjk.isPlaying
    }

    /**
     * 是否用控制界面显示
     */
    private fun isControlViewShow(): Boolean {
        return isOverlayShow || isOptionsShow
    }

    /**
     * 尝试隐藏控制界面
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

}