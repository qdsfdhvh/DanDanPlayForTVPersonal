package com.seiko.player.di

import android.content.Context
import com.seiko.player.data.db.SlaveRepository
import com.seiko.player.data.prefs.PrefDataSource
import com.seiko.player.media.PlayerListManager
import com.seiko.player.media.PlayerController
import com.seiko.player.media.PlayerOptions
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val playModule = module {
    single { createPlayerOptions(androidContext(), get()) }
    single { createPlayerController(get()) }
    single { createPlayListManager(get(), get(), get()) }
}

private fun createPlayerOptions(context: Context, pref: PrefDataSource): PlayerOptions {
    return PlayerOptions(context, pref)
}

private fun createPlayerController(options: PlayerOptions): PlayerController {
    return PlayerController(options)
}

private fun createPlayListManager(
    options: PlayerOptions,
    playerController: PlayerController,
    slaveRepository: SlaveRepository
): PlayerListManager {
    return PlayerListManager(
        options,
        playerController,
        slaveRepository
    )
}