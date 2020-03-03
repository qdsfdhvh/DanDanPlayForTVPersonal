package com.seiko.player.util.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.seiko.player.R
import com.seiko.player.ui.StartActivity

object NotificationHelper {

    private const val MEDIA_LIBRARY_CHANNEL_ID = "player_media_library"
//    const val ACTION_RESUME_SCAN = "action_resume_scan"
//    const val ACTION_PAUSE_SCAN = "action_pause_scan"

//    private val notificationIntent = Intent()

    fun createScanNotification(ctx: Context, progressText: String): Notification {
        val intent = Intent(ctx, StartActivity::class.java)
        intent.action = Intent.ACTION_VIEW

        val scanCompatBuilder = NotificationCompat.Builder(ctx, MEDIA_LIBRARY_CHANNEL_ID)
            .setContentIntent(PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(ctx.getString(R.string.ml_scanning))
            .setAutoCancel(false)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setOngoing(true)
            .setContentText(progressText)
            .setDefaults(Notification.DEFAULT_SOUND)
//        notificationIntent.action = if (paused) ACTION_RESUME_SCAN else ACTION_PAUSE_SCAN
//        val pi = PendingIntent.getBroadcast(ctx, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//        val action = if (paused)
//            NotificationCompat.Action(R.drawable.ic_play, ctx.getString(R.string.resume), pi)
//        else
//            NotificationCompat.Action(R.drawable.ic_pause, ctx.getString(R.string.pause), pi)
//        scanCompatBuilder.addAction(action)
        return scanCompatBuilder.build()
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    fun createNotificationChannels(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channels = mutableListOf<NotificationChannel>()

        if (notificationManager.getNotificationChannel(MEDIA_LIBRARY_CHANNEL_ID) == null ) {
            val name = context.getString(R.string.media_library_scan)
            val description = context.getString(R.string.media_library_progress)
            val channel = NotificationChannel(MEDIA_LIBRARY_CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW)
            channel.description = description
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channels.add(channel)
        }
        if (channels.isNotEmpty()) notificationManager.createNotificationChannels(channels)
    }


}