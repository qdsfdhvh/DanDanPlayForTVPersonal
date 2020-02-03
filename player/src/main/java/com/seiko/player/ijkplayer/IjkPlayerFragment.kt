package com.seiko.player.ijkplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seiko.player.databinding.PlayerFragmentIjkBinding
import com.seiko.player.media.player.MediaPlayerCreatorFactory
import org.koin.android.ext.android.inject

class IjkPlayerFragment : Fragment() {

    private lateinit var binding: PlayerFragmentIjkBinding

    private val mediaPlayerFactory: MediaPlayerCreatorFactory by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = PlayerFragmentIjkBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
//        val mediaPlayer = mediaPlayerFactory.getCreator(MediaPlayerCreatorFactory.Type.IJK_PLAYER)
//        binding.playerVideoViewIjk.setMediaPlayer(mediaPlayer, true)
    }

}