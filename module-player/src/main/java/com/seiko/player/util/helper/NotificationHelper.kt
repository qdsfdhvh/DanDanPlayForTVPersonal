package com.seiko.player.util.helper

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.seiko.player.R
import com.seiko.player.ui.StartActivity

object NotificationHelper {

    private const val MEDIALIBRRARY_CHANNEL_ID = "vlc_medialibrary"
    private const val ACTION_RESUME_SCAN = "action_resume_scan"
    private const val ACTION_PAUSE_SCAN = "action_pause_scan"

    private val notificationIntent = Intent()

    fun createScanNotification(ctx: Context, progressText: String, paused: Boolean): Notification {
        val intent = Intent(ctx, StartActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        val scanCompatBuilder = NotificationCompat.Builder(ctx, MEDIALIBRRARY_CHANNEL_ID)
            .setContentIntent(PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(ctx.getString(R.string.ml_scanning))
            .setAutoCancel(false)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setOngoing(true)
        scanCompatBuilder.setContentText(progressText)

        notificationIntent.action = if (paused) ACTION_RESUME_SCAN else ACTION_PAUSE_SCAN
        val pi = PendingIntent.getBroadcast(ctx, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val playpause = if (paused)
            NotificationCompat.Action(R.drawable.ic_play, ctx.getString(R.string.resume), pi)
        else
            NotificationCompat.Action(R.drawable.ic_pause, ctx.getString(R.string.pause), pi)
        scanCompatBuilder.addAction(playpause)
        return scanCompatBuilder.build()
    }

}