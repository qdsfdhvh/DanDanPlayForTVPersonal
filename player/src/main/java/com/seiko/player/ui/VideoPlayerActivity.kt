package com.seiko.player.ui

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.*
import android.widget.SeekBar
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.common.ui.adapter.OnItemClickListener
import com.seiko.common.ui.dialog.DialogSelectFragment
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.player.R
import com.seiko.player.data.model.PlayerOption
import com.seiko.player.databinding.PlayerActivityVideoBinding
import com.seiko.player.databinding.PlayerControlBottomBinding
import com.seiko.player.delegate.VideoKeyDownDelegate
import com.seiko.player.delegate.VideoTouchDelegate
import com.seiko.player.service.PlaybackService
import com.seiko.player.media.checkCpuCompatibility
import com.seiko.player.ui.adapter.*
import com.seiko.player.vm.PlayerViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.videolan.libvlc.util.DisplayManager
import org.videolan.medialibrary.MLServiceLocator
import org.videolan.medialibrary.Tools
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import timber.log.Timber
import java.lang.ref.WeakReference

//private const val ID_PLAY_AS_AUDIO = 0
//private const val ID_SLEEP = 1
//private const val ID_JUMP_TO = 2
private const val ID_AUDIO_TRACK = 31
private const val ID_AUDIO_DELAY = 32
//private const val ID_SPU_DELAY = 4
//private const val ID_CHAPTER_TITLE = 5
private const val ID_PLAYBACK_SPEED = 6
//private const val ID_EQUALIZER = 7
//private const val ID_SAVE_PLAYLIST = 8
//private const val ID_POPUP_VIDEO = 9
//private const val ID_REPEAT = 10
//private const val ID_SHUFFLE = 11
//private const val ID_PASS_THROUGH = 12
private const val ID_AB_REPEAT = 13
//private const val ID_OVERLAY_SIZE = 14
//private const val ID_VIDEO_STATS = 15

@Route(path = Routes.Player.PATH)
class VideoPlayerActivity: FragmentActivity()
    , View.OnClickListener
    , OnItemClickListener
    , SeekBar.OnSeekBarChangeListener {

    private lateinit var binding: PlayerActivityVideoBinding
    private lateinit var bindingControlBottom: PlayerControlBottomBinding

    private lateinit var displayManager: DisplayManager

    private val handler by lazyAndroid { VideoPlayerHandler(this) }
    private val keyDownDelegate by lazyAndroid { VideoKeyDownDelegate(handler) }
    private val touchDelegate by lazyAndroid { VideoTouchDelegate(handler) }

    private val optionsAdapter by lazyAndroid { OptionsAdapter(this) }

    private val viewModel: PlayerViewModel by viewModel()

    private var isOverlayShow = false
    private var isOptionsShow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkCpuCompatibility()
        initDisplayManager()
        binding = PlayerActivityVideoBinding.inflate(layoutInflater)
        bindingControlBottom = binding.playerViewStubHud.playerLayoutControlBottom
        setContentView(binding.root)
        setupUI()
        bindViewModel()
        loadMedia()
    }

    override fun onResume() {
        super.onResume()
        initUI()
        viewModel.play()
    }

    override fun onPause() {
        super.onPause()
        viewModel.pause()
        clearUI()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        displayManager.release()
        viewModel.release()
    }

    private fun initDisplayManager() {
        displayManager = DisplayManager(this, null,
            false, false, false)
    }

    private fun setupUI() {
        // 底部控制
        bindingControlBottom.playerBtnOverlayTracks.setOnClickListener(this)
        bindingControlBottom.playerBtnOverlayPlay.setOnClickListener(this)
        bindingControlBottom.playerBtnOverlayAdvFunction.setOnClickListener(this)
        bindingControlBottom.playerOverlaySeekbar.setOnSeekBarChangeListener(this)
        // 右侧配置
        binding.playerViewStubHud.playerOptionsList.layoutManager = LinearLayoutManager(this)
        binding.playerViewStubHud.playerOptionsList.adapter = optionsAdapter
        optionsAdapter.setOnItemClickListener(this)
    }

    private fun bindViewModel() {
        viewModel.progress.observe(this::getLifecycle) { progress ->
            bindingControlBottom.playerOverlayTime.text = Tools.millisToString(progress.time)
            bindingControlBottom.playerOverlayLength.text = Tools.millisToString(progress.length)
            bindingControlBottom.playerOverlaySeekbar.progress = if (progress.length == 0L)
                0 else (progress.time * 100 / progress.length).toInt()
            bindingControlBottom.playerOverlaySeekbar.max = 100
        }
    }

    private fun initUI() {
        viewModel.attachView(binding.videoLayout, displayManager)
        displayManager.setMediaRouterCallback()
        binding.root.keepScreenOn = true
    }

    private fun clearUI() {
        binding.root.keepScreenOn = false
        displayManager.removeMediaRouterCallback()
        viewModel.detachView()
    }

    /**
     * 读取播放源
     */
    private fun loadMedia() {
        val intent = intent
        var videoUri: Uri? = null
        if (intent.data != null) {
            videoUri = intent.data
        }

        if (videoUri != null) {
            var media = Medialibrary.getInstance().getMedia(videoUri)
            if (media == null) {
                media = MLServiceLocator.getAbstractMediaWrapper(videoUri)
            }
            media.addFlags(MediaWrapper.MEDIA_VIDEO)
            PlaybackService.openMedia(this, media)
        }
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
                toast("播放/暂停")
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
                    ID_PLAYBACK_SPEED -> {
                        toast("回放速度")
                        handler.optionsShow(false)
                    }
                    ID_AB_REPEAT -> {
                        toast("A-B 循环")
                        handler.optionsShow(false)
                    }
                    ID_AUDIO_TRACK -> {
                        toast("音频轨")
                        handler.optionsShow(false)

                    }
                    ID_AUDIO_DELAY -> {
                        toast("音频延迟")
                        handler.optionsShow(false)
                    }
                }
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            viewModel.seekTo(progress)
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    /**
     * 点击屏幕，切换控制相关界面的显示/隐藏
     */
    internal fun setControlShow() {
        if (checkControlShow()) {
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
            optionsAdapter.items = when(type) {
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
            }
            binding.playerViewStubHud.playerOptionsList.visibility = View.VISIBLE
        } else {
            isOptionsShow = false
            binding.playerViewStubHud.playerOptionsList.visibility = View.GONE
        }
    }

    /**
     * 检测是否有控制界面显示
     */
    private fun checkControlShow(): Boolean {
        // 控制界面显示中，隐藏
        if (isOverlayShow) {
            setOverlayShow(false)
            return true
        }
        // 右侧配置界面显示中，隐藏
        if (isOptionsShow) {
            setOptionsShow(false)
            return true
        }
        return false
    }

    /**
     * 触摸
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return touchDelegate.onTouchEvent(event) || super.onTouchEvent(event)
    }

    /**
     * 按键
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyDownDelegate.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    /**
     * 返回
     */
    override fun onBackPressed() {
        if (checkControlShow()) {
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

}

class VideoPlayerHandler(activity: VideoPlayerActivity) : Handler(Looper.getMainLooper()) {

    companion object {
        private const val SET_CONTROL_SHOW = 10
        private const val SET_OVERLAY_SHOW = 11
        private const val SET_OPTIONS_SHOW = 12
    }

    private val activity = WeakReference(activity)

    override fun handleMessage(msg: Message) {
        when(msg.what) {
            SET_CONTROL_SHOW -> {
                activity.get()?.setControlShow()
            }
            SET_OVERLAY_SHOW -> {
                activity.get()?.setOverlayShow(msg.obj as? Boolean)
            }
            SET_OPTIONS_SHOW -> {
                activity.get()?.setOptionsShow(msg.obj as? Boolean, msg.arg1)
            }
        }
    }

    /**
     * 点击屏幕，切换控制相关界面的显示/隐藏
     * @param delay 延时ms
     */
    fun controlShow(delay: Long = ViewConfiguration.getDoubleTapTimeout().toLong()) {
        removeMessages(SET_CONTROL_SHOW)
        val msg = obtainMessage(SET_CONTROL_SHOW)
        sendMessageDelayed(msg, delay)
    }

    /**
     * 显示/隐藏 底部控制界面
     * @param show 是否显示，null时切换当前状态
     * @param delay 延时ms
     */
    fun overlayShow(
        show: Boolean? = null,
        delay: Long = ViewConfiguration.getDoubleTapTimeout().toLong()
    ) {
        removeMessages(SET_OVERLAY_SHOW)
        val msg = obtainMessage(SET_OVERLAY_SHOW, show)
        sendMessageDelayed(msg, delay)
    }

    /**
     * 显示/隐藏 右侧配置界面
     * @param type 类型 字幕配置 or 其他配置
     * @param show 是否显示，null时切换当前状态
     * @param delay 延时ms
     */
    fun optionsShow(
        show: Boolean? = null,
        @PlayerOption.PlayerOptionType type: Int = -1,
        delay: Long = ViewConfiguration.getDoubleTapTimeout().toLong()
    ) {
        removeMessages(SET_OPTIONS_SHOW)
        val msg = obtainMessage(SET_OPTIONS_SHOW, type, -1, show)
        sendMessageDelayed(msg, delay)
    }

}