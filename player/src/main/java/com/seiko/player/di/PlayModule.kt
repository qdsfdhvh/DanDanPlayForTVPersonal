package com.seiko.player.di

import android.content.Context
import com.seiko.player.data.db.SlaveRepository
import com.seiko.player.service.PlayListManager
import com.seiko.player.service.PlayerController
import com.seiko.player.util.VLCOptions
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.videolan.libvlc.FactoryManager
import org.videolan.libvlc.interfaces.ILibVLC
import org.videolan.libvlc.interfaces.ILibVLCFactory

val playModule = module {
    single { createLibVlc(androidContext()) }
    single { createPlayController(androidContext(), get()) }
    single { createPlayListManager(androidContext(), get(), get(), get()) }
}

private fun createLibVlc(context: Context): ILibVLC {
    val libVLCFactory = FactoryManager.getFactory(ILibVLCFactory.factoryId) as ILibVLCFactory
    return libVLCFactory.getFromOptions(context, VLCOptions.getLibOptions(context))
}

private fun createPlayController(context: Context, libVLC: ILibVLC): PlayerController {
    return PlayerController(context, libVLC)
}

private fun createPlayListManager(
    context: Context,
    libVLC: ILibVLC,
    playerController: PlayerController,
    slaveRepository: SlaveRepository
): PlayListManager {
    return PlayListManager(context, libVLC, playerController, slaveRepository)
}