package com.seiko.player.ui

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.seiko.common.router.Routes
import com.seiko.common.ui.dialog.DialogSelectFragment
import com.seiko.player.R
import com.seiko.player.databinding.PlayerBinding
import com.seiko.player.media.PlayerListManager
import com.seiko.player.service.PlaybackService
import com.seiko.player.util.checkCpuCompatibility
import org.koin.android.ext.android.inject
import org.videolan.libvlc.util.DisplayManager
import org.videolan.medialibrary.MLServiceLocator
import org.videolan.medialibrary.interfaces.Medialibrary
import org.videolan.medialibrary.interfaces.media.MediaWrapper

@Route(path = Routes.Player.PATH)
class VideoPlayerActivity: FragmentActivity() {

    private lateinit var binding: PlayerBinding
    private lateinit var displayManager: DisplayManager

    private val playListManager: PlayerListManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkCpuCompatibility()
        initDisplayManager()
        binding = PlayerBinding.inflate(layoutInflater)
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