package com.seiko.player.ui

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.*
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.common.ui.dialog.DialogSelectFragment
import com.seiko.common.util.extensions.lazyAndroid
import com.seiko.common.util.toast.toast
import com.seiko.player.R
import com.seiko.player.databinding.PlayerActivityVideoBinding
import com.seiko.player.databinding.PlayerControlBottomBinding
import com.seiko.player.databinding.PlayerHudBinding
import com.seiko.player.delegate.VideoKeyDownDelegate
import com.seiko.player.delegate.VideoTouchDelegate
import com.seiko.player.media.PlayerListManager
import com.seiko.player.service.PlaybackService
import com.seiko.player.media.checkCpuCompatibility
import org.koin.android.ext.android.inject
import org.videolan.libvlc.util.DisplayManager
import org.videolan.medialibrary.MLServiceLocator
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import timber.log.Timber
import java.lang.ref.WeakReference

@Route(path = Routes.Player.PATH)
class VideoPlayerActivity: FragmentActivity()
    , ViewStub.OnInflateListener
    , View.OnClickListener {

    private lateinit var binding: PlayerActivityVideoBinding
    private lateinit var bindingHub: PlayerControlBottomBinding

//    private var playerHudBinding: PlayerHudBinding? = null
    private lateinit var displayManager: DisplayManager

    private val playListManager: PlayerListManager by inject()

    private val handler by lazyAndroid { VideoPlayerHandler(this) }
    private val keyDownDelegate by lazyAndroid { VideoKeyDownDelegate(playListManager) }
    private val touchDelegate by lazyAndroid { VideoTouchDelegate(handler) }

    private var isOverlayShow = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkCpuCompatibility()
        initDisplayManager()
        binding = PlayerActivityVideoBinding.inflate(layoutInflater)
        bindingHub = binding.playerViewStubHud.playerLayoutHub
        setContentView(binding.root)
        setupUI()
        loadMedia()
    }

    override fun onResume() {
        super.onResume()
        initUI()
        playListManager.play()
    }

    override fun onPause() {
        super.onPause()
        playListManager.pause()
        clearUI()
    }

    override fun onStop() {
        super.onStop()
        playListManager.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        playListManager.release()
        displayManager.release()
    }

    private fun initDisplayManager() {
        displayManager = DisplayManager(this, null,
            false, false, false)
    }

    private fun setupUI() {
//        if (!binding.playerViewStubHud.isInflated) {
//            binding.playerViewStubHud.setOnInflateListener(this)
//            binding.playerViewStubHud.viewStub!!.inflate()
//        }
        setupHudUI()
    }

    override fun onInflate(stub: ViewStub, inflated: View) {
//        when(stub.id) {
//            R.id.player_view_stub_hud -> {
//                playerHudBinding = PlayerHudBinding.bind(inflated)
//                setupHudUI(playerHudBinding!!)
//            }
//        }
    }

    private fun setupHudUI() {
//        binding.root.visibility = View.INVISIBLE
        bindingHub.playerBtnOverlayTracks.setOnClickListener(this)
        bindingHub.playerBtnOverlayPlay.setOnClickListener(this)
        bindingHub.playerBtnOverlayAdvFunction.setOnClickListener(this)
        handler.overlayShow(false,1500L)
    }

    private fun initUI() {
        playListManager.attachView(binding.videoLayout, displayManager)
        displayManager.setMediaRouterCallback()
        binding.root.keepScreenOn = true
    }

    private fun clearUI() {
        binding.root.keepScreenOn = false
        displayManager.removeMediaRouterCallback()
        playListManager.detachView()
    }

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

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.player_btn_overlay_tracks -> {
                toast("字幕管理")
            }
            R.id.player_btn_overlay_play -> {
                toast("播放/暂停")
            }
            R.id.player_btn_overlay_adv_function -> {
                toast("设置")
            }
        }
    }

    /**
     * 显示/隐藏 控制界面
     * PS：显示时，播放按钮获取焦点
     * @param show 是否显示，null时切换当前状态
     */
    fun setOverlayShow(show: Boolean?) {
        if (show == isOverlayShow) return
        if (show ?: !isOverlayShow) {
            isOverlayShow = true
            binding.playerViewStubHud.root.visibility = View.VISIBLE
            bindingHub.playerBtnOverlayPlay.requestFocus()
        } else {
            isOverlayShow = false
            binding.playerViewStubHud.root.visibility = View.INVISIBLE
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return touchDelegate.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return keyDownDelegate.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (isOverlayShow) {
            setOverlayShow(false)
            return
        }
        if (supportFragmentManager.findFragmentByTag(DialogSelectFragment.TAG) == null) {
            DialogSelectFragment.Builder()
                .setTitle("你真的确认退出播放吗？")
                .setConfirmText(getString(R.string.ok))
                .setCancelText(getString(R.string.cancel))
                .setConfirmClickListener {
                    finish()
                }
                .build()
                .show(supportFragmentManager)
        }
    }

}

class VideoPlayerHandler(activity: VideoPlayerActivity) : Handler(Looper.getMainLooper()) {

    companion object {
        private const val SET_OVERLAY_SHOW = 0
    }

    fun overlayShow(show: Boolean? = null, delay: Long = ViewConfiguration.getDoubleTapTimeout().toLong()) {
        removeMessages(SET_OVERLAY_SHOW)
        val msg = obtainMessage(SET_OVERLAY_SHOW, show)
        sendMessageDelayed(msg, delay)
    }

    private val activity = WeakReference(activity)

    override fun handleMessage(msg: Message) {
        when(msg.what) {
            SET_OVERLAY_SHOW -> {
                activity.get()?.setOverlayShow(msg.obj as? Boolean)
            }
        }
    }
}