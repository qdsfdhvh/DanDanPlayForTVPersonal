package com.seiko.player.util.extensions

import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import org.videolan.libvlc.util.AndroidUtil

@TargetApi(Build.VERSION_CODES.O)
fun Context.getPendingIntent(iPlay: Intent): PendingIntent {
    return if (AndroidUtil.isOOrLater) PendingIntent.getForegroundService(applicationContext, 0, iPlay, PendingIntent.FLAG_UPDATE_CURRENT)
    else PendingIntent.getService(applicationContext, 0, iPlay, PendingIntent.FLAG_UPDATE_CURRENT)
}

//suspend fun Context.awaitMediaLibraryStarted() = getFromMl { isStarted }

//@ExperimentalCoroutinesApi
//suspend inline fun <reified T> Context.getFromMl(crossinline block: Medialibrary.() -> T) = withContext(
//    Dispatchers.IO) {
//    val ml = Medialibrary.getInstance()
//    if (ml.isStarted) block.invoke(ml)
//    else {
//        val scan = Settings.getInstance(this@getFromMl).getInt(KEY_MEDIALIBRARY_SCAN, ML_SCAN_ON) == ML_SCAN_ON
//        suspendCancellableCoroutine { continuation ->
//            val listener = object : Medialibrary.OnMedialibraryReadyListener {
//                override fun onMedialibraryReady() {
//                    val cb = this
//                    if (!continuation.isCompleted) launch(start = CoroutineStart.UNDISPATCHED) {
//                        continuation.resume(block.invoke(ml))
//                        yield()
//                        ml.removeOnMedialibraryReadyListener(cb)
//                    }
//                }
//                override fun onMedialibraryIdle() {}
//            }
//            continuation.invokeOnCancellation { ml.removeOnMedialibraryReadyListener(listener) }
//            ml.addOnMedialibraryReadyListener(listener)
//            startMediaLibrary(false, false, scan)
//        }
//    }
//}

//fun Context.startMediaLibrary(
//    firstRun: Boolean = false,
//    upgrade: Boolean = false,
//    parse: Boolean = true) = AppScope.launch {
//    if (Medialibrary.getInstance().isStarted || !canReadStorage()) return@launch
//    val prefs = withContext(Dispatchers.IO) { Settings.getInstance(this@startMediaLibrary) }
//    val scanOpt = if (Settings.showTvUi) ML_SCAN_ON else prefs.getInt(KEY_MEDIALIBRARY_SCAN, -1)
//    if (parse && scanOpt == -1) {
//        if (dbExists()) {
//            prefs.edit().putInt(KEY_MEDIALIBRARY_SCAN, ML_SCAN_ON).apply()
//        }
//    }
//    val intent = Intent.makeMainActivity(ComponentName(applicationContext, "org.videolan.vlc.MediaParsingService"))
//    intent.action = ACTION_INIT
//    ContextCompat.startForegroundService(this@startMediaLibrary, intent
//        .putExtra(EXTRA_FIRST_RUN, firstRun)
//        .putExtra(EXTRA_UPGRADE, upgrade)
//        .putExtra(EXTRA_PARSE, parse && scanOpt != ML_SCAN_OFF))
//}
//
//private suspend fun Context.dbExists() = withContext(Dispatchers.IO) {
//    File(getDir("db", Context.MODE_PRIVATE).toString() + Medialibrary.VLC_MEDIA_DB_NAME).exists()
//}
//
//fun Context.canReadStorage(): Boolean {
//    return !AndroidUtil.isMarshMallowOrLater || ContextCompat.checkSelfPermission(this,
//        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
//}
