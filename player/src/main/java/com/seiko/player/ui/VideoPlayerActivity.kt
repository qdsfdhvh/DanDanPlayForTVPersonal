package com.seiko.player.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.common.ui.dialog.DialogSelectFragment
import com.seiko.player.R
import com.seiko.player.databinding.PlayerBinding
import com.seiko.player.service.PlayListManager
import com.seiko.player.service.PlaybackService
import com.seiko.player.util.VLCOptions
import com.seiko.player.util.checkCpuCompatibility
import com.seiko.player.util.getUri
import org.koin.android.ext.android.inject
import org.videolan.libvlc.FactoryManager
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.interfaces.ILibVLCFactory
import org.videolan.libvlc.interfaces.IVLCVout
import org.videolan.libvlc.util.DisplayManager
import org.videolan.medialibrary.MLServiceLocator
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import timber.log.Timber

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
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        startPlayback()
        initUI()
    }

    override fun onPause() {
        clearUI()
        stopPlayback()
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
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

    }

    private fun startPlayback() {
        if (playbackStarted) return
        playbackStarted = true
        playListManager.attachView(binding.videoLayout, displayManager)
        loadMedia()
    }

    private fun stopPlayback() {
        if (!playbackStarted) return
        playbackStarted = false
        playListManager.detachView()
        playListManager.stop(false)
    }

    private fun initUI() {
        displayManager.setMediaRouterCallback()
        binding.root.keepScreenOn = true
    }

    private fun clearUI() {
        binding.root.keepScreenOn = false
        displayManager.removeMediaRouterCallback()
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
            PlaybackService.openMediaNoUi(this, media)
        }
    }

    override fun onBackPressed() {
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