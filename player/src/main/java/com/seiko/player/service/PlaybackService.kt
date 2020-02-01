package com.seiko.player.service

import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import androidx.media.MediaBrowserServiceCompat
import com.seiko.player.ui.VideoPlayerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.videolan.medialibrary.MLServiceLocator
import org.videolan.medialibrary.interfaces.media.MediaWrapper
import timber.log.Timber

class PlaybackService : IntentService("PlaybackService"), CoroutineScope by MainScope() {

    companion object {

        private const val ACTION_LOAD_MEDIA = "ACTION_LOAD_MEDIA"
        private const val ACTION_LOAD_MEDIA_LIST = "ACTION_LOAD_MEDIA_LIST"

        private const val EXTRA_LOAD_MEDIA = "EXTRA_LOAD_MEDIA"
        private const val EXTRA_LOAD_MEDIA_LIST = "EXTRA_LOAD_MEDIA_LIST"
        private const val EXTRA_LOAD_MEDIA_POSITION = "EXTRA_LOAD_MEDIA_POSITION"

//        @JvmStatic
//        fun start(context: Context) {
//            val serviceIntent = Intent(context, PlaybackService::class.java)
//            ContextCompat.startForegroundService(context, serviceIntent)
//        }

//        @JvmStatic
//        fun openMediaNoUi(context: Context, uri: Uri) {
//            openMediaNoUi(context, MLServiceLocator.getAbstractMediaWrapper(uri))
//        }

        @JvmStatic
        fun openMediaNoUi(context: Context, media: MediaWrapper) {
            val intent = Intent(context, PlaybackService::class.java)
            intent.action = ACTION_LOAD_MEDIA
            intent.putExtra(EXTRA_LOAD_MEDIA, media)
            context.startService(intent)
        }

        @JvmStatic
        fun openList(context: Context, list: List<MediaWrapper>, position: Int) {
            val intent = Intent(context, PlaybackService::class.java)
            intent.action = ACTION_LOAD_MEDIA_LIST
            intent.putParcelableArrayListExtra(EXTRA_LOAD_MEDIA_LIST, ArrayList(list))
            intent.putExtra(EXTRA_LOAD_MEDIA_POSITION, position)
            context.startService(intent)
        }
    }

    private val playListManager: PlayListManager by inject()

    override fun onHandleIntent(intent: Intent?) {
        when(intent?.action) {
            ACTION_LOAD_MEDIA -> {
                val media: MediaWrapper = intent.getParcelableExtra(EXTRA_LOAD_MEDIA)!!
                load(listOf(media), 0)
            }
            ACTION_LOAD_MEDIA_LIST -> {
                val mediaList: List<MediaWrapper> = intent.getParcelableArrayListExtra(EXTRA_LOAD_MEDIA_LIST)!!
                val position = intent.getIntExtra(EXTRA_LOAD_MEDIA_POSITION, 0)
                load(mediaList, position)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
    }

    private fun load(mediaList: List<MediaWrapper>, position: Int) = runBlocking(coroutineContext) {
        val success = playListManager.load(mediaList, position)
        Timber.d("Play Success=$success")
//        if (success) {
//            val intent = Intent(this@PlaybackService, VideoPlayerActivity::class.java)
//            startActivity(intent)
//        }
    }


}