package com.seiko.player.vlc.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay

fun CoroutineScope.conflatedActor(time: Long = 2000L, action: () -> Unit) = actor<Unit>(capacity = Channel.CONFLATED) {
    for (evt in channel) {
        action()
        delay(time)
    }
}
