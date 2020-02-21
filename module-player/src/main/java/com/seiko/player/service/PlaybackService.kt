package com.seiko.player.service

import android.app.IntentService
import android.content.Context
import android.content.Intent

class PlaybackService : IntentService("PlaybackService") {

    companion object {

        private const val ACTION_LOAD_MEDIA = "ACTION_LOAD_MEDIA"
        private const val ACTION_LOAD_MEDIA_LIST = "ACTION_LOAD_MEDIA_LIST"
        private const val ACTION_EXIT_PLAYER = "ACTION_EXIT_PLAYER"

        private const val EXTRA_LOAD_MEDIA = "EXTRA_LOAD_MEDIA"
        private const val EXTRA_LOAD_MEDIA_LIST = "EXTRA_LOAD_MEDIA_LIST"
        private const val EXTRA_LOAD_MEDIA_POSITION = "EXTRA_LOAD_MEDIA_POSITION"

//        @JvmStatic
//        fun openMedia(context: Context, media: MediaWrapper) {
//            val intent = Intent(context, PlaybackService::class.java)
//            intent.action = ACTION_LOAD_MEDIA
//            intent.putExtra(EXTRA_LOAD_MEDIA, media)
//            context.startService(intent)
//        }
//
//        @JvmStatic
//        fun openMediaList(context: Context, list: List<MediaWrapper>, position: Int) {
//            val intent = Intent(context, PlaybackService::class.java)
//            intent.action = ACTION_LOAD_MEDIA_LIST
//            intent.putParcelableArrayListExtra(EXTRA_LOAD_MEDIA_LIST, ArrayList(list))
//            intent.putExtra(EXTRA_LOAD_MEDIA_POSITION, position)
//            context.startService(intent)
//        }

        @JvmStatic
        fun exitPlay(context: Context) {
            val intent = Intent(context, PlaybackService::class.java)
            intent.action = ACTION_EXIT_PLAYER
            context.startService(intent)
        }

    }

//    private val playListManager: PlayerListManager by inject()

    override fun onHandleIntent(intent: Intent?) {
        when(intent?.action) {
            ACTION_LOAD_MEDIA -> {
//                val media: MediaWrapper = intent.getParcelableExtra(EXTRA_LOAD_MEDIA)!!
//                load(listOf(media), 0)
            }
            ACTION_LOAD_MEDIA_LIST -> {
//                val mediaList: List<MediaWrapper> = intent.getParcelableArrayListExtra(EXTRA_LOAD_MEDIA_LIST)!!
//                val position = intent.getIntExtra(EXTRA_LOAD_MEDIA_POSITION, 0)
//                load(mediaList, position)
            }
        }
    }

//    private fun load(mediaList: List<MediaWrapper>, position: Int) = runBlocking(coroutineContext) {
//        playListManager.load(mediaList, position)
//    }

}