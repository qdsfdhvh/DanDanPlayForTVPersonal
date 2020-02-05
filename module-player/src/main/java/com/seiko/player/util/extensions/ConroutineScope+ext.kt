package com.seiko.player.util.extensions

import kotlinx.coroutines.delay

suspend fun retry(
    times: Int = 3,
    delayTime: Long = 500L,
    block: suspend () -> Boolean
): Boolean {
    repeat(times - 1) {
        if (block()) return true
        if (delayTime > 0L) delay(delayTime)
    }
    return block() // last attempt
}