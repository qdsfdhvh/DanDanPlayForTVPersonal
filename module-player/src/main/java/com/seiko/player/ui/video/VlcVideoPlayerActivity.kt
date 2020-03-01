package com.seiko.player.ui.video

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.*
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.seiko.common.ui.dialog.DialogSelectFragment
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.danma.IDanmakuEngine
import com.seiko.danma.SimpleDrawHandlerCallback
import com.seiko.player.R
import com.seiko.player.data.model.PlayParam
import com.seiko.player.databinding.PlayerActivityVideoVlcBinding
import com.seiko.player.util.Tools
import com.seiko.player.util.constants.MAX_VIDEO_SEEK
import com.seiko.player.util.extensions.setInvisible
import com.seiko.player.util.extensions.setVisible
import com.seiko.player.vlc.media.PlayerManager
import com.seiko.player.vm.PlayerViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.DisplayManager
import org.videolan.medialibrary.MLServiceLocator
import org.videolan.medialibrary.interfaces.Medialibrary
import timber.log.Timber
import kotlin.math.abs

class VlcVideoPlayerActivity : FragmentActivity()
    , View.OnClickListener
    , MediaPlayer.EventListener {

    companion object {

        private const val ARGS_VIDEO_PARAMS = "ARGS_VIDEO_PARAMS"

        /**
         * 控制界面显示4s后关闭
         */
        private const val OVERLAY_TIMEOUT = 4000

        /**
         * 不自动关闭控制界面
         */
        private const val OVERLAY_INFINITE = -1

        private const val FADE_OUT = 1
        private const val FADE_OUT_TIP = 2
        private const val SHOW_INFO = 8
        private const val HIDE_INFO = 9

        fun launch(context: Context, param: PlayParam) {
            val intent = Intent(context, VlcVideoPlayerActivity::class.java)
            intent.putExtra(ARGS_VIDEO_PARAMS, param)
            context.startActivity(intent)
        }
    }

    private var _binding: PlayerActivityVideoVlcBinding? = null
    private val binding get() = _binding!!
    private val videoLayout get() = binding.playerVideoViewVlc
    private val bottomControl get() = binding.playerViewController.playerLayoutControlBottom
    private val seekbar get() = bottomControl.playerOverlaySeekbar
    private val tipProgress get() = binding.playerViewController.playerTipLayout

    private val viewModel: PlayerViewModel by inject()
    private val danmakuEngine: IDanmakuEngine by inject()
    private val player: PlayerManager by inject()

    private lateinit var displayManager: DisplayManager

    private var isShowing = false
    private var isNavMenu = false
    private var isDragging = false

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when(msg.what) {
                FADE_OUT -> hideOverlay(false)
                FADE_OUT_TIP -> fadeOutTipProgress()
                HIDE_INFO -> hideOverlay(true)
                SHOW_INFO -> showOverlay()
            }
        }
    }

    private val seekListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) {
            isDragging = true
            showOverlayTimeout(OVERLAY_INFINITE)
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            isDragging = false
            showOverlay(true)
        }

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if (!isFinishing && fromUser && player.seekable) {
                val position = progress * player.getCurrentDuration() / MAX_VIDEO_SEEK
                seekTo(position)
                showTipProgress(position, 1000)
            }
            if (fromUser) {
                showOverlay(true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = PlayerActivityVideoVlcBinding.inflate(layoutInflater)
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
        play()
        binding.root.keepScreenOn = true
    }

    override fun onPause() {
        binding.root.keepScreenOn = false
        pause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        danmakuEngine.release()
        displayManager.release()
        GlobalScope.launch {
            player.release()
        }
    }

    /**
     * 读取播放源
     */
    private fun setVideoUri() {
        val intent = intent ?: return
        val param: PlayParam = intent.getParcelableExtra(ARGS_VIDEO_PARAMS) ?: return

        var media = Medialibrary.getInstance().getMedia(param.videoPath)
        if (media == null) {
            media = MLServiceLocator.getAbstractMediaWrapper(Uri.parse(param.videoPath))
        }

        // 加载视频标题
        var videoTitle = param.videoTitle
        if (videoTitle.isEmpty()) {
            videoTitle = media.title
        }
        bottomControl.playerOverlayTitle.text = videoTitle

        // 加载弹幕
        viewModel.videoParam.value = param

        lifecycleScope.launch {
            player.load(media, this@VlcVideoPlayerActivity)
        }
    }

    private fun setupUI() {
        // 播放器
        displayManager = DisplayManager(this, null, false, false, false)
        val mediaPlayer = player.mediaPlayer
        mediaPlayer.attachViews(videoLayout, displayManager, true, false)
        mediaPlayer.videoScale = MediaPlayer.ScaleType.SURFACE_BEST_FIT
        // 底部控制
        bottomControl.playerBtnOverlayPlay.setOnClickListener(this)
        bottomControl.playerBtnOverlayDanma.setOnClickListener(this)
        bottomControl.playerBtnOverlayAdvFunction.setOnClickListener(this)
        seekbar.setOnSeekBarChangeListener(seekListener)
        seekbar.max = MAX_VIDEO_SEEK
        tipProgress.playerTipProgress.max = MAX_VIDEO_SEEK
        // 弹幕
        danmakuEngine.bindDanmakuView(binding.playerDanmakuView)
        danmakuEngine.setCallback(object : SimpleDrawHandlerCallback() {
            override fun prepared() {
                lifecycleScope.launchWhenStarted {
                    danmakuEngine.seekTo(player.getCurrentPosition())
                }
            }
        })
    }

    private fun bindViewModel() {
        player.getProgressLiveData().observe(this::getLifecycle) { progress ->
            if (progress == null) return@observe
            // 更新进度条
            seekbar.progress = (MAX_VIDEO_SEEK * progress.position / progress.duration).toInt()
            // 更新文本时间
            val timeFormat = Tools.millisToString(progress.position)
            val lengthFormat = Tools.millisToString(progress.duration)
            bottomControl.playerOverlayLength.text ="%s/%s".format(timeFormat, lengthFormat)
        }
        viewModel.showDanma.observe(this::getLifecycle) { show ->
            if (show) {
                danmakuEngine.show()
                bottomControl.playerBtnOverlayDanma.isSelected = true
            } else {
                danmakuEngine.hide()
                bottomControl.playerBtnOverlayDanma.isSelected = false
            }
        }
        viewModel.danma.observe(this::getLifecycle, danmakuEngine::setDanmaList)
    }

    /**
     * 播放
     */
    private fun play() {
        danmakuEngine.play()
        player.play()
    }

    /**
     * 暂停
     */
    private fun pause() {
        danmakuEngine.pause()
        player.pause()
    }

    /**
     * 跳转
     */
    private fun seekTo(position: Long) {
        danmakuEngine.seekTo(position)
        player.seekTo(position)
    }

    /**
     * 切换 播放/暂停
     */
    private fun doPlayPause() {
        if (player.isPlaying()) {
            showOverlayTimeout(OVERLAY_INFINITE)
            pause()
        } else {
            handler.sendEmptyMessageDelayed(FADE_OUT, 300)
            play()
        }
    }

    /**
     * 显示左上角进度条
     */
    private fun showTipProgress(position: Long, delay: Int) {
        if (tipProgress.root.visibility != View.VISIBLE) {
            tipProgress.root.setVisible()
        }

        val duration = player.getCurrentDuration()
        tipProgress.playerTipProgress.progress = (MAX_VIDEO_SEEK * position / duration).toInt()
        val timeFormat = Tools.millisToString(position)
        val lengthFormat = Tools.millisToString(duration)
        tipProgress.playerTipTime.text = "%s/%s".format(timeFormat, lengthFormat)

        handler.removeMessages(FADE_OUT_TIP)
        handler.sendEmptyMessageDelayed(FADE_OUT_TIP, delay.toLong())
    }

    /**
     * 隐藏左上角进度条
     */
    private fun fadeOutTipProgress() {
        if (tipProgress.root.visibility == View.VISIBLE) {
            tipProgress.root.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out))
            tipProgress.root.setInvisible()
        }
    }

    /**
     * 控件点击
     */
    override fun onClick(v: View?) {
        if (v == null) return
        when(v.id) {
            R.id.player_btn_overlay_danma -> {
                viewModel.setDanmaShow()
            }
            R.id.player_btn_overlay_play -> {
                doPlayPause()
            }
            R.id.player_btn_overlay_adv_function -> {

            }
        }
    }

    /**
     * 触摸
     */
    private var numberOfTaps = 0
    private var lastTapTimeMs: Long = 0
    private var touchDownMs: Long = 0
    private var initTouchY = 0f
    private var initTouchX = 0f
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val now = System.currentTimeMillis()
        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownMs = now
                initTouchX = event.x
                initTouchY = event.y
            }
            MotionEvent.ACTION_MOVE -> {

            }
            MotionEvent.ACTION_UP -> {
                val touchSlop = ViewConfiguration.get(this).scaledTouchSlop

                handler.removeCallbacksAndMessages(null)

                if (now - touchDownMs > ViewConfiguration.getDoubleTapTimeout()) {
                    numberOfTaps = 0
                    lastTapTimeMs = 0
                }

                if (abs(event.x - initTouchX) < touchSlop && abs(event.y - initTouchY) < touchSlop) {
                    if (numberOfTaps > 0 && now - lastTapTimeMs < ViewConfiguration.getDoubleTapTimeout()) {
                        numberOfTaps += 1
                    } else {
                        numberOfTaps = 1
                    }
                }

                lastTapTimeMs = now

                handler.postDelayed({
                    if (numberOfTaps == 1) {
                        val what = if (isShowing) HIDE_INFO else SHOW_INFO
                        handler.sendEmptyMessage(what)
                    }
                }, ViewConfiguration.getDoubleTapTimeout().toLong())
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /**
     * 按键
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when(keyCode) {
            // ok键
            KeyEvent.KEYCODE_DPAD_CENTER -> {
                if (!isShowing) {
                    doPlayPause()
                    return true
                }
            }
            // ↑键： TODO 显示/隐藏 播放列表
            KeyEvent.KEYCODE_DPAD_UP -> {
                return true
            }
            // ↓键 显示 底部控制界面
            KeyEvent.KEYCODE_DPAD_DOWN -> {

            }
            // →键： 快退10秒
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                return true
            }
            // ←键： 快进10秒
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                return true
            }
            // 菜单键： 显示/ 隐藏 菜单
            KeyEvent.KEYCODE_MENU -> {
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * show overlay
     */
    private var overlayTimeout = 0
    private fun showOverlay(forceCheck: Boolean = false) {
        Timber.d("showOverlay forceCheck=$forceCheck isShowing=$isShowing")
        if (forceCheck) {
            overlayTimeout = 0
        }
        showOverlayTimeout(0)
    }

    private fun showOverlayTimeout(timeout: Int) {
        overlayTimeout = when {
            timeout != 0 -> timeout
            player.isPlaying() -> OVERLAY_TIMEOUT
            else -> OVERLAY_TIMEOUT
        }
        if (isNavMenu) {
            isShowing = true
            return
        }
        if (!isShowing) {
            isShowing = true

            // 显示控制界面
            bottomControl.root.setVisible()
            bottomControl.playerBtnOverlayPlay.requestFocus()

            disStatusBar(false)

        }
        handler.removeMessages(FADE_OUT)
        if (overlayTimeout != OVERLAY_INFINITE) {
            Timber.d("delay hideOverlay time=$overlayTimeout")
            handler.sendMessageDelayed(handler.obtainMessage(FADE_OUT), overlayTimeout.toLong())
        }
    }

    /**
     * hide overlay
     */
    private fun hideOverlay(fromUser: Boolean) {
        Timber.d("hideOverlay fromUser=$fromUser isShowing=$isShowing")
        if (isShowing) {
            isShowing = false
            handler.removeMessages(FADE_OUT)

            // 隐藏控制界面
            bottomControl.root.setInvisible()

            disStatusBar(true)
        } else if (!fromUser) {
            disStatusBar(true)
        }
    }

    /**
     * Dim the status bar and navigation icons
     */
    private fun disStatusBar(dim: Boolean) {
        if (isNavMenu) return

        var visibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        var navBar = 0
        if (dim) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            navBar = navBar or (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            visibility = visibility or (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            visibility = visibility or (View.SYSTEM_UI_FLAG_VISIBLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
        window.decorView.systemUiVisibility = visibility or navBar
    }

    /**
     * 播放器事件监听
     */
    override fun onEvent(event: MediaPlayer.Event?) {
        if (event == null) return
        when(event.type) {
            MediaPlayer.Event.Playing,
            MediaPlayer.Event.Paused -> updateOverlayPlayPause()
        }
    }

    /**
     * 更新播放按钮
     */
    private val playToPause by lazyAndroid { AnimatedVectorDrawableCompat.create(this, R.drawable.anim_play_pause)!! }
    private val pauseToPlay by lazyAndroid { AnimatedVectorDrawableCompat.create(this, R.drawable.anim_pause_play)!! }
    private fun updateOverlayPlayPause(skipAnim: Boolean = false) {
        if (skipAnim) {
            val resource = if (player.isPlaying()) {
                R.drawable.ic_pause_player
            } else {
                R.drawable.ic_play_player
            }
            bottomControl.playerBtnOverlayPlay.setImageResource(resource)
        } else {
            val drawable = if (player.isPlaying()) {
                playToPause
            } else {
                pauseToPlay
            }
            bottomControl.playerBtnOverlayPlay.setImageDrawable(drawable)
            drawable.start()
        }
    }

    override fun onBackPressed() {
        if (isShowing) {
            hideOverlay(true)
            return
        }

        if (supportFragmentManager.findFragmentByTag(DialogSelectFragment.TAG) == null) {
            pause()

            DialogSelectFragment.Builder()
                .setTitle("你真的确认退出播放吗？")
                .setConfirmText(getString(R.string.ok))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener { finish() }
                .setDismissClickListener { play() }
                .build()
                .show(supportFragmentManager)
        }
    }
}