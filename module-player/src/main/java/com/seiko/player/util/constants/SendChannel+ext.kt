package com.seiko.player.util.constants

import kotlinx.coroutines.channels.SendChannel
import java.util.concurrent.CancellationException

fun <E> SendChannel<E>.safeOffer(value: E) = !isClosedForSend && try {
    offer(value)
} catch (e: CancellationException) {
    false
}