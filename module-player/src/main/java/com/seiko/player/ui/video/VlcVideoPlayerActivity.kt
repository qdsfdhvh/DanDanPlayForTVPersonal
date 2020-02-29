package com.seiko.player.ui.video

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.seiko.player.data.model.PlayParam
import com.seiko.player.databinding.PlayerActivityVideoVlcBinding
import com.seiko.player.vlc.media.PlayerListManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.DisplayManager
import org.videolan.medialibrary.MLServiceLocator
import org.videolan.medialibrary.interfaces.Medialibrary
import timber.log.Timber

class VlcVideoPlayerActivity : FragmentActivity() {

    companion object {

        private const val ARGS_VIDEO_PARAMS = "ARGS_VIDEO_PARAMS"

        fun launch(context: Context, param: PlayParam) {
            val intent = Intent(context, VlcVideoPlayerActivity::class.java)
            intent.putExtra(ARGS_VIDEO_PARAMS, param)
            context.startActivity(intent)
        }
    }

    private var _binding: PlayerActivityVideoVlcBinding? = null
    private val binding get() = _binding!!
    private val videoLayout get() = binding.playerVideoViewVlc

    private val playerListManager: PlayerListManager by inject()
    private val mediaPlayer get() = playerListManager.mediaPlayer

    private lateinit var displayManager: DisplayManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = PlayerActivityVideoVlcBinding.inflate(layoutInflater)
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
        playerListManager.play()
        binding.root.keepScreenOn = true
    }

    override fun onPause() {
        binding.root.keepScreenOn = false
        playerListManager.pause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        displayManager.release()
        GlobalScope.launch {
            playerListManager.release()
        }
    }

    private fun setupUI() {
        displayManager = DisplayManager(this, null, false, false, false)
        mediaPlayer.attachViews(videoLayout, displayManager, true, false)
        mediaPlayer.videoScale = MediaPlayer.ScaleType.SURFACE_BEST_FIT
    }

    /**
     * 读取播放源
     */
    private fun setVideoUri() {
        val intent = intent ?: return
        val param: PlayParam = intent.getParcelableExtra(ARGS_VIDEO_PARAMS) ?: return
        Timber.d(param.videoPath)
        var media = Medialibrary.getInstance().getMedia(param.videoPath)
        if (media == null) {
            media = MLServiceLocator.getAbstractMediaWrapper(Uri.parse(param.videoPath))
        }
        Timber.d(media.toString())
        lifecycleScope.launch {
            playerListManager.load(listOf(media), 0)
        }
    }

}