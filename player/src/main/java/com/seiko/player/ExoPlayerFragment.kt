package com.seiko.player

import android.net.Uri
import android.os.Bundle
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.leanback.LeanbackPlayerAdapter
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.seiko.common.toast.toast

class ExoPlayerFragment : VideoSupportFragment() {

//    private val args by navArgs<PlayerManagerActivityArgs>()

    private lateinit var mPlaybackControlGlue: com.seiko.player.PlaybackControlsGlue<LeanbackPlayerAdapter>
    private lateinit var exoPlayer: ExoPlayer

//    private var isUseSurfaceView = true

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // 是否有效的判断放入PlayManagerActivity，这里直接认为有效。
        val bundle = arguments!!
        val videoTitle = bundle.getString(ARGS_VIDEO_TITLE)!!
        val videoPath =  bundle.getString(ARGS_VIDEO_PATH)!!

        setupPlayer(videoTitle)
        setVideoPath(videoPath)
    }

    private fun setupPlayer(videoTitle: String) {
        val bandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        exoPlayer = ExoPlayerFactory.newSimpleInstance(activity, trackSelector)
        val playerAdapter = LeanbackPlayerAdapter(activity, exoPlayer,
            UPDATE_DELAY
        )
        mPlaybackControlGlue =
            com.seiko.player.PlaybackControlsGlue(activity, playerAdapter)
        mPlaybackControlGlue.host = VideoSupportFragmentGlueHost(this)
        mPlaybackControlGlue.title = videoTitle
//        transportControlGlue.subtitle = video.description
        mPlaybackControlGlue.playWhenPrepared()

        exoPlayer.addListener(object : Player.EventListener {
            override fun onPlayerError(error: ExoPlaybackException?) {
                toast("播放失败")
            }
        })
    }

    private fun setVideoPath(videoPath: String) {
        val userAgent = Util.getUserAgent(activity, "com.dandanplay.tv.player")
        val dataSourceFactory = DefaultDataSourceFactory(activity, userAgent)
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .setExtractorsFactory(DefaultExtractorsFactory())
            .createMediaSource(Uri.parse(videoPath))
        exoPlayer.prepare(mediaSource)
    }

    override fun onPause() {
        super.onPause()
        mPlaybackControlGlue.pause()
    }

    override fun onDestroy() {
        exoPlayer.release()
        super.onDestroy()
    }

    companion object {
        const val TAG = "ExoPlayerFragment"

        private const val UPDATE_DELAY = 16

        private const val ARGS_VIDEO_TITLE = "ARGS_VIDEO_TITLE"
        private const val ARGS_VIDEO_PATH = "ARGS_VIDEO_PATH"

        fun newInstance(videoTitle: String, videoPath: String): ExoPlayerFragment {
            val bundle = Bundle()
            bundle.putString(ARGS_VIDEO_TITLE, videoTitle)
            bundle.putString(ARGS_VIDEO_PATH, videoPath)

            val fragment = ExoPlayerFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

}