package com.seiko.player.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.common.ui.dialog.DialogSelectFragment
import com.seiko.player.databinding.PlayerBinding
import com.seiko.player.service.PlayListManager
import com.seiko.player.util.checkCpuCompatibility
import org.koin.android.ext.android.inject
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.DisplayManager
import java.lang.ref.WeakReference

@Route(path = Routes.Player.PATH)
class VideoPlayerActivity: FragmentActivity() {

    private lateinit var binding: PlayerBinding
    private lateinit var displayManager: DisplayManager

    private val playListManager: PlayListManager by inject()

    private var playbackStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkCpuCompatibility()
        initDisplayManager()
        binding = PlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()

        onBackPressedDispatcher.addCallback(this) { launchExitDialog() }
    }

    override fun onResume() {
        super.onResume()
        startPlayback()
    }

    override fun onPause() {
        super.onPause()
        stopPlayback()
    }

    private fun initDisplayManager() {
        displayManager = DisplayManager(this,
            MutableLiveData(playListManager.getRendererItem()), true, false, false)

    }

    private fun setupUI() {

    }

    fun startPlayback() {
        if (playbackStarted) return

        playbackStarted = true
        val mediaPlayer = playListManager.getMediaPlayer()
        val vlcVout = mediaPlayer.vlcVout
        if (vlcVout.areViewsAttached()) {
            vlcVout.detachViews()
        }
        mediaPlayer.attachViews(binding.videoLayout, displayManager, true, false)
        mediaPlayer.videoScale = MediaPlayer.ScaleType.values()[
                MediaPlayer.ScaleType.SURFACE_BEST_FIT.ordinal
        ]
    }

    private fun stopPlayback() {
        if (!playbackStarted) return

        playbackStarted = false
        val mediaPlayer = playListManager.getMediaPlayer()
        mediaPlayer.detachViews()
        playListManager.stop(false)
    }

    private fun launchExitDialog() {
        if (supportFragmentManager.findFragmentByTag(DialogSelectFragment.TAG) == null) {
            DialogSelectFragment.Builder()
                .setTitle("你真的确认退出播放吗？")
                .setConfirmText("确认")
                .setCancelText("取消")
                .setConfirmClickListener {
                    ActivityCompat.finishAffinity(this)
                }
                .build()
                .show(supportFragmentManager)
        }
    }

    override fun onStop() {
        super.onStop()
//        val thunderTaskId = intent?.getLongExtra(ARGS_THUNDER_TASK_ID, -1L) ?: -1L
//        if (thunderTaskId != -1L) {
//            Timber.d("停止thunderTaskId=$thunderTaskId。")
//        }
        finish()
    }
}

private const val START_PLAYBACK = 0

private class ViewPlayerHandler(activity: VideoPlayerActivity) : Handler(Looper.getMainLooper()) {
    private val activity = WeakReference(activity)
    override fun handleMessage(msg: Message) {
        when(msg.what) {
            START_PLAYBACK -> activity.get()?.startPlayback()
        }
    }
}